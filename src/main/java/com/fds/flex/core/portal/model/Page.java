package com.fds.flex.core.portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "pages")
@Getter
@Setter
public class Page {
    
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "site_id")
    private Long siteId;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "path")
    private String path;

    @Column(name = "target")
    private String target;

    @Column(name = "module_id")
    private Long moduleId;

    @Column(name = "template")
    private String template;

    @Column(name = "type")
    private String type;

    @Column(name = "fragment")
    private String fragment;

    @Column(name = "secure")
    private boolean secure;

    @Column(name = "module_directly")
    private boolean moduleDirectly;

    @OneToOne(mappedBy = "page", cascade = CascadeType.ALL)
    private PageLayouts layout;

    @ElementCollection
    @CollectionTable(name = "page_roles", joinColumns = @JoinColumn(name = "page_id"))
    @Column(name = "role")
    private List<String> roles;

    @ManyToOne
    @JoinColumn(name = "site_id", insertable = false, updatable = false)
    private Site site;
} 