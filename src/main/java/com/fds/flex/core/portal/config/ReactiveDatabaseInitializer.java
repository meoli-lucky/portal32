package com.fds.flex.core.portal.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveDatabaseInitializer {
    private final DatabaseClient databaseClient;

    @PostConstruct
    public void init() {
        databaseClient
            .sql("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'flex_sys') AS table_exists")
            .map(row -> row.get("table_exists", Boolean.class))
            .first()
            .flatMap(tableExists -> {
                if (Boolean.TRUE.equals(tableExists)) {
                    return databaseClient.sql("SELECT initialized FROM flex_sys ORDER BY created_date DESC LIMIT 1")
                        .map(row -> row.get("initialized", Boolean.class))
                        .first()
                        .flatMap(initialized -> {
                            if (Boolean.FALSE.equals(initialized)) {
                                log.info("Table flex_sys exists but not initialized, running script...");
                                return executeInitScript("db/migration/V1__create_default_site.sql");
                            } else {
                                log.info("Table flex_sys is already initialized.");
                                return Mono.empty();
                            }
                        });
                } else {
                    log.info("Table flex_sys does not exist, running script...");
                    return executeInitScript("db/migration/V1__create_default_site.sql");
                }
            })
            .onErrorResume(e -> {
                log.error("Error during DB initialization: {}", e.getMessage(), e);
                return Mono.empty();
            })
            .subscribe();
    }

    private Mono<Void> executeInitScript(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String sql = new BufferedReader(new InputStreamReader(resource.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Split câu lệnh theo dấu chấm phẩy (;) nếu có nhiều lệnh SQL
            String[] statements = sql.split(";");

            Mono<Void> result = Mono.empty();
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    result = result.then(databaseClient.sql(trimmed).then());
                }
            }

            return result;

        } catch (Exception e) {
            log.error("Failed to load SQL script {}: {}", path, e.getMessage(), e);
            return Mono.error(e);
        }
    }
} 