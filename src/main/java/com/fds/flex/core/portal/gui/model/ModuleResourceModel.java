package com.fds.flex.core.portal.gui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleResourceModel {
	public ScriptResource scriptResource;
	public StyleResource styleResource;
	public ImageResource imageResource;
	public List<ExtResources> extResources;

	@Setter
	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class ScriptResource {
		String resourceFolder;
		String importType;// auto, manual, none
		List<Resource> resources;
	}

	@Setter
	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class StyleResource {
		String resourceFolder;
		String importType;// auto, manual, none
		List<Resource> resources;
	}

	@Setter
	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class ImageResource {
		String resourceFolder;
		String importType;// auto, manual, none
		List<Resource> resources;
	}

	@Setter
	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Resource {
		String fileName;
		String attributes;
	}

	@Setter
	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ExtResources {
		String resourceFolder;
		String type;//css,js,img
		String fileName;
		String attributes;
	}
}
