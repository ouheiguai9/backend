package com.byakuya.boot.backend.component.unique;

import com.byakuya.boot.backend.SystemVersion;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/9/12 20:54
 */
@Data
@Entity
@Table(name = "T_SYS_TABLE_UNIQUE")
class Unique implements Persistable<UniqueId>, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Transient
    private boolean isNew;
    @EmbeddedId
    private UniqueId id;

    public Unique() {

    }

    public Unique(Type uniqueType, Long tenantId, String uniqueValue) {
        this.id = new UniqueId(uniqueType, tenantId, uniqueValue);
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
