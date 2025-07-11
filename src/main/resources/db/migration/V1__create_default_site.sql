DROP TABLE IF EXISTS flex_user;
CREATE TABLE flex_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL,
    avatar VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS flex_role;
CREATE TABLE flex_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS flex_user_role;
CREATE TABLE flex_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

DROP TABLE IF EXISTS flex_site;
CREATE TABLE flex_site (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_name VARCHAR(255) NOT NULL,
    context VARCHAR(255) NOT NULL,
    private_site BOOLEAN NOT NULL,
    spa_or_static BOOLEAN NOT NULL,
    description VARCHAR(255),
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS flex_user_site;
CREATE TABLE flex_user_site (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL
);  

DROP TABLE IF EXISTS flex_site_role;
CREATE TABLE flex_site_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);  

DROP TABLE IF EXISTS flex_page;
CREATE TABLE flex_page (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    site_id BIGINT NOT NULL,
    parent_id BIGINT,
    page_name VARCHAR(255) NOT NULL,
    page_title VARCHAR(255),
    page_path VARCHAR(255) NOT NULL,
    view_template_id BIGINT,
    secure BOOLEAN NOT NULL,
    visible BOOLEAN NOT NULL,
    seq INT NOT NULL,
    include_content VARCHAR(255),
    include_script VARCHAR(255),
    include_style VARCHAR(255)
);  

DROP TABLE IF EXISTS flex_page_role;
CREATE TABLE flex_page_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    page_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);  

DROP TABLE IF EXISTS flex_view_template;
CREATE TABLE flex_view_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    site_id BIGINT NOT NULL,
    template_name VARCHAR(255) NOT NULL,
    template_type VARCHAR(255) NOT NULL,
    template_location VARCHAR(255) NOT NULL,
    relative_path VARCHAR(255),
    content VARCHAR(255)
);

DROP TABLE IF EXISTS flex_header;
CREATE TABLE flex_header (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    site_id BIGINT NOT NULL,
    logo VARCHAR(255),
    slogan VARCHAR(255),
    include_script VARCHAR(255),
    include_style VARCHAR(255),
    include_content VARCHAR(255)
);

DROP TABLE IF EXISTS flex_footer;
CREATE TABLE flex_footer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,   
    site_id BIGINT NOT NULL,
    logo VARCHAR(255),
    slogan VARCHAR(255),
    include_script VARCHAR(255),
    include_style VARCHAR(255),
    include_content VARCHAR(255)
);

DROP TABLE IF EXISTS flex_navbar;
CREATE TABLE flex_navbar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    site_id BIGINT NOT NULL,
    parent_id BIGINT,
    page_id BIGINT,
    name VARCHAR(255) NOT NULL,
    href VARCHAR(255) NOT NULL,
    followed_parent_href BOOLEAN NOT NULL,
    visible BOOLEAN NOT NULL,   
    target VARCHAR(255),
    seq INT NOT NULL,
    secure BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS flex_sys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    initialized BOOLEAN NOT NULL
);

-- Insert default admin site
INSERT INTO flex_user (
    user_name,
    password,
    email,
    full_name,
    status, 
    avatar,
    created_date,
    modified_date
) VALUES (
    'admin',
    'admin',
    'admin@flexportal.com',
    'admin',
    true,
    'admin.jpg',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO flex_role (
    user_id,
    role_name,
    created_date,
    modified_date
) VALUES (
    1,
    'admin',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
INSERT INTO flex_role (
    user_id,
    role_name,
    created_date,
    modified_date
) VALUES (
    1,
    'content_manager',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
INSERT INTO flex_role (
    user_id,
    role_name,
    created_date,
    modified_date
) VALUES (
    1,
    'system_manager',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO flex_role (
    user_id,
    role_name,
    created_date,
    modified_date
) VALUES (
    1,
    'user',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO flex_user_role (
    user_id,
    role_id
) VALUES ( 
    1,
    1
);

INSERT INTO flex_site (
    site_name,
    context,
    private_site,
    spa_or_static,
    description,
    created_date,
    modified_date
) VALUES (
    'admin',
    '/site/admin/',
    true,
    true,
    'admin site',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert default guest site
INSERT INTO flex_site (
    site_name,
    context,
    private_site,
    spa_or_static,
    description,
    created_date,
    modified_date
) VALUES (
    'guest',
    '/',
    false,
    true,
    'guest site',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
); 

INSERT INTO flex_user_site (
    user_id,
    site_id
) VALUES (
    1,
    1
);

INSERT INTO flex_user_site (
    user_id,
    site_id
) VALUES (
    2,
    1
);

INSERT INTO flex_user_site (
    user_id,
    site_id
) VALUES (
    3,
    1
);

INSERT INTO flex_site_role (
    site_id,
    role_id
) VALUES (
    1,
    1
);

INSERT INTO flex_site_role (
    site_id,
    role_id
) VALUES (
    1,
    2
);

INSERT INTO flex_site_role (
    site_id,
    role_id
) VALUES (
    1,
    3
);

INSERT INTO flex_view_template (
    user_id,
    created_date,
    modified_date,
    site_id,
    template_name, 
    template_type,
    template_location,
    relative_path,
    content
) VALUES (
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1,
    'index',
    'html',
    'on_file',
    'index.html',
    NULL
);

INSERT INTO flex_view_template (
    user_id,
    created_date,
    modified_date,
    site_id,
    template_name, 
    template_type,
    template_location,
    relative_path,
    content
) VALUES (
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    2,
    'index',
    'html',
    'on_file',
    'index.html',
    NULL
);

INSERT INTO flex_header (
    user_id,
    created_date,
    modified_date,
    site_id,
    logo,
    slogan,
    include_script,
    include_style,
    include_content
) VALUES (
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    2,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
);

INSERT INTO flex_page (
    user_id,
    created_date,
    modified_date,
    site_id,
    parent_id,
    page_name,
    page_title,
    page_path,
    view_template_id,
    secure,
    visible,
    seq,
    include_content,
    include_script,
    include_style
) VALUES (
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1,
    NULL,
    'home',
    'Home',
    '/', 
    1,
    false,
    true,
    1,
    NULL,
    NULL,
    NULL
);

INSERT INTO flex_page (
    user_id,
    created_date,
    modified_date,
    site_id,
    parent_id,
    page_name,
    page_title,
    page_path,
    view_template_id,
    secure,
    visible,
    seq,
    include_content,
    include_script,
    include_style
) VALUES (
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    2,
    NULL,
    'home',
    'Home',
    '/', 
    2,
    false,
    true,
    1,
    NULL,
    NULL,
    NULL
);

INSERT INTO flex_footer (
    user_id,
    created_date,
    modified_date,
    site_id,
    logo,
    slogan,
    include_script,
    include_style,
    include_content
) VALUES (      
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    2,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
);

INSERT INTO flex_sys (
    created_date,
    modified_date,
    initialized
) VALUES (
    CURRENT_TIMESTAMP,  
    CURRENT_TIMESTAMP,
    true
);
