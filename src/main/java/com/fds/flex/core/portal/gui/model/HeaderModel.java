package com.fds.flex.core.portal.gui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeaderModel {
	public String logo;
	public String slogan;
}
