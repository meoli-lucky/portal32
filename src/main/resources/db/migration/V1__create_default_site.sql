-- Insert default admin site

CREATE TABLE user (
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

CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);


CREATE TABLE site (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_name VARCHAR(255) NOT NULL,
    context VARCHAR(255) NOT NULL,
    private_site BOOLEAN NOT NULL,
    spa_or_static BOOLEAN NOT NULL,
    description VARCHAR(255),
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE user_site (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL
);  

CREATE TABLE site_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);  

CREATE TABLE page (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
    site_id BIGINT NOT NULL,
    parent_id BIGINT,
    page_name VARCHAR(255) NOT NULL,
    page_title VARCHAR(255),
    page_path VARCHAR(255) NOT NULL,
    view_template_id BIGINT,
    secure BOOLEAN NOT NULL,
    seq INT NOT NULL,
    include_content VARCHAR(255),
    include_script VARCHAR(255),
    include_style VARCHAR(255)
);  

CREATE TABLE page_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    page_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);  

CREATE TABLE view_template (
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

CREATE TABLE header (
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

CREATE TABLE footer (
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

CREATE TABLE navbar (
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

-- Insert default admin site
INSERT INTO site (
    site_name,
    context,
    private_site,
    spa_or_static,
    description,
    roles,
    created_date,
    modified_date
) VALUES (
    'admin',
    '/site/admin/',
    true,
    true,
    'admin site',
    ARRAY['admin', 'mod'],
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert default guest site
INSERT INTO site (
    site_name,
    context,
    private_site,
    spa_or_static,
    description,
    roles,
    created_date,
    modified_date
) VALUES (
    'guest',
    '/',
    false,
    true,
    'guest site',
    ARRAY[]::text[],
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
); 