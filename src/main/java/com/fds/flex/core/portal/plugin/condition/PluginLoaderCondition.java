package com.fds.flex.core.portal.plugin.condition;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.MethodMetadata;

import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.string.StringUtil;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalUtil;

public class PluginLoaderCondition implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		MethodMetadata classMetadata = (MethodMetadata) metadata;
		List<String> services = Arrays.asList(StringUtil.split(
				GetterUtil.getString(context.getEnvironment().getProperty(PropKey.FLEXCORE_PORTAL_PLUGIN_SERVICES))));

		try {
			String className = classMetadata.getReturnTypeName();
			System.out.println("Service Load -------------------- " + className);
			Class.forName(className);
			if (services.contains(className)) {
				PortalUtil._PLUGIN_AVAILABLE_SERVICES.add(className);
			}

			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
