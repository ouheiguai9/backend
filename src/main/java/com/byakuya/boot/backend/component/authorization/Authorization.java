package com.byakuya.boot.backend.component.authorization;

import com.byakuya.boot.backend.SystemVersion;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/10/4 23:30
 */
@Data
@Entity
@Table(name = "T_SYS_AUTHORIZATION", indexes = {@Index(columnList = "subjectId,subjectType,authType,content", unique = true)})
@Accessors(chain = true)
public class Authorization implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @GeneratedValue(generator = "table_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @Column(nullable = false)
    private Long subjectId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubjectType subjectType;
    @Enumerated(EnumType.STRING)
    private AuthType authType;
    private String content;

    public enum SubjectType {
        ACCOUNT, ROLE;
    }

    public enum AuthType {
        MENU, API;
    }
}
