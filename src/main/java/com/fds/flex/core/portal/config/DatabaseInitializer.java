package com.fds.flex.core.portal.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {
    private final DataSource dataSource;

    @PostConstruct
    public void init() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Kiểm tra xem có bảng nào không
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'");
            rs.next();
            int tableCount = rs.getInt(1);
            
            if (tableCount == 0) {
                log.info("No tables found, initializing database...");
                executeScript("db/migration/V1__create_tables.sql");
                log.info("Database initialized successfully");
            } else {
                log.info("Database already initialized with {} tables", tableCount);
            }
        } catch (Exception e) {
            log.error("Error initializing database: {}", e.getMessage());
        }
    }

    private void executeScript(String scriptPath) {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource(scriptPath));
            populator.execute(dataSource);
        } catch (Exception e) {
            log.error("Error executing script {}: {}", scriptPath, e.getMessage());
            throw new RuntimeException("Failed to execute database script", e);
        }
    }
} 