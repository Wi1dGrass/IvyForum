-- ============================================
-- IvyForum Init Script
-- Target: MySQL 5.7.26, root / 123456
-- ============================================
DROP DATABASE IF EXISTS ivy_forum;
CREATE DATABASE ivy_forum DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ivy_forum;

CREATE TABLE t_user (
  user_id      BIGINT       NOT NULL AUTO_INCREMENT,
  username     VARCHAR(32)  NOT NULL,
  password     VARCHAR(72)  NOT NULL,
  nickname     VARCHAR(64)  NOT NULL,
  avatar       VARCHAR(255) DEFAULT NULL,
  signature    VARCHAR(255) DEFAULT NULL,
  role         VARCHAR(16)  NOT NULL DEFAULT 'STUDENT',
  status       VARCHAR(16)  NOT NULL DEFAULT 'NORMAL',
  email        VARCHAR(128) DEFAULT NULL,
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_channel (
  channel_id   BIGINT       NOT NULL AUTO_INCREMENT,
  parent_id    BIGINT       NOT NULL DEFAULT 0,
  name         VARCHAR(64)  NOT NULL,
  sort         INT          NOT NULL DEFAULT 0,
  icon         VARCHAR(255) DEFAULT NULL,
  description  VARCHAR(255) DEFAULT NULL,
  status       VARCHAR(16)  NOT NULL DEFAULT 'NORMAL',
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (channel_id),
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_tag (
  tag_id       BIGINT       NOT NULL AUTO_INCREMENT,
  name         VARCHAR(32)  NOT NULL,
  color        VARCHAR(16)  DEFAULT '#409EFF',
  ref_count    INT          NOT NULL DEFAULT 0,
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (tag_id),
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_post (
  post_id        BIGINT       NOT NULL AUTO_INCREMENT,
  author_id      BIGINT       NOT NULL,
  channel_id     BIGINT       NOT NULL,
  title          VARCHAR(128) NOT NULL,
  content        MEDIUMTEXT   NOT NULL,
  view_count     INT          NOT NULL DEFAULT 0,
  like_count     INT          NOT NULL DEFAULT 0,
  comment_count  INT          NOT NULL DEFAULT 0,
  collect_count  INT          NOT NULL DEFAULT 0,
  is_top         TINYINT(1)   NOT NULL DEFAULT 0,
  is_essence     TINYINT(1)   NOT NULL DEFAULT 0,
  status         VARCHAR(16)  NOT NULL DEFAULT 'NORMAL',
  create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (post_id),
  KEY idx_channel_status (channel_id, status, create_time),
  KEY idx_top_status (is_top, status),
  KEY idx_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_post_tag (
  post_id  BIGINT NOT NULL,
  tag_id   BIGINT NOT NULL,
  PRIMARY KEY (post_id, tag_id),
  KEY idx_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_comment (
  comment_id   BIGINT        NOT NULL AUTO_INCREMENT,
  post_id      BIGINT        NOT NULL,
  parent_id    BIGINT        DEFAULT NULL,
  root_id     BIGINT        DEFAULT NULL,
  author_id    BIGINT        NOT NULL,
  content      VARCHAR(1024) NOT NULL,
  like_count   INT           NOT NULL DEFAULT 0,
  floor        INT           NOT NULL DEFAULT 0,
  is_deleted   TINYINT(1)    NOT NULL DEFAULT 0,
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id),
  KEY idx_post_root (post_id, root_id),
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_like (
  like_id      BIGINT      NOT NULL AUTO_INCREMENT,
  user_id      BIGINT      NOT NULL,
  target_type  VARCHAR(16) NOT NULL,
  target_id    BIGINT      NOT NULL,
  create_time  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (like_id),
  UNIQUE KEY uk_user_target (user_id, target_type, target_id),
  KEY idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_collect (
  collect_id   BIGINT   NOT NULL AUTO_INCREMENT,
  user_id       BIGINT   NOT NULL,
  post_id       BIGINT   NOT NULL,
  create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (collect_id),
  UNIQUE KEY uk_user_post (user_id, post_id),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_notification (
  notify_id    BIGINT       NOT NULL AUTO_INCREMENT,
  receiver_id  BIGINT       NOT NULL,
  sender_id    BIGINT       DEFAULT NULL,
  type         VARCHAR(16)  NOT NULL,
  target_type  VARCHAR(16)  DEFAULT NULL,
  target_id    BIGINT       DEFAULT NULL,
  content      VARCHAR(255) DEFAULT NULL,
  is_read      TINYINT(1)   NOT NULL DEFAULT 0,
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (notify_id),
  KEY idx_receiver_read (receiver_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_report (
  report_id     BIGINT       NOT NULL AUTO_INCREMENT,
  reporter_id   BIGINT       NOT NULL,
  target_type   VARCHAR(16)  NOT NULL,
  target_id     BIGINT       NOT NULL,
  reason_type   VARCHAR(32)  NOT NULL,
  reason_text   VARCHAR(255) DEFAULT NULL,
  status        VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
  handler_id    BIGINT       DEFAULT NULL,
  handle_remark VARCHAR(255) DEFAULT NULL,
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  handle_time   DATETIME     DEFAULT NULL,
  PRIMARY KEY (report_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_hot_post (
  post_id      BIGINT   NOT NULL,
  hot_score    DOUBLE   NOT NULL DEFAULT 0,
  rank_no      INT      NOT NULL DEFAULT 0,
  refresh_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- seed data
INSERT INTO t_user (username, password, nickname, role, status, email, signature) VALUES
('admin', '$2a$10$1vca0X5PJwTqVFzFic3EreuOUuj5QnNceycx5mz1qfTqEfkPvzFme', 'Admin', 'ADMIN', 'NORMAL', 'admin@ivy.edu', 'Forum Admin'),
('teacher01', '$2a$10$9L9mu1DtCdUM5F7DvJOJ8.Ke6w/1ecT4dQUoCsUPMcnTsvC5KvFCi', '王老师', 'TEACHER', 'NORMAL', 'wang@school.edu', '答疑解惑'),
('stu01', '$2a$10$9L9mu1DtCdUM5F7DvJOJ8.Ke6w/1ecT4dQUoCsUPMcnTsvC5KvFCi', '小明', 'STUDENT', 'NORMAL', 'stu01@school.edu', '努力学习ing'),
('stu02', '$2a$10$9L9mu1DtCdUM5F7DvJOJ8.Ke6w/1ecT4dQUoCsUPMcnTsvC5KvFCi', '小红', 'STUDENT', 'NORMAL', 'stu02@school.edu', '爱技术');

INSERT INTO t_channel (parent_id, name, sort, description, status) VALUES
(0, '学习交流', 1, '学术问题与课程讨论', 'NORMAL'),
(0, '校园生活', 2, '食宿活动周边', 'NORMAL'),
(0, '技术分享', 3, '编程与新技术', 'NORMAL'),
(0, '求职招聘', 4, '实习与就业', 'NORMAL'),
(0, '答疑解惑', 5, '提问求助', 'NORMAL');

INSERT INTO t_tag (name, color) VALUES
('Java', '#F56C6C'),
('Python', '#67C23A'),
('前端', '#409EFF'),
('后端', '#909399'),
('算法', '#E6A23C'),
('考研', '#F56C6C'),
('实习', '#67C23A'),
('考试', '#409EFF'),
('课程', '#909399'),
('其他', '#C0C4CC');