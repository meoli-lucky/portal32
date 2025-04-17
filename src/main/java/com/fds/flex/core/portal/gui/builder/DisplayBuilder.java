package com.fds.flex.core.portal.gui.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.comparator.NavbarComparator;
import com.fds.flex.core.portal.gui.model.DisplayModel;
import com.fds.flex.core.portal.gui.model.DisplayNavModel;
import com.fds.flex.core.portal.gui.model.ModuleModel;
import com.fds.flex.core.portal.gui.model.NavbarModel;
import com.fds.flex.core.portal.gui.model.PageModel;
import com.fds.flex.core.portal.gui.model.ResourceModel;
import com.fds.flex.core.portal.gui.model.SiteModel;
import com.fds.flex.core.portal.gui.model.ThemeModel;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalConstant;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class DisplayBuilder {
	// public HashMap<String, SiteModel> themeDisplays;

	public ResourceModel resources;

	public Map<String, SiteModel> siteMap;

	public Map<String, ThemeModel> themeMap;

	public Map<String, ModuleModel> moduleMap;

	public String configurations;

	// public List<DisplayModel.Nav> navs;

	public SiteModel root;

	public DisplayBuilder(ResourceModel resources, List<ThemeModel> themes, List<ModuleModel> modules,
			String configurations, List<SiteModel> sites) {

		this.setResources(resources);

		this.setConfigurations(configurations);

		init(themes, modules, sites);
	}

	private void init(List<ThemeModel> themes, List<ModuleModel> modules, List<SiteModel> sites) {

		siteMap = sites.stream().collect(Collectors.toMap(SiteModel::getId, Function.identity()));

		themeMap = themes.stream().collect(Collectors.toMap(ThemeModel::getId, Function.identity()));

		moduleMap = modules.stream().collect(Collectors.toMap(ModuleModel::getId, Function.identity()));

		sites.forEach(site -> {

			ThemeModel theme = themeMap.get(site.getThemeId());

			if (theme == null) {
				log.error("cannot get theme by id: {}", site.getThemeId());
				return;
			}

			List<DisplayNavModel> navs = buildDisplayNav(site, new ArrayList<>(), null, site.getNavbars());

			HashMap<String, DisplayNavModel> navMap = buildDisplayNavMap(new HashMap<>(), navs);

			// JSONArray array = new JSONArray(navs);

			// log.info("nav: {}", array.toString());

			// update site
			// path cua page va path cua nav ko duoc trung nhau -> sau lam chuc nang them
			// moi thi validate doan nay
			// currentNav chi build khi DisplayModel build ra tu path cua navbar
			site = updateSite(theme, site, site.getPages(), navs);

			site = updateSite(theme, site, null, site.getNavbars(), navs, navMap);
			// update sitemap
			siteMap.put(site.getId(), site);

			root = null;

			if (site.isRoot()) {
				root = site;
			}
		});

		this.setModuleMap(moduleMap);
		this.setRoot(root);
		this.setSiteMap(siteMap);
		this.setThemeMap(themeMap);

	}

	private SiteModel updateSite(ThemeModel theme, SiteModel site, List<PageModel> pages, List<DisplayNavModel> navs) {

		pages.forEach(page -> {
			String path = (site.getContext() + page.getPath()).replaceAll("//", "/");

			path = PortalUtil.getContextPathFromRequestUri(path);

			if (!site.getSitePaths().contains(path)) {
				site.getSitePaths().add(path);
				
				if (page.isSecure()) {
					site.getSecureSitePaths().add(path);
				}
				
				site.getRoleMap().put(path, page.getRoles());

				String moduleCssFolderPath = StringPool.BLANK;

				String moduleImgFolderPath = StringPool.BLANK;

				String moduleJsFolderPath = StringPool.BLANK;

				ModuleModel module = moduleMap.get(page.getModuleId());

				if (module != null) {

					moduleCssFolderPath = module.getRelativePath() + module.getCssFolder();

					if (moduleCssFolderPath.endsWith("/")) {
						moduleCssFolderPath = moduleCssFolderPath.substring(0, moduleCssFolderPath.length() - 1);
					}

					moduleImgFolderPath = module.getRelativePath() + module.getImgFolder();

					if (moduleImgFolderPath.endsWith("/")) {
						moduleImgFolderPath = moduleImgFolderPath.substring(0, moduleImgFolderPath.length() - 1);
					}

					moduleJsFolderPath = module.getRelativePath() + module.getJsFolder();

					if (moduleJsFolderPath.endsWith("/")) {
						moduleJsFolderPath = moduleJsFolderPath.substring(0, moduleJsFolderPath.length() - 1);
					}
				}

				DisplayModel displayModel = DisplayModel.builder().contextPath(site.getContext()).pageId(page.getId())
						.pageTitle(page.getTitle()).logo(site.getHeader().getLogo())
						.slogan(site.getHeader().getSlogan()).navs(navs).footer("").templateName(page.getTemplate())
						.fragment(page.getFragment())
						//.templatePath(page.isModuleDirectly() ? module.getRelativePath() + page.getTemplate(): theme.getRelativePath() + page.getTemplate())
						//.templateAbsolutePath(GetterUtil
						//		.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR))
						//		+ StringPool.SLASH + theme.getRelativePath() + page.getTemplate() + ".html")
						.templatePath(page.isModuleDirectly() ? module.getRelativePath() + page.getTemplate(): PortalConstant.SITE_INSTALLATION_FOLDER + "/" + theme.getId() + "/" + page.getTemplate())
						.templateAbsolutePath(GetterUtil
								.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR))
								+ StringPool.SLASH + PortalConstant.SITE_INSTALLATION_FOLDER + "/" + theme.getId() + "/" + page.getTemplate() + ".html")
						.resourceAbsolutePath(GetterUtil
								.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR))
								+ StringPool.SLASH)
						.moduleCssFolderPath(moduleCssFolderPath).moduleImgFolderPath(moduleImgFolderPath)
						.moduleJsFolderPath(moduleJsFolderPath)
						.moduleResourcePath(module != null ? module.getRelativePath() : StringPool.BLANK)
						.themeCssFolderPath(theme.getRelativePath() + theme.getCssFolder())
						.themeImgFolderPath(theme.getRelativePath() + theme.getImgFolder())
						.themeJsFolderPath(theme.getRelativePath() + theme.getJsFolder())
						.themeFontFolderPath(theme.getRelativePath() + theme.getFontFolder())
						.themeResourcePath(theme.getRelativePath()).themeId(theme.getId())
						.moduleJsResources(module != null ? module.getJsResources() : new ArrayList<>())
						.moduleCssResources(module != null ? module.getCssResources() : new ArrayList<>())
						.moduleImgResources(module != null ? module.getImgResources() : new ArrayList<>())
						.moduleExtResources(module != null ? module.getExtResources() : new ArrayList<>())
						.configurations(configurations).build();
				site.getDisplayModelMap().put(path, displayModel);

				// PortalUtil.siteContextMap.put((site.getContext() + path).replaceAll("//",
				// "/"), site);
				PortalUtil._SITE_CONTEXT_MAP.put(PortalUtil.getContextPathFromRequestUri((path).replaceAll("//", "/")),
						site);
			}

		});

		return site;
	}

	private SiteModel updateSite(ThemeModel theme, SiteModel site, NavbarModel parent, List<NavbarModel> navbarModels,
			List<DisplayNavModel> navs, HashMap<String, DisplayNavModel> navMap) {

		navbarModels.forEach(navbar -> {
			String navPath = ((parent != null && !ObjectUtils.isEmpty(parent.getHref())
					&& navbar.isFollowedParentHref()) ? (parent.getHref() + navbar.getHref()) : navbar.getHref());

			navPath = (site.getContext() + navPath).replaceAll("//", "/");

			navPath = PortalUtil.getContextPathFromRequestUri(navPath);

			if (!site.getSitePaths().contains(navPath) && !ObjectUtils.isEmpty(navPath)) {
				site.getSitePaths().add(navPath);
				if (navbar.isSecure()) {
					site.getSecureSitePaths().add(navPath);
				}

				PageModel page = site.getPage(navbar.getPageId());

				if (page == null) {
					log.warn("not found page with: pageId = {}, siteId = {}", navbar.getPageId(), site.getId());
					return;
				}

				ModuleModel module = moduleMap.get(page.getModuleId());

				String moduleCssFolderPath = StringPool.BLANK;

				String moduleImgFolderPath = StringPool.BLANK;

				String moduleJsFolderPath = StringPool.BLANK;

				if (module != null) {

					moduleCssFolderPath = module.getRelativePath() + module.getCssFolder();

					if (moduleCssFolderPath.endsWith("/")) {
						moduleCssFolderPath = moduleCssFolderPath.substring(0, moduleCssFolderPath.length() - 1);
					}

					moduleImgFolderPath = module.getRelativePath() + module.getImgFolder();

					if (moduleImgFolderPath.endsWith("/")) {
						moduleImgFolderPath = moduleImgFolderPath.substring(0, moduleImgFolderPath.length() - 1);
					}

					moduleJsFolderPath = module.getRelativePath() + module.getJsFolder();

					if (moduleJsFolderPath.endsWith("/")) {
						moduleJsFolderPath = moduleJsFolderPath.substring(0, moduleJsFolderPath.length() - 1);
					}
				}

				DisplayModel displayModel = DisplayModel.builder().contextPath(site.getContext()).pageId(page.getId())
						.pageTitle(page.getTitle()).logo(site.getHeader().getLogo())
						.slogan(site.getHeader().getSlogan()).navs(navs).fragment(page.getFragment())
						// .currentNav(getDisplayNav(navPath, navs))
						.currentNav(navMap.get(navPath)).footer("").templateName(page.getTemplate())
						//.templatePath(page.isModuleDirectly() ? module.getRelativePath() + page.getTemplate(): theme.getRelativePath() + page.getTemplate())
						//.templateAbsolutePath(GetterUtil
						//		.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR))
						//		+ StringPool.SLASH + theme.getRelativePath() + page.getTemplate() + ".html")
						.templatePath(page.isModuleDirectly() ? module.getRelativePath() + page.getTemplate(): PortalConstant.SITE_INSTALLATION_FOLDER + "/" + theme.getId() + "/" + page.getTemplate())
						.templateAbsolutePath(GetterUtil
								.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR))
								+ StringPool.SLASH + PortalConstant.SITE_INSTALLATION_FOLDER + "/" + theme.getId() + "/" + page.getTemplate() + ".html")
						.resourceAbsolutePath(GetterUtil
								.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR))
								+ StringPool.SLASH)
						.moduleCssFolderPath(moduleCssFolderPath).moduleImgFolderPath(moduleImgFolderPath)
						.moduleJsFolderPath(moduleJsFolderPath)
						.moduleResourcePath(module != null ? module.getRelativePath() : StringPool.BLANK)
						.themeCssFolderPath(theme.getRelativePath() + theme.getCssFolder())
						.themeImgFolderPath(theme.getRelativePath() + theme.getImgFolder())
						.themeJsFolderPath(theme.getRelativePath() + theme.getJsFolder())
						.themeFontFolderPath(theme.getRelativePath() + theme.getFontFolder())
						.themeResourcePath(theme.getRelativePath()).themeId(theme.getId())
						.moduleJsResources(module != null ? module.getJsResources() : new ArrayList<>())
						.moduleCssResources(module != null ? module.getCssResources() : new ArrayList<>())
						.moduleImgResources(module != null ? module.getImgResources() : new ArrayList<>())
						.moduleExtResources(module != null ? module.getExtResources() : new ArrayList<>())
						.configurations(configurations).build();

				site.getDisplayModelMap().put(navPath, displayModel);

				// PortalUtil.siteContextMap.put((site.getContext() + navPath).replaceAll("//",
				// "/"), site);
				// PortalUtil.siteContextMap.put((navPath).replaceAll("//", "/"), site);
				PortalUtil._SITE_CONTEXT_MAP.put(PortalUtil.getContextPathFromRequestUri((navPath).replaceAll("//", "/")),
						site);
			}

			if (navbar.isHasChild() && !navbar.getChildrens().isEmpty()) {
				updateSite(theme, site, navbar, navbar.getChildrens(), navs, navMap);
			}

		});

		return site;
	}

	private List<DisplayNavModel> buildDisplayNav(SiteModel site, List<DisplayNavModel> displayNavs, NavbarModel parent,
			List<NavbarModel> navbarModels) {

		if (navbarModels != null && !navbarModels.isEmpty()) {
			navbarModels.forEach(navbar -> {
				// if(navbar.isVisible()) {
				DisplayNavModel nav = DisplayNavModel.builder()
						// .parent(parent)
						.childrens(buildDisplayNav(site, new ArrayList<>(), navbar, navbar.getChildrens()))
						.hasChild(navbar.isHasChild())
						.href((navbar.isFollowedParentHref() && parent != null && Validator.isNotNull(parent.getHref())
								&& Validator.isNotNull(navbar.getHref()))
										? (site.getContext() + (parent.getHref() + navbar.getHref())).replaceAll("//",
												"/")
										: (site.getContext() + navbar.getHref()).replaceAll("//", "/"))
						.name(navbar.getName()).target(navbar.getTarget()).visible(navbar.isVisible())
						.css(navbar.getCss()).seq(navbar.getSeq()).level(navbar.getLevel())
						.parentHref(parent != null ? parent.getHref() : StringPool.BLANK)
						.parentId(parent != null ? parent.getId() : StringPool.BLANK)
						.parentName(parent != null ? parent.getName() : StringPool.BLANK)
						.parentVisible(parent != null ? parent.isVisible() : false).isSecure(navbar.isSecure()).build();

				displayNavs.add(nav);
				// }

			});
		}
		displayNavs.sort(new NavbarComparator());

		return displayNavs;
	}

	private HashMap<String, DisplayNavModel> buildDisplayNavMap(HashMap<String, DisplayNavModel> map,
			List<DisplayNavModel> navs) {

		for (DisplayNavModel nav : navs) {

			if (Validator.isNotNull(nav.getHref())) {

				map.put(nav.getHref(), nav);
			}

			if (nav.hasChild) {
				buildDisplayNavMap(map, nav.getChildrens());
			}

		}

		return map;
	}

	private DisplayNavModel getDisplayNav(String path, List<DisplayNavModel> navs) {

		for (DisplayNavModel nav : navs) {

			if (Validator.isNotNull(nav.getHref()) && nav.getHref().equals(path)) {

				return nav;
			}

			if (nav.hasChild) {
				return getDisplayNav(path, nav.getChildrens());
			}

		}

		return null;
	}

}
