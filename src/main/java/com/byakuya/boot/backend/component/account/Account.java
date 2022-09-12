package com.byakuya.boot.backend.component.account;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.organization.Organization;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2022/8/21 9:09
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_ACCOUNT")
@Accessors(chain = true)
public class Account implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @GenericGenerator(name = "snowflake_id", strategy = "com.byakuya.boot.backend.component.SnowIdGenerator")
    @GeneratedValue(generator = "snowflake_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private boolean locked = false;
    private int loginErrorCount;
    @Nullable
    private LocalDateTime loginErrorTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company", nullable = false)
    private Organization company;
    private boolean admin = false;
}
