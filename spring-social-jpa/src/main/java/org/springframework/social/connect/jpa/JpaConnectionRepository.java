package org.springframework.social.connect.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */
public class JpaConnectionRepository implements ConnectionRepository {

	private JpaTemplate jpaTemplate;
	
	private final String userId;
	
	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final TextEncryptor textEncryptor;
	
	public JpaConnectionRepository(String userId, JpaTemplate jpaTemplate, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor){
		this.jpaTemplate = jpaTemplate;
		this.userId = userId;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
	}

	public MultiValueMap<String, Connection<?>> findAllConnections() {	
		List<Connection<?>> resultList = connectionMapper.mapEntities(jpaTemplate.getAll(userId));
		
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.<Connection<?>>emptyList());
		}
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<Connection<?>>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	public List<Connection<?>> findConnections(String providerId) {
		return connectionMapper.mapEntities(jpaTemplate.getAll(userId,providerId));
	}

	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}
	
	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
		if (providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}		
		
		List<Connection<?>> resultList = connectionMapper.mapEntities(jpaTemplate.getAll(userId,providerUsers));
		
		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			List<String> userIds = providerUsers.get(providerId);
			List<Connection<?>> connections = connectionsForUsers.get(providerId);
			if (connections == null) {
				connections = new ArrayList<Connection<?>>(userIds.size());
				for (int i = 0; i < userIds.size(); i++) {
					connections.add(null);
				}
				connectionsForUsers.put(providerId, connections);
			}
			String providerUserId = connection.getKey().getProviderUserId();
			int connectionIndex = userIds.indexOf(providerUserId);
			connections.set(connectionIndex, connection);
		}
		return connectionsForUsers;
	}

	public Connection<?> getConnection(ConnectionKey connectionKey) {
		try {
			return connectionMapper.mapEntity(jpaTemplate.get(userId,connectionKey.getProviderId(), connectionKey.getProviderUserId()));
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		if (connection == null) {
			throw new NotConnectedException(providerId);
		}
		return connection;
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}
	
	@Transactional
	public void addConnection(Connection<?> connection) {
		try {
			ConnectionData data = connection.createData();
			int rank = jpaTemplate.getRank(userId, data.getProviderId()) ;
			
			jpaTemplate.createRemoteUser(userId, data.getProviderId(), data.getProviderUserId(), rank, data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime());
		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}
	
	public void updateConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();
		
		RemoteUser su = jpaTemplate.get(userId,data.getProviderId(),data.getProviderUserId());
		if(su != null){
			su.setDisplayName(data.getDisplayName());
			su.setProfileUrl(data.getProfileUrl());
			su.setImageUrl(data.getImageUrl());
			su.setAccessToken(encrypt(data.getAccessToken()));
			su.setSecret(encrypt(data.getSecret()));
			su.setRefreshToken(encrypt(data.getRefreshToken()));
			su.setExpireTime(data.getExpireTime());
			
			su = jpaTemplate.save(su);
		}
	}

	public void removeConnections(String providerId) {
		jpaTemplate.remove(userId,providerId);
	}

	public void removeConnection(ConnectionKey connectionKey) {
		jpaTemplate.remove(userId,connectionKey.getProviderId(), connectionKey.getProviderUserId());		
	}
	
	private Connection<?> findPrimaryConnection(String providerId) {
		List<Connection<?>> connections = connectionMapper.mapEntities(jpaTemplate.getPrimary(userId,providerId));
		if (connections.size() > 0) {
			return connections.get(0);
		} else {
			return null;
		}		
	}
	
	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	
	private final class ServiceProviderConnectionMapper  {
		
		public List<Connection<?>> mapEntities(List<RemoteUser> socialUsers){
			List<Connection<?>> result = new ArrayList<Connection<?>>();
			for(RemoteUser su : socialUsers){
				result.add(mapEntity(su));
			}
			return result;
		}
		
		public Connection<?> mapEntity(RemoteUser socialUser){
			ConnectionData connectionData = mapConnectionData(socialUser);
			ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}
		
		private ConnectionData mapConnectionData(RemoteUser socialUser){
			return new ConnectionData(socialUser.getProviderId(), socialUser.getProviderUserId(), socialUser.getDisplayName(), socialUser.getProfileUrl(), socialUser.getImageUrl(),
					decrypt(socialUser.getAccessToken()), decrypt(socialUser.getSecret()), decrypt(socialUser.getRefreshToken()), expireTime(socialUser.getExpireTime()));
		}
		
		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
		
		private Long expireTime(Long expireTime) {
			return expireTime == null || expireTime == 0 ? null : expireTime;
		}
		
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}
	
	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}
}