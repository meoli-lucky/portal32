package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.model.Page;
import com.fds.flex.core.portal.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/pages")
@RequiredArgsConstructor
public class PageController {
    private final PageService pageService;

    @GetMapping("/site/{siteId}")
    public Flux<Page> findBySiteId(@PathVariable Long siteId) {
        return pageService.findBySiteId(siteId);
    }

    @GetMapping("/{friendlyURL}/site/{siteId}")
    public Mono<ResponseEntity<Page>> findByFriendlyURLAndSiteId(
            @PathVariable String friendlyURL,
            @PathVariable Long siteId) {
        return pageService.findByFriendlyURLAndSiteId(friendlyURL, siteId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Page>> create(@RequestBody Page page) {
        return pageService.save(page)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Page>> update(@PathVariable Long id, @RequestBody Page page) {
        page.setId(id);
        return pageService.save(page)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return pageService.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 