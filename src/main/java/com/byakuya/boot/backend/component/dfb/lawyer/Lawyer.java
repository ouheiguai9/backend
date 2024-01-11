package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.dfb.order.Order;
import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.jackson.Desensitize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 田伯光 at 2023/1/5 22:13
 */
@Data
@Entity
@Table(name = "T_DFB_LAWYER")
@Accessors(chain = true)
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Lawyer.Order", attributeNodes = @NamedAttributeNode("orderList")),
        @NamedEntityGraph(name = "Lawyer.User",
                attributeNodes = @NamedAttributeNode(value = "user", subgraph = "account"),
                subgraphs = @NamedSubgraph(name = "account", attributeNodes = @NamedAttributeNode("account")))
})
public class Lawyer implements Serializable {
    @Serial
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
    private String bank;
    private String bankId;
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

    @Desensitize(strategy = Desensitize.DesensitizeStrategy.PHONE)
    public String getPhone() {
        return phone;
    }

    @Desensitize(strategy = Desensitize.DesensitizeStrategy.ID_CARD)
    public String getCertificate() {
        return certificate;
    }

    @Desensitize(strategy = Desensitize.DesensitizeStrategy.LONG_CODE)
    public String getLawId() {
        return lawId;
    }

    @Desensitize(strategy = Desensitize.DesensitizeStrategy.LONG_CODE)
    public String getBankId() {
        return bankId;
    }

    public String getStateText() {
        return state == null ? null : state.text;
    }
}
