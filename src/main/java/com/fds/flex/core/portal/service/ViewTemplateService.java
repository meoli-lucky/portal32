package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.ViewTemplate;
import com.fds.flex.core.portal.repository.ViewTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ViewTemplateService {
    private final ViewTemplateRepository viewTemplateRepository;

    public Mono<ViewTemplate> findByPageId(Long pageId) {
        return viewTemplateRepository.findByPageId(pageId);
    }

    public Mono<ViewTemplate> save(ViewTemplate viewTemplate) {
        return viewTemplateRepository.save(viewTemplate);
    }

    public Mono<Void> delete(Long id) {
        return viewTemplateRepository.deleteById(id);
    }
} 