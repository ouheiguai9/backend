package com.byakuya.boot.backend.component.dfb.lawyer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * Created by 相亲于盛夏 at 2023/4/3 21:54
 */
public class LawyerFullVO {
    @JsonIgnore
    private final Lawyer lawyer;

    public LawyerFullVO(Lawyer lawyer) {
        this.lawyer = lawyer;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long getId() {
        return lawyer.getId();
    }

    public String getPhone() {
        return lawyer.getPhone();
    }

    public String getName() {
        return lawyer.getName();
    }

    public String getCertificate() {
        return lawyer.getCertificate();
    }

    public String getLawId() {
        return lawyer.getLawId();
    }

    public String getLawFirm() {
        return lawyer.getLawFirm();
    }

    public String getBank() {
        return lawyer.getBank();
    }

    public String getBankId() {
        return lawyer.getBankId();
    }

    public Boolean getKey1() {
        return lawyer.getKey1();
    }

    public Boolean getKey2() {
        return lawyer.getKey2();
    }

    public Boolean getKey3() {
        return lawyer.getKey3();
    }

    public Boolean getKey4() {
        return lawyer.getKey4();
    }

    public Boolean getKey5() {
        return lawyer.getKey5();
    }

    public Boolean getKey6() {
        return lawyer.getKey6();
    }

    public Boolean getKey7() {
        return lawyer.getKey7();
    }

    public Boolean getKey8() {
        return lawyer.getKey8();
    }

    public Boolean getKey9() {
        return lawyer.getKey9();
    }

    public Boolean getBackup() {
        return lawyer.getBackup();
    }

    public LawyerState getState() {
        return lawyer.getState();
    }

    public LocalDateTime getCreateTime() {
        return lawyer.getUser().getAccount().getCreateTime();
    }

    public Boolean getLocked() {
        return lawyer.getUser().getAccount().isLocked();
    }

    public String getStateText() {
        return lawyer.getStateText();
    }
}
