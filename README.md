# 在线学习平台系统

## 📖 项目简介

这是一个完整的在线学习平台系统，采用前后端分离架构，包含用户管理、课程管理、学习功能、考试系统、社区交流等完整的在线教育功能。

## 🏗️ 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.7.18
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **认证**: JWT
- **文档**: Swagger 2.9.2
- **存储**: 阿里云OSS (可选)

### 开发环境要求
- JDK 8+
- MySQL 8.0+
- Redis (可选)
- Maven 3.6+

## 📋 功能模块

### 1. 用户认证模块 (Auth)
- 用户注册和登录
- JWT Token管理
- 密码加密存储
- 登出功能

### 2. 用户管理模块 (User)
- 用户信息管理
- 密码修改
- 用户状态控制

### 3. 课程管理模块 (Course)
- 课程CRUD操作
- 课程分类管理
- 章节和课时管理
- 课程发布/下架

### 4. 学习模块 (Learning)
- 课程选修/退选
- 学习进度跟踪
- 学习记录统计
- 断点续播功能

### 5. 练习作业模块 (Exercise)
- 练习题管理 (单选/多选/判断/填空)
- 自动评分系统
- 练习记录管理
- 练习统计分析

### 6. 考试模块 (Exam)
- 在线考试系统
- 考试时间控制
- 自动评分功能
- 考试统计分析

### 7. 讨论区模块 (Discussion)
- 课程讨论发帖
- 回复和点赞功能
- 热门内容推荐
- 讨论区管理

### 8. 评价反馈模块 (Review)
- 课程评价系统
- 星级评分功能
- 评价统计分析
- 评价管理控制

### 9. 文件服务模块 (File)
- 文件上传下载
- 多文件类型支持
- 本地/OSS双存储
- 文件分类管理

### 10. 系统管理模块 (System)
- RBAC权限管理
- 操作日志记录
- 数据统计分析
- 用户角色分配

## 🔗 核心API接口

### 健康检查接口
```
GET    /api/health            # 基础健康检查
GET    /api/health/detail     # 详细健康状态（JVM、系统信息）
GET    /api/health/ready      # 就绪检查（K8s就绪探针）
GET    /api/health/liveness   # 存活检查（K8s存活探针）
```

### 用户认证接口
```
POST   /api/auth/login         # 用户登录
POST   /api/auth/register      # 用户注册
POST   /api/auth/logout        # 用户登出
GET    /api/auth/info          # 获取当前用户信息
POST   /api/auth/refresh       # 刷新Token
```

### 课程管理接口
```
GET    /api/courses            # 获取课程列表
POST   /api/courses            # 创建课程
GET    /api/courses/{id}       # 获取课程详情
PUT    /api/courses/{id}       # 更新课程
DELETE /api/courses/{id}       # 删除课程
PUT    /api/courses/{id}/publish   # 发布课程
PUT    /api/courses/{id}/unpublish # 下架课程
GET    /api/courses/hot        # 获取热门课程
GET    /api/courses/search     # 搜索课程
```

### 章节课时管理接口
```
GET    /api/courses/{courseId}/chapters     # 获取章节列表
POST   /api/courses/{courseId}/chapters     # 创建章节
PUT    /api/courses/{courseId}/chapters/{chapterId}   # 更新章节
DELETE /api/courses/{courseId}/chapters/{chapterId}   # 删除章节
GET    /api/chapters/{chapterId}/lessons    # 获取课时列表
POST   /api/chapters/{chapterId}/lessons    # 创建课时
PUT    /api/chapters/{chapterId}/lessons/{lessonId}   # 更新课时
DELETE /api/chapters/{chapterId}/lessons/{lessonId}   # 删除课时
```

### 学习功能接口
```
POST   /api/learning/enroll     # 选课
POST   /api/learning/unenroll   # 退课
GET    /api/learning/my-courses # 获取我的选课
GET    /api/learning/progress/{lessonId}    # 获取学习进度
POST   /api/learning/progress   # 更新学习进度
POST   /api/learning/complete/{lessonId}    # 完成课时
GET    /api/learning/stats      # 获取学习统计
```

### 练习作业接口
```
GET    /api/exercises/course/{courseId}     # 获取课程练习题
POST   /api/exercises           # 创建练习题
PUT    /api/exercises/{id}      # 更新练习题
DELETE /api/exercises/{id}      # 删除练习题
POST   /api/exercises/{exerciseId}/submit   # 提交练习答案
GET    /api/exercises/{exerciseId}/my-submission   # 获取我的提交
GET    /api/exercises/{exerciseId}/stats   # 获取练习统计
```

### 考试功能接口
```
GET    /api/exams/course/{courseId}   # 获取课程考试
POST   /api/exams               # 创建考试
PUT    /api/exams/{id}          # 更新考试
DELETE /api/exams/{id}          # 删除考试
POST   /api/exams/{examId}/start     # 开始考试
POST   /api/exams/{examId}/submit    # 提交考试
GET    /api/exams/{examId}/questions # 获取考试题目
GET    /api/exams/{examId}/stats     # 获取考试统计
GET    /api/exams/my-records    # 获取我的考试记录
```

### 讨论区接口
```
GET    /api/discussions/course/{courseId}   # 获取课程讨论
POST   /api/discussions           # 发布讨论帖
PUT    /api/discussions/{id}      # 更新讨论帖
DELETE /api/discussions/{id}      # 删除讨论帖
GET    /api/discussions/{id}      # 获取讨论详情
GET    /api/discussions/course/{courseId}/hot   # 获取热门讨论
PUT    /api/discussions/{id}/toggle-top         # 置顶/取消置顶
PUT    /api/discussions/{id}/toggle-essence     # 设为精华
```

### 回复功能接口
```
GET    /api/discussions/{discussionId}/replies    # 获取回复列表
POST   /api/discussions/{discussionId}/replies    # 发表回复
DELETE /api/discussions/{discussionId}/replies/{replyId}   # 删除回复
POST   /api/discussions/{discussionId}/replies/{replyId}/like   # 点赞回复
GET    /api/discussions/{discussionId}/replies/{replyId}/likes  # 获取点赞数
```

### 评价功能接口
```
GET    /api/reviews/course/{courseId}      # 获取课程评价
POST   /api/reviews               # 发表评价
PUT    /api/reviews/{id}          # 更新评价
DELETE /api/reviews/{id}          # 删除评价
GET    /api/reviews/my-reviews    # 获取我的评价
GET    /api/reviews/course/{courseId}/average-rating   # 获取平均评分
GET    /api/reviews/course/{courseId}/stats            # 获取评价统计
GET    /api/reviews/course/{courseId}/has-reviewed     # 检查是否已评价
GET    /api/reviews/user/stats    # 获取用户评价统计
```

### 文件上传接口
```
POST   /api/upload/file          # 上传通用文件
POST   /api/upload/avatar        # 上传头像
POST   /api/upload/course-cover  # 上传课程封面
POST   /api/upload/lesson-video  # 上传课时视频
POST   /api/upload/exercise-attachment  # 上传练习附件
GET    /api/upload/signature     # 获取OSS上传签名
DELETE /api/upload/file          # 删除文件
GET    /api/upload/file-info     # 获取文件信息
GET    /api/upload/files         # 获取关联文件列表
GET    /api/upload/file-url      # 获取文件访问URL
```

## ⚙️ 配置说明

### 1. 数据库配置

在 `src/main/resources/application.yml` 中配置：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/edu_online?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: your_username
    password: your_password
```

**需要执行的SQL脚本**：项目根目录下的 `edu_online.sql`

### 2. Redis配置 (可选)

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password  # 如果有密码
    database: 0
```

### 3. JWT配置

```yaml
jwt:
  secret: your-secret-key  # 建议使用32位以上的随机字符串
  expire: 604800000        # Token过期时间，默认7天
  header: Authorization
  token-head: Bearer
```

### 4. OSS配置 (可选)

```yaml
oss:
  enabled: true
  provider: aliyun
  endpoint: https://oss-cn-hangzhou.aliyuncs.com
  access-key-id: your-access-key-id
  access-key-secret: your-access-key-secret
  bucket-name: your-bucket-name
  domain: https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com
```

### 5. 文件上传配置

```yaml
file:
  upload:
    max-size: 50MB
    local-path: uploads/
  oss:
    enabled: false  # 如需使用OSS，设为true
    expire: 3600    # 文件URL过期时间(秒)
```

## 🚀 快速开始

### 1. 环境准备
```bash
# 1. 安装JDK 8+
# 2. 安装MySQL 8.0+
# 3. 安装Maven 3.6+
# 4. 可选：安装Redis
```

### 2. 数据库初始化
```bash
# 创建数据库
CREATE DATABASE edu_online CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行SQL脚本
mysql -u username -p edu_online < edu_online.sql
```

### 3. 配置修改
```bash
# 修改 application.yml 中的数据库连接信息
# 如需使用OSS，配置相应的OSS参数
# 修改JWT secret密钥
```

### 4. 启动应用
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/edu-online-1.0.0.jar
```

### 5. 访问应用
- **API文档**: http://localhost:8080/swagger-ui.html
- **应用接口**: http://localhost:8080/api/*

## 📊 数据统计接口

### 系统概览
```
GET /api/system/overview  # 系统概览统计
```

### 用户统计
```
GET /api/system/user-stats  # 用户统计信息
```

### 课程统计
```
GET /api/system/course-stats  # 课程统计信息
```

### 学习统计
```
GET /api/system/learning-stats  # 学习统计信息
```

## 🔐 权限说明

### 用户角色
- **STUDENT (1)**: 学生，可以学习课程、参加考试、发表评价
- **INSTRUCTOR (2)**: 讲师，可以创建课程、管理内容
- **ADMIN (3)**: 管理员，拥有系统全部权限

### 主要权限
- `COURSE_VIEW`: 查看课程
- `COURSE_MANAGE`: 管理课程
- `USER_MANAGE`: 用户管理
- `SYSTEM_MANAGE`: 系统管理

## 🧪 测试说明

项目包含完整的JUnit测试用例：

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -Dtest=CourseModuleTest
mvn test -Dtest=LearningModuleTest
mvn test -Dtest=ExamModuleTest
```

## 📝 注意事项

1. **数据库编码**: 必须使用UTF-8编码
2. **时区设置**: 数据库时区设置为Asia/Shanghai
3. **文件权限**: uploads目录需要写权限
4. **OSS配置**: 生产环境建议使用OSS存储
5. **Redis缓存**: 可选配置，提升系统性能

## 🤝 技术支持

如有问题，请检查：
1. 数据库连接是否正常
2. 配置文件是否正确
3. 端口8080是否被占用
4. JDK版本是否为8+

## 📅 更新日志

### v1.2.0 (2026-02-26)
- 新增健康检查模块（支持 K8s 探针）
- 添加 JVM 和系统 information 监控接口
- 优化项目对 JDK 8 的兼容性

### v1.1.0 (2026-02-10)
- 新增系统管理模块（RBAC 权限管理、操作日志、数据统计）
- 优化课程搜索性能
- 修复学习进度统计不准确的问题
- 完善 API 文档和错误码说明

### v1.0.0
- 初始版本发布
- 完成用户认证、课程管理、学习、考试、讨论、评价、文件服务等核心模块

## 📄 开源协议

本项目采用 MIT 许可证。