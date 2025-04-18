package com.fds.flex.core.portal.action;

import com.fds.flex.core.portal.model.ViewTemplate;
import com.fds.flex.core.portal.service.ViewTemplateService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ViewTemplateAction {
    private final ViewTemplateService viewTemplateService;

    public Mono<ViewTemplate> create(ViewTemplate template) {
        return validateTemplate(template)
                .flatMap(validatedTemplate -> viewTemplateService.save(validatedTemplate));
    }

    public Mono<ViewTemplate> findById(Long id) {
        return viewTemplateService.findById(id)
                .flatMap(template -> {
                    // Thêm logic xử lý trước khi trả về template
                    return Mono.just(template);
                });
    }

    public Mono<ViewTemplate> update(Long id, ViewTemplate template) {
        template.setId(id);
        return validateTemplate(template)
                .flatMap(validatedTemplate -> viewTemplateService.save(validatedTemplate));
    }

    public Mono<Void> delete(Long id) {
        return viewTemplateService.delete(id);
    }

    public Mono<Page<ViewTemplate>> filter(Long siteId, String keyword, int start, int limit) {
        return viewTemplateService.filter(siteId, keyword, start, limit);
    }

    private Mono<ViewTemplate> validateTemplate(ViewTemplate template) {
        // Thêm logic validate template
        return Mono.just(template);
    }
}