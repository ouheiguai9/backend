package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.account.Account;
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
@Table(name = "T_DFB_CUSTOMER")
@Accessors(chain = true)
public class Customer {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private Account account;
    @Column(length = 20, nullable = false)
    private String phone;
}
