package com.fds.flex.core.portal.gui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.gui.model.DisplayModel;
import com.fds.flex.core.portal.gui.model.SiteModel;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.security.CustomUserDetails;
import com.fds.flex.core.portal.util.PortalConstant;
import com.fds.flex.core.portal.util.PortalUtil;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;

@Controller
@Slf4j
public class ViewRenderController {

	@Autowired
	DisplayBuilder displayBuilder;

	@Autowired
	PebbleEngine templateEngine;

	@GetMapping(value = {"/viewRender"})
	@ResponseBody
	public Mono<Rendering> page(ServerWebExchange exchange) {
		return Mono.defer(() -> {
			String servletPath = exchange.getRequest().getPath().value();
			String originContextPath = exchange.getAttribute("originContextPath");
			SiteModel site = exchange.getAttribute("site");

			log.info("servletPath: {}", servletPath);

			if (site == null) {
				log.warn("not found site");
				return Mono.just(Rendering.view("commons/404").build());
			}

			if (site.getSitePaths().contains(originContextPath)) {
				DisplayModel displayModel = site.getDisplayModelMap().get(originContextPath);

				// Khởi tạo với giá trị mặc định vì không còn sử dụng Spring Security MVC
				boolean isSignedIn = false;
				CustomUserDetails customUserDetails = new CustomUserDetails();

				displayModel.setSignedIn(isSignedIn);
				displayModel.setUserContext(customUserDetails);

				if (Validator.isNotNull(customUserDetails)) {
					List<String> attribute = Arrays.asList("username", "email", "fullName", "roles");

					try {
						JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(displayModel.getUserContext()));
						JSONObject result = new JSONObject();
						AtomicBoolean isSuperAdmin = new AtomicBoolean(false);
						attribute.forEach(e -> {
							result.put(e, jsonObject.get(e));
							if (e.equals("roles")) {
								JSONArray jsonArray = jsonObject.getJSONArray(e);
								for (int i = 0; i < jsonArray.length(); i++) {
									String role = jsonArray.getString(i);
									if (role.equalsIgnoreCase("superadmin")) {
										isSuperAdmin.set(true);
									}
								}
							}
						});
						result.put("isSuperAdmin", isSuperAdmin);

						if (Validator.isNotNull(displayModel.getUserContext().getProfiles())
								&& displayModel.getUserContext().getProfiles().has("CanBo")
								&& displayModel.getUserContext().getProfiles().getJSONObject("CanBo").has("CoQuanChuQuan")
								&& Validator.isNotNull(displayModel.getUserContext().getProfiles().getJSONObject("CanBo"))
								&& Validator.isNotNull(displayModel.getUserContext().getProfiles().getJSONObject("CanBo").getJSONObject("CoQuanChuQuan"))) {
							result.put("CoQuanChuQuan", displayModel.getUserContext().getProfiles().getJSONObject("CanBo").getJSONObject("CoQuanChuQuan"));
						}

						displayModel.setUserContextDetail(result.toString());
					} catch (Exception e) {
						log.error("Error while processing user details", e);
						displayModel.setUserContextDetail(StringPool.DOUBLE_QUOTE);
					}
				} else {
					displayModel.setUserContextDetail(StringPool.DOUBLE_QUOTE);
				}

				if (Validator.isNotNull(PortalUtil._EXTERNAL_PROPERTY_JSON)) {
					displayModel.setConfigurations(PortalUtil._EXTERNAL_PROPERTY_JSON);
				}

				String page = displayModel.getTemplatePath();

				if (GetterUtil.getBoolean(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_THEME_PRELOAD))) {
					String tmp = PortalConstant.WORK_FOLDER + StringPool.SLASH + PortalConstant.RUNTIME_FOLDER
							+ StringPool.SLASH + displayModel.getThemeId() + StringPool.SLASH + displayModel.getPageId();

					log.info("======>>> Tmp: {}", tmp);

					return Mono.just(Rendering.view(tmp).modelAttribute("display", displayModel).build());
				}

				log.info("======>>> Page: {}", page);

				// Sử dụng templateEngine để render template Pebble
				return Mono.fromCallable(() -> {
					PebbleTemplate template = templateEngine.getTemplate(page);
					StringWriter writer = new StringWriter();
					template.evaluate(writer, Map.of("display", displayModel));
					return writer.toString();
				}).map(html -> Rendering.view("pebble").modelAttribute("html", html).build());
			} else {
				log.warn("not found resource: {}", servletPath);
				return Mono.just(Rendering.view("commons/404").build());
			}
		});
	}
}
