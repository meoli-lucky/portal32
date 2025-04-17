package com.fds.flex.core.portal.model;

import org.apache.hc.client5.http.entity.mime.Header;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Table("site")
@Getter
@Setter
public class Site {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("site_name")
    private String siteName;

    @Column("context_path")
    private String context;

    @Column("private_site")
    private boolean privateSite;

    @Column("spa_or_static")
    private boolean spaOrStatic;

    @Column("description")
    private String description;

    @Column("roles")
    private List<String> roles;

    @Transient
    private List<Page> pages = new ArrayList<>();

    @Transient
    private List<Navbar> navbars = new ArrayList<>();

    @Transient
    private List<ViewTemplate> viewTemplates = new ArrayList<>();

    @Transient
    private List<Header> headers = new ArrayList<>();

    @Transient
    private List<Footer> footers = new ArrayList<>();
} 