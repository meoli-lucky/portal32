package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Header;
import com.fds.flex.core.portal.repository.HeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HeaderService {
    private final HeaderRepository headerRepository;

    public Mono<Header> findById(Long id) {
        return headerRepository.findById(id);
    }

    public Mono<Header> save(Header header) {
        return headerRepository.save(header);
    }

    public Mono<Void> delete(Long id) {
        return headerRepository.deleteById(id);
    }

    public Flux<Header> findBySiteId(Long siteId) {
        return headerRepository.findBySiteId(siteId);
    }
} 