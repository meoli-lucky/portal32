package com.fds.flex.core.portal.action;

import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SiteAction {
    private final SiteService siteService;

    public Mono<Site> findBySiteName(String friendlyURL) {
        return siteService.findBySiteName(friendlyURL)
                .flatMap(site -> {
                    // Thêm logic xử lý trước khi trả về site
                    return Mono.just(site);
                });
    }

    public Mono<Site> create(Site site) {
        // Validate và xử lý dữ liệu trước khi lưu
        return validateSite(site)
                .flatMap(validatedSite -> siteService.save(validatedSite));
    }

    public Mono<Site> update(Long id, Site site) {
        site.setId(id);
        return validateSite(site)
                .flatMap(validatedSite -> siteService.save(validatedSite));
    }

    public Mono<Void> delete(Long id) {
        return siteService.delete(id);
    }

    private Mono<Site> validateSite(Site site) {
        // Thêm logic validate site
        if (site.getContext() == null || site.getContext().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Context is required"));
        }
        return Mono.just(site);
    }
} 