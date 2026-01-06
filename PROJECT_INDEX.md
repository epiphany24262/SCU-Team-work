# 项目索引 (Project Index)

## 项目概述 (Project Overview)

这是一个基于 Spring Boot 的健身房管理系统 (Gym Management System)，用于管理健身房的日常运营，包括会员管理、教练管理、课程安排、器材预约等功能。

**技术栈 (Tech Stack):**
- Spring Boot 2.7.12
- Java 1.8
- MySQL 8.0.33
- Thymeleaf (模板引擎)
- Spring JDBC (数据访问)

**项目结构 (Project Structure):**
```
SCU-Team-work/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── controller/      # REST API 控制器
│   │   │   ├── service/         # 业务逻辑层
│   │   │   ├── dao/             # 数据访问层
│   │   │   ├── entity/          # 实体类 (数据模型)
│   │   │   └── DemoApplication.java  # 应用入口
│   │   └── resources/
│   │       ├── application.properties  # 配置文件
│   │       └── static/          # 静态资源 (HTML, CSS, JS)
│   └── test/                    # 测试代码
├── pom.xml                      # Maven 配置文件
└── README.md                    # 项目说明
```

---

## 核心模块 (Core Modules)

### 1. 用户管理 (User Management)
**实体:** `User.java`
**功能:** 管理健身房会员和管理员账户

**属性:**
- id: 用户ID
- username: 用户名
- password: 密码
- realName: 真实姓名
- phone: 电话
- birthDate: 出生日期
- cardTime: 办卡时间
- expireTime: 到期时间
- role: 角色 (admin/user)
- status: 状态
- gender: 性别
- age: 年龄

**API端点 (UserController):**
- `POST /api/users/list` - 获取所有用户列表 (支持搜索)
- `POST /api/users/detail` - 根据ID获取用户详情
- `POST /api/users/add` - 新增用户
- `POST /api/users/update` - 更新用户信息
- `POST /api/users/delete` - 删除用户
- `POST /api/users/stats` - 获取用户统计信息
- `POST /api/users/check-username` - 检查用户名是否存在

### 2. 认证管理 (Authentication)
**控制器:** `AuthController.java`

**API端点:**
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 3. 教练管理 (Coach Management)
**实体:** `Coach.java`
**功能:** 管理健身房教练信息

**API端点 (CoachController):**
- `POST /api/coaches/page` - 分页获取教练列表
- `GET /api/coaches/all` - 获取所有教练
- `POST /api/coaches/detail` - 获取教练详情
- `POST /api/coaches/add` - 新增教练
- `POST /api/coaches/update` - 更新教练信息
- `POST /api/coaches/delete` - 删除教练

### 4. 课程管理 (Course Management)
**实体:** `Course.java`
**功能:** 管理健身课程

**API端点 (CourseController):**
- `POST /api/courses/page` - 分页获取课程列表
- `POST /api/courses/add` - 新增课程
- `POST /api/courses/update` - 更新课程
- `POST /api/courses/delete` - 删除课程
- `GET /api/courses/{id}` - 获取课程详情

### 5. 课程报名 (Enrollment Management)
**实体:** `Enrollment.java`
**功能:** 管理会员课程报名

**API端点 (EnrollmentController):**
- `GET /api/enrollments` - 获取所有报名记录
- `GET /api/enrollments/{id}` - 获取报名详情
- `POST /api/enrollments` - 创建报名
- `POST /api/enrollments/{id}/approve` - 批准报名
- `POST /api/enrollments/batch-approve` - 批量批准
- `POST /api/enrollments/{id}/cancel-approval` - 取消批准
- `DELETE /api/enrollments/{id}` - 删除报名
- `GET /api/enrollments/my` - 获取我的报名
- `GET /api/enrollments/my-schedule` - 获取我的课程表
- `GET /api/enrollments/available-courses` - 获取可用课程

### 6. 器材管理 (Equipment Management)
**实体:** `Equipment.java`
**功能:** 管理健身器材

**API端点 (EquipmentController):**
- `GET /api/equipment` - 获取所有器材
- `GET /api/equipment/{id}` - 获取器材详情
- `POST /api/equipment` - 新增器材
- `PUT /api/equipment/{id}` - 更新器材
- `DELETE /api/equipment/{id}` - 删除器材
- `POST /api/equipment/{id}/status` - 更新器材状态
- `GET /api/equipment/available` - 获取可用器材
- `GET /api/equipment/type/{type}` - 按类型获取器材
- `POST /api/equipment/{id}/maintain` - 设置维护状态

### 7. 器材预约 (Equipment Reservation)
**实体:** `EquipmentReservation.java`
**功能:** 管理器材预约

**API端点 (EquipmentReservationController):**
- `GET /api/equipment-reservations` - 获取所有预约
- `GET /api/equipment-reservations/{id}` - 获取预约详情
- `POST /api/equipment-reservations` - 创建预约
- `POST /api/equipment-reservations/{id}/approve` - 批准预约
- `POST /api/equipment-reservations/{id}/reject` - 拒绝预约
- `POST /api/equipment-reservations/batch-approve` - 批量批准
- `PUT /api/equipment-reservations/{id}/cancel` - 取消预约
- `DELETE /api/equipment-reservations/{id}` - 删除预约
- `GET /api/equipment-reservations/my` - 获取我的预约
- `GET /api/equipment-reservations/date/{date}` - 按日期获取预约
- `GET /api/equipment-reservations/stats/{equipmentId}` - 获取器材统计
- `GET /api/equipment-reservations/check-conflict` - 检查预约冲突

### 8. 房间管理 (Room Management)
**实体:** `Room.java`
**功能:** 管理健身房房间/场地

**API端点 (RoomController):**
- `GET /api/rooms` - 获取所有房间
- `GET /api/rooms/{id}` - 获取房间详情
- `POST /api/rooms` - 新增房间
- `PUT /api/rooms/{id}` - 更新房间
- `DELETE /api/rooms/{id}` - 删除房间

### 9. 排课管理 (Schedule Management)
**实体:** `Schedule.java`
**功能:** 管理课程时间表

**API端点 (ScheduleController):**
- `GET /api/schedules/weekly` - 获取周课程表
- `GET /api/schedules/{id}` - 获取排课详情
- `POST /api/schedules` - 新增排课
- `PUT /api/schedules/{id}` - 更新排课
- `DELETE /api/schedules/{id}` - 删除排课
- `GET /api/schedules/courses` - 获取课程列表
- `GET /api/schedules/coaches` - 获取教练列表
- `GET /api/schedules/rooms` - 获取房间列表

### 10. 会员卡与VIP管理 (Card & VIP Management)
**控制器:** `CardAndVipController.java`
**功能:** 管理会员卡和VIP服务

**API端点:**
- `GET /api/cardAndVip/gymCard/{userId}` - 获取会员卡信息
- `GET /api/cardAndVip/vipInfo/{userId}` - 获取VIP信息
- `POST /api/cardAndVip/renewGymCard` - 续费会员卡
- `POST /api/cardAndVip/upgradeToVip` - 升级到VIP
- `POST /api/cardAndVip/renewAndUpgrade` - 续费并升级
- `GET /api/cardAndVip/cardStatus/{userId}` - 获取卡状态
- `GET /api/cardAndVip/vipStatus/{userId}` - 获取VIP状态

### 11. 菜单/权限管理 (Menu/Permission Management)
**实体:** `Menu.java`, `RolePermission.java`
**功能:** 管理系统菜单和角色权限

**API端点 (MenuController):**
- `POST /api/menu/getUserMenus` - 获取用户菜单
- `POST /api/menu/all-menus` - 获取所有菜单

---

## 数据访问层 (Data Access Layer - DAO)

所有DAO类都位于 `src/main/java/com/example/demo/dao/` 目录下，使用Spring JDBC进行数据库操作。

**DAO列表:**
1. `UserDao.java` - 用户数据访问
2. `CoachDao.java` - 教练数据访问
3. `CourseDao.java` - 课程数据访问
4. `EnrollmentDao.java` - 报名数据访问
5. `EquipmentDao.java` - 器材数据访问
6. `EquipmentReservationDao.java` - 器材预约数据访问
7. `RoomDao.java` - 房间数据访问
8. `ScheduleDao.java` - 排课数据访问
9. `MenuDao.java` - 菜单数据访问

---

## 业务逻辑层 (Service Layer)

所有Service类都位于 `src/main/java/com/example/demo/service/` 目录下，实现业务逻辑。

**Service列表:**
1. `UserService.java` - 用户业务逻辑
2. `CoachService.java` - 教练业务逻辑
3. `CourseService.java` - 课程业务逻辑
4. `EnrollmentService.java` - 报名业务逻辑
5. `EquipmentService.java` - 器材业务逻辑
6. `EquipmentReservationService.java` - 器材预约业务逻辑
7. `RoomService.java` - 房间业务逻辑
8. `ScheduleService.java` - 排课业务逻辑
9. `MenuService.java` - 菜单业务逻辑
10. `CardAndVipService.java` - 会员卡与VIP业务逻辑

---

## 前端页面 (Frontend Pages)

所有HTML页面位于 `src/main/resources/static/` 目录下。

**页面列表:**
1. `index.html` - 首页
2. `login.html` - 登录页面
3. `home.html` - 管理员主页
4. `user_home.html` - 用户主页
5. `member.html` - 会员管理
6. `coaches.html` - 教练管理
7. `equipment.html` - 器材管理
8. `equipment_reservation.html` - 器材预约管理
9. `reserve_equipment.html` - 预约器材
10. `my_equipment_reservations.html` - 我的器材预约
11. `reserve_course.html` - 预约课程
12. `schedules_table.html` - 课程表
13. `schedules_course.html` - 课程排课
14. `schedules_room.html` - 房间排课
15. `schedules_enrollment.html` - 报名管理
16. `mycard.html` - 我的会员卡
17. `myvip.html` - 我的VIP
18. `profile.html` - 个人资料

---

## 数据库配置 (Database Configuration)

**配置文件:** `src/main/resources/application.properties`

```properties
spring.application.name=demo
spring.datasource.url=jdbc:mysql://www.scushujin.xyz:3306/gym_system
spring.datasource.username=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**数据库:** MySQL 8.0.33
**数据库名:** gym_system

---

## 构建与运行 (Build & Run)

### 构建项目
```bash
./mvnw clean install
```

### 运行项目
```bash
./mvnw spring-boot:run
```

### 访问应用
```
http://localhost:8080
```

---

## 项目架构模式 (Architecture Pattern)

本项目采用经典的三层架构：

1. **表现层 (Presentation Layer)** - Controller
   - 处理HTTP请求
   - 返回JSON响应
   - 支持CORS跨域

2. **业务逻辑层 (Business Logic Layer)** - Service
   - 实现业务规则
   - 数据验证
   - 事务管理

3. **数据访问层 (Data Access Layer)** - DAO
   - 数据库CRUD操作
   - SQL查询执行
   - 使用Spring JdbcTemplate

---

## 关键特性 (Key Features)

1. **用户角色管理** - 支持管理员和普通用户两种角色
2. **会员卡系统** - 会员卡管理、续费、到期提醒
3. **VIP服务** - VIP会员升级和特权管理
4. **课程报名** - 在线课程报名和审批流程
5. **器材预约** - 健身器材预约和冲突检测
6. **排课管理** - 课程时间表管理
7. **教练管理** - 教练信息和资质管理
8. **数据统计** - 各类数据统计和报表

---

## API响应格式 (API Response Format)

所有API返回统一的JSON格式：

```json
{
  "success": true/false,
  "message": "操作消息",
  "data": {} // 返回的数据
}
```

---

## 开发建议 (Development Guidelines)

1. **代码规范:** 遵循Java编码规范
2. **注释:** 所有公共方法都应有中文注释
3. **异常处理:** 使用try-catch捕获异常并返回友好错误消息
4. **参数验证:** 在Service层进行业务参数验证
5. **事务管理:** 对于涉及多表操作的方法使用@Transactional

---

## 文件统计 (File Statistics)

- **Java源文件:** 42个
- **HTML页面:** 21个
- **Controller:** 12个
- **Service:** 10个
- **DAO:** 9个
- **Entity:** 10个

---

## 快速索引 (Quick Reference)

### 查找用户相关代码
- Entity: `src/main/java/com/example/demo/entity/User.java`
- DAO: `src/main/java/com/example/demo/dao/UserDao.java`
- Service: `src/main/java/com/example/demo/service/UserService.java`
- Controller: `src/main/java/com/example/demo/controller/UserController.java`

### 查找课程相关代码
- Entity: `src/main/java/com/example/demo/entity/Course.java`
- DAO: `src/main/java/com/example/demo/dao/CourseDao.java`
- Service: `src/main/java/com/example/demo/service/CourseService.java`
- Controller: `src/main/java/com/example/demo/controller/CourseController.java`

### 查找器材相关代码
- Entity: `src/main/java/com/example/demo/entity/Equipment.java`
- DAO: `src/main/java/com/example/demo/dao/EquipmentDao.java`
- Service: `src/main/java/com/example/demo/service/EquipmentService.java`
- Controller: `src/main/java/com/example/demo/controller/EquipmentController.java`

---

## 维护说明 (Maintenance Notes)

本索引文件会随着项目的更新而更新。如果添加新功能或修改现有功能，请相应更新此文档。

**最后更新:** 2026-01-06
**版本:** 1.0.0
