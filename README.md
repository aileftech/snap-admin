[![javadoc](https://javadoc.io/badge2/tech.ailef/spring-boot-db-admin/javadoc.svg)](https://javadoc.io/doc/tech.ailef/spring-boot-db-admin) 

<!--
> **LIVE DEMO** Check if it's up at http://dbadmin.ailef.tech/admin. Feel free to edit the content, it's an in-memory database that resets every hour with the same sample data.
-->

# Spring Boot Database Admin Panel

Generate a powerful CRUD management dashboard for your Spring Boot application in a few minutes. 

Spring Boot Database Admin scans your `@Entity` classes and automatically builds a web UI with CRUD operations
for your database schema. No modifications required to your existing code (well, you will need to add **1 line** to it...)!

[![Example page listing products](https://i.imgur.com/Nz19f8e.png)](https://i.imgur.com/Nz19f8e.png)

**Features:**

 * List objects with pagination and sorting
 * Object detail page, which also includes `@OneToMany` and `@ManyToMany` related objects
 * Create/Edit objects
 * Action logs: history of all write operations executed through the web UI
 * Advanced search and filtering
 * Annotation-based customization

**Supported JPA annotations**

 * Core: @Entity, @Table, @Column, @Lob, @Id, @GeneratedValue
 * Relationships: @OneToMany, @ManyToOne, @ManyToMany, @OneToOne
 * Validation: all JPA validation annotations (`jakarta.validation.constraints.*`)

The behaviour you specify with these annotations should be applied automatically by Spring Boot Database Admin as well. Keep in mind that using non-supported annotations will not necessarily result in an error, as they are simply ignored. Depending on what the annotation actually does, this could be just fine or result in an error if it interferes with something that Spring Boot Database Admin relies on.

**Supported field types**

These are the supported types for fields inside your `@Entity` classes (excluding fields for relationships to other entities). Fields with unsupported types are ignored, but functionality may be limited; refer to the [documentation](https://aileftech.github.io/spring-boot-database-admin/index.html#supported-field-types) for more information.

 * Double, Float, Integer, Short, Byte, Character, BigDecimal, BigInteger
 * Boolean
 * String, UUID
 * Date, LocalDate, LocalDateTime, OffsetDateTime
 * byte[]

The code is still in a very early stage and it might not be robust if you use not-yet-supported JPA annotations and/or other custom configurations (e.g., custom naming strategy). If you find a bug with your settings, please report it as an issue and I will take a look at it.

## Installation

1. Spring Boot Database Admin is distributed on Maven. For the latest stable release you can simply include the following snippet in your `pom.xml` file:

```
<dependency>
	<groupId>tech.ailef</groupId>
	<artifactId>spring-boot-db-admin</artifactId>
	<version>0.1.6</version>
</dependency>
```

2. A few simple configuration steps are required on your end in order to integrate the library into your project. 
If you don't want to test on your own code, you can clone the [test project](https://github.com/aileftech/spring-boot-database-admin-test) which provides
a sample database and already configured code.

Otherwise, go ahead and add these to your `application.properties` file:

```
## The first-level part of the URL path: http://localhost:8080/${baseUrl}/
dbadmin.baseUrl=admin

## The package(s) that contain your @Entity classes
## accepts multiple comma separated values
dbadmin.modelsPackage=your.models.package,your.second.models.package

## At the moment, it's required to have open-in-view set to true.
# spring.jpa.open-in-view=true

## OPTIONAL PARAMETERS
## Whether to enable Spring Boot Database Admin
# dbadmin.enabled=true
#
## Set to true if you need to run the tests, as it will customize
## the database configuration for the internal DataSource
# dbadmin.testMode=false
```

Now annotate your `@SpringBootApplication` class containing the `main` method with the following:

```
@ImportAutoConfiguration(DbAdminAutoConfiguration.class)
```

This will autoconfigure Spring Boot Database Admin when your application starts. You are good to go!

3. At this point, when you run your application, you should be able to visit `http://localhost:${port}/${dbadmin.baseUrl}` and see the web interface.

## Documentation

* [Latest Javadoc](https://javadoc.io/doc/tech.ailef/spring-boot-db-admin)
* [Reference Guide](https://aileftech.github.io/spring-boot-database-admin/)

## Issues

If you find a problem or a bug, please report it as an issue. When doing so, include as much information as possible, and in particular:

 * provide the code for the involved `@Entity` classes, if possible/relevant
 * provide the full stack trace of the error
 * specify if you are using any particular configuration either in your `application.properties` or through annotations
 * if the problem occurs at startup, enable `DEBUG`-level logs and report what `grep DbAdmin` returns
