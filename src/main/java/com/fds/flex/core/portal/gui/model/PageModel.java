package com.fds.flex.core.portal.gui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageModel {
	public String id;
	public String siteId;
	public String name;
	public String title;
	public String path;
	public String target;
	public String moduleId;
	public String template;
	public String type;
	public String fragment;
	public boolean secure;
	public boolean moduleDirectly;
	public PageLayoutModel layout;
	public List<String> roles;
}
