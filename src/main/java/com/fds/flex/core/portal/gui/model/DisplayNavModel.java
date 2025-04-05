package com.fds.flex.core.portal.gui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DisplayNavModel {
	public String name;
	public String href;
	public String target;
	public String css;
	public int level;
	public int seq;
	public boolean visible;
	public boolean isSecure;
	public boolean hasChild;
	public String parentId;
	public String parentName;
	public String parentHref;
	public boolean parentVisible;
	public List<DisplayNavModel> childrens;
}
