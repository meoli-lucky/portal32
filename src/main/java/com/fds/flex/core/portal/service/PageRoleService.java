package com.fds.flex.core.portal.service;

import org.springframework.stereotype.Service;

import com.fds.flex.core.portal.model.PageRole;
import com.fds.flex.core.portal.repository.PageRoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PageRoleService {

    private final PageRoleRepository pageRoleRepository;

    public Mono<PageRole> findById(Long id) {
        return pageRoleRepository.findById(id);
    }

    public Mono<PageRole> save(PageRole pageRole) {
        return pageRoleRepository.save(pageRole);
    }

    public Mono<Void> delete(Long id) {
        return pageRoleRepository.deleteById(id);
    }
}
