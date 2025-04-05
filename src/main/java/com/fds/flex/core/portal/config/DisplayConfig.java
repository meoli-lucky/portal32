package com.fds.flex.core.portal.config;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.gui.model.ModuleModel;
import com.fds.flex.core.portal.gui.model.ModuleResourceModel;
import com.fds.flex.core.portal.gui.model.ResourceModel;
import com.fds.flex.core.portal.gui.model.SiteModel;
import com.fds.flex.core.portal.gui.model.ThemeModel;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalConstant;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class DisplayConfig {

	@Bean
	@DependsOn("applicationProperty")
	DisplayBuilder initPortalDisplay() {

		String builderContent = StringPool.BLANK;

		ResourceModel resources = null;

		List<SiteModel> sites = new ArrayList<SiteModel>();

		List<ThemeModel> themes = new ArrayList<ThemeModel>();

		List<ModuleModel> modules = new ArrayList<ModuleModel>();

		String configurations = StringPool.BLANK;

		try {

			builderContent = new String(Files.readAllBytes(Paths.get(PortalUtil.remakeStaticResourceDir(
					GetterUtil.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_CONF_DIR)))
					+ GetterUtil.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_SITECONFIG_FILE)))));

			log.info("builderContent {}", builderContent);

			ObjectMapper mapper = new ObjectMapper();

			JsonNode nodes = mapper.readTree(builderContent);

			JsonNode resourceNodes = nodes.get("resources");

			JsonNode siteNodes = nodes.get("sites");

			JsonNode themeNodes = nodes.get("themes");

			JsonNode moduleNodes = nodes.get("modules");

			if(Validator.isNull(PortalUtil._EXTERNAL_PROPERTY_JSON)) {
				JsonNode configurationNode = nodes.get("configurations");
				configurations = configurationNode != null ? configurationNode.toString() : StringPool.BLANK;
			}else {
				configurations = PortalUtil._EXTERNAL_PROPERTY_JSON;
			}

			// parse resource node
			resources = mapper.readValue(resourceNodes.toString(), ResourceModel.class);

			if (resources != null) {

				final String resourceAbsolutePath = PortalUtil.remakeStaticResourceDir(
						GetterUtil.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR)));

				if (Validator.isNull(resourceAbsolutePath)) {
					return null;
				}

				final String runtimeDir = resourceAbsolutePath + PortalConstant.WORK_FOLDER + StringPool.SLASH
						+ PortalConstant.RUNTIME_FOLDER;

				File tmp = new File(runtimeDir);

				boolean isCreated = true;

				if (!tmp.exists()) {
					isCreated = tmp.mkdirs();
				}

				if (!isCreated) {
					return null;
				}

				final String fragmentDir = resourceAbsolutePath + PortalConstant.FRAGMENT_FOLDER;

				tmp = new File(fragmentDir);

				if (!tmp.exists()) {
					isCreated = tmp.mkdirs();
				}

				if (!isCreated) {
					return null;
				}

				moduleNodes.forEach(moduleNode -> {

					try {
						String moduleRelativePath = moduleNode.get("relativePath").textValue();

						String resourceConfigFile = moduleNode.get("resourceConfigFile").textValue();

						if (ObjectUtils.isEmpty(moduleRelativePath) || ObjectUtils.isEmpty(resourceConfigFile)) {
							return;
						}
						String resourceConfigFileDir = resourceAbsolutePath + moduleRelativePath + resourceConfigFile;

						File file = new File(resourceConfigFileDir);

						if (!file.exists() || !file.isFile()) {
							return;
						}

						JsonNode moduleResourceNode = mapper.readTree(file);

						ModuleResourceModel moduleResourceModel = mapper.readValue(moduleResourceNode.toString(),
								ModuleResourceModel.class);

						if (moduleResourceModel == null) {
							return;
						}

						ModuleModel module = mapper.readValue(moduleNode.toString(), ModuleModel.class);

						if (module == null) {
							return;
						}

						module.setCssFolder(moduleResourceModel.getStyleResource().getResourceFolder());
						switch (moduleResourceModel.getStyleResource().getImportType()) {
						case PortalConstant.MODULE_RESOURCE_IMPORT_AUTO:
							String resourceDir = resourceAbsolutePath + module.getRelativePath()
									+ moduleResourceModel.getStyleResource().getResourceFolder();
							File dir = new File(resourceDir);
							if (dir.isDirectory() && dir.exists()) {

								String[] files = dir.list(new FilenameFilter() {

									@Override
									public boolean accept(File dir, String name) {
										return (name.substring(name.lastIndexOf('.') + 1).equals("css")
												|| name.substring(name.lastIndexOf('.') + 1).equals("scss"));
									}
								});
								List<ModuleResourceModel.Resource> resouces = new ArrayList<ModuleResourceModel.Resource>();
								for (String fileName : files) {
									ModuleResourceModel.Resource resouce = new ModuleResourceModel.Resource();
									resouce.setAttributes("");
									resouce.setFileName(fileName);
									resouces.add(resouce);
								}
								module.setCssResources(resouces);
							}
							break;
						case PortalConstant.MODULE_RESOURCE_IMPORT_MANUAL:
							module.setCssResources(moduleResourceModel.getStyleResource().getResources());
							break;
						default:
							break;
						}

						module.setImgFolder(moduleResourceModel.getImageResource().getResourceFolder());
						switch (moduleResourceModel.getImageResource().getImportType()) {
						case PortalConstant.MODULE_RESOURCE_IMPORT_AUTO:
							String resourceDir = resourceAbsolutePath + module.getRelativePath()
									+ moduleResourceModel.getImageResource().getResourceFolder();
							File dir = new File(resourceDir);
							if (dir.isDirectory() && dir.exists()) {

								String[] files = dir.list(new FilenameFilter() {

									@Override
									public boolean accept(File dir, String name) {

										return (name.substring(name.lastIndexOf('.') + 1).equals("jpeg")
												|| name.substring(name.lastIndexOf('.') + 1).equals("jpg")
												|| name.substring(name.lastIndexOf('.') + 1).equals("png")
												|| name.substring(name.lastIndexOf('.') + 1).equals("ico")
												|| name.substring(name.lastIndexOf('.') + 1).equals("bitmap"));
									}
								});
								List<ModuleResourceModel.Resource> resouces = new ArrayList<ModuleResourceModel.Resource>();
								for (String fileName : files) {
									ModuleResourceModel.Resource resouce = new ModuleResourceModel.Resource();
									resouce.setAttributes("");
									resouce.setFileName(fileName);
									resouces.add(resouce);
								}
								module.setImgResources(resouces);
							}
							break;
						case PortalConstant.MODULE_RESOURCE_IMPORT_MANUAL:
							module.setImgResources(moduleResourceModel.getImageResource().getResources());
							break;
						default:
							break;
						}

						module.setJsFolder(moduleResourceModel.getScriptResource().getResourceFolder());
						switch (moduleResourceModel.getScriptResource().getImportType()) {
						case PortalConstant.MODULE_RESOURCE_IMPORT_AUTO:
							String resourceDir = resourceAbsolutePath + module.getRelativePath()
									+ moduleResourceModel.getScriptResource().getResourceFolder();
							File dir = new File(resourceDir);
							if (dir.isDirectory() && dir.exists()) {

								String[] files = dir.list(new FilenameFilter() {

									@Override
									public boolean accept(File dir, String name) {

										return name.substring(name.lastIndexOf('.') + 1).equals("js");
									}
								});
								List<ModuleResourceModel.Resource> resouces = new ArrayList<ModuleResourceModel.Resource>();
								for (String fileName : files) {
									ModuleResourceModel.Resource resouce = new ModuleResourceModel.Resource();
									resouce.setAttributes("");
									resouce.setFileName(fileName);
									resouces.add(resouce);
								}
								module.setJsResources(resouces);
							}
							break;
						case PortalConstant.MODULE_RESOURCE_IMPORT_MANUAL:
							module.setJsResources(moduleResourceModel.getScriptResource().getResources());
							break;
						default:
							break;
						}
						module.setExtResources(moduleResourceModel.getExtResources());
						module.setRelativePath(moduleRelativePath);
						modules.add(module);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				});

				siteNodes.forEach(siteNode -> {

					try {
						SiteModel site = mapper.readValue(siteNode.toString(), SiteModel.class);

						if (site == null) {
							return;
						}

						log.info("site {}", site.getId());
						sites.add(site);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				});

				themeNodes.forEach(themeNode -> {
					ThemeModel theme = null;
					try {
						theme = mapper.readValue(themeNode.toString(), ThemeModel.class);

						if (theme == null) {
							return;
						}

						log.info("theme {}", theme.getId());
						themes.add(theme);

					} catch (Exception e) {
						log.error(e.getMessage());
					}
				});
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return new DisplayBuilder(resources, themes, modules, configurations, sites);
	}
}
