package org.springframework.social.quickstart.service.impl;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.jpa.RemoteUser;
import org.springframework.social.quickstart.dao.SocialUserDao;
import org.springframework.social.quickstart.model.LocalUser;
import org.springframework.social.quickstart.service.LocalUserManager;
import org.springframework.social.quickstart.service.SocialUserManager;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */

@Service("socialUserManager")
public class SocialUserManagerImpl implements SocialUserManager {

	private SocialUserDao socialUserDao;
	private LocalUserManager userMgr;

	@Autowired
	public void setSocialUserDao(SocialUserDao socialUserDao) {
		this.socialUserDao = socialUserDao;
	}

	@Autowired
	public void setLocalUserManager(LocalUserManager userMgr) {
		this.userMgr = userMgr;
	}

	/**
	 * The command to execute to create a new local user profile in the event no
	 * user id could be mapped to a connection. Allows for implicitly creating a
	 * user profile from connection data during a provider sign-in attempt.
	 * 
	 * @see #nl.msw.findUserIdWithConnection(Connection)
	 */

	public String execute(Connection<?> connection) {
		// First check to see if there already is a user for this email address.
		// If there is, return that user id
		// If there isn't, create a new user, activate him and send a welcome
		// email.
		UserProfile profile = connection.fetchUserProfile();

		// If we can't get the users email, e.g. twitter
		if (profile.getEmail() == null)
			return null;

		LocalUser user = null;
		user = userMgr.getUserByEmail(connection.fetchUserProfile().getEmail());
		if (user != null)
			return user.getEmail();

		user = userMgr.createUser(profile.getUsername(), profile.getEmail(),
				"create random password here");

		user = userMgr.save(user);
		return user.getEmail();

	}

	public Set<String> findUsersConnectedTo(
			String providerId,
			Set<String> providerUserIds) {
		return socialUserDao.findUsersConnectedTo(providerId, providerUserIds);
	}

	public List<RemoteUser> getPrimary(String userId, String providerId) {
		return socialUserDao.getPrimary(userId, providerId);
	}

	public int getRank(String userId, String providerId) {
		return socialUserDao.getRank(userId, providerId);
	}

	public List<RemoteUser> getAll(
			String userId,
			MultiValueMap<String, String> providerUsers) {
		return socialUserDao.getAll(userId, providerUsers);
	}

	public List<RemoteUser> getAll(String userId) {
		return socialUserDao.getAll(userId);
	}

	public List<RemoteUser> getAll(String userId, String providerId) {
		return socialUserDao.getAll(userId, providerId);
	}

	public RemoteUser get(
			String userId,
			String providerId,
			String providerUserId) {
		return socialUserDao.get(userId, providerId, providerUserId);
	}

	public List<RemoteUser> get(String providerId, String providerUserId)
			throws IncorrectResultSizeDataAccessException {
		return socialUserDao.get(providerId, providerUserId);
	}

	public void remove(String userId, String providerId) {
		socialUserDao.remove(userId, providerId);
	}

	public void remove(String userId, String providerId, String providerUserId) {
		socialUserDao.remove(userId, providerId, providerUserId);

	}

	public RemoteUser createRemoteUser(
			String userId,
			String providerId,
			String providerUserId,
			int rank,
			String displayName,
			String profileUrl,
			String imageUrl,
			String accessToken,
			String secret,
			String refreshToken,
			Long expireTime) {
		return socialUserDao.createRemoteUser(userId, providerId,
				providerUserId, rank, displayName, profileUrl, imageUrl,
				accessToken, secret, refreshToken, expireTime);
	}

	public RemoteUser save(RemoteUser user) {
		return socialUserDao.save(user);
	}

}