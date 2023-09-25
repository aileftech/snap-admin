# Spring Boot Database Admin Panel

Generate a powerful CRUD management dashboard for your Spring Boot application in a few minutes.

Spring Boot Database Admin scans your `@Entity` classes to generate a simple but powerful database management interface.

[![Example page listing products](https://i.imgur.com/Nz19f8e.png)](https://i.imgur.com/Nz19f8e.png)

Features:
 * List objects with pagination and sorting
 * Show detailed object page which also includes `@OneToMany`, `@ManyToMany`, etc... fields
 * Create/Edit objects
 * Action logs (i.e. see a history of all write operations done through the web UI)
 * Search
 * Customization

The code is in a very early version and I'm trying to collect as much feedback as possible in order to fix the
most common issues that will inevitably arise. If you are so kind to try the project and you find something
broken, please report it as an issue and I will try to take a look at it.

## Installation

1. The code is not yet distributed on Maven, so for now you need to install manually. Clone the Github repo and `mvn install` the project, then include the dependency in your `pom.xml`:

```
<dependency>
	<groupId>tech.ailef</groupId>
	<artifactId>spring-boot-db-admin</artifactId>
	<version>0.0.4</version>
</dependency>
```

2. A few simple configuration steps are required on your end in order to integrate the library into your project. If you don't want
to test on your own code, you can clone the [test project](https://github.com/aileftech/spring-boot-database-admin-test) which provides
a sample database and already configured code.

If you wish to integrate it into your project instead, the first step is adding these to your `application.properties` file:

```
# Optional, default true
dbadmin.enabled=true

# The first-level part of the URL path: http://localhost:8080/${baseUrl}/
dbadmin.baseUrl=admin

# The package that contains your @Entity classes
dbadmin.modelsPackage=tech.ailef.dbadmin.test.models
```

The last step is to annotate your `@SpringBootApplication` class containing the `main` method with the following:

```
@ImportAutoConfiguration(DbAdminAutoConfiguration.class)
```

This will autoconfigure the various DbAdmin components when your application starts.

3. At this point, when you run your application, you should be able to visit `http://localhost:$PORT/${baseUrl}` and access the web interface.

## Documentation

Once you are correctly running Spring Boot Database Admin, you will be able to access the web interface. Most of the features are already available with the basic configuration. However, some customization to the interface might be applied by using appropriate annotations on your classes fields or methods. 
The following annotations are supported.

### @DisplayName
```
@DisplayName
public String getName() {
	return name;
}
```

When displaying a reference to an item, by default we show its primary key. If a class has a `@DisplayName`, this method will be used in addition to the primary key whenever possible, giving the user a more readable option. 

### @DisplayFormat
```
@DisplayFormat(format = "$%.2f")
private Double price;
```

Specify a format string to apply when displaying the field.

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

This annotation can be used to add values computed at runtime that are shown like additional columns.

### @Filterable

```
@Filterable
private LocalDate createdAt;
```

Place on one or more fields in a class to activate the faceted search feature. This will allow you to easily combine all these filters when operating on the table. Can only be placed on fields that correspond to physical columns on the table (e.g. no `@ManyToMany`/`@OneToMany`) and that are not binary (`byte[]`).

### @DisplayImage

```
@DisplayImage
private byte[] image;
```

This annotation can be placed on binary fields to declare they are storing an image and that we want it displayed when possible. The image will be shown as a small thumbnail.


## Changelog

**0.1.0**
- Implemented action logs
- Implemented user settings

**0.0.4**
- Simplified setup/configuration: now it only requires a couple of config properties and 1 annotation
- Support of custom base url for the web UI, instead of hardcoded "/dbadmin"
- Continued implementation of automated testing with Selenium

**0.0.3**
- @DisplayImage; Selenium tests; Fixed/greatly improved edit page;

**0.0.2**
- Faceted search with `@Filterable` annotation

**0.0.1**
- First alpha release (basic CRUD features)
