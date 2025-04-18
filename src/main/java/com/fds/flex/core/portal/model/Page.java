package com.fds.flex.core.portal.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "page")
@Getter
@Setter
public class Page {
    
    @Id
    @Column("id")
    private Long id;

    @Column("site_id")
    private Long siteId;

    @Column("parent_id")
    private Long parentId;

    @Column("name")
    private String name;

    @Column("title")
    private String title;

    @Column("path")
    private String path;

    @Column("view_template_id")
    private Long viewTemplateId;

    @Column("secure")
    private boolean secure;

    @Column("seq")
    private int seq;

    @Column("include_content")
    private String includeContent;

    @Column("include_script")
    private String includeScript;

    @Column("include_style")
    private String includeStyle;

    @Column("roles")
    private List<String> roles;
} 