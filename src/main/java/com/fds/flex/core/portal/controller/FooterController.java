package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.action.FooterAction;
import com.fds.flex.core.portal.model.Footer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/footers")
@RequiredArgsConstructor
public class FooterController {
    private final FooterAction footerAction;

    @GetMapping("/site/{siteId}")
    public Flux<Footer> findBySiteId(@PathVariable Long siteId) {
        return footerAction.findBySiteId(siteId);
    }

    @PostMapping
    public Mono<ResponseEntity<Footer>> create(@RequestBody Footer footer) {
        return footerAction.create(footer)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Footer>> update(@PathVariable Long id, @RequestBody Footer footer) {
        footer.setId(id);
        return footerAction.update(id, footer)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return footerAction.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 