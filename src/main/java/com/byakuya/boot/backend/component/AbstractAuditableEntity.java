package com.byakuya.boot.backend.component;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.util.ProxyUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by 田伯光 at 2022/10/10 1:43
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity extends AbstractBaseEntity implements Auditable<Account, Long, LocalDateTime> {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @Id
    @GeneratedValue(generator = ConstantUtils.ID_GENERATOR_SNOW_NAME)
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", updatable = false)
    private Account createdBy;
    @JsonIgnore
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updater")
    private Account lastModifiedBy;
    @JsonIgnore
    private LocalDateTime lastModifiedDate;

    @Override
    public Optional<Account> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    @Override
    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty
    @Override
    public Optional<LocalDateTime> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    @Override
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Optional<Account> getLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    @Override
    public void setLastModifiedBy(Account lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @JsonProperty
    @Override
    public Optional<LocalDateTime> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    @Override
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!getClass().equals(ProxyUtils.getUserClass(obj))) {
            return false;
        }
        AbstractAuditableEntity that = (AbstractAuditableEntity) obj;
        return null != this.getId() && this.getId().equals(that.getId());
    }

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
        return null == getId();
    }
}
