package com.fds.flex.core.portal.gui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThemeModel {
	public String id;
	public String relativePath;
	public String imgFolder;
	public String jsFolder;
	public String cssFolder;
	public String fontFolder;
	public String[] templates;
}
