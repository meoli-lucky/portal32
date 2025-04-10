package com.fds.flex.core.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import com.fds.flex.core.portal.security.ReactiveCustomAuthentication;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.config.JWTIssuersConfig;
import com.fds.flex.core.portal.gui.model.SiteModel;
import com.fds.flex.core.portal.model.GatewayModel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class PortalUtil {
	public static JWTIssuersConfig jwtIssuersConfig = null;

	public static SSLContext sslContext = null;
	public static ReactiveCustomAuthentication customAuthentication = null;

	public final static String _CONTEXT_PATH_PATTEN = "(^/)[a-z0-9-/_]+";

	public static Map<String, SiteModel> _SITE_CONTEXT_MAP = new LinkedHashMap<>();

	public static Map<String, GatewayModel> _GATEWAY_CONTEXT_MAP = new LinkedHashMap<>();

	public static Map<String, String> _EXTERNAL_PROPERTY_MAP = new HashMap<>();
	
	public static String _EXTERNAL_PROPERTY_JSON = null;

	public static List<String> _GATEWAY_EXCLUDE_PATHS = new ArrayList<String>();

	public static List<String> _PLUGIN_AVAILABLE_SERVICES = new ArrayList<String>();

	public static String createErrorResponseMessage(String error, int status) {

		JSONObject msgObject = new JSONObject();

		msgObject.put(PortalConstant.TIMESTAMP, System.currentTimeMillis());

		msgObject.put(PortalConstant.STATUS, status);

		msgObject.put(PortalConstant.ERROR, error);

		return msgObject.toString();
	}

	public static String getContextPathFromRequestUri(String requestUri) {

		Pattern pattern = Pattern.compile(_CONTEXT_PATH_PATTEN, Pattern.CASE_INSENSITIVE);

		final Matcher matcher = pattern.matcher(requestUri);

		if (matcher.find()) {
			return removeEndSlash(matcher.group());
		}

		return removeEndSlash(requestUri);
	}

	public static boolean matchContextPaths(String ctx, List<String> patterns) {

		boolean result = false;

		if (Validator.isNull(ctx) || Validator.isNull(patterns)) {
			return result;
		}

		for (String pattern : patterns) {
			if (matchContextPaths(ctx, pattern)) {

				result = true;
				break;
			}
		}

		return result;
	}

	public static boolean matchContextPaths(String ctx, Set<String> patterns) {

		boolean result = false;

		if (Validator.isNull(ctx) || Validator.isNull(patterns)) {
			return result;
		}

		for (String pattern : patterns) {
			if (matchContextPaths(ctx, pattern)) {

				result = true;

				break;
			}
		}

		return result;
	}

	public static boolean matchContextPaths(String ctx, String pattern) {
		if (Validator.isNull(ctx) || Validator.isNull(pattern) || !String.valueOf(pattern.charAt(0)).equals("/")) {
			return false;
		}

		if (pattern.contains("/**")) {
			pattern = pattern.substring(0, pattern.indexOf("/**"));
		}

		if (ctx.contains(pattern)) {
			return true;
		}

		return false;
	}

	public static List<String> remakeStaticResourceDir(List<String> dirs) {
		dirs.forEach(dir -> {
			int index = dirs.indexOf(dir);
			dir = remakeStaticResourceDir(dir);
			dirs.set(index, dir);
		});

		return dirs;
	}

	public static String remakeStaticResourceDir(String dir) {
		if (dir.endsWith(StringPool.SLASH)) {
			return dir;
		}

		return dir.concat(StringPool.SLASH);
	}

	public static String removeEndSlash(String s) {

		if (!s.equals("/") && s.lastIndexOf("/") == s.length() - 1) {
			s = s.substring(0, s.length() - 1);
		}

		return s;
	}

	public static String convertMapToJson(Map<String, Object> map) {
		String json = StringPool.BLANK;
		Map<String, Object> nestedMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String[] parts = entry.getKey().split("\\.");
			Map<String, Object> currentMap = nestedMap;
			for (int i = 0; i < parts.length - 1; i++) {
				currentMap = (Map<String, Object>) currentMap.computeIfAbsent(parts[i], k -> new HashMap<>());
			}
			currentMap.put(parts[parts.length - 1], entry.getValue());
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(nestedMap);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return json;
	}

	public static String findMatchingGatewayContext(String requestPath) {
		if (Validator.isNull(requestPath)) {
			return null;
		}

		for (String gatewayContext : _GATEWAY_CONTEXT_MAP.keySet()) {
			if (requestPath.startsWith(gatewayContext)) {
				return gatewayContext;
			}
		}

		return null;
	}

}
