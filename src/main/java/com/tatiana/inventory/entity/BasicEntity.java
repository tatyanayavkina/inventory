package com.tatiana.inventory.entity;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

/**
 * Base class for auditable entities
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BasicEntity{
    @Column(name = "creation_time", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @CreatedDate
    private ZonedDateTime creationTime;

    @Column(name = "modification_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @LastModifiedDate
    private ZonedDateTime modificationTime;

    @Column(name = "created_by_user", nullable = false)
    @CreatedBy
    private String createdByUser;

    @Column(name = "modified_by_user", nullable = false)
    @LastModifiedBy
    private String modifiedByUser;
}
