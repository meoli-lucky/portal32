package com.fds.flex.core.portal.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.common.ultility.string.StringUtil;
import com.fds.flex.core.portal.model.GatewayModel;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ApplicationPropertyConfig {

	@Autowired
	private ConfigurableEnvironment env;

	@Autowired
	private Environment environment;

	@Bean("applicationProperty")
	public Map<String, Object> applicationProperty() {
		PropKey.setKeyMap(new HashMap<>());
		for (PropertySource<?> propertySource : env.getPropertySources()) {
			if (propertySource instanceof EnumerablePropertySource && (propertySource.getName().contains(".properties")
					|| propertySource.getName().contains(".yml"))) {
				for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
					String value = environment.getProperty(key);
					log.info("key={}, value={}, origin={}", key, propertySource.getProperty(key), value);
					PropKey.getKeyMap().put(key, value);
					if (key.equals(PropKey.FLEXCORE_PORTAL_GATEWAY_CONFIG) && Validator.isNotNull(value)) {
						parseGatewayConfig(value);
					}

					if (key.equals(PropKey.FLEXCORE_PORTAL_GATEWAY_SECURE_EXCLUDE_PATHS)
							&& Validator.isNotNull(value)) {
						String[] paths = StringUtil.split(value);
						if (paths != null) {
							for (int i = 0; i < paths.length; i++) {
								PortalUtil._GATEWAY_EXCLUDE_PATHS.add(paths[i]);
							}
						}
					}
				}
			}
		}
		return PropKey.getKeyMap();
	}

	private void parseGatewayConfig(String gatewayConfig) {

		ObjectMapper mapper = new ObjectMapper();

		JsonNode nodes;

		try {
			nodes = mapper.readTree(gatewayConfig);

			nodes.forEach(node -> {

				String gatewayContext = node.get("gatewayContext").textValue();

				String endpoint = node.get("endpoint").textValue();

				List<String> resources = Arrays.asList(StringUtil.split(node.get("resources").textValue()));

				gatewayContext = gatewayContext.replace("/**", StringPool.BLANK);

				GatewayModel gatewayModel = GatewayModel.builder().endpoint(endpoint).gatewayContext(gatewayContext)
						.resources(resources).build();

				PortalUtil._GATEWAY_CONTEXT_MAP.put(gatewayContext, gatewayModel);

			});
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
