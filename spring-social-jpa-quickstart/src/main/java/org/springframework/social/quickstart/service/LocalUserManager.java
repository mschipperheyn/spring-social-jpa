package org.springframework.social.quickstart.service;

import org.springframework.social.quickstart.model.LocalUser;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */

public interface LocalUserManager {
	
	public LocalUser getUserByEmail(String email);
	
	public LocalUser createUser(
			String nickName,
			String email,
			String password);
	
	public LocalUser save(LocalUser user);
}
