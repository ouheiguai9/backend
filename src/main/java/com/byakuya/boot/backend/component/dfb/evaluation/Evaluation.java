package com.byakuya.boot.backend.component.dfb.evaluation;

import com.byakuya.boot.backend.SystemVersion;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2023/2/10 11:13
 */
@Data
@Entity
@Table(name = "T_DFB_EVALUATION")
@Accessors(chain = true)
public class Evaluation implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @Column(length = 20, nullable = false)
    private String phone;
    private LocalDateTime createTime;
    private int value;
}
