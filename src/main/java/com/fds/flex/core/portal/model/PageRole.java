package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("page_role")
@Getter
@Setter
public class PageRole {
    @Id
    @Column("id")
    private Long id;

    @Column("page_id")
    private Long pageId;

    @Column("role_id")
    private Long roleId;
}
