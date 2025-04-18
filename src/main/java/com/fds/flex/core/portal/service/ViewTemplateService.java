package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.ViewTemplate;
import com.fds.flex.core.portal.repository.ViewTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ViewTemplateService {
    private final ViewTemplateRepository viewTemplateRepository;

    public Mono<ViewTemplate> findById(Long id) {
        return viewTemplateRepository.findById(id);
    }

    public Mono<ViewTemplate> save(ViewTemplate viewTemplate) {
        return viewTemplateRepository.save(viewTemplate);
    }

    public Mono<Void> delete(Long id) {
        return viewTemplateRepository.deleteById(id);
    }

    public Mono<Page<ViewTemplate>> filter(Long siteId, String keyword, int start, int limit) {
        if (start < 0) {
            start = 0;
        }
        if (limit < 1) {
            limit = 10;
        }
        if(limit > 100) {
            limit = 100;
        }
        Pageable pageable = PageRequest.of(start, limit);
        return viewTemplateRepository.filter(siteId, keyword, pageable)
                .collectList()
                .zipWith(viewTemplateRepository.countFilter(siteId, keyword))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
} 