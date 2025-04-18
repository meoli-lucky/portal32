package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("site_role")
@Getter
@Setter
public class SiteRole {
    @Id
    @Column("id")
    private Long id;

    @Column("site_id")
    private Long siteId;

    @Column("role_id")
    private Long roleId;
}
