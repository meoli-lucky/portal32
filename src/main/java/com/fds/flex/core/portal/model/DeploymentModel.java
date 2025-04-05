package com.fds.flex.core.portal.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DeploymentModel {
	public String deployPath;
	public String targetPath;
	public String[] deployFolders = new String[] {"theme", "module", "site"};
	public String[] targetFolders;
}
