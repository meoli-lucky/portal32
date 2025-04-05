package com.fds.flex.core.portal.gui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NavbarModel {
	public String id;
	public String parentId;
	public String pageId;
	public String name;
	public String href;
	public boolean followedParentHref;
	public String css;
	public boolean visible;
	public String target;
	public int level;
	public int seq;
	public boolean hasChild;
	public List<NavbarModel> childrens;
	public boolean secure;
	public List<String> roles;
}
