package com.fds.flex.core.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Table("header")
@Getter
@Setter
public class Headers {
    @Id
    @Column("id")
    private String id;

    @Column("logo")
    private String logo;

    @Column("slogan")
    private String slogan;

    @Column("payload")
    private String payload;
} 