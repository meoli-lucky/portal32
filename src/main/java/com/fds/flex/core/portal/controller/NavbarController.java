package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.model.Navbar;
import com.fds.flex.core.portal.service.NavbarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/navbars")
@RequiredArgsConstructor
public class NavbarController {
    private final NavbarService navbarService;

    @GetMapping("/site/{siteId}")
    public Flux<Navbar> findBySiteId(@PathVariable Long siteId) {
        return navbarService.findBySiteIdAndParentIdIsNull(siteId);
    }

    @PostMapping
    public Mono<ResponseEntity<Navbar>> create(@RequestBody Navbar navbar) {
        return navbarService.save(navbar)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Navbar>> update(@PathVariable Long id, @RequestBody Navbar navbar) {
        navbar.setId(id);
        return navbarService.save(navbar)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return navbarService.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 