package com.fds.flex.core.portal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.fds.flex.core.portal.model.SiteRole;

@Repository
public interface SiteRoleRepository extends R2dbcRepository<SiteRole, Long> {

}
