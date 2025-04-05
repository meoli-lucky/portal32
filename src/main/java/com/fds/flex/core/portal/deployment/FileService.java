package com.fds.flex.core.portal.deployment;

import com.fds.flex.core.portal.model.DeploymentPath;
import com.fds.flex.core.portal.util.DeploymentConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class FileService {

    private final DeploymentPath deploymentPath;

    @Autowired
    public FileService(DeploymentPath deploymentPath) {
        this.deploymentPath = deploymentPath;
    }

    @PostConstruct
    public void init() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(this::watchFolderModule);
        executorService.submit(this::watchFolderTheme);
        executorService.submit(this::watchFolderSite);
    }

    public void watchFolderModule() {
        WatchProcessFile doDepoy = new WatchProcessFile();
        log.info("Watching module: {}", deploymentPath.getFileSourcePathModule());
        doDepoy.watchFolder(deploymentPath.getFileSourcePathModule(), deploymentPath.getFileTargetPathModule(), DeploymentConstant.MODULE_DEPLOY_FOLDER);
    }

    public void watchFolderTheme() {
        WatchProcessFile doDepoy = new WatchProcessFile();
        log.info("Watching theme: {}", deploymentPath.getFileSourcePathTheme());
        doDepoy.watchFolder(deploymentPath.getFileSourcePathTheme(), deploymentPath.getFileTargetPathTheme(), DeploymentConstant.THEME_DEPLOY_FOLDER);
    }

    public void watchFolderSite() {
        WatchProcessFile doDepoy = new WatchProcessFile();
        log.info("Watching json file: {}", deploymentPath.getFileSourcePathSite());
        doDepoy.watchFolder(deploymentPath.getFileSourcePathSite(), null, DeploymentConstant.SITE_DEPLOY_FOLDER);
    }
}
