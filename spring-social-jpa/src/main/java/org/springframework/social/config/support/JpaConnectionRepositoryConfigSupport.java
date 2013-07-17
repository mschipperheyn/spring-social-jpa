package org.springframework.social.config.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.social.connect.jpa.JpaUsersConnectionRepository;

public abstract class JpaConnectionRepositoryConfigSupport extends AbstractConnectionRepositoryConfigSupport{

	private final static Log logger = LogFactory.getLog(JpaConnectionRepositoryConfigSupport.class);

	public BeanDefinition registerJpaConnectionRepositoryBeans(BeanDefinitionRegistry registry, String connectionRepositoryId, String usersConnectionRepositoryId, 
			String connectionFactoryLocatorRef, String jpaTemplate, String encryptorRef, String userIdSourceRef, String connectionSignUpRef) {
		registerUsersConnectionRepositoryBeanDefinition(registry, usersConnectionRepositoryId, connectionFactoryLocatorRef, jpaTemplate, encryptorRef, connectionSignUpRef);
		return registerConnectionRepository(registry, usersConnectionRepositoryId, connectionRepositoryId, userIdSourceRef);		
	}
	
	
	private BeanDefinition registerUsersConnectionRepositoryBeanDefinition(BeanDefinitionRegistry registry, String usersConnectionRepositoryId, 
			String connectionFactoryLocatorRef, String jpaTemplate, String encryptorRef, String connectionSignUpRef) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering JpaUsersConnectionRepository bean");
		}				
		BeanDefinitionBuilder usersConnectionRepositoryBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(JpaUsersConnectionRepository.class)
				.addConstructorArgReference(jpaTemplate)
				.addConstructorArgReference(connectionFactoryLocatorRef)
				.addConstructorArgReference(encryptorRef);
		if (connectionSignUpRef != null && connectionSignUpRef.length() > 0) {
			usersConnectionRepositoryBeanBuilder.addPropertyReference("connectionSignUp", connectionSignUpRef);
		}
		BeanDefinition usersConnectionRepositoryBD = usersConnectionRepositoryBeanBuilder.getBeanDefinition();
		BeanDefinition scopedProxyBean = decorateWithScopedProxy(usersConnectionRepositoryId, usersConnectionRepositoryBD, registry);
		registry.registerBeanDefinition(usersConnectionRepositoryId, scopedProxyBean);
		return scopedProxyBean;
	}

}
