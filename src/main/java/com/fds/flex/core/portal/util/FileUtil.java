package com.fds.flex.core.portal.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class FileUtil {

	public static void copyFile(Path sourceFilePath, Path destinationFilePath) throws IOException {
		// Di chuyển file từ thư mục nguồn sang thư mục đích
		Files.copy(sourceFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void moveFile(Path sourceFilePath, Path destinationFilePath) throws IOException {
		// Di chuyển file từ thư mục nguồn sang thư mục đích
		Files.move(sourceFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void unZipFile(String zipFilePath, String outputFolderPath) throws IOException {
		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
			byte[] buffer = new byte[1024];

			
			File folder = new File(outputFolderPath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null) {
				String fileName = zipEntry.getName();
				File newFile = new File(outputFolderPath + File.separator + fileName);

				if (zipEntry.isDirectory()) {
					newFile.mkdirs();
				} else {
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fileOutputStream = null;
					try {
						fileOutputStream = new FileOutputStream(newFile);
						int length;
						while ((length = zipInputStream.read(buffer)) > 0) {
							fileOutputStream.write(buffer, 0, length);
						}
						fileOutputStream.close();
					} catch (Exception e) {
						log.error(e.getMessage());
					} finally {
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
					}

				}
				zipEntry = zipInputStream.getNextEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.info("Unzip file fails: " + e.getMessage());
			throw new IOException(e);
		}
	}

	public static void deleteZipFile(String zipFilePath) {
		File zipFile = new File(zipFilePath);
		if (zipFile.exists()) {
			zipFile.delete();
		}
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					file.delete();
				}
			}
		}
		folder.delete();
	}

	public static void renameZipFile(String filePath, String newFileName) {
		File file = new File(filePath);
		String parentPath = file.getParent();
		File newFile = new File(parentPath, newFileName);
		file.renameTo(newFile);
	}

	// Phương thức để trích xuất danh sách ID từ một mảng JSON
	public static List<String> extractIdsFromJsonArray(JsonNode jsonArray) {
		List<String> idList = new ArrayList<>();

		// Lặp qua các phần tử trong mảng và lấy giá trị của trường "id"
		Iterator<JsonNode> elements = jsonArray.elements();
		while (elements.hasNext()) {
			JsonNode element = elements.next();
			if (element.has("id")) {
				idList.add(element.get("id").asText());
			}
		}
		return idList;
	}

	// Phương thức để xóa phần tử từ một mảng JSON dựa trên giá trị trường "id"
	public static void removeElementFromJsonArrayById(ArrayNode jsonArray, String idToRemove) {
		int indexToRemove = -1;
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonNode jsonNode = jsonArray.get(i);
			if (jsonNode.has("id") && jsonNode.get("id").asText().equals(idToRemove)) {
				indexToRemove = i;
				break; // Nếu tìm thấy và xóa, thoát khỏi vòng lặp
			}
		}
		if (indexToRemove != -1) {
			jsonArray.remove(indexToRemove);
		}
	}
}
