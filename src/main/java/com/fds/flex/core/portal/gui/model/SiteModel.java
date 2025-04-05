package com.fds.flex.core.portal.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteModel {

	@JsonProperty("id")
	public String id;

	@JsonProperty("context")
	public String context;

	@JsonProperty("root")
	public boolean root;

	@JsonProperty("themeId")
	public String themeId;

	@JsonProperty("moduleIds")
	public List<String> moduleIds;

	@JsonProperty("pages")
	public List<PageModel> pages;

	@JsonProperty("navbars")
	public List<NavbarModel> navbars;

	@JsonProperty("header")
	public HeaderModel header;

	@JsonProperty("footer")
	public FooterModel footer;

	@JsonProperty("siteSecure")
	public String siteSecure;

	@JsonProperty("sitePaths")
	public List<String> sitePaths = new ArrayList<>();

	@JsonProperty("secureSitePaths")
	public List<String> secureSitePaths = new ArrayList<>();

	/**
	 * key: site paths(include page path, nav href) value: RoleName collections
	 */
	@JsonProperty("roleMap")
	public Map<String, List<String>> roleMap = new HashMap<>();

	/**
	 * key: site paths(include page path, nav href) value: DisplayModel
	 */
	@JsonProperty("displayModelMap")
	public Map<String, DisplayModel> displayModelMap = new LinkedHashMap<>();

	public PageModel getPage(String id) {
		PageModel p = null;
		if (getPages() == null) {
			return p;
		}
		for (PageModel page : getPages()) {
			if (page.getId().equals(id)) {
				p = page;
				break;
			}
		}

		return p;
	}

	public NavbarModel getNavbar(String id, List<NavbarModel> navbarModels) {
		NavbarModel n = null;
		if (navbarModels == null || navbarModels.isEmpty()) {
			return n;
		}

		for (NavbarModel nav : navbarModels) {
			if (nav.getId().equals(id)) {
				n = nav;
				break;
			}

			if (nav.isHasChild() && nav.getChildrens() != null && !nav.getChildrens().isEmpty()) {
				n = getNavbar(id, nav.getChildrens());
			}
		}

		return n;
	}
}
