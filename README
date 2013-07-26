Spring Social JPA is a plugin for Spring Social that allows you to use JPA as a basis for your repository as opposed to straight JDBC.

## Setting up jpa
In order to set up your jpa environment you will need to implement a number of interfaces:

* RemoteUser: This interface represents your model class for storing the user connection information
* SocialUserManager: This interface represents the interaction with the jpa environment: the queries for storing and retrieving connections.

## Configuration options jpa connection repository

These days, it's getting more and more common to initialize Spring applications through @Configuration. You can also do this for Spring Social JPA

```bash
@Configuration
@EnableJpaConnectionRepository
public class SocialConfig{

}
```

The only thing different from the @EnableJdbcConnectionRepository configuration (see original documentation) is the "jpaTemplate" attribute.

```bash
@EnableJpaConnectionRepository(
	jpaTemplate="socialUserManager"
)

```

The jpaTemplate will reference the component that implements the SocialUserManager interface.

