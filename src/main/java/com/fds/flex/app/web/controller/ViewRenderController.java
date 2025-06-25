package com.fds.flex.app.web.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.gui.model.DisplayModel;
import com.fds.flex.core.portal.gui.model.SiteModel;
import com.fds.flex.core.portal.model.Page;
import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.model.SiteDisplay;
import com.fds.flex.core.portal.model.ViewTemplate;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.security.CustomUserDetails;
import com.fds.flex.core.portal.util.PortalConstant;
import com.fds.flex.core.portal.util.PortalUtil;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class ViewRenderController {

	@Autowired
	DisplayBuilder displayBuilder;

	@Autowired
	PebbleEngine templateEngine;

	@Autowired
	CacheManager cacheManager;

	@GetMapping(value = { "/viewRender" })
	@ResponseBody
	public Mono<String> page(ServerWebExchange exchange) {
		String servletPath = exchange.getRequest().getPath().value();
		String originContextPath = exchange.getAttribute("originContextPath");
		//SiteModel site = exchange.getAttribute("site");
		Site site = exchange.getAttribute("site");

		log.info("servletPath: {}", servletPath);

		if (site == null) {
			log.warn("not found site");
			return Mono.just(render404());
		}
		String currentPagePath = originContextPath.replace(site.getContext(), "");
		if(currentPagePath.equals("")){
			currentPagePath = "/";
		}
		if (originContextPath.contains(site.getContext())) {
			SiteDisplay cachedSiteDisplay = cacheManager.getCache("siteDisplayCache").get(site.getId(), SiteDisplay.class);
			SiteDisplay siteDisplay = cachedSiteDisplay != null ? cachedSiteDisplay : SiteDisplay.build(site);
			
			//DisplayModel displayModel = site.getDisplayModelMap().get(originContextPath);

			// Khởi tạo với giá trị mặc định vì không còn sử dụng Spring Security MVC
			boolean isSignedIn = false;

			CustomUserDetails customUserDetails = new CustomUserDetails();

			//displayModel.setSignedIn(isSignedIn);
			//displayModel.setUserContext(customUserDetails);

			siteDisplay.setSignedIn(isSignedIn);
			siteDisplay.setUserContext(customUserDetails);

			/* if (Validator.isNotNull(customUserDetails)) {
				List<String> attribute = Arrays.asList("username", "email", "fullName", "roles");

				try {
					JSONObject jsonObject = new JSONObject(
							new ObjectMapper().writeValueAsString(displayModel.getUserContext()));
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
							&& Validator.isNotNull(displayModel.getUserContext().getProfiles().getJSONObject("CanBo")
									.getJSONObject("CoQuanChuQuan"))) {
						result.put("CoQuanChuQuan", displayModel.getUserContext().getProfiles().getJSONObject("CanBo")
								.getJSONObject("CoQuanChuQuan"));
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
			} */
			Page page = siteDisplay.getPageMap().get(currentPagePath);
			if (page == null) {
				log.warn("not found page: {}", servletPath);
				return Mono.just(render404());
			}

			ViewTemplate viewTemplate = siteDisplay.getViewTemplateMap().get(page.getViewTemplateId());

		
			String pageTemplate = viewTemplate.getTemplateLocation().equals("on_file") ? pageTemplate = "viewtemplates/" +  viewTemplate.getTemplateName() : "TODO Load from DB";
			

			/* if (GetterUtil.getBoolean(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_THEME_PRELOAD))) {
				String tmp = PortalConstant.WORK_FOLDER + StringPool.SLASH + PortalConstant.RUNTIME_FOLDER
						+ StringPool.SLASH + displayModel.getThemeId() + StringPool.SLASH + displayModel.getPageId();

				log.info("======>>> Tmp: {}", tmp);

				return Mono.fromCallable(() -> {
					try {
						PebbleTemplate template = templateEngine.getTemplate(tmp);
						StringWriter writer = new StringWriter();
						template.evaluate(writer, Map.of("display", displayModel));
						return writer.toString();
					} catch (IOException e) {
						log.error("Error rendering 404 page", e);
						return render404();
					}
				});
			} */

			log.info("======>>> Page: {}", page);

			return Mono.fromCallable(() -> {
				try {
					PebbleTemplate template = templateEngine.getTemplate(pageTemplate);
					StringWriter writer = new StringWriter();
					template.evaluate(writer, Map.of("display", siteDisplay));
					return writer.toString();
				} catch (IOException e) {
					log.error("Error rendering 404 page", e);
					return render404();
				}
			});
		} else {
			log.warn("not found resource: {}", servletPath);
			return Mono.just(render404());
		}
	}

	private String render404() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/404");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering 404 page", e);
			return "Page not found";
		}
	}
}
