package com.fds.flex.core.portal.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

import com.fds.flex.core.portal.model.SiteRole;
@Repository
public interface SiteRoleRepository extends ReactiveCrudRepository<SiteRole, Long> {
    //@Query("SELECT * FROM site_role WHERE site_id = :siteId")
    Flux<SiteRole> findBySiteId(Long siteId);
}
