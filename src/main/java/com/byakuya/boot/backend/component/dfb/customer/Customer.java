package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 田伯光 at 2023/1/5 22:13
 */
@Data
@Entity
@Table(name = "T_DFB_CUSTOMER")
@Accessors(chain = true)
@NamedEntityGraph(name = "Customer.User",
        attributeNodes = @NamedAttributeNode(value = "user", subgraph = "account"),
        subgraphs = @NamedSubgraph(name = "account", attributeNodes = @NamedAttributeNode("account")))
public class Customer implements Serializable {
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
}
