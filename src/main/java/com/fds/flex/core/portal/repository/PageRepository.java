package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Page;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PageRepository extends R2dbcRepository<Page, Long> {
    Mono<Page> findById(Long id);
    
    @Query("SELECT * FROM page WHERE site_id = :siteId")
    Flux<Page> findBySiteId(Long siteId);

    @Query("SELECT * FROM page WHERE site_id = :siteId AND parent_id = :parentId")
    Flux<Page> findBySiteIdAndParentId(Long siteId, Long parentId);

    @Query("WITH RECURSIVE page_tree AS (" +
           "  SELECT id, parent_id, name, path, seq, 0 as level " +
           "  FROM page " +
           "  WHERE site_id = :siteId AND parent_id IS NULL " +
           "  UNION ALL " +
           "  SELECT p.id, p.parent_id, p.name, p.path, p.seq, pt.level + 1 " +
           "  FROM page p " +
           "  JOIN page_tree pt ON p.parent_id = pt.id " +
           "  WHERE p.site_id = :siteId " +
           ") " +
           "SELECT * FROM page_tree " +
           "ORDER BY level, COALESCE(parent_id, 0), seq")
    Flux<Page> findBySiteIdOrderByTree(Long siteId);

    Flux<Page> findByParentId(Long parentId);
} 