package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("view_template")
@Getter
@Setter
public class ViewTemplate {

    @Id
    @Column("id")
    private Long id;

    @Column("site_id")
    private Long siteId;
    
    @Column("template_location")
    private String templateLocation;//on_db, on_file

    @Column("relative_path")
    private String relativePath;//if templateLocation is on_file
    
    @Column("content")
    private String content;//if templateLocation is on_db
}
