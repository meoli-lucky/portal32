package com.fds.flex.core.portal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.fds.flex.core.portal.model.UserSite;

@Repository
public interface UserSiteRepository extends R2dbcRepository<UserSite, Long> {

}
