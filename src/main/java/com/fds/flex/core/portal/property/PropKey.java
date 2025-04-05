package com.fds.flex.core.portal.property;

import java.util.Map;

/**
 * @author vietdd
 */
public class PropKey {

    //Module Prop
    public static final String FLEXCORE_PORTAL_INTEGRATED_GRPC_MASTER_TOKEN = "flexcore.portal.integrated.grpc.master_token";
    public static final String FLEXCORE_PORTAL_INTEGRATED_GRPC_GATEWAY_HOST = "flexcore.portal.integrated.grpc.gateway.host";
    public static final String FLEXCORE_PORTAL_INTEGRATED_GRPC_GATEWAY_PORT = "flexcore.portal.integrated.grpc.gateway.port";
    public static final String FLEXCORE_PORTAL_HOME_DIR = "flexcore.portal.home.dir";
    public static final String FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR = "flexcore.portal.web.static-resource.dir";
    public static final String FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_LOCATION = "flexcore.portal.web.static-resource.location";
    public static final String FLEXCORE_PORTAL_OTHER_STATIC_RESOURCE_LOCATIONS = "flexcore.portal.other.static-resource.locations";
    public static final String FLEXCORE_PORTAL_CONF_DIR = "flexcore.portal.conf.dir";
    public static final String FLEXCORE_PORTAL_DEPLOY_LOCATION = "flexcore.portal.deploy.location";
    public static final String FLEXCORE_PORTAL_SITECONFIG_MODE = "flexcore.portal.siteconfig.mode";
    public static final String FLEXCORE_PORTAL_SITECONFIG_FILE = "flexcore.portal.siteconfig.file";
    public static final String FLEXCORE_PORTAL_TRUST_STORE_PATH = "flexcore.portal.trust.store.path";
    public static final String FLEXCORE_PORTAL_TRUST_STORE_PASSWORD = "flexcore.portal.trust.store.password";
    public static final String FLEXCORE_PORTAL_REQUEST_CONNECTION_TIMEOUT = "flexcore.portal.request.connection.timeout";
    public static final String FLEXCORE_PORTAL_REQUEST_READ_TIMEOUT = "flexcore.portal.request.read.timeout";
    public static final String FLEXCORE_PORTAL_REQUEST_SSL_NOOP_HOSTNAME_VERIFIER = "flexcore.portal.request.ssl.noop-hostname-verifier";
    public static final String FLEXCORE_PORTAL_REQUEST_CONTEXT_RE = "flexcore.portal.request.context.re";
    public static final String FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE = "flexcore.portal.distributed.cache.redis.enable";
    public static final String FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_HOST = "flexcore.portal.distributed.cache.redis.host";
    public static final String FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_PORT = "flexcore.portal.distributed.cache.redis.port";
    public static final String FLEXCORE_PORTAL_GATEWAY_SSL_ENABLE = "flexcore.portal.gateway.ssl.enable";
    public static final String FLEXCORE_PORTAL_GATEWAY_CONFIG = "flexcore.portal.gateway.config";
    public static final String FLEXCORE_PORTAL_GATEWAY_SECURE_EXCLUDE_PATHS = "flexcore.portal.gateway.secure.exclude_paths";
    public static final String FLEXCORE_PORTAL_GATEWAY_SECURE = "flexcore.portal.gateway.secure";
    public static final String FLEXCORE_PORTAL_WEB_THEME_PRELOAD = "flexcore.portal.web.theme.preload";
    public static final String FLEXCORE_PORTAL_PLUGIN_SERVICES = "flexcore.portal.plugin.services";

    //Local Authentication Prop
    public static final String FLEXCORE_PORTAL_INTEGRATED_FLEXAUTH_SSO_OAUTH2_TOKEN_URI = "flexcore.idpmgt.integrated.flexauth.sso.oauth2_token_uri";
    public static final String FLEXCORE_PORTAL_INTEGRATED_FLEXAUTH_SSO_OAUTH2_TOKEN_PARAMS_CLIENT_SECRET = "flexcore.idpmgt.integrated.flexauth.sso.oauth2_token_params.client_secret";
    public static final String FLEXCORE_PORTAL_INTEGRATED_FLEXAUTH_SSO_OAUTH2_TOKEN_PARAMS_CLIENT_ID = "flexcore.idpmgt.integrated.flexauth.sso.oauth2_token_params.client_id";
    public static final String FLEXCORE_PORTAL_INTEGRATED_FLEXAUTH_SSO_OAUTH2_TOKEN_PARAMS_GRANT_TYPE = "flexcore.idpmgt.integrated.flexauth.sso.oauth2_token_params.grant_type";
    public static final String FLEXCORE_PORTAL_INTEGRATED_FLEXCORE_PROFILE_URI = "flexcore.idpmgt.integrated.flexcore.profile_uri";


    //Redis Prop
    public static final String FLEXCORE_PORTAL_INTEGRATED_REDIS_HOST = "flexcore.idpmgt.integrated.redis.host";
    public static final String FLEXCORE_PORTAL_INTEGRATED_REDIS_PORT = "flexcore.idpmgt.integrated.redis.port";

    //DVCQG Prop
    public static final String FLEXCORE_PORTAL_INTEGRATED_DVCQG_SSO_OAUTH2_TOKEN_URI = "flexcore.idpmgt.integrated.dvcqg.sso.oauth2_token_uri";
    public static final String FLEXCORE_PORTAL_INTEGRATED_DVCQG_SSO_OAUTH2_TOKEN_PARAMS_REDIRECT_URI = "flexcore.idpmgt.integrated.dvcqg.sso.oauth2_token_params.redirect_uri";
    public static final String FLEXCORE_PORTAL_INTEGRATED_DVCQG_SSO_OAUTH2_TOKEN_PARAMS_GRANT_TYPE = "flexcore.idpmgt.integrated.dvcqg.sso.oauth2_token_params.grant_type";
    public static final String FLEXCORE_PORTAL_INTEGRATED_DVCQG_SSO_OAUTH2_TOKEN_PARAMS_CLIENT_ID = "flexcore.idpmgt.integrated.dvcqg.sso.oauth2_token_params.client_id";
    public static final String FLEXCORE_PORTAL_INTEGRATED_DVCQG_SSO_OAUTH2_TOKEN_PARAMS_CLIENT_SECRET = "flexcore.idpmgt.integrated.dvcqg.sso.oauth2_token_params.client_secret";

    //Keycloak Prop
    public static final String FLEXCORE_PORTAL_INTEGRATED_KEYCLOAK_SSO_OAUTH2_TOKEN_URI = "flexcore.portal.integrated.keycloak.sso.oauth2_token_uri";
    public static final String FLEXCORE_PORTAL_INTEGRATED_KEYCLOAK_SSO_OAUTH2_TOKEN_PARAMS_GRANT_TYPE = "flexcore.portal.integrated.keycloak.sso.oauth2_token_params.grant_type";
    public static final String FLEXCORE_PORTAL_INTEGRATED_KEYCLOAK_SSO_OAUTH2_TOKEN_PARAMS_CLIENT_ID = "flexcore.portal.integrated.keycloak.sso.oauth2_token_params.client_id";
    public static final String FLEXCORE_PORTAL_INTEGRATED_KEYCLOAK_SSO_OAUTH2_TOKEN_PARAMS_CLIENT_SECRET = "flexcore.portal.integrated.keycloak.sso.oauth2_token_params.client_secret";
    public static final String FLEXCORE_PORTAL_INTEGRATED_KEYCLOAK_SSO_OAUTH2_TOKEN_PARAMS_REDIRECT_URI = "flexcore.portal.integrated.keycloak.sso.oauth2_token_params.redirect_uri";
    public static final String FLEXCORE_PORTAL_INTEGRATED_OAUTH2_ISSUERS = "flexcore.portal.integrated.oauth2.issuers";
    public static final String FLEXCORE_PORTAL_CONF_PREFIX = "flexcore.portal.conf.prefix";
    public static final String FLEXCORE_PORTAL_INTEGRATED_JWKS_URI = "flexcore.portal.integrated.oauth2.jwks_uri";

	public static Map<String, Object> keyMap;

    public static Map<String, Object> getKeyMap() {
        return keyMap;
    }

    public static void setKeyMap(Map<String, Object> keyMap) {
        PropKey.keyMap = keyMap;
    }

}
