package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Created by 田伯光 at 2023/1/5 22:13
 */
@Data
@Entity
@Table(name = "T_DFB_LAWYER")
@Accessors(chain = true)
public class Lawyer {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private User user;
    @Column(length = 20, nullable = false)
    private String phone;
    private String name;
    private String certificate;
    private String lawId;
    private String lawFirm;
    private boolean key1;
    private boolean key2;
    private boolean key3;
    private boolean key4;
    private boolean key5;
    private boolean key6;
    private boolean key7;
    private boolean key8;
    private boolean key9;
    private boolean backup;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LawyerState state;

    public String getStateText() {
        return state == null ? null : state.text;
    }
}
