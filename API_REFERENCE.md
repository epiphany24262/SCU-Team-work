# API接口文档 (API Documentation)

## 基础信息 (Basic Information)

**Base URL:** `http://localhost:8080`

**响应格式 (Response Format):**
```json
{
  "success": true/false,
  "message": "操作消息",
  "data": {}
}
```

**请求头 (Request Headers):**
```
Content-Type: application/json
```

---

## 1. 认证接口 (Authentication API)

### 1.1 用户登录
**URL:** `/api/auth/login`  
**Method:** `POST`  
**请求参数:**
```json
{
  "username": "string",
  "password": "string"
}
```
**响应示例:**
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "admin",
    "role": "admin"
  }
}
```

### 1.2 用户注册
**URL:** `/api/auth/register`  
**Method:** `POST`  
**请求参数:**
```json
{
  "username": "string",
  "password": "string",
  "realName": "string",
  "phone": "string"
}
```

---

## 2. 用户管理接口 (User Management API)

### 2.1 获取用户列表
**URL:** `/api/users/list`  
**Method:** `POST`  
**请求参数 (可选):**
```json
{
  "username": "string",
  "phone": "string"
}
```

### 2.2 获取用户详情
**URL:** `/api/users/detail`  
**Method:** `POST`  
**请求参数:**
```json
{
  "id": 1
}
```

### 2.3 新增用户
**URL:** `/api/users/add`  
**Method:** `POST`  
**请求参数:**
```json
{
  "username": "string",
  "password": "string",
  "realName": "string",
  "phone": "string",
  "birthDate": "2000-01-01",
  "role": "user",
  "gender": "男",
  "age": 25
}
```

### 2.4 更新用户
**URL:** `/api/users/update`  
**Method:** `POST`  
**请求参数:**
```json
{
  "id": 1,
  "username": "string",
  "realName": "string",
  "phone": "string",
  "birthDate": "2000-01-01",
  "role": "user",
  "gender": "男",
  "age": 25
}
```

### 2.5 删除用户
**URL:** `/api/users/delete`  
**Method:** `POST`  
**请求参数:**
```json
{
  "id": 1
}
```

### 2.6 获取用户统计
**URL:** `/api/users/stats`  
**Method:** `POST`

### 2.7 检查用户名是否存在
**URL:** `/api/users/check-username`  
**Method:** `POST`  
**请求参数:**
```json
{
  "username": "string"
}
```

---

## 3. 教练管理接口 (Coach Management API)

### 3.1 分页获取教练列表
**URL:** `/api/coaches/page`  
**Method:** `POST`

### 3.2 获取所有教练
**URL:** `/api/coaches/all`  
**Method:** `GET`

### 3.3 获取教练详情
**URL:** `/api/coaches/detail`  
**Method:** `POST`  
**请求参数:**
```json
{
  "id": 1
}
```

### 3.4 新增教练
**URL:** `/api/coaches/add`  
**Method:** `POST`

### 3.5 更新教练
**URL:** `/api/coaches/update`  
**Method:** `POST`

### 3.6 删除教练
**URL:** `/api/coaches/delete`  
**Method:** `POST`  
**请求参数:**
```json
{
  "id": 1
}
```

---

## 4. 课程管理接口 (Course Management API)

### 4.1 分页获取课程列表
**URL:** `/api/courses/page`  
**Method:** `POST`

### 4.2 新增课程
**URL:** `/api/courses/add`  
**Method:** `POST`

### 4.3 更新课程
**URL:** `/api/courses/update`  
**Method:** `POST`

### 4.4 删除课程
**URL:** `/api/courses/delete`  
**Method:** `POST`

### 4.5 获取课程详情
**URL:** `/api/courses/{id}`  
**Method:** `GET`

---

## 5. 报名管理接口 (Enrollment API)

### 5.1 获取所有报名
**URL:** `/api/enrollments`  
**Method:** `GET`

### 5.2 获取报名详情
**URL:** `/api/enrollments/{id}`  
**Method:** `GET`

### 5.3 创建报名
**URL:** `/api/enrollments`  
**Method:** `POST`

### 5.4 批准报名
**URL:** `/api/enrollments/{id}/approve`  
**Method:** `POST`

### 5.5 批量批准
**URL:** `/api/enrollments/batch-approve`  
**Method:** `POST`

### 5.6 取消批准
**URL:** `/api/enrollments/{id}/cancel-approval`  
**Method:** `POST`

### 5.7 删除报名
**URL:** `/api/enrollments/{id}`  
**Method:** `DELETE`

### 5.8 获取我的报名
**URL:** `/api/enrollments/my`  
**Method:** `GET`

### 5.9 获取我的课程表
**URL:** `/api/enrollments/my-schedule`  
**Method:** `GET`

### 5.10 获取可用课程
**URL:** `/api/enrollments/available-courses`  
**Method:** `GET`

---

## 6. 器材管理接口 (Equipment API)

### 6.1 获取所有器材
**URL:** `/api/equipment`  
**Method:** `GET`

### 6.2 获取器材详情
**URL:** `/api/equipment/{id}`  
**Method:** `GET`

### 6.3 新增器材
**URL:** `/api/equipment`  
**Method:** `POST`

### 6.4 更新器材
**URL:** `/api/equipment/{id}`  
**Method:** `PUT`

### 6.5 删除器材
**URL:** `/api/equipment/{id}`  
**Method:** `DELETE`

### 6.6 更新器材状态
**URL:** `/api/equipment/{id}/status`  
**Method:** `POST`

### 6.7 获取可用器材
**URL:** `/api/equipment/available`  
**Method:** `GET`

### 6.8 按类型获取器材
**URL:** `/api/equipment/type/{type}`  
**Method:** `GET`

### 6.9 设置维护状态
**URL:** `/api/equipment/{id}/maintain`  
**Method:** `POST`

---

## 7. 器材预约接口 (Equipment Reservation API)

### 7.1 获取所有预约
**URL:** `/api/equipment-reservations`  
**Method:** `GET`

### 7.2 获取预约详情
**URL:** `/api/equipment-reservations/{id}`  
**Method:** `GET`

### 7.3 创建预约
**URL:** `/api/equipment-reservations`  
**Method:** `POST`

### 7.4 批准预约
**URL:** `/api/equipment-reservations/{id}/approve`  
**Method:** `POST`

### 7.5 拒绝预约
**URL:** `/api/equipment-reservations/{id}/reject`  
**Method:** `POST`

### 7.6 批量批准
**URL:** `/api/equipment-reservations/batch-approve`  
**Method:** `POST`

### 7.7 取消预约
**URL:** `/api/equipment-reservations/{id}/cancel`  
**Method:** `PUT`

### 7.8 删除预约
**URL:** `/api/equipment-reservations/{id}`  
**Method:** `DELETE`

### 7.9 获取我的预约
**URL:** `/api/equipment-reservations/my`  
**Method:** `GET`

### 7.10 按日期获取预约
**URL:** `/api/equipment-reservations/date/{date}`  
**Method:** `GET`

### 7.11 获取器材统计
**URL:** `/api/equipment-reservations/stats/{equipmentId}`  
**Method:** `GET`

### 7.12 检查预约冲突
**URL:** `/api/equipment-reservations/check-conflict`  
**Method:** `GET`

---

## 8. 房间管理接口 (Room API)

### 8.1 获取所有房间
**URL:** `/api/rooms`  
**Method:** `GET`

### 8.2 获取房间详情
**URL:** `/api/rooms/{id}`  
**Method:** `GET`

### 8.3 新增房间
**URL:** `/api/rooms`  
**Method:** `POST`

### 8.4 更新房间
**URL:** `/api/rooms/{id}`  
**Method:** `PUT`

### 8.5 删除房间
**URL:** `/api/rooms/{id}`  
**Method:** `DELETE`

---

## 9. 排课管理接口 (Schedule API)

### 9.1 获取周课程表
**URL:** `/api/schedules/weekly`  
**Method:** `GET`

### 9.2 获取排课详情
**URL:** `/api/schedules/{id}`  
**Method:** `GET`

### 9.3 新增排课
**URL:** `/api/schedules`  
**Method:** `POST`

### 9.4 更新排课
**URL:** `/api/schedules/{id}`  
**Method:** `PUT`

### 9.5 删除排课
**URL:** `/api/schedules/{id}`  
**Method:** `DELETE`

### 9.6 获取课程列表
**URL:** `/api/schedules/courses`  
**Method:** `GET`

### 9.7 获取教练列表
**URL:** `/api/schedules/coaches`  
**Method:** `GET`

### 9.8 获取房间列表
**URL:** `/api/schedules/rooms`  
**Method:** `GET`

---

## 10. 会员卡与VIP接口 (Card & VIP API)

### 10.1 获取会员卡信息
**URL:** `/api/cardAndVip/gymCard/{userId}`  
**Method:** `GET`

### 10.2 获取VIP信息
**URL:** `/api/cardAndVip/vipInfo/{userId}`  
**Method:** `GET`

### 10.3 续费会员卡
**URL:** `/api/cardAndVip/renewGymCard`  
**Method:** `POST`

### 10.4 升级到VIP
**URL:** `/api/cardAndVip/upgradeToVip`  
**Method:** `POST`

### 10.5 续费并升级
**URL:** `/api/cardAndVip/renewAndUpgrade`  
**Method:** `POST`

### 10.6 获取卡状态
**URL:** `/api/cardAndVip/cardStatus/{userId}`  
**Method:** `GET`

### 10.7 获取VIP状态
**URL:** `/api/cardAndVip/vipStatus/{userId}`  
**Method:** `GET`

---

## 11. 菜单接口 (Menu API)

### 11.1 获取用户菜单
**URL:** `/api/menu/getUserMenus`  
**Method:** `POST`

### 11.2 获取所有菜单
**URL:** `/api/menu/all-menus`  
**Method:** `POST`

---

## 错误码 (Error Codes)

| HTTP状态码 | 说明 |
|-----------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 通用响应示例 (Common Response Examples)

### 成功响应
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    // 返回的数据
  }
}
```

### 失败响应
```json
{
  "success": false,
  "message": "操作失败的原因",
  "data": null
}
```

---

**注意事项:**
1. 所有POST请求的Content-Type应为application/json
2. 日期格式统一使用ISO 8601格式 (yyyy-MM-dd)
3. 分页参数通常包含page和size
4. 所有API都支持CORS跨域访问
