package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.action.SiteAction;
import com.fds.flex.core.portal.model.Site;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteAction siteAction;

    @GetMapping("/{siteName}")
    public Mono<ResponseEntity<Site>> findBySiteName(@PathVariable String siteName) {
        return siteAction.findBySiteName(siteName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Site>> create(@RequestBody Site site) {
        return siteAction.create(site)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Site>> update(@PathVariable Long id, @RequestBody Site site) {
        site.setId(id);
        return siteAction.update(id, site)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return siteAction.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 