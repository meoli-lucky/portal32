package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Page;
import com.fds.flex.core.portal.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;

    public Flux<Page> findBySiteId(Long siteId) {
        return pageRepository.findBySiteId(siteId);
    }

    public Mono<Page> findByFriendlyURLAndSiteId(String friendlyURL, Long siteId) {
        return pageRepository.findByFriendlyURLAndSiteId(friendlyURL, siteId);
    }

    public Mono<Page> save(Page page) {
        return pageRepository.save(page);
    }

    public Mono<Void> delete(Long id) {
        return pageRepository.deleteById(id);
    }
} 