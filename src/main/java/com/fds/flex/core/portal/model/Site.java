package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
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

    @Column("context")
    private String context;

    @Column("root")
    private boolean root;

    @Column("theme_id")
    private String themeId;

    @Column("module_id")
    private String moduleId;

    @Column("pages")
    private List<Pages> pages = new ArrayList<>();

    @Column("navbars")
    private List<Navbars> navbars = new ArrayList<>();

    @Column("header")
    private Header header;

    @Column("footer")
    private Footer footer;
} 