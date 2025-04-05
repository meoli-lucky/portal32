package com.fds.flex.core.portal.model;

import lombok.Data;

@Data
public class DeploymentPath {

    private String fileSourcePathModule;
    private String fileSourcePathTheme;
    private String fileTargetPathModule;
    private String fileTargetPathTheme;
    private String fileSourcePathSite;

    public DeploymentPath(String fileSourcePathModule, String fileSourcePathTheme, String fileTargetPathModule,
                          String fileTargetPathTheme, String fileSourcePathSite){
        this.fileSourcePathModule = fileSourcePathModule;
        this.fileSourcePathTheme = fileSourcePathTheme;
        this.fileTargetPathModule = fileTargetPathModule;
        this.fileTargetPathTheme = fileTargetPathTheme;
        this.fileSourcePathSite = fileSourcePathSite;
    }
}
