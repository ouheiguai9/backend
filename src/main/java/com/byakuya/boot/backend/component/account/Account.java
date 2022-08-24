package com.byakuya.boot.backend.component.account;

import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by 田伯光 at 2022/8/21 9:09
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_ACCOUNT")
public class Account extends AbstractAuditableEntity<Account> {
    private int loginErrorCount;
}
