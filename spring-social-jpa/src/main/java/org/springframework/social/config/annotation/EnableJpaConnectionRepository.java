package org.springframework.social.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value=org.springframework.social.config.annotation.JpaConnectionRepositoryRegistrar.class)
public @interface EnableJpaConnectionRepository {
	
	/**
	 * The ID to assign to the ConnectionRepository bean.
	 * Defaults to "connectionRepository". 
	 */
	String connectionRepositoryId() default "connectionRepository";
	
	/**
	 * The ID to assign to the UsersConnectionRepository bean.
	 * Defaults to "usersConnectionRepository". 
	 */
	String usersConnectionRepositoryId() default "usersConnectionRepository";
	
	/**
	 * The ID of the ConnectionFactoryLocator bean to fetch a ConnectionFactory from when creating/persisting connections.
	 * Defaults to "connectionFactoryLocator". 
	 */
	String connectionFactoryLocatorRef() default "connectionFactoryLocator";
	
	/**
	 * The ID of a jpaTemplate that allows interaction with the jpa environment
	 * Defaults to "jpaTemplate"
	 */
	
	String jpaTemplate() default "jpaTemplate";
	
	/**
	 * The ID of a TextEncryptor used when persisting connection details.
	 * Defaults to "textEncryptor". 
	 */
	String encryptorRef() default "textEncryptor";
	
	/**
	 * The ID of a UserIdSource bean used to determine the unique identifier of the current user.
	 * Defaults to "userIdSource". 
	 */
	String userIdSourceRef() default "userIdSource";
	
	
	/**
	 * Reference to {@link ConnectionSignUp} bean to execute to create a new local user profile in the event no user id could be mapped to a connection.
	 * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
	 * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
	 */
	String connectionSignUpRef() default "";
}
