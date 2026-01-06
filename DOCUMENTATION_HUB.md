# 文档导航 (Documentation Navigation)

欢迎来到健身房管理系统文档中心！本文档帮助您快速找到所需的信息。

Welcome to the Gym Management System Documentation Hub! This guide helps you quickly find the information you need.

---

## 📚 文档列表 (Documentation List)

### 1. 📖 README.md - 项目首页
**用途:** 项目简介和快速开始指南  
**适合:** 新手开发者、项目经理  
**包含内容:**
- 项目简介
- 技术栈
- 快速启动命令
- 主要功能列表

👉 [查看 README.md](README.md)

---

### 2. 🗂️ PROJECT_INDEX.md - 项目索引（中文）
**用途:** 完整的项目结构和功能说明  
**适合:** 开发人员、架构师、产品经理  
**包含内容:**
- 项目概述和技术栈
- 核心模块详细说明（11个主要模块）
- 数据访问层(DAO)列表
- 业务逻辑层(Service)列表
- 前端页面列表
- 数据库配置
- 构建和运行指南
- 项目架构模式
- 关键特性
- API响应格式
- 开发建议
- 快速索引

👉 [查看 PROJECT_INDEX.md](PROJECT_INDEX.md)

---

### 3. 🌐 PROJECT_INDEX_EN.md - Project Index (English)
**Purpose:** Complete project structure and functionality description in English  
**For:** International developers, architects, product managers  
**Contains:**
- Project overview and tech stack
- Core modules detailed description (11 main modules)
- Data Access Layer (DAO) list
- Service Layer list
- Frontend pages list
- Database configuration
- Build and run guide
- Architecture pattern
- Key features
- API response format
- Development guidelines
- Quick reference

👉 [View PROJECT_INDEX_EN.md](PROJECT_INDEX_EN.md)

---

### 4. 🛣️ CODE_NAVIGATION.md - 代码导航指南
**用途:** 快速定位代码文件  
**适合:** 开发人员、代码审查者  
**包含内容:**
- 按功能模块查找代码（10个模块的完整文件路径）
- 按文件类型查找（实体、DAO、服务、控制器、页面）
- 常见开发任务指南
- 配置文件位置
- 测试文件位置
- 静态资源位置
- 常用命令

👉 [查看 CODE_NAVIGATION.md](CODE_NAVIGATION.md)

---

### 5. 🔌 API_REFERENCE.md - API接口文档
**用途:** API接口详细说明  
**适合:** 前端开发者、API集成开发者、测试人员  
**包含内容:**
- 基础信息（Base URL、响应格式、请求头）
- 11个模块的所有API接口：
  1. 认证接口（2个）
  2. 用户管理接口（7个）
  3. 教练管理接口（6个）
  4. 课程管理接口（5个）
  5. 报名管理接口（10个）
  6. 器材管理接口（9个）
  7. 器材预约接口（12个）
  8. 房间管理接口（5个）
  9. 排课管理接口（8个）
  10. 会员卡与VIP接口（7个）
  11. 菜单接口（2个）
- 错误码说明
- 通用响应示例

👉 [查看 API_REFERENCE.md](API_REFERENCE.md)

---

## 🎯 快速导航 (Quick Navigation)

### 我想了解项目整体情况
➡️ 先看 [README.md](README.md)，再看 [PROJECT_INDEX.md](PROJECT_INDEX.md)

### 我要开发新功能
➡️ 先看 [CODE_NAVIGATION.md](CODE_NAVIGATION.md) 了解代码结构，再看 [PROJECT_INDEX.md](PROJECT_INDEX.md) 了解架构

### 我要调用API接口
➡️ 直接查看 [API_REFERENCE.md](API_REFERENCE.md)

### 我要找特定文件
➡️ 使用 [CODE_NAVIGATION.md](CODE_NAVIGATION.md) 按模块或文件类型查找

### 我要了解数据模型
➡️ 查看 [PROJECT_INDEX.md](PROJECT_INDEX.md) 的"核心模块"章节

### 我要了解技术架构
➡️ 查看 [PROJECT_INDEX.md](PROJECT_INDEX.md) 的"项目架构模式"章节

---

## 📊 项目统计 (Project Statistics)

- **Java源文件:** 42个
- **HTML页面:** 21个
- **控制器(Controllers):** 12个
- **服务(Services):** 10个
- **DAO:** 9个
- **实体(Entities):** 10个
- **API接口:** 70+个

---

## 🏗️ 项目架构快览 (Architecture Overview)

```
前端层 (Frontend)
    ↓
控制器层 (Controller) - REST API
    ↓
服务层 (Service) - 业务逻辑
    ↓
数据访问层 (DAO) - 数据库操作
    ↓
数据库 (MySQL)
```

---

## 🔑 关键概念 (Key Concepts)

### 用户角色 (User Roles)
- **admin:** 管理员 - 可以管理所有功能
- **user:** 普通用户 - 可以预约课程、预约器材、查看个人信息

### 核心功能流程 (Core Workflows)

1. **用户注册和登录**
   - 注册 → 登录 → 获取菜单 → 访问功能

2. **课程报名**
   - 查看可用课程 → 创建报名 → 等待批准 → 查看我的课程表

3. **器材预约**
   - 查看可用器材 → 检查冲突 → 创建预约 → 等待批准 → 使用器材

4. **会员卡管理**
   - 查看卡状态 → 续费 → 升级VIP

---

## 💡 开发提示 (Development Tips)

1. **添加新功能的标准流程:**
   - 创建Entity → 创建DAO → 创建Service → 创建Controller → 创建前端页面

2. **代码规范:**
   - 所有公共方法需要有中文注释
   - 使用统一的API响应格式
   - 在Service层进行参数验证
   - 使用try-catch处理异常

3. **测试建议:**
   - 先测试DAO层的数据库操作
   - 再测试Service层的业务逻辑
   - 最后测试Controller层的API接口

---

## 🔍 常见问题 (FAQ)

### Q: 如何找到某个功能的代码？
A: 查看 [CODE_NAVIGATION.md](CODE_NAVIGATION.md)，按功能模块查找

### Q: 如何查看某个API的详细参数？
A: 查看 [API_REFERENCE.md](API_REFERENCE.md)

### Q: 如何了解项目的整体架构？
A: 查看 [PROJECT_INDEX.md](PROJECT_INDEX.md) 的"项目架构模式"章节

### Q: 如何运行项目？
A: 查看 [README.md](README.md) 的"快速开始"章节

---

## 📝 文档维护 (Documentation Maintenance)

**维护原则:**
- 每次添加新功能时，同步更新相关文档
- 每次修改API接口时，同步更新API_REFERENCE.md
- 每次重构代码时，同步更新CODE_NAVIGATION.md
- 保持文档的准确性和时效性

**文档更新顺序:**
1. 修改代码
2. 更新API_REFERENCE.md（如果涉及API）
3. 更新CODE_NAVIGATION.md（如果添加新文件）
4. 更新PROJECT_INDEX.md（如果添加新模块）
5. 更新README.md（如果是重大变更）

---

## 🌟 为什么需要这些文档？

### 对开发者的价值
- ✅ 快速了解项目结构
- ✅ 减少代码查找时间
- ✅ 降低学习曲线
- ✅ 提高开发效率

### 对团队的价值
- ✅ 统一理解项目架构
- ✅ 便于代码审查
- ✅ 方便知识传递
- ✅ 提升协作效率

### 对AI的价值
- ✅ 提供结构化的项目信息
- ✅ 便于快速定位相关代码
- ✅ 支持精准的代码生成和修改
- ✅ 提高AI辅助开发的准确性

---

**最后更新:** 2026-01-06  
**维护者:** SCU Team  
**版本:** 1.0.0

---

**🎉 文档已就绪！开始您的开发之旅吧！**
