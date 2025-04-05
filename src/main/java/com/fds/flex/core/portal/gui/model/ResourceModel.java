package com.fds.flex.core.portal.gui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceModel {

	@JsonProperty("resourcePatterns")
	public String resourcePatterns;
	
	@JsonProperty("themeFolder")
	public String themeFolder;
	
	@JsonProperty("moduleFolder")
	public String moduleFolder;

	@JsonProperty("denyContextPaths")
	public List<String> denyContextPaths;
}
