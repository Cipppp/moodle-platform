
# 🎓 Moodle platform



The Course Management Platform is a backend application built with Spring Boot, designed to handle the administration and management of courses. This system is tested and managed through Postman, ensuring a robust and secure environment for course enrollments, scheduling, and grading.



## 🚀 Features

- **Secure User Authentication**: Implemented using Spring Security with JWT-based authentication, providing strong access control and protection of user data.
    
- **Course and Enrollment Management**: Efficiently handles course creation, student enrollment requests, schedule management, and grading. The platform ensures all business logic is thoroughly tested to handle edge cases like overlapping schedules, maximum enrollment limits, and validation of dates (e.g., ensuring start dates are before end dates).
    
- **Automated Testing**: Comprehensive API testing using Postman, covering over 70 API requests. We've utilized Postman's request chaining feature to automatically pass response parameters to subsequent requests, streamlining the testing process and reducing manual intervention.
    
- **High Test Coverage**: Achieved over 96% unit test coverage, ensuring the backend is reliable, secure, and ready for production.
    



## 🎨 Database Design

The platform’s database, managed using MySQL, is structured to efficiently handle various aspects of course management, including user roles, course enrollments, schedules, and grades. The schema is designed to ensure data integrity and support complex queries.

![Database Diagram](src/main/resources/moodle_diagram.png)



## 🛠️ Technologies & Tools

- **Backend**: Java, Spring Boot, Spring Security
- **Database**: MySQL
- **Testing**: JUnit, Mockito, Postman (for API testing)
- **CI/CD**: GitHub Actions
- **Infrastructure**: Docker



## 📦 Installation

1. **Clone the Repository**:
```bash
git clone https://github.com/YourUsername/CourseManagementPlatform.git cd CourseManagementPlatform
``` 

2. **Set up the Backend**:
    
```bash
cd backend ./mvnw clean install ./mvnw spring-boot:run
```
3. **Configure the Database**:
    
    - Ensure MySQL is installed and running.
    - Update the `application.properties` file with your database credentials.

4. **Run the Postman Collection**:
    
    - Import the provided Postman collection and environment files.
    - Execute the collection to simulate API requests and validate the application's functionality.



## 🚀 Usage

1. **User Authentication**:
    
    - Users can securely sign up and log in using email and password.
    - Admins can manage users, courses, and grades through authenticated API requests.
2. **Course Management**:
    
    - Create and manage courses, including adding schedules and handling enrollment requests.
    - Ensure all business logic is validated, such as checking for overlapping schedules, adhering to maximum student limits, and processing enrollment requests from pending to accepted or rejected.
3. **Advanced Postman Features**:
    
    - Leveraged Postman's request chaining feature to automatically pass response parameters from one request to the next, ensuring a smooth and efficient testing workflow.
    - This approach minimizes manual intervention, ensuring consistency and accuracy across all test cases.


## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

