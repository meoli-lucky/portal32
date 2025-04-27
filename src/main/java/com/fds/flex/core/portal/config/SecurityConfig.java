package com.fds.flex.core.portal.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

//import com.fds.flex.core.portal.filter.ContextPathWebFilter;
//import com.fds.flex.core.portal.filter.GatewayPathWebFilter;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfig {

	@Autowired
	DisplayBuilder displayBuilder;

	private final ReactiveUserDetailsService userDetailsService;

    public SecurityConfig(ReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

	
	
	//@Autowired
	//GatewayPathWebFilter gatewayPathFilter;
	
	//@Autowired
	//ContextPathWebFilter contextPathFilter;

	/* @Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.disable())
			.authorizeExchange(exchanges -> exchanges
				.anyExchange().permitAll()
			);
		
		displayBuilder.getSiteMap().forEach((id, site) -> {
			try {
				// Cấu hình cho các đường dẫn bảo mật của site
				http.authorizeExchange(exchanges -> exchanges
					.pathMatchers(site.getSecureSitePaths().toArray(new String[0]))
					.authenticated()
				);
				
				// Cấu hình role cho từng path
				site.getSecureSitePaths().forEach(p -> {
					try {
						List<String> roles = site.getRoleMap() != null ? 
							site.getRoleMap().get(p) : new ArrayList<String>();
							
						http.authorizeExchange(exchanges -> exchanges
							.pathMatchers(p)
							.hasAnyRole(roles.toArray(new String[0]))
						);
					} catch (Exception e) {
						log.error("Error configuring security for path: " + p, e);
					}
				});
			} catch (Exception e) {
				log.error("Error configuring security for site: " + id, e);
			}
		});
		
		// Cấu hình Gateway contexts
		List<String> gatewayContexts = new ArrayList<String>();
		PortalUtil._GATEWAY_CONTEXT_MAP.forEach((k, v) -> {
			gatewayContexts.add(k + "/**");
		});
		
		try {
			http.authorizeExchange(exchanges -> exchanges
				.pathMatchers(gatewayContexts.toArray(new String[0]))
				.authenticated()
			);
			
			PortalUtil._GATEWAY_CONTEXT_MAP.forEach((k, v) -> {
				http.addFilterAt(gatewayPathFilter, SecurityWebFiltersOrder.AUTHENTICATION);
			});
		} catch (Exception e) {
			log.error("Error configuring gateway contexts", e);
		}
		
		// Cấu hình Deny paths
		if (displayBuilder.getResources().getDenyContextPaths() != null
				&& !displayBuilder.getResources().getDenyContextPaths().isEmpty()) {
			displayBuilder.getResources().getDenyContextPaths().forEach(r -> {
				try {
					http.authorizeExchange(exchanges -> exchanges
						.pathMatchers(r)
						.denyAll()
					);
				} catch (Exception e) {
					log.error("Error configuring deny path: " + r, e);
				}
			});
		}
		
		// Thêm context path filter vào chuỗi filter
		http.addFilterAt(contextPathFilter, SecurityWebFiltersOrder.HTTP_BASIC);
		
		return http.build();
	} */
}
