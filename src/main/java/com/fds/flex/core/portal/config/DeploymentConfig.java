package com.fds.flex.core.portal.config;

import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.model.DeploymentPath;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class DeploymentConfig {

	@Value("${flexcore.portal.deploy.location}")
	private String deployLocation;

	private final DisplayBuilder displayBuilder;

	@Autowired
	public DeploymentConfig(DisplayBuilder displayBuilder) {
		this.displayBuilder = displayBuilder;
	}

	@Bean
	public DeploymentPath deploymentPath() {

		String webResourceLocation = GetterUtil
				.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_LOCATION));

		return new DeploymentPath(createDir(deployLocation, PortalConstant.MODULE_DEPLOY_FOLDER),
				createDir(deployLocation, PortalConstant.THEME_DEPLOY_FOLDER),
				createDir(webResourceLocation, displayBuilder.getResources().moduleFolder),
				createDir(webResourceLocation, displayBuilder.getResources().themeFolder),
				createDir(deployLocation, PortalConstant.SITE_DEPLOY_FOLDER));
	}

	private String createDir(String baseDir, String folderName) {
		if (baseDir == null || folderName == null) {
			log.error("Invalid arguments. basePath or folderName is null.");
			return null;
		}
		String dir = (String.valueOf(baseDir.charAt(baseDir.length() - 1)).equals(StringPool.SLASH))
				? baseDir.concat(folderName)
				: baseDir.concat(StringPool.SLASH).concat(folderName);

		Path path = Paths.get(URI.create(dir));

		if (!Files.exists(path)) {
			try {
				// Tạo mới thư mục nếu nó chưa tồn tại
				Files.createDirectories(path);
				log.info("created a new folder: " + path);
			} catch (Exception e) {
				log.error("error creating folder: " + e.getMessage());
				return null;
			}
		}
		return dir;
	}
}
