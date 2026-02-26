-- 在线学习平台数据库表创建脚本
-- 数据库：edu_online

-- 创建数据库
CREATE DATABASE IF NOT EXISTS edu_online DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE edu_online;

-- 删除表（如果存在）
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS tb_course_review;
DROP TABLE IF EXISTS tb_discussion_reply;
DROP TABLE IF EXISTS tb_discussion;
DROP TABLE IF EXISTS tb_exam_record;
DROP TABLE IF EXISTS tb_exam_question;
DROP TABLE IF EXISTS tb_exam;
DROP TABLE IF EXISTS tb_exercise_submission;
DROP TABLE IF EXISTS tb_exercise;
DROP TABLE IF EXISTS tb_learning_progress;
DROP TABLE IF EXISTS tb_course_enrollment;
DROP TABLE IF EXISTS tb_course_lesson;
DROP TABLE IF EXISTS tb_course_chapter;
DROP TABLE IF EXISTS tb_course;
DROP TABLE IF EXISTS tb_course_category;
DROP TABLE IF EXISTS tb_operation_log;
DROP TABLE IF EXISTS tb_permission;
DROP TABLE IF EXISTS tb_role_permission;
DROP TABLE IF EXISTS tb_role;
DROP TABLE IF EXISTS tb_user_role;
DROP TABLE IF EXISTS tb_user;

SET FOREIGN_KEY_CHECKS = 1;

-- 用户表
CREATE TABLE tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    birthday DATE COMMENT '生日',
    role TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-学生，2-讲师，3-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 课程分类表
CREATE TABLE tb_course_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';

-- 课程表
CREATE TABLE tb_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '课程标题',
    description TEXT COMMENT '课程描述',
    cover_url VARCHAR(500) COMMENT '封面图片URL',
    category_id BIGINT COMMENT '分类ID',
    instructor_id BIGINT NOT NULL COMMENT '讲师ID',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
    level TINYINT DEFAULT 1 COMMENT '难度级别：1-初级，2-中级，3-高级',
    duration INT DEFAULT 0 COMMENT '总时长（分钟）',
    lesson_count INT DEFAULT 0 COMMENT '课时数',
    student_count INT DEFAULT 0 COMMENT '学员数',
    rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '平均评分',
    review_count INT DEFAULT 0 COMMENT '评价数',
    status TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-发布，2-下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (category_id) REFERENCES tb_course_category(id),
    FOREIGN KEY (instructor_id) REFERENCES tb_user(id),
    INDEX idx_category_id (category_id),
    INDEX idx_instructor_id (instructor_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 课程章节表
CREATE TABLE tb_course_chapter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '章节ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '章节标题',
    description TEXT COMMENT '章节描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    INDEX idx_course_id (course_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';

-- 课程课时表
CREATE TABLE tb_course_lesson (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课时ID',
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    title VARCHAR(200) NOT NULL COMMENT '课时标题',
    description TEXT COMMENT '课时描述',
    video_url VARCHAR(500) COMMENT '视频URL',
    duration INT DEFAULT 0 COMMENT '时长（秒）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_free TINYINT DEFAULT 0 COMMENT '是否免费：0-收费，1-免费',
    status TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (chapter_id) REFERENCES tb_course_chapter(id),
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程课时表';

-- 选课记录表
CREATE TABLE tb_course_enrollment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    enroll_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-取消，1-正常',
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    UNIQUE KEY uk_user_course (user_id, course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选课记录表';

-- 学习进度表
CREATE TABLE tb_learning_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '进度ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    lesson_id BIGINT NOT NULL COMMENT '课时ID',
    progress INT DEFAULT 0 COMMENT '进度百分比（0-100）',
    last_position INT DEFAULT 0 COMMENT '最后播放位置（秒）',
    duration INT DEFAULT 0 COMMENT '观看时长（秒）',
    completed TINYINT DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
    last_access_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后访问时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (lesson_id) REFERENCES tb_course_lesson(id),
    UNIQUE KEY uk_user_lesson (user_id, lesson_id),
    INDEX idx_user_id (user_id),
    INDEX idx_lesson_id (lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习进度表';

-- 练习题表
CREATE TABLE tb_exercise (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '练习ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    chapter_id BIGINT COMMENT '章节ID',
    title VARCHAR(200) NOT NULL COMMENT '题目',
    type TINYINT NOT NULL COMMENT '类型：1-单选，2-多选，3-判断，4-填空',
    content TEXT COMMENT '题目内容',
    options JSON COMMENT '选项（JSON格式）',
    answer VARCHAR(500) COMMENT '答案',
    explanation TEXT COMMENT '解析',
    score DECIMAL(5,2) DEFAULT 0 COMMENT '分数',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    FOREIGN KEY (chapter_id) REFERENCES tb_course_chapter(id),
    INDEX idx_course_id (course_id),
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习题表';

-- 练习提交表
CREATE TABLE tb_exercise_submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    exercise_id BIGINT NOT NULL COMMENT '练习ID',
    answer VARCHAR(500) COMMENT '答案',
    score DECIMAL(5,2) DEFAULT 0 COMMENT '得分',
    is_correct TINYINT DEFAULT 0 COMMENT '是否正确',
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (exercise_id) REFERENCES tb_exercise(id),
    UNIQUE KEY uk_user_exercise (user_id, exercise_id),
    INDEX idx_user_id (user_id),
    INDEX idx_exercise_id (exercise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习提交表';

-- 考试表
CREATE TABLE tb_exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '考试标题',
    description TEXT COMMENT '考试描述',
    duration INT DEFAULT 60 COMMENT '考试时长（分钟）',
    total_score DECIMAL(6,2) DEFAULT 100 COMMENT '总分',
    pass_score DECIMAL(6,2) DEFAULT 60 COMMENT '及格分数',
    question_count INT DEFAULT 0 COMMENT '题目数量',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-未开始，1-进行中，2-已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试表';

-- 考试题目表
CREATE TABLE tb_exam_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    title VARCHAR(500) NOT NULL COMMENT '题目',
    type TINYINT NOT NULL COMMENT '类型：1-单选，2-多选，3-判断，4-填空',
    content TEXT COMMENT '题目内容',
    options JSON COMMENT '选项（JSON格式）',
    answer VARCHAR(500) COMMENT '答案',
    score DECIMAL(5,2) DEFAULT 0 COMMENT '分数',
    sort_order INT DEFAULT 0 COMMENT '排序',
    FOREIGN KEY (exam_id) REFERENCES tb_exam(id),
    INDEX idx_exam_id (exam_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试题目表';

-- 考试记录表
CREATE TABLE tb_exam_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    score DECIMAL(6,2) DEFAULT 0 COMMENT '得分',
    total_score DECIMAL(6,2) DEFAULT 0 COMMENT '总分',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未完成，1-已完成，2-超时',
    answers JSON COMMENT '答案（JSON格式）',
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (exam_id) REFERENCES tb_exam(id),
    UNIQUE KEY uk_user_exam (user_id, exam_id),
    INDEX idx_user_id (user_id),
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';

-- 讨论帖表
CREATE TABLE tb_discussion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '讨论ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    view_count INT DEFAULT 0 COMMENT '查看数',
    reply_count INT DEFAULT 0 COMMENT '回复数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    is_essence TINYINT DEFAULT 0 COMMENT '是否精华：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    INDEX idx_course_id (course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论帖表';

-- 讨论回复表
CREATE TABLE tb_discussion_reply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回复ID',
    discussion_id BIGINT NOT NULL COMMENT '讨论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父回复ID',
    content TEXT NOT NULL COMMENT '内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (discussion_id) REFERENCES tb_discussion(id),
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (parent_id) REFERENCES tb_discussion_reply(id),
    INDEX idx_discussion_id (discussion_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论回复表';

-- 课程评价表
CREATE TABLE tb_course_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    rating DECIMAL(2,1) NOT NULL COMMENT '评分（1-5）',
    comment TEXT COMMENT '评价内容',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    UNIQUE KEY uk_user_course (user_id, course_id),
    INDEX idx_course_id (course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程评价表';

-- 操作日志表
CREATE TABLE tb_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(64) COMMENT 'IP地址',
    location VARCHAR(255) COMMENT '操作地点',
    duration BIGINT COMMENT '执行时长(ms)',
    status TINYINT COMMENT '状态：0-失败，1-成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_operation (operation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 插入基础数据
INSERT INTO tb_user (username, password, nickname, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7VdGjNpEKJC.', '管理员', 3, 1),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7VdGjNpEKJC.', '张老师', 2, 1),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7VdGjNpEKJC.', '学生1', 1, 1);

INSERT INTO tb_course_category (name, parent_id, sort_order) VALUES
('编程开发', 0, 1),
('前端开发', 1, 1),
('后端开发', 1, 2),
('数据库', 1, 3),
('人工智能', 0, 2),
('机器学习', 5, 1),
('深度学习', 5, 2);

-- ============================================
-- 缺失的5张表（添加于2026-02-05）
-- ============================================

-- 文件信息表
CREATE TABLE tb_file_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
    file_type VARCHAR(100) COMMENT '文件类型',
    file_extension VARCHAR(20) COMMENT '文件扩展名',
    file_path VARCHAR(500) COMMENT '文件存储路径',
    file_url VARCHAR(500) COMMENT '文件访问URL',
    category TINYINT DEFAULT 5 COMMENT '分类：1-头像，2-课程封面，3-课时视频，4-练习附件，5-其他',
    relation_id BIGINT COMMENT '关联ID',
    upload_user_id BIGINT COMMENT '上传用户ID',
    storage_type TINYINT DEFAULT 1 COMMENT '存储类型：1-本地，2-OSS',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_category (category),
    INDEX idx_relation_id (relation_id),
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 角色表
CREATE TABLE tb_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY uk_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE tb_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(50) NOT NULL COMMENT '权限编码',
    resource VARCHAR(200) COMMENT '资源路径',
    method VARCHAR(20) COMMENT '请求方法',
    description VARCHAR(255) COMMENT '权限描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY uk_perm_code (perm_code),
    INDEX idx_resource (resource),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE tb_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (role_id) REFERENCES tb_role(id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE tb_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES tb_role(id),
    FOREIGN KEY (permission_id) REFERENCES tb_permission(id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 插入默认角色数据
INSERT INTO tb_role (role_name, role_code, description, status) VALUES
('学生', 'ROLE_STUDENT', '学生角色，可以学习课程、参加考试、发表评价', 1),
('讲师', 'ROLE_INSTRUCTOR', '讲师角色，可以创建课程、管理内容', 1),
('管理员', 'ROLE_ADMIN', '管理员角色，拥有系统全部权限', 1);

-- 插入默认权限数据（常用权限）
INSERT INTO tb_permission (perm_name, perm_code, resource, method, description, status) VALUES
('用户查看', 'USER_VIEW', '/api/users', 'GET', '查看用户信息', 1),
('用户管理', 'USER_MANAGE', '/api/users', 'POST,PUT,DELETE', '管理用户', 1),
('课程查看', 'COURSE_VIEW', '/api/courses', 'GET', '查看课程', 1),
('课程管理', 'COURSE_MANAGE', '/api/courses', 'POST,PUT,DELETE', '管理课程', 1),
('考试查看', 'EXAM_VIEW', '/api/exams', 'GET', '查看考试', 1),
('考试管理', 'EXAM_MANAGE', '/api/exams', 'POST,PUT,DELETE', '管理考试', 1),
('系统管理', 'SYSTEM_MANAGE', '/api/system', '*', '系统管理权限', 1);

-- 为管理员角色分配所有权限
INSERT INTO tb_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM tb_role r, tb_permission p WHERE r.role_code = 'ROLE_ADMIN';

-- 为讲师角色分配课程、考试相关权限
INSERT INTO tb_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM tb_role r, tb_permission p 
WHERE r.role_code = 'ROLE_INSTRUCTOR' AND p.perm_code IN ('COURSE_VIEW', 'COURSE_MANAGE', 'EXAM_VIEW', 'EXAM_MANAGE');

-- 为学生角色分配查看权限
INSERT INTO tb_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM tb_role r, tb_permission p 
WHERE r.role_code = 'ROLE_STUDENT' AND p.perm_code IN ('USER_VIEW', 'COURSE_VIEW', 'EXAM_VIEW');