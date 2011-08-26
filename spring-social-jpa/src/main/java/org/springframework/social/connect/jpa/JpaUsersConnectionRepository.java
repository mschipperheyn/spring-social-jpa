package org.springframework.social.connect.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */
public class JpaUsersConnectionRepository implements UsersConnectionRepository{
	
	private JpaTemplate jpaTemplate;

	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final TextEncryptor textEncryptor;

	private ConnectionSignUp connectionSignUp;
	
	public JpaUsersConnectionRepository(JpaTemplate jpaTemplate, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
		this.jpaTemplate = jpaTemplate;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
	}

	/**
	 * The command to execute to create a new local user profile in the event no user id could be mapped to a connection.
	 * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
	 * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
	 * @see #findUserIdWithConnection(Connection)
	 */
	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
	}
	
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		List<String> usrs = new ArrayList<String>();
		ConnectionKey key = connection.getKey();
		List<RemoteUser> users = jpaTemplate.get(key.getProviderId(), key.getProviderUserId());
		if(!users.isEmpty()){
			for(RemoteUser user : users){
				usrs.add(user.getUserId());
			}
			return usrs;
		}

		if (connectionSignUp != null) {
			String newUserId = connectionSignUp.execute(connection);
			if(newUserId == null)
				//auto signup failed, so we need to go to a sign up form
				return usrs;
			createConnectionRepository(newUserId).addConnection(connection);
			usrs.add(newUserId);
		}
		//if empty we should go to the sign up form
		return usrs;
	}

	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		return jpaTemplate.findUsersConnectedTo(providerId, providerUserIds);
	}

	public ConnectionRepository createConnectionRepository(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userId cannot be null");
		}
		return new JpaConnectionRepository(userId, jpaTemplate, connectionFactoryLocator, textEncryptor);
	}
}
