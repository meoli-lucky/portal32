package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Footer;
import com.fds.flex.core.portal.repository.FooterRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FooterService {
    @Autowired
    private final FooterRepository footerRepository;

    public Mono<Footer> findById(Long id) {
        return footerRepository.findById(id);
    }

    public Mono<Footer> save(Footer footer) {
        return footerRepository.save(footer);
    }

    public Mono<Void> delete(Long id) {
        return footerRepository.deleteById(id);
    }

    public Flux<Footer> findBySiteId(Long siteId) {
        return footerRepository.findBySiteId(siteId);
    }
} 