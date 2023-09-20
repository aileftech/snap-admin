# Spring Boot Database Admin Panel

An add-on for Spring Boot apps that automatically generates a database admin panel based on your `@Entity` annotated classes.
The panel offers basic CRUD and search functionalities to manage the database.

[![Example page listing products](https://i.imgur.com/knAKPxQ.png)](https://i.imgur.com/knAKPxQ.png)

The code is in a very early version and I'm trying to collect as much feedback as possible in order to fix the
most common issues that will inevitably arise. If you are so kind to try the project and you find something
broken, please report it as an issue and I will try to take a look at it.

## Installation

1. The code is not yet distributed on Maven, so for now you need to install manually. Clone the Github repo and `mvn install` the project, then include the dependency in your `pom.xml`:

```
<dependency>
	<groupId>tech.ailef</groupId>
	<artifactId>spring-boot-db-admin</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

2. A few configuration steps are required on your code in order to integrate the library in your project. If you don't want
to test on your own code, you can clone the [test project](https://github.com/aileftech/spring-boot-database-admin-test) which provides
a sample database and already configured code.

If you wish to integrate it into your project instead, the first step is create creating a configuration class:

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

## Documentation

Once you are correctly running Spring Boot Database Admin you will see the web interface at `http://localhost:$PORT/dbadmin`. Most of the features are already available with the basic configuration. However, some customization to the interface might be applied by using appropriate annotations on your classes fields or methods. 
The following annotations are supported.

### @DisplayName
```
@DisplayName
public String getName() {
	return name;
}
```

To show an item in a table its primary key is used by default. If you set a method as `@DisplayName` in your `@Entity` class, this result will be shown in addition to its primary key wherever possible.

### @DisplayFormat
```
@DisplayFormat(format = "$%.2f")
private Double price;
```

Specify a format to apply when displaying the field.

### @ComputedColumn
```
@ComputedColumn
public double totalSpent() {
	double total = 0;
	for (Order o : orders) {
		total += o.total();
	}
	return total;
}
```

Add an extra field that's computed at runtime instead of a database column. It will be displayed everywhere as a normal, read-only column.

### @Filterable

```
@Filterable
private LocalDate createdAt;
```

Place on one or more fields in a class to activate the faceted search feature. This will allow you to easily combine all these filters when operating on this table.


## Changelog

0.0.2 - Faceted search with `@Filterable` annotation

0.0.1 - First alpha release (basic CRUD features)
