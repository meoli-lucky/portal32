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