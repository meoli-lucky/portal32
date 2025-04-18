package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.fds.flex.core.portal.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository siteRepository;

    public Mono<Site> findById(Long id) {
        return siteRepository.findById(id);
    }

    public Mono<Site> save(Site site) {
        return siteRepository.save(site);
    }

    public Mono<Void> delete(Long id) {
        return siteRepository.deleteById(id);
    }

    public Mono<Page<Site>> filter(Long siteId, String keyword, int start, int limit) {
        if (start < 0) {
            start = 0;
        }
        if (limit < 1) {
            limit = 10;
        }
        if(limit > 100) {
            limit = 100;
        }
        Pageable pageable = PageRequest.of(start, limit);
        return siteRepository.filter(keyword, pageable)
                .collectList()
                .zipWith(siteRepository.countFilter(keyword))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
} 