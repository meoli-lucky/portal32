package com.fds.flex.core.portal.service;

import org.springframework.stereotype.Service;

import com.fds.flex.core.portal.model.SiteRole;
import com.fds.flex.core.portal.repository.SiteRoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SiteRoleService {
    private final SiteRoleRepository siteRoleRepository;

    public Mono<SiteRole> findById(Long id) {
        return siteRoleRepository.findById(id);
        }

    public Mono<SiteRole> save(SiteRole siteRole) {
        return siteRoleRepository.save(siteRole);
    }

    public Mono<Void> delete(Long id) {
        return siteRoleRepository.deleteById(id);
    }
}
