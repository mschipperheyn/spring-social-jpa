package org.springframework.social.quickstart.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */

@Entity
@Table(name = "User")
public class LocalUser {
	private Long id;
	private String email,nickName,password;
	private Set<SocialUser> socialUsers;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="userId", nullable=true, insertable=false, updatable=false)
	public Long getId() {
		return id;
	}

	public void setId(Long userId) {
		this.id = userId;
	}
	
	@Column(length=55)	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(length=55)
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Column(length=55)	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@OneToMany(cascade={CascadeType.ALL},mappedBy="user",fetch=FetchType.LAZY)
	public Set<SocialUser> getSocialUsers() {
		if(socialUsers == null)
			socialUsers = new HashSet<SocialUser>();
		return socialUsers;
	}


	public void setSocialUsers(Set<SocialUser> socialUsers) {
		this.socialUsers = socialUsers;
	}
}
