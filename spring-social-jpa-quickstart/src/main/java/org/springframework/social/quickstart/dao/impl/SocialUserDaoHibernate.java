package org.springframework.social.quickstart.dao.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.social.connect.jpa.RemoteUser;
import org.springframework.social.quickstart.dao.SocialUserDao;
import org.springframework.social.quickstart.model.LocalUser;
import org.springframework.social.quickstart.model.SocialUser;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */

@Repository
public class SocialUserDaoHibernate extends HibernateDaoSupport implements SocialUserDao{
	
	public LocalUser getUser(String userId) {
		return (LocalUser) this.getSessionFactory().getCurrentSession()
		.createCriteria(LocalUser.class)
		.add(Restrictions.eq("email", userId))
		.uniqueResult();
	}

	public Set<String> findUsersConnectedTo(
			String providerId,
			Set<String> providerUserIds) {
		String hql = "select distinct su.user.email from SocialUser su where su.providerId = :providerId and su.providerUserId in (:providerUserIds)";
		return new HashSet<String>(this.getSessionFactory().getCurrentSession()
				.createQuery(hql).setString("providerId", providerId)
				.setParameterList("providerUserId", providerUserIds).list());
	}

	public List<RemoteUser> getAll(String userId) {
		return this.getSessionFactory().getCurrentSession()
				.createCriteria(SocialUser.class).createAlias("user", "user")
				.add(Restrictions.eq("user.email", userId))
				.addOrder(Order.desc("providerId"))
				.addOrder(Order.desc("rank")).list();
	}

	public List<RemoteUser> getAll(String userId, String providerId) {
		return this.getSessionFactory().getCurrentSession()
				.createCriteria(SocialUser.class).createAlias("user", "user")
				.add(Restrictions.eq("user.email", userId))
				.add(Restrictions.eq("providerId", providerId))
				.addOrder(Order.desc("rank")).list();
	}

	public List<RemoteUser> getAll(
			String userId,
			MultiValueMap<String, String> providerUsers) {
		if (providerUsers.isEmpty())
			return Collections.<RemoteUser> emptyList();

		Criteria c = this.getSessionFactory().getCurrentSession()
				.createCriteria(SocialUser.class).createAlias("user", "user")
				.add(Restrictions.eq("user.email", userId));

		Disjunction orExp = Restrictions.disjunction();
		for (Iterator<Entry<String, List<String>>> it = providerUsers
				.entrySet().iterator(); it.hasNext();) {
			Entry<String, List<String>> entry = it.next();
			String providerId = entry.getKey();
			LogicalExpression andExp = Restrictions.and(
					Restrictions.eq("providerId", providerId),
					Restrictions.in("providerUserId", entry.getValue()));
			orExp.add(andExp);
		}
		c.add(orExp);
		return c.list();
	}

	public RemoteUser get(
			String userId,
			String providerId,
			String providerUserId) {
		return (RemoteUser) this.getSessionFactory().getCurrentSession()
				.createCriteria(SocialUser.class).createAlias("user", "user")
				.add(Restrictions.eq("user.email", userId))
				.add(Restrictions.eq("providerId", providerId))
				.add(Restrictions.eq("providerUserId", providerUserId))
				.uniqueResult();
	}

	public List<RemoteUser> get(String providerId, String providerUserId)  throws IncorrectResultSizeDataAccessException{
		return this.getSessionFactory().getCurrentSession()
				.createCriteria(SocialUser.class).createAlias("user", "user")
				.add(Restrictions.eq("providerId", providerId))
				.add(Restrictions.eq("providerUserId", providerUserId))
				.list();
	}

	public void remove(String userId, String providerId) {
		SocialUser user = (SocialUser) this
			.getSessionFactory()
			.getCurrentSession()
			.createCriteria(SocialUser.class)
			.createAlias("user", "user")
			.add(Restrictions.eq("user.email", userId))
			.add(Restrictions.eq("providerId", providerId))
			.uniqueResult();
		if(user != null)
			this.getSessionFactory()
			.getCurrentSession()
			.delete(user);

	}

	public void remove(String userId, String providerId, String providerUserId) {
		SocialUser user = (SocialUser) this
			.getSessionFactory()
			.getCurrentSession()
			.createCriteria(SocialUser.class)
			.createAlias("user", "user")
			.add(Restrictions.eq("user.email", userId))
			.add(Restrictions.eq("providerId", providerId))
			.add(Restrictions.eq("providerUserId", providerUserId))
			.uniqueResult();
		
		if(user != null)
			this.getSessionFactory()
			.getCurrentSession()
			.delete(user);
	}

	public List<RemoteUser> getPrimary(String userId, String providerId) {
		return this.getSessionFactory().getCurrentSession()
				.createCriteria(SocialUser.class).createAlias("user", "user")
				.add(Restrictions.eq("user.email", userId))
				.add(Restrictions.eq("providerId", providerId))
				.add(Restrictions.eq("rank", 1)).list();
	}

	public int getRank(String userId, String providerId) {
		String hql = "select max(rank) as rank from SocialUser su where su.user.email = :userId and su.providerId = :providerId";
		List<Integer> result = this.getSessionFactory().getCurrentSession()
				.createQuery(hql).setString("userId", userId)
				.setString("providerId", providerId).list();
		if (result.isEmpty() || result.get(0) == null)
			return 1;
		return result.get(0) + 1;
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
		RemoteUser ru = get(userId, providerId, providerUserId);
		if(ru != null)
			return ru;
		
		LocalUser user = this.getUser(userId);
		
		SocialUser su = new SocialUser(user, providerId, providerUserId, rank,
				displayName, profileUrl, imageUrl, accessToken, secret,
				refreshToken, expireTime);
		return save(su);
	}

	public RemoteUser save(RemoteUser user) {
		return this.save((SocialUser) user);
	}
}
