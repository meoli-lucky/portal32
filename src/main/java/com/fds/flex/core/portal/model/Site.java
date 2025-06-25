package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("flex_site")
@Getter
@Setter
public class Site {
    
    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("modified_date")
    private LocalDateTime modifiedDate;
    
    @Column("site_name")
    private String siteName;

    @Column("context")
    private String context;

    @Column("private_site")
    private boolean privateSite;//if true, only the users in the site can access the site

    @Column("spa_or_static")
    private boolean spaOrStatic;//if true, the site is a SPA, otherwise it is a static site

    @Column("description")
    private String description;

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
    
    @Transient
    private List<Role> roles = new ArrayList<>();
} 