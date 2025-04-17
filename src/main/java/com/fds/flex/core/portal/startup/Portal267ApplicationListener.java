package com.fds.flex.core.portal.startup;

import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;

import com.fds.flex.core.portal.config.SSLContextConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.common.ultility.string.StringUtil;
import com.fds.flex.core.portal.config.JWTIssuersConfig;
//import com.fds.flex.core.portal.controller.ForwardController;
import com.fds.flex.core.portal.model.GatewayModel;
import com.fds.flex.core.portal.property.GlobalEnvProperty;
import com.fds.flex.core.portal.util.PortalUtil;


import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Portal267ApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${flexcore.portal.gateway.config}")
	String gatewayConfig;

	//@Autowired
	//ForwardController forwardController;

	@Autowired
	JWTIssuersConfig jwtIssuersConfig;

	//@Autowired
	//RouterFunction<ServerResponse> routerFunction;

	@Autowired
	SSLContext sslContext;

	@Autowired
	GlobalEnvProperty globalEnvProperty;

	private final DatabaseClient db;

	public Portal267ApplicationListener(DatabaseClient db) {
		this.db = db;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		db.sql("SELECT 1").fetch().first().subscribe();
		if (Validator.isNotNull(gatewayConfig)) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode nodes;
			try {
				nodes = mapper.readTree(gatewayConfig);

				nodes.forEach(node -> {

					String gatewayContext = node.get("gatewayContext").textValue();

					String endpoint = node.get("endpoint").textValue();

					List<String> resources = Arrays.asList(StringUtil.split(node.get("resources").textValue()));

					// Trong WebFlux không cần đăng ký route theo cách này
					// Routes được định nghĩa thông qua RouterFunction bean
					try {
						gatewayContext = gatewayContext.replace("/**", StringPool.BLANK);

						GatewayModel gatewayModel = GatewayModel.builder().endpoint(endpoint)
								.gatewayContext(gatewayContext).resources(resources).build();

						PortalUtil._GATEWAY_CONTEXT_MAP.put(gatewayContext, gatewayModel);

					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}

				});
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}

		}
		// Không nên gán giá trị cho biến static từ phương thức non-static
		// PortalUtil.sslContext = sslContext;
		// PortalUtil.customAuthentication = customAuthentication;

		log.info("ClassPath:===>>> {}", System.getProperty("java.class.path"));

	}
}
