package com.fds.flex.core.portal.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "flex_page")
@Getter
@Setter
public class Page {
    
    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("modified_date")
    private LocalDateTime modifiedDate;

    @Column("site_id")
    private Long siteId;

    @Column("parent_id")
    private Long parentId;

    @Column("page_name")
    private String pageName;

    @Column("page_title")
    private String title;

    @Column("page_path")
    private String pagePath;

    @Column("view_template_id")
    private Long viewTemplateId;

    @Column("secure")
    private boolean secure;

    @Column("visible")
    private boolean visible;

    @Column("seq")
    private int seq;

    @Column("include_content")
    private String includeContent;

    @Column("include_script")
    private String includeScript;

    @Column("include_style")
    private String includeStyle;

    @Transient
    private List<Page> children;

    @Transient
    private List<Role> roles;

    @Transient
    private ViewTemplate viewTemplate;
} 