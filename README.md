# Spring Boot Admin Panel

An add-on for Spring Boot apps that automatically generates a database admin panel based on your `@Entity` annotated classes.
The panel offers basic CRUD and search functionalities to manage the database.

[![Example page listing products](https://i.imgur.com/knAKPxQ.png)](https://i.imgur.com/knAKPxQ.png)

The code is in a very early version and I'm trying to collect as much feedback as possible in order to fix the
most common issues that will inevitably arise. If you are so kind to try the project and you find something
broken, please report it as an issue and I will try to take a look at it.

## Installation

1. Clone the Github repo and `mvn install` the project, then include the dependency in your `pom.xml`:

```
<dependency>
	<groupId>tech.ailef</groupId>
	<artifactId>spring-boot-db-admin</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

2. A few configuration steps are required on your code in order to integrate the library in your project. The first one
is to create a configuration class:

```
@DbAdminConfiguration
@Configuration
public class TestConfiguration implements DbAdminAppConfiguration {

	@Override
	public String getModelsPackage() {
		return "your.models.package"; // The package where your @Entity classes are located
	}
}
```

The last step is to annotate your `@SpringBootApplication` class containing the `main` method with the following:

```
@ComponentScan(basePackages = {"your.project.root.package", "tech.ailef.dbadmin"})
@EnableJpaRepositories(basePackages = {"your.project.root.package", "tech.ailef.dbadmin"})
@EntityScan(basePackages = {"your.project.root.package", "tech.ailef.dbadmin"})
```

This tells Spring to scan the `tech.ailef.dbadmin` packages and look for components there as well. Remember to also include
your original root package as shown, or Spring will not scan it otherwise.

3. At this point, when you run your application, you should be able to visit `http://localhost:$PORT/dbadmin` and access the web interface.