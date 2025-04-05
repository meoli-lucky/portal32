package com.fds.flex.core.portal.gui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleModel {
	public String id;
	public String relativePath;
	public String resourceConfigFile;
	public String imgFolder;
	public String jsFolder;
	public String cssFolder;
	public List<ModuleResourceModel.Resource> jsResources;
	public List<ModuleResourceModel.Resource> cssResources;
	public List<ModuleResourceModel.Resource> imgResources;
	public List<ModuleResourceModel.ExtResources> extResources;
}
