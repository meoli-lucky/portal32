package com.fds.flex.core.portal.action;

import com.fds.flex.core.portal.model.Page;
import com.fds.flex.core.portal.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PageAction {
    private final PageService pageService;

    public Flux<Page> findBySiteId(Long siteId) {
        return pageService.findBySiteId(siteId);
    }

    public Mono<Page> findById(Long id) {
        return pageService.findById(id);
    }

    public Mono<Page> create(Page page) {
        return validatePage(page)
                .flatMap(validatedPage -> pageService.save(validatedPage));
    }

    public Mono<Page> update(Long id, Page page) {
        page.setId(id);
        return validatePage(page)
                .flatMap(validatedPage -> pageService.save(validatedPage));
    }

    public Mono<Void> delete(Long id) {
        return pageService.delete(id);
    }

    private Mono<Page> validatePage(Page page) {
        if (page.getSiteId() == null) {
            return Mono.error(new IllegalArgumentException("Site ID is required"));
        }
        if (page.getPagePath() == null || page.getPagePath().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Path is required"));
        }
        return Mono.just(page);
    }
} 