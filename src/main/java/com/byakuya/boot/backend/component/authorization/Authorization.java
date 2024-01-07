package com.byakuya.boot.backend.component.authorization;

import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/10/4 23:30
 */
@Data
@Entity
@Table(name = "T_SYS_AUTHORIZATION", indexes = {@Index(columnList = "subjectId,authType,content,subjectType", unique = true)})
@Accessors(chain = true)
public class Authorization implements Serializable {
    @Id
    @GeneratedValue(generator = ConstantUtils.ID_GENERATOR_SEQUENCE_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private Long subjectId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectType subjectType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthType authType;
    @NotBlank
    @Column(nullable = false)
    private String content;

    public enum SubjectType {
        ACCOUNT, ROLE
    }

    public enum AuthType {
        MENU, API
    }
}
