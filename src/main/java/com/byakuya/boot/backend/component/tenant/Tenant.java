package com.byakuya.boot.backend.component.tenant;

import com.byakuya.boot.backend.SystemVersion;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2022/10/9 21:12
 */
@Data
@Entity
@Table(name = "T_SYS_TENANT")
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Tenant implements Persistable<Long>, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Transient
    private boolean isNew = false;
    @Id
    private Long id;
    @NotBlank(message = "error.validation.tenant.code.required")
    @Column(nullable = false, unique = true, length = 64)
    private String code;
    @NotBlank(message = "error.validation.tenant.name.required")
    @Column(nullable = false, length = 128)
    private String name;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    private String description;

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
