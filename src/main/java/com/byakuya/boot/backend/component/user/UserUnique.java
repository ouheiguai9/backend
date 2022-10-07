package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.organization.Organization;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/9/12 20:54
 */
@Data
@Entity
@Table(name = "T_SYS_USER_UNIQUE", indexes = {@Index(columnList = "company,uniqueType,uniqueValue", unique = true)})
@Accessors(chain = true)
class UserUnique implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String uniqueValue;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company", nullable = false)
    private Organization company;
    @Enumerated(EnumType.STRING)
    private UniqueType uniqueType;

    public enum UniqueType {
        USERNAME, EMAIL, PHONE
    }

}
