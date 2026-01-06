# 代码导航指南 (Code Navigation Guide)

## 快速查找代码 (Quick Code Lookup)

### 按功能模块查找 (Find by Feature Module)

#### 1. 用户相关 (User Related)
```
用户实体: src/main/java/com/example/demo/entity/User.java
用户DAO: src/main/java/com/example/demo/dao/UserDao.java
用户服务: src/main/java/com/example/demo/service/UserService.java
用户控制器: src/main/java/com/example/demo/controller/UserController.java
认证控制器: src/main/java/com/example/demo/controller/AuthController.java
```

#### 2. 教练相关 (Coach Related)
```
教练实体: src/main/java/com/example/demo/entity/Coach.java
教练DAO: src/main/java/com/example/demo/dao/CoachDao.java
教练服务: src/main/java/com/example/demo/service/CoachService.java
教练控制器: src/main/java/com/example/demo/controller/CoachController.java
教练管理页面: src/main/resources/static/coaches.html
```

#### 3. 课程相关 (Course Related)
```
课程实体: src/main/java/com/example/demo/entity/Course.java
课程DAO: src/main/java/com/example/demo/dao/CourseDao.java
课程服务: src/main/java/com/example/demo/service/CourseService.java
课程控制器: src/main/java/com/example/demo/controller/CourseController.java
课程预约页面: src/main/resources/static/reserve_course.html
```

#### 4. 报名相关 (Enrollment Related)
```
报名实体: src/main/java/com/example/demo/entity/Enrollment.java
报名DAO: src/main/java/com/example/demo/dao/EnrollmentDao.java
报名服务: src/main/java/com/example/demo/service/EnrollmentService.java
报名控制器: src/main/java/com/example/demo/controller/EnrollmentController.java
报名管理页面: src/main/resources/static/schedules_enrollment.html
```

#### 5. 器材相关 (Equipment Related)
```
器材实体: src/main/java/com/example/demo/entity/Equipment.java
器材DAO: src/main/java/com/example/demo/dao/EquipmentDao.java
器材服务: src/main/java/com/example/demo/service/EquipmentService.java
器材控制器: src/main/java/com/example/demo/controller/EquipmentController.java
器材管理页面: src/main/resources/static/equipment.html
```

#### 6. 器材预约相关 (Equipment Reservation Related)
```
预约实体: src/main/java/com/example/demo/entity/EquipmentReservation.java
预约DAO: src/main/java/com/example/demo/dao/EquipmentReservationDao.java
预约服务: src/main/java/com/example/demo/service/EquipmentReservationService.java
预约控制器: src/main/java/com/example/demo/controller/EquipmentReservationController.java
预约器材页面: src/main/resources/static/reserve_equipment.html
我的预约页面: src/main/resources/static/my_equipment_reservations.html
预约管理页面: src/main/resources/static/equipment_reservation.html
```

#### 7. 房间相关 (Room Related)
```
房间实体: src/main/java/com/example/demo/entity/Room.java
房间DAO: src/main/java/com/example/demo/dao/RoomDao.java
房间服务: src/main/java/com/example/demo/service/RoomService.java
房间控制器: src/main/java/com/example/demo/controller/RoomController.java
房间排课页面: src/main/resources/static/schedules_room.html
```

#### 8. 排课相关 (Schedule Related)
```
排课实体: src/main/java/com/example/demo/entity/Schedule.java
排课DAO: src/main/java/com/example/demo/dao/ScheduleDao.java
排课服务: src/main/java/com/example/demo/service/ScheduleService.java
排课控制器: src/main/java/com/example/demo/controller/ScheduleController.java
课程表页面: src/main/resources/static/schedules_table.html
课程排课页面: src/main/resources/static/schedules_course.html
```

#### 9. 会员卡与VIP相关 (Card & VIP Related)
```
会员卡服务: src/main/java/com/example/demo/service/CardAndVipService.java
会员卡控制器: src/main/java/com/example/demo/controller/CardAndVipController.java
我的会员卡页面: src/main/resources/static/mycard.html
我的VIP页面: src/main/resources/static/myvip.html
```

#### 10. 菜单权限相关 (Menu & Permission Related)
```
菜单实体: src/main/java/com/example/demo/entity/Menu.java
权限实体: src/main/java/com/example/demo/entity/RolePermission.java
菜单DAO: src/main/java/com/example/demo/dao/MenuDao.java
菜单服务: src/main/java/com/example/demo/service/MenuService.java
菜单控制器: src/main/java/com/example/demo/controller/MenuController.java
```

---

## 按文件类型查找 (Find by File Type)

### 实体类 (Entities)
所有实体类位于: `src/main/java/com/example/demo/entity/`
- User.java - 用户
- Coach.java - 教练
- Course.java - 课程
- Enrollment.java - 报名
- Equipment.java - 器材
- EquipmentReservation.java - 器材预约
- Room.java - 房间
- Schedule.java - 排课
- Menu.java - 菜单
- RolePermission.java - 角色权限

### DAO层 (Data Access Objects)
所有DAO位于: `src/main/java/com/example/demo/dao/`
- UserDao.java
- CoachDao.java
- CourseDao.java
- EnrollmentDao.java
- EquipmentDao.java
- EquipmentReservationDao.java
- RoomDao.java
- ScheduleDao.java
- MenuDao.java

### 服务层 (Services)
所有服务位于: `src/main/java/com/example/demo/service/`
- UserService.java
- CoachService.java
- CourseService.java
- EnrollmentService.java
- EquipmentService.java
- EquipmentReservationService.java
- RoomService.java
- ScheduleService.java
- MenuService.java
- CardAndVipService.java

### 控制器层 (Controllers)
所有控制器位于: `src/main/java/com/example/demo/controller/`
- AuthController.java - 认证
- UserController.java - 用户
- CoachController.java - 教练
- CourseController.java - 课程
- EnrollmentController.java - 报名
- EquipmentController.java - 器材
- EquipmentReservationController.java - 器材预约
- RoomController.java - 房间
- ScheduleController.java - 排课
- MenuController.java - 菜单
- CardAndVipController.java - 会员卡与VIP

### 前端页面 (Frontend Pages)
所有页面位于: `src/main/resources/static/`

#### 管理页面 (Admin Pages)
- home.html - 管理员主页
- member.html - 会员管理
- coaches.html - 教练管理
- equipment.html - 器材管理
- equipment_reservation.html - 器材预约管理
- schedules_table.html - 课程表
- schedules_course.html - 课程排课
- schedules_room.html - 房间排课
- schedules_enrollment.html - 报名管理

#### 用户页面 (User Pages)
- user_home.html - 用户主页
- reserve_course.html - 预约课程
- reserve_equipment.html - 预约器材
- my_equipment_reservations.html - 我的器材预约
- mycard.html - 我的会员卡
- myvip.html - 我的VIP
- profile.html - 个人资料

#### 公共页面 (Public Pages)
- index.html - 首页
- login.html - 登录

---

## 常见开发任务 (Common Development Tasks)

### 添加新的实体 (Add New Entity)
1. 在 `entity/` 创建实体类
2. 在 `dao/` 创建DAO类
3. 在 `service/` 创建Service类
4. 在 `controller/` 创建Controller类
5. 创建对应的前端页面

### 添加新的API接口 (Add New API Endpoint)
1. 在对应的Controller中添加方法
2. 在Service中实现业务逻辑
3. 在DAO中实现数据访问逻辑

### 修改数据库查询 (Modify Database Query)
1. 找到对应的DAO类
2. 修改SQL查询语句
3. 更新Service层调用（如需要）

### 添加新页面 (Add New Page)
1. 在 `static/` 目录创建HTML文件
2. 在对应的Controller中添加路由
3. 在Menu表中添加菜单项（如需要）

---

## 配置文件位置 (Configuration Files)

- **Maven配置:** `pom.xml`
- **应用配置:** `src/main/resources/application.properties`
- **Git配置:** `.gitignore`, `.gitattributes`

---

## 测试文件 (Test Files)

测试文件位于: `src/test/java/com/example/demo/`

---

## 静态资源 (Static Resources)

- **CSS文件:** `src/main/resources/static/css/`
- **JavaScript文件:** `src/main/resources/static/js/`
- **图片文件:** `src/main/resources/static/images/`

---

## 常用命令 (Common Commands)

### Maven命令
```bash
# 清理构建
./mvnw clean

# 编译项目
./mvnw compile

# 打包项目
./mvnw package

# 运行测试
./mvnw test

# 运行应用
./mvnw spring-boot:run
```

### Git命令
```bash
# 查看状态
git status

# 查看修改
git diff

# 提交更改
git add .
git commit -m "提交信息"

# 推送到远程
git push
```

---

**提示:** 本指南配合 [PROJECT_INDEX.md](PROJECT_INDEX.md) 使用效果更佳！
