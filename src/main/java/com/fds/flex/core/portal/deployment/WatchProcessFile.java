package com.fds.flex.core.portal.deployment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.FileUtil;
import com.fds.flex.core.portal.util.PortalConstant;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

@Slf4j
public class WatchProcessFile {

	public void watchFolder(String deployDir, String installDir, String type) {
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Path pathModule = Paths.get(URI.create(deployDir));
			pathModule.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

			while (true) {
				WatchKey key = watchService.take();

				try {
					for (WatchEvent<?> event : key.pollEvents()) {
						if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
							Path deploymentFileRelativePath = (Path) event.context();
							Path deploymentFileAbsolutePath = pathModule
									.resolve(deploymentFileRelativePath.getFileName());

							// chờ file có thể đọc được mới được thêm vào thư mục
							waitForFileToBeReadable(deploymentFileAbsolutePath);

							String fileName = deploymentFileRelativePath.getFileName().toString();

							if (type.equals(PortalConstant.MODULE_DEPLOY_FOLDER)
									|| type.equals(PortalConstant.THEME_DEPLOY_FOLDER)) {
								Path installationDirAbsolutePath = Paths.get(URI.create(installDir)).toAbsolutePath();
								Path installationFileAbsolutePath = installationDirAbsolutePath.resolve(fileName);

								// Kiểm tra xem file mới có định dạng .zip không
								System.out.println(fileName.toLowerCase().endsWith(".zip"));
								if (!fileName.toLowerCase().endsWith(".zip")
										&& !fileName.toLowerCase().endsWith(".war")) {
									continue;
								}

								doDeploy(fileName, deploymentFileAbsolutePath, installationDirAbsolutePath,
										installationFileAbsolutePath);

								Path resourceFileAbsolutePath = installationDirAbsolutePath
										.resolve(fileName.replace(".zip", "").replace(".war", ""))
										.resolve("resources.json");

								updateSiteConfig(fileName.replace(".zip", "").replace(".war", ""),
										resourceFileAbsolutePath, type);

							} else if (type.equals(PortalConstant.SITE_DEPLOY_FOLDER)) {

								String siteConfigDir = GetterUtil
										.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_CONF_DIR))
										.concat(StringPool.SLASH).concat(GetterUtil.getString(
												PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_SITECONFIG_FILE)));

								Path siteConfigPath = Paths.get(siteConfigDir);

								FileUtil.moveFile(deploymentFileAbsolutePath, siteConfigPath);

								log.info("Move file success.");
							} else {
								return;
							}
						}
					}
				} catch (Exception e) {
					log.error("Event error: " + e.getMessage(), e);
					key.reset();
					continue;
				}
				key.reset();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	private void waitForFileToBeReadable(Path filePath) throws IOException {
		int count = 0;
		while (!Files.isReadable(filePath) && count < 10) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Thread interrupted while waiting: " + e.getMessage(), e);
				throw new IOException("Thread interrupted while waiting", e);
			}
			count++;
		}
		if (count >= 10) {
			throw new IOException();
		}
	}

	private void doDeploy(String fileName, Path deploymentFileAbsolutePath, Path installationDirAbsolutePath,
			Path installationFileAbsolutePath) throws IOException {
		try {
			// chuyển file zip từ thư mục nguồn sang thư mục đích
			FileUtil.copyFile(deploymentFileAbsolutePath, installationFileAbsolutePath);
			// xóa file cũ trong thư mục đích
			FileUtil.deleteFolder(
					new File(installationFileAbsolutePath.toString().replace(".zip", "").replace(".war", "")));
			// unzip file thư mục đích
			FileUtil.unZipFile(deploymentFileAbsolutePath.toString(), installationDirAbsolutePath.toString());
			// xóa file zip mới trong thư mục đích
			FileUtil.deleteZipFile(installationFileAbsolutePath.toString());
			// xóa file zip mới trong thư mục nguồn
			FileUtil.deleteZipFile(deploymentFileAbsolutePath.toString());
			// xóa file zip.error thư mục nguồn
			FileUtil.deleteZipFile(deploymentFileAbsolutePath.toString().concat(".error"));

			log.info("Deploy success.");
		} catch (IOException e) {
			// xóa file zip.error thư mục nguồn cũ
			FileUtil.deleteZipFile(deploymentFileAbsolutePath.toString().concat(".error"));
			// xóa file zip trong thư mục đích
			FileUtil.deleteZipFile(installationFileAbsolutePath.toString());
			// đổi tên file thành file lỗi trong thư mục nguồn
			FileUtil.renameZipFile(deploymentFileAbsolutePath.toString(), fileName + ".error");

			log.error("Deploy fails.");
			throw e;
		}
	}

	private void updateSiteConfig(String deploymentName, Path resourceFileAbsolutePath, String type)
			throws IOException {
		try {

			String siteConfigDir = GetterUtil.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_CONF_DIR))
					.concat(StringPool.SLASH)
					.concat(GetterUtil.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_SITECONFIG_FILE)));

			Path siteConfigPath = Paths.get(siteConfigDir);

			if (!Files.exists(resourceFileAbsolutePath)) {
				log.error("File resource not found, path: " + resourceFileAbsolutePath);
				return;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

			// Đọc dữ liệu từ file JSON ban đầu
			ObjectNode jsonInput = objectMapper.readValue(resourceFileAbsolutePath.toFile(), ObjectNode.class);

			// Lấy dữ liệu từ file JSON ban đầu map vào đối tượng mới
			ObjectNode jsonObject = objectMapper.createObjectNode();

			if (type.equals(PortalConstant.THEME_DEPLOY_FOLDER)) {
				jsonObject.put("id", deploymentName);
				jsonObject.put("relativePath", PortalConstant.THEME_INSTALLATION_FOLDER.concat(StringPool.SLASH)
						.concat(deploymentName).concat(StringPool.SLASH));
				jsonObject.put("imgFolder", jsonInput.get("imgFolder").asText());
				jsonObject.put("jsFolder", jsonInput.get("jsFolder").asText());
				jsonObject.put("fontFolder", jsonInput.get("fontFolder").asText());
				jsonObject.put("cssFolder", jsonInput.get("cssFolder").asText());
				jsonObject.set("templates", jsonInput.get("templates"));
			} else if (type.equals(PortalConstant.MODULE_DEPLOY_FOLDER)) {
				jsonObject.put("id", deploymentName);
				jsonObject.put("relativePath", PortalConstant.MODULE_INSTALLATION_FOLDER.concat(StringPool.SLASH)
						.concat(deploymentName).concat(StringPool.SLASH));
				jsonObject.put("resourceConfigFile", "resources.json");

			}
			// Ghi thêm dữ liệu mới vào file JSON đích
			File outputFile = siteConfigPath.toFile();
			JsonNode jsonData = objectMapper.readTree(siteConfigPath.toFile());
			if (jsonData == null) {
				log.error("Update sitebuilder fails: JSON data is null");
				return;
			}
			// Check dữ liệu thêm mới
			if (resourceFileAbsolutePath.toString().contains(PortalConstant.MODULE_INSTALLATION_FOLDER)) {
				if (jsonData.has(PortalConstant.MODULE_INSTALLATION_FOLDER)
						&& jsonData.get(PortalConstant.MODULE_INSTALLATION_FOLDER).isArray()) {
					List<String> idList = FileUtil
							.extractIdsFromJsonArray(jsonData.get(PortalConstant.MODULE_INSTALLATION_FOLDER));
					// check id
					if (idList.contains(deploymentName)) {
						FileUtil.removeElementFromJsonArrayById(
								(ArrayNode) jsonData.get(PortalConstant.MODULE_INSTALLATION_FOLDER),
								deploymentName);
					}
					((ArrayNode) jsonData.get(PortalConstant.MODULE_INSTALLATION_FOLDER)).add(jsonObject);
				} else {
					ArrayNode moduleTargetArray = objectMapper.createArrayNode().add(jsonObject);
					((ObjectNode) jsonData).set(PortalConstant.MODULE_INSTALLATION_FOLDER, moduleTargetArray);
				}
				objectMapper.writeValue(outputFile, jsonData);
			} else if (resourceFileAbsolutePath.toString().contains(PortalConstant.THEME_INSTALLATION_FOLDER)) {
				if (jsonData.has(PortalConstant.THEME_INSTALLATION_FOLDER)
						&& jsonData.get(PortalConstant.THEME_INSTALLATION_FOLDER).isArray()) {
					List<String> idList = FileUtil
							.extractIdsFromJsonArray(jsonData.get(PortalConstant.THEME_INSTALLATION_FOLDER));
					if (idList.contains(deploymentName)) {
						FileUtil.removeElementFromJsonArrayById(
								(ArrayNode) jsonData.get(PortalConstant.THEME_INSTALLATION_FOLDER), deploymentName);
					}
					((ArrayNode) jsonData.get(PortalConstant.THEME_INSTALLATION_FOLDER)).add(jsonObject);
				} else {
					ArrayNode themeTargetArray = objectMapper.createArrayNode().add(jsonObject);
					((ObjectNode) jsonData).set(PortalConstant.THEME_INSTALLATION_FOLDER, themeTargetArray);
				}
				objectMapper.writeValue(outputFile, jsonData);
			} else {
				return;
			}
			log.info("Update siteconfig success!");
		} catch (IOException e) {
			log.error("Update siteconfig fails!");
			throw new IOException(e);
		}
	}
}
