package com.fds.flex.core.portal.action;

import com.fds.flex.core.portal.model.Navbar;
import com.fds.flex.core.portal.service.NavbarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NavbarAction {
    private final NavbarService navbarService;
 
    public Mono<Navbar> create(Navbar navbar) {
        return validateNavbar(navbar)
                .flatMap(validatedNavbar -> navbarService.save(validatedNavbar));
    }

    public Mono<Navbar> findById(Long id) {
        return navbarService.findById(id)
                .flatMap(navbar -> {
                    // Thêm logic xử lý trước khi trả về navbar
                    return Mono.just(navbar);
                });
    }

    public Mono<Navbar> update(Long id, Navbar navbar) {
        navbar.setId(id);
        return validateNavbar(navbar)
                .flatMap(validatedNavbar -> navbarService.save(validatedNavbar));
    }

    public Mono<Void> delete(Long id) {
        return navbarService.delete(id);
    }

    public Flux<Navbar> findBySiteId(Long siteId) {
        return navbarService.findBySiteId(siteId)
                .flatMap(navbar -> {
                    // Thêm logic xử lý trước khi trả về navbar
                    return Mono.just(navbar);
                });
    }

    private Mono<Navbar> validateNavbar(Navbar navbar) {
        // Thêm logic validate navbar
        if (navbar.getSiteId() == null) {
            return Mono.error(new IllegalArgumentException("Site ID is required"));
        }
        if (navbar.getName() == null || navbar.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Name is required"));
        }
        return Mono.just(navbar);
    }
} 