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
        return pageRepository.findBySiteIdOrderByTree(siteId);
    }

    public Mono<Page> findById(Long id) {
        return pageRepository.findById(id);
    }

    public Mono<Page> save(Page page) {
        return pageRepository.save(page);
    }

    public Mono<Void> delete(Long id) {
        return pageRepository.deleteById(id);
    }
} 