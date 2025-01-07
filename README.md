# **Spring Boot Application with H2 Database and Remember-Me Authentication**
## **Overview**
This project is a Spring Boot application that features user authentication with **Spring Security**, **H2 Database**, and a persistent "Remember Me" functionality. It also demonstrates proper management of persistent login tokens and troubleshooting tips for database initialization.
## **Features**
- **H2 Database** for testing and development.
- User authentication using **Spring Security** with "Remember Me" functionality.
- Automatic storage of persistent login tokens in the `PERSISTENT_LOGINS` table.
- Automatic database schema creation (with optional manual schema setup).
- Data initialization through `data.sql` and programmatic approaches.
- Logging configurations for debugging SQL commands.

## **Getting Started**
### **Prerequisites**
- Java 22+
- Maven or Gradle build tool
- An IDE such as IntelliJ IDEA

### **Setup Instructions**
1. **Clone the Repository**:
``` bash
   git clone <repository-url>
   cd <project-name>
```
1. **Build the Project**: Ensure all dependencies are downloaded by running:
``` bash
   ./mvnw clean install
```
_(or the Gradle equivalent `./gradlew build`.)_
1. **Run the Application**: Use the following command:
``` bash
   ./mvnw spring-boot:run
```
1. **Access the Application**: Open your browser and go to [http://localhost:8080]().

## **Configuration**
### **Database Configuration**
This project uses an embedded H2 database for local development.
- **H2 Database URL**: `jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE`
- Default username: `sa`
- Default password: (no password)

You can edit these properties in `src/main/resources/application.properties`:
``` properties
spring.datasource.url=jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```
### **Initializing the Database**
The application uses `data.sql` for test data. Check that the file `src/main/resources/data.sql` has correct data for your use case. Example:
``` sql
INSERT INTO application_user (username, hashed_password, name, profile_picture, version)
VALUES ('testuser', 'hashedPassword', 'Test User', NULL, 1);
```
If `data.sql` is not running, ensure the `spring.sql.init.mode=always` property is set and logging for SQL initialization is enabled.
## **Authentication and Remember Me**
This project includes user authentication via Spring Security, with "Remember Me" functionality enabled. Persistent logins are stored in the `PERSISTENT_LOGINS` table, which has the following schema:
``` sql
CREATE TABLE PERSISTENT_LOGINS (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);
```
### **Configuration for Persistent Logins**
The `SecurityConfiguration` class manages the security settings:
``` java
http
    .authorizeRequests()
        .anyRequest().authenticated()
    .and()
    .formLogin()
        .loginPage("/login")
        .permitAll()
    .and()
    .rememberMe()
        .tokenValiditySeconds(1209600) // Tokens valid for 2 weeks
        .tokenRepository(persistentTokenRepository());
```
- The project uses **JdbcTokenRepositoryImpl** with the configured H2 database for storing persistent login tokens.
- You can clean or audit the tokens from the `PERSISTENT_LOGINS` table if necessary.

## **Troubleshooting**
### **1. `PERSISTENT_LOGINS` or Other Tables Missing**
Ensure that the database schema is being created automatically. Check the `spring.jpa.hibernate.ddl-auto` property in `application.properties`. Recommended value for development:
``` properties
spring.jpa.hibernate.ddl-auto=update
```
### **2. `data.sql` Not Running**
Verify that Spring Boot is configured to always run SQL files:
``` properties
spring.sql.init.mode=always
```
Enable additional logging for SQL initialization:
``` properties
logging.level.org.springframework.jdbc.datasource.init.ScriptUtils=DEBUG
logging.level.org.springframework.boot.autoconfigure.jdbc.DataSourceInitializer=DEBUG
```
Logs should contain entries like:
``` 
Executing SQL script from class path resource [data.sql]
```
### **3. H2 Console**
Access the H2 database console at:
[Hyperlink removed for security reasons]()
- **JDBC URL**: `jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE`
- Username: `sa`
- Password: _(leave blank)_

Note: Ensure the application is running for live database access.
### **4. Cleaning Old Tokens**
Older tokens in `PERSISTENT_LOGINS` may not be cleaned automatically. Use the following SQL to clean tokens older than 2 weeks:
``` sql
DELETE FROM PERSISTENT_LOGINS WHERE last_used < NOW() - INTERVAL 14 DAY;
```
Alternatively, implement a scheduled cleanup task in code:
``` java
@Scheduled(cron = "0 0 3 * * ?") // At 3 AM daily
public void cleanupTokens() {
    jdbcTemplate.update("DELETE FROM PERSISTENT_LOGINS WHERE last_used < ?", LocalDateTime.now().minusWeeks(2));
}
```
## **Contributing**
1. Fork this repository.
2. Create a feature branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m "Add a new feature"`).
4. Push the branch and create a pull request.

## **License**
This project is licensed under the [MIT License]().
## **Acknowledgements**
- **Spring Boot:** Powerful framework for building Java applications.
- **H2 Database:** Lightweight, in-memory database for development.
- **Spring Security:** Comprehensive and customizable security framework.
- **Vaadin:** Building modern web interfaces (if used in your project).
