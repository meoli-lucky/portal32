package com.fds.flex.core.portal.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "flexcore.portal.integrated.securityconfig")
public class JWTIssuersConfig {
    private List<String> issuers;
}
