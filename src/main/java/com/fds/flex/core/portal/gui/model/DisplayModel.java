package com.fds.flex.core.portal.gui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fds.flex.core.portal.security.CustomUserDetails;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)

//represents the look and feel of the page or navbar
public class DisplayModel {
	public String themeId;
	public String pageId;
	public String pageTitle;
	public String logo;
	public String slogan;
	public String footer;
	public String contextPath;
	public String fragment;
	public String themeResourcePath;
	public String themeImgFolderPath;
	public String themeJsFolderPath;
	public String themeCssFolderPath;
	public String themeFontFolderPath;
	public String moduleResourcePath;
	public String moduleImgFolderPath;
	public String moduleJsFolderPath;
	public String moduleCssFolderPath;
	public String templateName;
	public String templatePath;
	public String templateAbsolutePath;
	public String resourceAbsolutePath;
	public boolean isSignedIn;
	public String layoutStructure;
	public String configurations;
	public CustomUserDetails userContext;
	public String userContextDetail;
	
	//null if it's represents the look and feel of the page
	public DisplayNavModel currentNav;

	//all site' navs
	public List<DisplayNavModel> navs;
	public List<ModuleResourceModel.Resource> moduleJsResources;
	public List<ModuleResourceModel.Resource> moduleCssResources;
	public List<ModuleResourceModel.Resource> moduleImgResources;
	public List<ModuleResourceModel.ExtResources> moduleExtResources;


}
