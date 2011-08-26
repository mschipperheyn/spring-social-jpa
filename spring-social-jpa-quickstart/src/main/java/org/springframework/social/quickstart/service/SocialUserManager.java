package org.springframework.social.quickstart.service;

import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.jpa.JpaTemplate;

/*
 * @author Marc Schipperheyn marc@orangebits.nl
 */

public interface SocialUserManager extends ConnectionSignUp,JpaTemplate{
}