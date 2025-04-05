package com.fds.flex.core.portal.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Configuration
public class GlobalEnvProperty {


	private final static String _PATTERN_1 = "([$][{][a-zA-Z0-9.]+[}])";
	public Environment env;
	public Map<String, Object> dvcqgSSOProps;
	public Map<String, Object> grpcProps;
	public Map<String, Object> cookieProps;
	public Map<String, Object> keycloakProps;
	public Map<String, Object> flexAuthProps;
	public Map<String, Object> integratedProps;
	public Map<String, Object> distributedProps;
	 Map<String, Object> dvcqgProps;

	public GlobalEnvProperty(Environment env) {

		this.env = env;
		dvcqgSSOProps = new HashMap<>();
		grpcProps = new HashMap<>();
		keycloakProps = new HashMap<>();
		flexAuthProps = new HashMap<>();

		if (env instanceof ConfigurableEnvironment) {
			for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
				if (propertySource instanceof EnumerablePropertySource) {
					for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
						if (key.contains("flexcore.portal.integrated.dvcqg.")) {
							dvcqgSSOProps.put(key, propertySource.getProperty(key));
						} else if (key.contains("flexcore.portal.integrated.grpc.")) {
							grpcProps.put(key, propertySource.getProperty(key));
						} else if (key.contains("flexcore.portal.integrated.keycloak.")) {
							keycloakProps.put(key, propertySource.getProperty(key));
						} else if (key.contains("flexcore.portal.integrated.flexauth.")) {
							flexAuthProps.put(key, propertySource.getProperty(key));
						} else if (key.contains("other keys here...")) {
						}
					}
				}
			}
		}
		System.out.println("DONE");
	}

	private String getPropValue() {
		return null;
	}

	private void validateConfig() {
		//TODO validate required properties
	}
}
