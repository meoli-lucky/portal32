package com.fds.flex.core.portal.controller;

import com.fds.flex.core.portal.action.ViewTemplateAction;
import com.fds.flex.core.portal.model.ViewTemplate;
import com.fds.flex.core.portal.service.ViewTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/view-templates")
@RequiredArgsConstructor
public class ViewTemplateController {
    private final ViewTemplateAction viewTemplateAction;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ViewTemplate>> findById(@PathVariable Long id) {
        return viewTemplateAction.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ViewTemplate>> create(@RequestBody ViewTemplate viewTemplate) {
        return viewTemplateAction.create(viewTemplate)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ViewTemplate>> update(@PathVariable Long id, @RequestBody ViewTemplate viewTemplate) {
        viewTemplate.setId(id);
        return viewTemplateAction.update(id, viewTemplate)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return viewTemplateAction.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping("/filter")
    public Mono<ResponseEntity<Page<ViewTemplate>>> filter(
            @RequestParam Long siteId,
            @RequestParam(required = false) String keyword,
            int start, int limit) {
        return viewTemplateAction.filter(siteId, keyword, start, limit)
                .map(ResponseEntity::ok);
    }

} 