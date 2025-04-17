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

    public Flux<Navbar> findBySiteIdAndParentIdIsNull(Long siteId) {
        return navbarRepository.findBySiteIdAndParentIdIsNull(siteId)
                .flatMap(navbar -> {
                    return navbarRepository.findByParentId(navbar.getId())
                            .collectList()
                            .map(children -> {
                                navbar.setChildrens(children);
                                navbar.setHasChild(!children.isEmpty());
                                return navbar;
                            });
                });
    }

    public Mono<Navbar> save(Navbar navbar) {
        return navbarRepository.save(navbar);
    }

    public Mono<Void> delete(Long id) {
        return navbarRepository.deleteById(id);
    }
} 