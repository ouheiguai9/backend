package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.component.organization.Organization;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Created by 田伯光 at 2022/9/12 20:54
 */
@Data
@Entity
@Table(name = "T_SYS_USER_UNIQUE", uniqueConstraints = {@UniqueConstraint(columnNames = {"uniqueValue", "company", "uniqueType"})})
@Accessors(chain = true)
class UserUnique {
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
        USERNAME, EMAIL, PHONE;
    }

}
