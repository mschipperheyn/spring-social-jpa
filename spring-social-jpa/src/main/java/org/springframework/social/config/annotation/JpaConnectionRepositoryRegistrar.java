package org.springframework.social.config.annotation;

import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.social.config.support.JpaConnectionRepositoryConfigSupport;

public class JpaConnectionRepositoryRegistrar extends JpaConnectionRepositoryConfigSupport implements ImportBeanDefinitionRegistrar {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.annotation.ImportBeanDefinitionRegistrar#
	 * registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata,
	 * org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		
		Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableJpaConnectionRepository.class.getName());
		if (annotationAttributes == null) {
			return;
		}
		AnnotationAttributes attributes = new AnnotationAttributes(annotationAttributes);
		String connectionRepositoryId = attributes.getString("connectionRepositoryId");
		String usersConnectionRepositoryId = attributes.getString("usersConnectionRepositoryId");
		String connectionFactoryLocatorRef = attributes.getString("connectionFactoryLocatorRef");
		String jpaTemplate = attributes.getString("jpaTemplate");
		String encryptorRef = attributes.getString("encryptorRef");
		String userIdSourceRef = attributes.getString("userIdSourceRef");
		String connectionSignUpRef = attributes.getString("connectionSignUpRef");
		
		registerJpaConnectionRepositoryBeans(registry, connectionRepositoryId,
				usersConnectionRepositoryId, connectionFactoryLocatorRef,
				jpaTemplate, encryptorRef, userIdSourceRef,
				connectionSignUpRef);

	}

}
