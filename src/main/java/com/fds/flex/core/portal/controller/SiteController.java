package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.action.SiteAction;
import com.fds.flex.core.portal.model.Site;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteAction siteAction;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Site>> findById(@PathVariable Long id) {
        return siteAction.findById(id)
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

    @GetMapping("/filter")
    public Mono<ResponseEntity<Page<Site>>> filter(@RequestParam(required = false) String keyword, @RequestParam(required = false) Long siteId, @RequestParam(required = false) Integer start, @RequestParam(required = false) Integer limit) {
        return siteAction.filter(siteId, keyword, start, limit)
                .map(ResponseEntity::ok);
    }
} 