package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.action.PageAction;
import com.fds.flex.core.portal.model.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/pages")
@RequiredArgsConstructor
public class PageController {
    private final PageAction pageAction;

    @GetMapping("/site/{siteId}")
    public Flux<Page> findBySiteId(@PathVariable Long siteId) {
        return pageAction.findBySiteId(siteId);
    }

    @GetMapping("/{id}")
    public Mono<Page> findById(@PathVariable Long id) {
        return pageAction.findById(id);
    }

    @PostMapping
    public Mono<Page> create(@RequestBody Page page) {
        return pageAction.create(page);
    }

    @PutMapping("/{id}")
    public Mono<Page> update(@PathVariable Long id, @RequestBody Page page) {
        return pageAction.update(id, page);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return pageAction.delete(id);
    }
} 