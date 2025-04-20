package com.fds.flex.core.portal.service;

import org.springframework.stereotype.Service;

import com.fds.flex.core.portal.model.UserSite;
import com.fds.flex.core.portal.repository.UserSiteRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserSiteService {
    private final UserSiteRepository userSiteRepository;

    public Mono<UserSite> findById(Long id) {
        return userSiteRepository.findById(id);
    }

    public Mono<UserSite> save(UserSite userSite) {
        return userSiteRepository.save(userSite);
    }

    public Mono<Void> delete(Long id) {
        return userSiteRepository.deleteById(id);
    }
}
