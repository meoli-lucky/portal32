package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.model.Footer;
import com.fds.flex.core.portal.service.FooterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/footers")
@RequiredArgsConstructor
public class FooterController {
    private final FooterService footerService;

    @GetMapping("/site/{siteId}")
    public Mono<ResponseEntity<Footer>> findBySiteId(@PathVariable Long siteId) {
        return footerService.findBySiteId(siteId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Footer>> create(@RequestBody Footer footer) {
        return footerService.save(footer)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Footer>> update(@PathVariable Long id, @RequestBody Footer footer) {
        footer.setId(id);
        return footerService.save(footer)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return footerService.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 