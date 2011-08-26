package org.springframework.social.quickstart.dao;

import org.springframework.social.connect.jpa.JpaTemplate;
import org.springframework.social.quickstart.model.LocalUser;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */
public interface SocialUserDao extends JpaTemplate {
	public LocalUser getUser(String id);
}
