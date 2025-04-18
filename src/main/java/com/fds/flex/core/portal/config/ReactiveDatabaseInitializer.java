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
            .sql("SELECT COUNT(*) AS cnt FROM information_schema.tables WHERE table_schema = 'public'")
            .map(row -> row.get("cnt", Long.class))
            .first()
            .flatMap(count -> {
                if (count != null && count == 0) {
                    log.info("No tables found, initializing database...");
                    return executeInitScript("db/migration/V1__create_default_site.sql");
                } else {
                    log.info("Database already initialized with {} tables", count);
                    return Mono.empty();
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