package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Table("navbars")
@Getter
@Setter
public class Navbars {
    
    @Id
    @Column("id")
    private String id;

    @Column("site_id")
    private String siteId;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("template")
    private String template;

    @Column("fragment")
    private String fragment;

    @Column("module_id")
    private String moduleId;

    @Column("module_directly")
    private boolean moduleDirectly;

    @Column("roles")
    private List<String> roles;

    @Column("site_id")
    private Sites site;
} 