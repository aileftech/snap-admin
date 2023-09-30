# Spring Boot Database Admin Panel

Generate a powerful CRUD management dashboard for your Spring Boot application in a few minutes. 

Spring Boot Database Admin scans your `@Entity` classes and automatically builds a web UI with CRUD operations
for your database schema. No modifications required to your existing code!

[![Example page listing products](https://i.imgur.com/Nz19f8e.png)](https://i.imgur.com/Nz19f8e.png)

**Features:**
 * List objects with pagination and sorting
 * Object detail page, which also includes `@OneToMany` and `@ManyToMany` related objects
 * Create/Edit objects
 * Action logs: history of all write operations executed through the web UI
 * Search
 * Customization

**Supported JPA annotations**
 * Core: @Entity, @Table, @Column, @Lob, @Id
 * Relationships: @OneToMany, @ManyToOne, @ManyToMany, @OneToOne

The behaviour you specify with these annotations should be applied automatically by Spring Boot Database Admin as well. Keep in mind that using non-supported annotations will not necessarily result in an error, as they are simply ignored. Depending on what the annotation actually does, this could be just fine or result in an error if it interferes with something that Spring Boot Database Admin relies on.

The code is still in a very early stage and it might not be robust if you use not-yet-supported JPA annotations and/or other custom configurations (e.g., custom naming strategy). If you find a bug with your settings, please report it as an issue and I will try to take a look at it.

## Installation

1. The code is not yet distributed on Maven, so for now you need to install manually. Clone the Github repo and execute `mvn install -DskipTests`  in the project's directory. Then, include the dependency in your `pom.xml`:

```
<dependency>
	<groupId>tech.ailef</groupId>
	<artifactId>spring-boot-db-admin</artifactId>
	<version>0.0.4</version> <!-- Make sure to put the correct version here -->
</dependency>
```

2. A few simple configuration steps are required on your end in order to integrate the library into your project. 
If you don't want to test on your own code, you can clone the [test project](https://github.com/aileftech/spring-boot-database-admin-test) which provides
a sample database and already configured code.

Otherwise, go ahead and add these to your `application.properties` file:

```
# Optional, default true
dbadmin.enabled=true

# The first-level part of the URL path: http://localhost:8080/${baseUrl}/
dbadmin.baseUrl=admin

# The package that contains your @Entity classes
dbadmin.modelsPackage=put.your.models.package.here
```

The last step is to annotate your `@SpringBootApplication` class containing the `main` method with the following:

```
@ImportAutoConfiguration(DbAdminAutoConfiguration.class)
```

This will autoconfigure Spring Boot Database Admin when your application starts. You are good to go!

3. At this point, when you run your application, you should be able to visit `http://localhost:${port}/${dbadmin.baseUrl}` and see the web interface.

## Documentation

Available at: https://aileftech.github.io/spring-boot-database-admin/.

## Issues

If you find a problem or a bug, please report it as issue. When doing so, include as much information as possible, and in particular:

 * provide the code for the involved `@Entity` classes, if possible
 * provide the full stack trace of the error
 * specify if you are using any particular configuration either in your `application.properties` or through annotations

## Changelog

**0.1.2**
- Better handling of large text fields (shown as `textarea`)
- Added `CATEGORICAL` option to `Filterable`
- Several bug fixes

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
