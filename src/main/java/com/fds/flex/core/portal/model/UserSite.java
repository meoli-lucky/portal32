package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("user_site")
@Getter
@Setter
public class UserSite {
    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("site_id")
    private Long siteId;
}
