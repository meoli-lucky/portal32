package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository siteRepository;

    public Mono<Site> findBySiteName(String siteName) {
        return siteRepository.findBySiteName(siteName);
    }

    public Mono<Site> save(Site site) {
        return siteRepository.save(site);
    }

    public Mono<Void> delete(Long id) {
        return siteRepository.deleteById(id);
    }
} 