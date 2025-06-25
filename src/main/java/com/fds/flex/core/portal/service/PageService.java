package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Page;
import com.fds.flex.core.portal.repository.PageRepository;
import com.fds.flex.core.portal.repository.PageRoleRepository;
import com.fds.flex.core.portal.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final RoleRepository roleRepository;
    private final PageRoleRepository pageRoleRepository;
    private final ViewTemplateService viewTemplateService;

    public Mono<List<Page>> buildPageTree(Long siteId) {
        return pageRepository.findBySiteId(siteId) // Trả Flux<Page>
                .flatMap(this::enrichWithChildren)
                .flatMap(this::enrichWithRoles)
                .flatMap(this::enrichWithViewTemplate)
                .collectList()
                .map(this::buildTreeFromFlatList);
    }

    public Flux<Page> findBySiteIdOrderByTree(Long siteId) {
        return pageRepository.findBySiteIdOrderByTree(siteId)
                .sort(Comparator.comparingInt(Page::getSeq))
                .flatMap(this::enrichWithChildren)
                .flatMap(this::enrichWithRoles)
                .flatMap(this::enrichWithViewTemplate);
    }

    public Flux<Page> findBySiteIdAndParentId(Long siteId, Long parentId) {
        return pageRepository.findBySiteIdAndParentId(siteId, parentId)
                .sort(Comparator.comparingInt(Page::getSeq))
                .flatMap(this::enrichWithChildren)
                .flatMap(this::enrichWithRoles)
                .flatMap(this::enrichWithViewTemplate);
    }

    public Flux<Page> findBySiteId(Long siteId) {
        return pageRepository.findBySiteId(siteId).flatMap(this::enrichWithChildren).flatMap(this::enrichWithRoles).flatMap(this::enrichWithViewTemplate);
    }

    public Mono<Page> findById(Long id) {
        return pageRepository.findById(id).flatMap(this::enrichWithChildren).flatMap(this::enrichWithRoles).flatMap(this::enrichWithViewTemplate);
    }

    public Mono<Page> save(Page page) {
        return pageRepository.save(page);
    }

    public Mono<Void> delete(Long id) {
        return pageRepository.deleteById(id);
    }

    public Mono<List<Page>> getAllChildPages(Long pageId) {
        return pageRepository.findByParentId(pageId)
                .flatMap(this::enrichWithChildren)
                .flatMap(this::enrichWithRoles)
                .flatMap(this::enrichWithViewTemplate)
                .collectList();
    }

    private Mono<Page> enrichWithChildren(Page page) {
        return getAllChildPages(page.getId())
                .map(children -> {
                    page.setChildren(children);
                    return page;
                });
    }

    private Mono<Page> enrichWithRoles(Page page) {
        return pageRoleRepository.findByPageId(page.getId())
                .flatMap(pageRole -> roleRepository.findById(pageRole.getRoleId()))
                .collectList()
                .map(roles -> {
                    page.setRoles(roles);
                    return page;
                });
    }

    private Mono<Page> enrichWithViewTemplate(Page page) {
        return viewTemplateService.findById(page.getViewTemplateId())
                .map(viewTemplate -> {
                    page.setViewTemplate(viewTemplate);
                    return page;
                });
    }

    private List<Page> buildTreeFromFlatList(List<Page> flatList) {
        Map<Long, Page> pageMap = new HashMap<>();
        List<Page> rootList = new ArrayList<>();

        // 1. Cho vào map theo id
        for (Page page : flatList) {
            pageMap.put(page.getId(), page);
        }

        // 2. Duyệt lại và gán children
        for (Page page : flatList) {
            if (page.getParentId() == null) {
                rootList.add(page);
            } else {
                Page parent = pageMap.get(page.getParentId());
                if (parent != null) {
                    parent.getChildren().add(page);
                }
            }
        }

        // 3. Sort children theo seq nếu cần
        sortPageTree(rootList);
        return rootList;
    }

    private void sortPageTree(List<Page> pages) {
        pages.sort(Comparator.comparingInt(Page::getSeq));
        for (Page page : pages) {
            if (page.getChildren() != null && !page.getChildren().isEmpty()) {
                sortPageTree(page.getChildren());
            }
        }
    }
}