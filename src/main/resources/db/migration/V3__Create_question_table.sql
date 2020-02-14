create table QUESTION
(
	ID INT AUTO_INCREMENT PRIMARY KEY,
	TITLE VARCHAR(50),
	DESCRIPTION TEXT,
	GMT_CREATE BIGINT,
	GMT_MODIFIED BIGINT,
	CREATOR INT,
	COMMENT_COUNT INT default 0,
	VIEW_COUNT INT default 0,
	LIKE_COUNT INT default 0,
	TAG VARCHAR(255)
);