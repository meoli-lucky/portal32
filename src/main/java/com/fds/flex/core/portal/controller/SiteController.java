package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;

    @GetMapping("/{friendlyURL}")
    public Mono<ResponseEntity<Site>> findByFriendlyURL(@PathVariable String friendlyURL) {
        return siteService.findByFriendlyURL(friendlyURL)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Site>> create(@RequestBody Site site) {
        return siteService.save(site)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Site>> update(@PathVariable Long id, @RequestBody Site site) {
        site.setId(id);
        return siteService.save(site)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return siteService.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 