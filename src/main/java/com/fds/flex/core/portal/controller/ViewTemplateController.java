package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.model.ViewTemplate;
import com.fds.flex.core.portal.service.ViewTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/view-templates")
@RequiredArgsConstructor
public class ViewTemplateController {
    private final ViewTemplateService viewTemplateService;

    @GetMapping("/page/{pageId}")
    public Mono<ResponseEntity<ViewTemplate>> findByPageId(@PathVariable Long pageId) {
        return viewTemplateService.findByPageId(pageId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ViewTemplate>> create(@RequestBody ViewTemplate viewTemplate) {
        return viewTemplateService.save(viewTemplate)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ViewTemplate>> update(@PathVariable Long id, @RequestBody ViewTemplate viewTemplate) {
        viewTemplate.setId(id);
        return viewTemplateService.save(viewTemplate)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return viewTemplateService.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 