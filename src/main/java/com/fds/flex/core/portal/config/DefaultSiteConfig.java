package com.fds.flex.core.portal.config;

import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultSiteConfig {
    private final SiteService siteService;

    @PostConstruct
    public void init() {
        log.info("Checking default sites...");
        createDefaultSites().subscribe();
    }

    private Mono<Void> createDefaultSites() {
        List<Site> defaultSites = Arrays.asList(
            createAdminSite(),
            createGuestSite()
        );

        return Flux.fromIterable(defaultSites)
                .flatMap(site -> siteService.findByFriendlyURL(site.getContext())
                        .switchIfEmpty(Mono.defer(() -> {
                            log.info("Creating default site: {}", site.getSiteName());
                            return siteService.save(site);
                        })))
                .then();
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