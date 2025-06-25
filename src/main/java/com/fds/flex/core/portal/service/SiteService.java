package com.fds.flex.core.portal.service;

import com.fds.flex.core.portal.model.Footer;
import com.fds.flex.core.portal.model.Header;
import com.fds.flex.core.portal.model.Navbar;
import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.model.ViewTemplate;
import com.fds.flex.core.portal.model.Role;
import com.fds.flex.core.portal.model.SiteRole;
import com.fds.flex.core.portal.model.SiteDisplay;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.fds.flex.core.portal.repository.SiteRepository;
import com.fds.flex.core.portal.repository.PageRepository;
import com.fds.flex.core.portal.repository.HeaderRepository;
import com.fds.flex.core.portal.repository.NavbarRepository;
import com.fds.flex.core.portal.repository.FooterRepository;
import com.fds.flex.core.portal.repository.ViewTemplateRepository;
import com.fds.flex.core.portal.repository.RoleRepository;
import com.fds.flex.core.portal.repository.SiteRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository siteRepository;
    private final PageService pageService;
    private final HeaderRepository headerRepository;
    private final FooterRepository footerRepository;
    private final NavbarRepository navbarRepository;
    private final ViewTemplateRepository viewTemplateRepository;
    private final RoleRepository roleRepository;
    private final SiteRoleRepository siteRoleRepository;

    public Mono<Site> findById(Long id) {
        return siteRepository.findById(id)
                .flatMap(site -> {
                    Mono<List<com.fds.flex.core.portal.model.Page>> pagesMono = pageService.buildPageTree(id);
                    Mono<List<Header>> headersMono = headerRepository.findBySiteId(id).collectList();
                    Mono<List<Footer>> footersMono = footerRepository.findBySiteId(id).collectList();
                    Mono<List<Navbar>> navbarsMono = navbarRepository.findBySiteId(id).collectList();
                    Mono<List<ViewTemplate>> templatesMono = viewTemplateRepository.findBySiteId(id).collectList();
                    Mono<List<Role>> rolesMono = siteRoleRepository.findBySiteId(id)
                            .flatMap(siteRole -> roleRepository.findById(siteRole.getRoleId()))
                            .collectList();
                    return Mono.zip(pagesMono, headersMono, footersMono, navbarsMono, templatesMono, rolesMono)
                            .map(tuple -> {
                                site.setPages(tuple.getT1());
                                site.setHeaders(tuple.getT2());
                                site.setFooters(tuple.getT3());
                                site.setNavbars(tuple.getT4());
                                site.setViewTemplates(tuple.getT5());
                                site.setRoles(tuple.getT6());
                                return site;
                            });
                });
    }

    public Mono<Site> save(Site site) {
        return siteRepository.save(site);
    }

    public Mono<Void> delete(Long id) {
        return siteRepository.deleteById(id);
    }

    public Mono<Site> getRootSite() {
        return siteRepository.getRootSite().flatMap(site -> {
            Mono<List<com.fds.flex.core.portal.model.Page>> pagesMono = pageService.buildPageTree(site.getId());
            Mono<List<Header>> headersMono = headerRepository.findBySiteId(site.getId()).collectList();
            Mono<List<Footer>> footersMono = footerRepository.findBySiteId(site.getId()).collectList();
            Mono<List<Navbar>> navbarsMono = navbarRepository.findBySiteId(site.getId()).collectList();
            Mono<List<ViewTemplate>> templatesMono = viewTemplateRepository.findBySiteId(site.getId()).collectList();
            Mono<List<Role>> rolesMono = siteRoleRepository.findBySiteId(site.getId())
                    .flatMap(siteRole -> roleRepository.findById(siteRole.getRoleId()))
                    .collectList();
            return Mono.zip(pagesMono, headersMono, footersMono, navbarsMono, templatesMono, rolesMono)
                    .map(tuple -> {
                        site.setPages(tuple.getT1());
                        site.setHeaders(tuple.getT2());
                        site.setFooters(tuple.getT3());
                        site.setNavbars(tuple.getT4());
                        site.setViewTemplates(tuple.getT5());
                        site.setRoles(tuple.getT6());
                        return site;
                    });
        });
    }

    public Mono<Site> findByContextPathAndIgnoreRootSite(String contextPath) {
        return siteRepository.findByContextPathAndIgnoreRootSite(contextPath).flatMap(site -> {
            Mono<List<com.fds.flex.core.portal.model.Page>> pagesMono = pageService.buildPageTree(site.getId());
            Mono<List<Header>> headersMono = headerRepository.findBySiteId(site.getId()).collectList();
            Mono<List<Footer>> footersMono = footerRepository.findBySiteId(site.getId()).collectList();
            Mono<List<Navbar>> navbarsMono = navbarRepository.findBySiteId(site.getId()).collectList();
            Mono<List<ViewTemplate>> templatesMono = viewTemplateRepository.findBySiteId(site.getId()).collectList();
            Mono<List<Role>> rolesMono = siteRoleRepository.findBySiteId(site.getId())
                    .flatMap(siteRole -> roleRepository.findById(siteRole.getRoleId()))
                    .collectList();
            return Mono.zip(pagesMono, headersMono, footersMono, navbarsMono, templatesMono, rolesMono)
                    .map(tuple -> {
                        site.setPages(tuple.getT1());
                        site.setHeaders(tuple.getT2());
                        site.setFooters(tuple.getT3());
                        site.setNavbars(tuple.getT4());
                        site.setViewTemplates(tuple.getT5());
                        site.setRoles(tuple.getT6());
                        return site;
                    });
        });
    }

    public Flux<Site> findAll() {
        return siteRepository.findAll()
                .flatMap(site -> {
                    Mono<List<com.fds.flex.core.portal.model.Page>> pagesMono = pageService.buildPageTree(site.getId());
                    Mono<List<Header>> headersMono = headerRepository.findBySiteId(site.getId()).collectList();
                    Mono<List<Footer>> footersMono = footerRepository.findBySiteId(site.getId()).collectList();
                    Mono<List<Navbar>> navbarsMono = navbarRepository.findBySiteId(site.getId()).collectList();
                    Mono<List<ViewTemplate>> templatesMono = viewTemplateRepository.findBySiteId(site.getId()).collectList();
                    Mono<List<Role>> rolesMono = siteRoleRepository.findBySiteId(site.getId())
                            .flatMap(siteRole -> roleRepository.findById(siteRole.getRoleId()))
                            .collectList();
                    
                    return Mono.zip(pagesMono, headersMono, footersMono, navbarsMono, templatesMono, rolesMono)
                            .map(tuple -> {
                                site.setPages(tuple.getT1());
                                site.setHeaders(tuple.getT2());
                                site.setFooters(tuple.getT3());
                                site.setNavbars(tuple.getT4());
                                site.setViewTemplates(tuple.getT5());
                                site.setRoles(tuple.getT6());
                                return site;
                            });
                });
    }

    public Mono<Page<Site>> filter(Long siteId, String keyword, int start, int limit) {
        if (start < 0) {
            start = 0;
        }
        if (limit < 1) {
            limit = 10;
        }
        if (limit > 100) {
            limit = 100;
        }
        Pageable pageable = PageRequest.of(start, limit);
        return siteRepository.filter(keyword, pageable)
                .collectList()
                .zipWith(siteRepository.countFilter(keyword))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
}