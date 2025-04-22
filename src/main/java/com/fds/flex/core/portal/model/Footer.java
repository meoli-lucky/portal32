package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


@Table("flex_footer")
@Getter
@Setter
public class Footer {
    
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