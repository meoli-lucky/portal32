package com.fds.flex.core.portal.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("flex_user")
@Getter
@Setter
public class User {
    @Id
    @Column("id")
    private Long id;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("modified_date")
    private LocalDateTime modifiedDate;
    
    @Column("user_name")
    private String username;

    @Column("password")
    private String password;
    
    @Column("email")
    private String email;

    @Column("full_name")
    private String fullName;    
    
    @Column("status")
    private boolean status;

    @Column("avatar")
    private String avatar;
    
}
