package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.dfb.order.Order;
import com.byakuya.boot.backend.component.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 田伯光 at 2023/1/5 22:13
 */
@Data
@Entity
@Table(name = "T_DFB_LAWYER")
@Accessors(chain = true)
@NamedEntityGraph(name = "Lawyer.Order", attributeNodes = @NamedAttributeNode("orderList"))
public class Lawyer implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private User user;
    @Column(length = 20, nullable = false)
    private String phone;
    private String name;
    private String certificate;
    private String lawId;
    private String lawFirm;
    private Boolean key1;
    private Boolean key2;
    private Boolean key3;
    private Boolean key4;
    private Boolean key5;
    private Boolean key6;
    private Boolean key7;
    private Boolean key8;
    private Boolean key9;
    private Boolean backup;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LawyerState state;
    @JsonIgnore
    @OneToMany(mappedBy = "lawyer")
    private List<Order> orderList;

    public String getStateText() {
        return state == null ? null : state.text;
    }
}
