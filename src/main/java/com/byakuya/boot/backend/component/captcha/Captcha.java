package com.byakuya.boot.backend.component.captcha;

import com.byakuya.boot.backend.SystemVersion;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2022/9/12 20:54
 */
@Data
@Entity
@Table(name = "T_SYS_CAPTCHA")
@Accessors(chain = true)
class Captcha implements Persistable<CaptchaId>, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Transient
    private boolean isNew;
    @EmbeddedId
    private CaptchaId id;
    @Column(nullable = false)
    private String value;
    @Column(nullable = false)
    private LocalDateTime start;
    @Column(nullable = false)
    private LocalDateTime end;
    private boolean valid;

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
