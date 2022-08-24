package com.byakuya.boot.backend.component.parameter;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import com.byakuya.boot.backend.component.account.Account;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * @author ganzl
 * @createTime 2022/4/14 16:19
 * @description 系统参数
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_PARAMETER", indexes = {@Index(columnList = "groupKey,itemKey")})
public class Parameter extends AbstractAuditableEntity<Account> {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @NotBlank
    @Column(nullable = false)
    private String groupKey;

    @NotBlank
    @Column(nullable = false)
    private String itemKey;

    @NotBlank
    @Column(nullable = false)
    private String itemValue;

    private int ordering;
    private String description;
}
