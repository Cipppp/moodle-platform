# GCP Setup with Cloud SQL Proxy

### Configuring `application.properties`

Copy and paste the following code into your `application.properties` file:

```
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/Moodle   spring.datasource.username=username   
spring.datasource.password=password 

spring.jpa.hibernate.ddl-auto=create   
logging.level.org.hibernate.SQL=DEBUG   
logging.level.org.hibernate.type=TRACE      
```
