package com.fds.flex.core.portal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.fds.flex.core.portal.model.PageRole;

@Repository
public interface PageRoleRepository extends R2dbcRepository<PageRole, Long> {

}
