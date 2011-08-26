package org.springframework.social.quickstart.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Index;
import org.springframework.social.connect.jpa.RemoteUser;

@Entity
@Table(name = "SocialUser")
@org.hibernate.annotations.Table(appliesTo = "SocialUser", indexes = {
		@org.hibernate.annotations.Index(name = "idxSocialProviderUser", columnNames = {
				"FK_UserId", "providerId", "irank", "providerUserId" }),
		@org.hibernate.annotations.Index(name = "idxSocialProvider", columnNames = {
				"providerId", "providerUserId" }) })
public class SocialUser implements RemoteUser {
	private Long id, expireTime;
	private String providerUserId, providerId, secret, displayName, profileUrl,
			imageUrl, accessToken, refreshToken;
	private int rank;
	private LocalUser user;

	public SocialUser() {
	}

	public SocialUser(LocalUser user, String providerId, String providerUserId,
			int rank, String displayName, String profileUrl, String imageUrl,
			String accessToken, String secret, String refreshToken,
			Long expireTime) {
		super();
		this.expireTime = expireTime;
		this.providerUserId = providerUserId;
		this.providerId = providerId;
		this.secret = secret;
		this.displayName = displayName;
		this.profileUrl = profileUrl;
		this.imageUrl = imageUrl;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.rank = rank;
		this.user = user;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "socialUserId", nullable = true, insertable = false, updatable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FK_UserId", nullable = false, updatable = false)
	public LocalUser getUser() {
		return user;
	}

	public void setUser(LocalUser user) {
		this.user = user;
	}

	/*
	 * Provider identifier: Facebook, Twitter, LinkedIn etc
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getProviderUserId()
	 */

	@Column(length = 25, nullable = false)
	public String getProviderUserId() {
		return providerUserId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.msw.compraventa.model.LocalUser#setProviderUserId(java.lang.String)
	 */

	public void setProviderUserId(String provider) {
		this.providerUserId = provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getProviderId()
	 */

	@Column(length = 25, nullable = false)
	@Index(name = "idxSocialProviderId")
	public String getProviderId() {
		return providerId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setProviderId(java.lang.String)
	 */

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getRank()
	 */

	@Column(name = "irank")
	public int getRank() {
		return rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setRank(int)
	 */

	public void setRank(int rank) {
		this.rank = rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getSecret()
	 */

	@Column(length = 55)
	public String getSecret() {
		return secret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setSecret(java.lang.String)
	 */

	public void setSecret(String secret) {
		this.secret = secret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getDisplayName()
	 */

	@Column(length = 55)
	public String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setDisplayName(java.lang.String)
	 */

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getProfileUrl()
	 */

	@Column
	public String getProfileUrl() {
		return profileUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setProfileUrl(java.lang.String)
	 */

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getImageUrl()
	 */

	@Column
	public String getImageUrl() {
		return imageUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setImageUrl(java.lang.String)
	 */

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getAccessToken()
	 */

	@Column(length = 55)
	public String getAccessToken() {
		return accessToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setAccessToken(java.lang.String)
	 */

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getRefreshToken()
	 */

	@Column(length = 55)
	public String getRefreshToken() {
		return refreshToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setRefreshToken(java.lang.String)
	 */

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#getExpireTime()
	 */

	@Column
	public Long getExpireTime() {
		return expireTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.msw.compraventa.model.LocalUser#setExpireTime(java.lang.Long)
	 */

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	@Transient
	public String getUserId() {
		return user.getEmail();
	}

	public void setUserId(String id) {
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((providerUserId == null) ? 0 : providerUserId.hashCode());
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!getClass().isAssignableFrom(obj.getClass()))
			return false;
		SocialUser other = (SocialUser) obj;
		if (providerUserId == null) {
			if (other.providerUserId != null)
				return false;
		} else if (!providerUserId.equals(other.providerUserId))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		return true;
	}

}
