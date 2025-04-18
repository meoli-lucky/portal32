package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Navbar;
import com.fds.flex.core.portal.repository.NavbarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NavbarService {
    private final NavbarRepository navbarRepository;

    public Flux<Navbar> findBySiteId(Long siteId) {
        return navbarRepository.findBySiteIdOrderByTree(siteId);
    }

    public Mono<Navbar> findById(Long id) {
        return navbarRepository.findById(id);
    }

    public Mono<Navbar> save(Navbar navbar) {
        return navbarRepository.save(navbar);
    }

    public Mono<Void> delete(Long id) {
        return navbarRepository.deleteById(id);
    }
} 