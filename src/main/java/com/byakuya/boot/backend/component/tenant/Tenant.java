package com.byakuya.boot.backend.component.tenant;

import com.byakuya.boot.backend.SystemVersion;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

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
public class Tenant implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @Id
    @GeneratedValue(generator = "table_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @NotBlank
    @Column(nullable = false, unique = true, length = 64)
    private String code;
    @NotBlank
    @Column(nullable = false, length = 128)
    private String name;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    private String description;
}
