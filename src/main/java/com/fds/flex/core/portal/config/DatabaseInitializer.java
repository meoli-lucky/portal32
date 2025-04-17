package com.fds.flex.core.portal.config;

import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {
    private final DataSource dataSource;
    private final SiteService siteService;

    @PostConstruct
    public void init() {
        log.info("Checking default sites...");
        checkAndCreateDefaultSites().subscribe();
    }

    private Mono<Void> checkAndCreateDefaultSites() {
        List<Site> defaultSites = Arrays.asList(
            createAdminSite(),
            createGuestSite()
        );

        return Flux.fromIterable(defaultSites)
                .flatMap(site -> siteService.findByFriendlyURL(site.getContext())
                        .switchIfEmpty(Mono.defer(() -> executeScriptForSite(site).thenReturn(site))))
                .then();
    }

    private Mono<Site> executeScriptForSite(Site site) {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            String scriptPath = "db/migration/V1__create_" + site.getSiteName() + "_site.sql";
            populator.addScript(new ClassPathResource(scriptPath));
            populator.execute(dataSource);
            log.info("Created site {} successfully", site.getSiteName());
            return Mono.just(site);
        } catch (Exception e) {
            log.error("Error creating site {}: {}", site.getSiteName(), e.getMessage());
            return Mono.error(e);
        }
    }

    private Site createAdminSite() {
        Site site = new Site();
        site.setSiteName("admin");
        site.setContext("/site/admin/");
        site.setPrivateSite(true);
        site.setSpaOrStatic(true);
        site.setDescription("admin site");
        site.setRoles(Arrays.asList("admin", "mod"));
        return site;
    }

    private Site createGuestSite() {
        Site site = new Site();
        site.setSiteName("guest");
        site.setContext("/");
        site.setPrivateSite(false);
        site.setSpaOrStatic(true);
        site.setDescription("guest site");
        site.setRoles(List.of());
        return site;
    }
} 