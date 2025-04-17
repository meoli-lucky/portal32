package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.model.Header;
import com.fds.flex.core.portal.service.HeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/headers")
@RequiredArgsConstructor
public class HeaderController {
    private final HeaderService headerService;

    @GetMapping("/site/{siteId}")
    public Mono<ResponseEntity<Header>> findBySiteId(@PathVariable Long siteId) {
        return headerService.findBySiteId(siteId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Header>> create(@RequestBody Header header) {
        return headerService.save(header)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Header>> update(@PathVariable Long id, @RequestBody Header header) {
        header.setId(id);
        return headerService.save(header)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return headerService.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 