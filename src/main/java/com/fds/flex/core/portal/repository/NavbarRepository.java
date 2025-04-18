package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Navbar;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Repository;

@Repository
public interface NavbarRepository extends R2dbcRepository<Navbar, Long> {
    
    @Query("SELECT * FROM navbar WHERE site_id = :siteId")
    Flux<Navbar> findBySiteId(Long siteId);

    @Query("WITH RECURSIVE navbar_tree AS (" +
           "  SELECT id, parent_id, name, path, seq, 0 as level " +
           "  FROM navbar " +
           "  WHERE site_id = :siteId AND parent_id IS NULL " +
           "  UNION ALL " +
           "  SELECT p.id, p.parent_id, p.name, p.path, p.seq, pt.level + 1 " +
           "  FROM navbar p " +
           "  JOIN navbar_tree pt ON p.parent_id = pt.id " +
           "  WHERE p.site_id = :siteId " +
           ") " +
           "SELECT * FROM navbar_tree " +
           "ORDER BY level, COALESCE(parent_id, 0), seq")
    Flux<Navbar> findBySiteIdOrderByTree(Long siteId);
} 