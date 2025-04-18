package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Table("navbar")
@Getter
@Setter
public class Navbar {

    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("modified_date")
    private LocalDateTime modifiedDate;

    @Column("parent_id")
    private Long parentId;

    @Column("site_id")
    private Long siteId;

    @Column("page_id")
    private Long pageId;

    @Column("name")
    private String name;

    @Column("href")
    private String href;

    @Column("followed_parent_href")
    private boolean followedParentHref;

    @Column("visible")
    private boolean visible;

    @Column("target")
    private String target;

    @Column("seq")
    private int seq;

    @Column("secure")
    private boolean secure;

    @Column("roles")
    private List<String> roles;

    @Transient
    private boolean hasChild;

    @Transient
    private List<Navbar> childrens;

    public boolean getHasChild() {
        return this.hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public List<Navbar> getChildrens() {
        return this.childrens;
    }

    public void setChildrens(List<Navbar> childrens) {
        this.childrens = childrens;
        // Cập nhật hasChild dựa trên childrens
        this.hasChild = childrens != null && !childrens.isEmpty();
    }
}