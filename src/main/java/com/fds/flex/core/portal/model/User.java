package com.fds.flex.core.portal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
@Table("flex_user")
@Getter
@Setter
public class User implements UserDetails {
    


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
    
    //private List<GrantedAuthority> authorities;

    //private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public List<String> getRoles() {
       return new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
