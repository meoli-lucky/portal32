package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

@Table("header")
@Getter
@Setter
public class Header {
    @Id
    @Column("id")
    private Long id;

    @Column("site_id")
    private Long siteId;

    @Column("logo")
    private String logo;

    @Column("slogan")
    private String slogan;

    @Column("include_script")
    private String includeScript;

    @Column("include_style")
    private String includeStyle;

    @Column("include_content")
    private String includeContent;
} 