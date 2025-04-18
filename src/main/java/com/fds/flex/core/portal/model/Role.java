package com.fds.flex.core.portal.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("role")
@Getter
@Setter
public class Role {
    @Id
    @Column("id")
    private Long id;

    @Column("role_name")
    private String roleName;
    
    @Column("user_id")
    private Long userId;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("modified_date")
    private LocalDateTime modifiedDate;
}
