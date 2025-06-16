package com.fds.flex.core.portal.action;

import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.service.SiteService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SiteAction {
    private final SiteService siteService;

    public Mono<Site> create(Site site) {
        // Validate và xử lý dữ liệu trước khi lưu
        return validateSite(site, "create")
                .flatMap(validatedSite -> siteService.save(validatedSite));
    }

    public Mono<Site> findById(Long id) {
        return siteService.findById(id)
                .flatMap(site -> {
                    return Mono.just(site);
                });
    }

    public Mono<Site> update(Long id, Site site) {
        site.setId(id);
        site.setModifiedDate(LocalDateTime.now());
        return validateSite(site, "update")
                .flatMap(validatedSite -> siteService.save(validatedSite));
    }

    public Mono<Void> delete(Long id) {
        return siteService.delete(id);
    }

    public Mono<Page<Site>> filter(Long siteId, String keyword, int start, int limit) {
        return siteService.filter(siteId, keyword, start, limit);
    }

    private Mono<Site> validateSite(Site site, String action) {
        // Thêm logic validate site
        if (action.equals("create")) {
            if (site.getContext() == null || site.getContext().isEmpty()) {
                return Mono.error(new IllegalArgumentException("Context is required"));
            }else if(site.getSiteName() == null || site.getSiteName().isEmpty()){
                return Mono.error(new IllegalArgumentException("Site name is required"));
            }
        }
        return Mono.just(site);
    }
} 