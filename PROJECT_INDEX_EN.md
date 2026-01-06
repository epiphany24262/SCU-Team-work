# Project Index

## Project Overview

This is a Gym Management System built with Spring Boot for managing daily gym operations, including member management, coach management, course scheduling, equipment reservation, and more.

**Tech Stack:**
- Spring Boot 2.7.12
- Java 1.8
- MySQL 8.0.33
- Thymeleaf (Template Engine)
- Spring JDBC (Data Access)

**Project Structure:**
```
SCU-Team-work/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── controller/      # REST API Controllers
│   │   │   ├── service/         # Business Logic Layer
│   │   │   ├── dao/             # Data Access Layer
│   │   │   ├── entity/          # Entity Classes (Data Models)
│   │   │   └── DemoApplication.java  # Application Entry Point
│   │   └── resources/
│   │       ├── application.properties  # Configuration File
│   │       └── static/          # Static Resources (HTML, CSS, JS)
│   └── test/                    # Test Code
├── pom.xml                      # Maven Configuration
└── README.md                    # Project Description
```

---

## Core Modules

### 1. User Management
**Entity:** `User.java`
**Purpose:** Manage gym members and administrator accounts

**Attributes:**
- id: User ID
- username: Username
- password: Password
- realName: Real Name
- phone: Phone Number
- birthDate: Birth Date
- cardTime: Card Registration Time
- expireTime: Expiration Time
- role: Role (admin/user)
- status: Status
- gender: Gender
- age: Age

**API Endpoints (UserController):**
- `POST /api/users/list` - Get all users list (with search support)
- `POST /api/users/detail` - Get user details by ID
- `POST /api/users/add` - Add new user
- `POST /api/users/update` - Update user information
- `POST /api/users/delete` - Delete user
- `POST /api/users/stats` - Get user statistics
- `POST /api/users/check-username` - Check if username exists

### 2. Authentication
**Controller:** `AuthController.java`

**API Endpoints:**
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### 3. Coach Management
**Entity:** `Coach.java`
**Purpose:** Manage gym coach information

**API Endpoints (CoachController):**
- `POST /api/coaches/page` - Get paginated coach list
- `GET /api/coaches/all` - Get all coaches
- `POST /api/coaches/detail` - Get coach details
- `POST /api/coaches/add` - Add new coach
- `POST /api/coaches/update` - Update coach information
- `POST /api/coaches/delete` - Delete coach

### 4. Course Management
**Entity:** `Course.java`
**Purpose:** Manage fitness courses

**API Endpoints (CourseController):**
- `POST /api/courses/page` - Get paginated course list
- `POST /api/courses/add` - Add new course
- `POST /api/courses/update` - Update course
- `POST /api/courses/delete` - Delete course
- `GET /api/courses/{id}` - Get course details

### 5. Enrollment Management
**Entity:** `Enrollment.java`
**Purpose:** Manage member course enrollments

**API Endpoints (EnrollmentController):**
- `GET /api/enrollments` - Get all enrollment records
- `GET /api/enrollments/{id}` - Get enrollment details
- `POST /api/enrollments` - Create enrollment
- `POST /api/enrollments/{id}/approve` - Approve enrollment
- `POST /api/enrollments/batch-approve` - Batch approve
- `POST /api/enrollments/{id}/cancel-approval` - Cancel approval
- `DELETE /api/enrollments/{id}` - Delete enrollment
- `GET /api/enrollments/my` - Get my enrollments
- `GET /api/enrollments/my-schedule` - Get my course schedule
- `GET /api/enrollments/available-courses` - Get available courses

### 6. Equipment Management
**Entity:** `Equipment.java`
**Purpose:** Manage gym equipment

**API Endpoints (EquipmentController):**
- `GET /api/equipment` - Get all equipment
- `GET /api/equipment/{id}` - Get equipment details
- `POST /api/equipment` - Add new equipment
- `PUT /api/equipment/{id}` - Update equipment
- `DELETE /api/equipment/{id}` - Delete equipment
- `POST /api/equipment/{id}/status` - Update equipment status
- `GET /api/equipment/available` - Get available equipment
- `GET /api/equipment/type/{type}` - Get equipment by type
- `POST /api/equipment/{id}/maintain` - Set maintenance status

### 7. Equipment Reservation
**Entity:** `EquipmentReservation.java`
**Purpose:** Manage equipment reservations

**API Endpoints (EquipmentReservationController):**
- `GET /api/equipment-reservations` - Get all reservations
- `GET /api/equipment-reservations/{id}` - Get reservation details
- `POST /api/equipment-reservations` - Create reservation
- `POST /api/equipment-reservations/{id}/approve` - Approve reservation
- `POST /api/equipment-reservations/{id}/reject` - Reject reservation
- `POST /api/equipment-reservations/batch-approve` - Batch approve
- `PUT /api/equipment-reservations/{id}/cancel` - Cancel reservation
- `DELETE /api/equipment-reservations/{id}` - Delete reservation
- `GET /api/equipment-reservations/my` - Get my reservations
- `GET /api/equipment-reservations/date/{date}` - Get reservations by date
- `GET /api/equipment-reservations/stats/{equipmentId}` - Get equipment statistics
- `GET /api/equipment-reservations/check-conflict` - Check reservation conflicts

### 8. Room Management
**Entity:** `Room.java`
**Purpose:** Manage gym rooms/venues

**API Endpoints (RoomController):**
- `GET /api/rooms` - Get all rooms
- `GET /api/rooms/{id}` - Get room details
- `POST /api/rooms` - Add new room
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room

### 9. Schedule Management
**Entity:** `Schedule.java`
**Purpose:** Manage course schedules

**API Endpoints (ScheduleController):**
- `GET /api/schedules/weekly` - Get weekly schedule
- `GET /api/schedules/{id}` - Get schedule details
- `POST /api/schedules` - Add new schedule
- `PUT /api/schedules/{id}` - Update schedule
- `DELETE /api/schedules/{id}` - Delete schedule
- `GET /api/schedules/courses` - Get course list
- `GET /api/schedules/coaches` - Get coach list
- `GET /api/schedules/rooms` - Get room list

### 10. Card & VIP Management
**Controller:** `CardAndVipController.java`
**Purpose:** Manage membership cards and VIP services

**API Endpoints:**
- `GET /api/cardAndVip/gymCard/{userId}` - Get gym card information
- `GET /api/cardAndVip/vipInfo/{userId}` - Get VIP information
- `POST /api/cardAndVip/renewGymCard` - Renew gym card
- `POST /api/cardAndVip/upgradeToVip` - Upgrade to VIP
- `POST /api/cardAndVip/renewAndUpgrade` - Renew and upgrade
- `GET /api/cardAndVip/cardStatus/{userId}` - Get card status
- `GET /api/cardAndVip/vipStatus/{userId}` - Get VIP status

### 11. Menu/Permission Management
**Entity:** `Menu.java`, `RolePermission.java`
**Purpose:** Manage system menus and role permissions

**API Endpoints (MenuController):**
- `POST /api/menu/getUserMenus` - Get user menus
- `POST /api/menu/all-menus` - Get all menus

---

## Data Access Layer (DAO)

All DAO classes are located in `src/main/java/com/example/demo/dao/` directory, using Spring JDBC for database operations.

**DAO List:**
1. `UserDao.java` - User data access
2. `CoachDao.java` - Coach data access
3. `CourseDao.java` - Course data access
4. `EnrollmentDao.java` - Enrollment data access
5. `EquipmentDao.java` - Equipment data access
6. `EquipmentReservationDao.java` - Equipment reservation data access
7. `RoomDao.java` - Room data access
8. `ScheduleDao.java` - Schedule data access
9. `MenuDao.java` - Menu data access

---

## Service Layer

All Service classes are located in `src/main/java/com/example/demo/service/` directory, implementing business logic.

**Service List:**
1. `UserService.java` - User business logic
2. `CoachService.java` - Coach business logic
3. `CourseService.java` - Course business logic
4. `EnrollmentService.java` - Enrollment business logic
5. `EquipmentService.java` - Equipment business logic
6. `EquipmentReservationService.java` - Equipment reservation business logic
7. `RoomService.java` - Room business logic
8. `ScheduleService.java` - Schedule business logic
9. `MenuService.java` - Menu business logic
10. `CardAndVipService.java` - Card and VIP business logic

---

## Frontend Pages

All HTML pages are located in `src/main/resources/static/` directory.

**Page List:**
1. `index.html` - Home page
2. `login.html` - Login page
3. `home.html` - Admin home
4. `user_home.html` - User home
5. `member.html` - Member management
6. `coaches.html` - Coach management
7. `equipment.html` - Equipment management
8. `equipment_reservation.html` - Equipment reservation management
9. `reserve_equipment.html` - Reserve equipment
10. `my_equipment_reservations.html` - My equipment reservations
11. `reserve_course.html` - Reserve course
12. `schedules_table.html` - Course schedule table
13. `schedules_course.html` - Course scheduling
14. `schedules_room.html` - Room scheduling
15. `schedules_enrollment.html` - Enrollment management
16. `mycard.html` - My membership card
17. `myvip.html` - My VIP
18. `profile.html` - Profile

---

## Database Configuration

**Configuration File:** `src/main/resources/application.properties`

```properties
spring.application.name=demo
spring.datasource.url=jdbc:mysql://www.scushujin.xyz:3306/gym_system
spring.datasource.username=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Database:** MySQL 8.0.33
**Database Name:** gym_system

---

## Build & Run

### Build Project
```bash
./mvnw clean install
```

### Run Project
```bash
./mvnw spring-boot:run
```

### Access Application
```
http://localhost:8080
```

---

## Architecture Pattern

This project uses a classic three-tier architecture:

1. **Presentation Layer** - Controller
   - Handle HTTP requests
   - Return JSON responses
   - Support CORS

2. **Business Logic Layer** - Service
   - Implement business rules
   - Data validation
   - Transaction management

3. **Data Access Layer** - DAO
   - Database CRUD operations
   - SQL query execution
   - Using Spring JdbcTemplate

---

## Key Features

1. **User Role Management** - Support admin and regular user roles
2. **Membership Card System** - Card management, renewal, expiration reminders
3. **VIP Services** - VIP member upgrade and privilege management
4. **Course Enrollment** - Online course enrollment and approval process
5. **Equipment Reservation** - Gym equipment reservation and conflict detection
6. **Schedule Management** - Course schedule management
7. **Coach Management** - Coach information and qualification management
8. **Data Statistics** - Various data statistics and reports

---

## API Response Format

All APIs return a unified JSON format:

```json
{
  "success": true/false,
  "message": "Operation message",
  "data": {} // Returned data
}
```

---

## Development Guidelines

1. **Code Standards:** Follow Java coding conventions
2. **Comments:** All public methods should have Chinese comments
3. **Exception Handling:** Use try-catch to handle exceptions and return friendly error messages
4. **Parameter Validation:** Perform business parameter validation in Service layer
5. **Transaction Management:** Use @Transactional for methods involving multiple table operations

---

## File Statistics

- **Java Source Files:** 42
- **HTML Pages:** 21
- **Controllers:** 12
- **Services:** 10
- **DAOs:** 9
- **Entities:** 10

---

## Quick Reference

### User Related Code
- Entity: `src/main/java/com/example/demo/entity/User.java`
- DAO: `src/main/java/com/example/demo/dao/UserDao.java`
- Service: `src/main/java/com/example/demo/service/UserService.java`
- Controller: `src/main/java/com/example/demo/controller/UserController.java`

### Course Related Code
- Entity: `src/main/java/com/example/demo/entity/Course.java`
- DAO: `src/main/java/com/example/demo/dao/CourseDao.java`
- Service: `src/main/java/com/example/demo/service/CourseService.java`
- Controller: `src/main/java/com/example/demo/controller/CourseController.java`

### Equipment Related Code
- Entity: `src/main/java/com/example/demo/entity/Equipment.java`
- DAO: `src/main/java/com/example/demo/dao/EquipmentDao.java`
- Service: `src/main/java/com/example/demo/service/EquipmentService.java`
- Controller: `src/main/java/com/example/demo/controller/EquipmentController.java`

---

## Maintenance Notes

This index file will be updated as the project evolves. If you add new features or modify existing ones, please update this document accordingly.

**Last Updated:** 2026-01-06
**Version:** 1.0.0
