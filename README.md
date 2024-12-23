# VueSPA

This is a starter template for a stateless SpringBootApplication with a Vue 3 single page application as the frontend.

It includes basic user creation, authentication, and role-based access control using JWT and refresh tokens.

## Server Side

The server side code is a Spring Boot application using Maven. In addition, the following repositories are used:

### Maven Repositories

* **[Spring Web](https://github.com/spring-projects/spring-framework)**  
  Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.

* **[Spring Boot Dev Tools](https://spring.io/projects/spring-boot)**  
Provides fast application restarts, LiveReload, and configurations for enhanced development experience.

* **[Spring Security](https://spring.io/projects/spring-security)**  
  Highly customizable authentication and access-control framework for Spring applications.

* **[Spring Data JPA](https://spring.io/projects/spring-boot)**  
  Persist data in SQL stores with Java Persistence API using Spring Data and Hibernate.

* **[Flyway Migration](https://www.red-gate.com/products/flyway/community/)**  
  Version control for your database so you can migrate from any version (including an empty database) to the latest version of the schema.

* **[MySQL Driver](http://dev.mysql.com/doc/connector-j/en/)**  
  MySQL JDBC driver.

* **[Lombok](https://projectlombok.org/)**  
Java annotation library which helps to reduce boilerplate code.

* **[Java JWT](https://github.com/jwtk/jjwt)**  
For creating and verifying JSON Web Tokens (JWTs) and JASON Web Keys (JWKs).

* **[JetBrains Java Annotations](https://github.com/JetBrains/java-annotations)**  
A set of annotations used for code inspection support and code documentation.

## Client Side

The front end of the website is a Vue 3 application written in TypeScript. The following packages are also used:

### NPM Packages

* **[Axios](https://axios-http.com/)**  
A simple promise based HTTP client for the browser and node.js. Axios provides a simple-to-use library in a small package with a very extensible interface.

* **[Pinia](https://pinia.vuejs.org/)**  
A store library for Vue, it allows you to share a state across components/pages.

* **[Vue Router](https://router.vuejs.org/)**  
The official router for Vue.

## Running the Site

1. Start the Java server. The site should start up at http://localhost:8080/.
2. Start the front end site:
	```
	cd src/main/frontend
	```
	```
	npm run dev
	```
	It will start a new server at a site with an available port: http://localhost:5174

## Additional Options

Since this is a pretty standard Vue 3 site, the following options are also available for the front end:

* ***Project Setup***  
	```
	npm install
	```

* ***Type-Check, Compile and Minify for Production***  
	```
	npm run build
	```

* ***Lint with ESLint***  
	```
	npm run lint
	```
