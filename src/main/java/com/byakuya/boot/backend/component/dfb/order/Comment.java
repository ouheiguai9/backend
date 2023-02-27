package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.jackson.Desensitize;
import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2023/2/10 11:13
 */
@Data
@Entity
@Table(name = "T_DFB_COMMENT")
@Accessors(chain = true)
public class Comment implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @GeneratedValue(generator = ConstantUtils.ID_GENERATOR_SNOW_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @OneToOne
    @JoinColumn(updatable = false)
    private Order order;
    @Desensitize(strategy = Desensitize.DesensitizeStrategy.PHONE)
    @Column(length = 20, nullable = false)
    private String customer;
    @Desensitize(strategy = Desensitize.DesensitizeStrategy.LAWYER)
    @Column(length = 20, nullable = false)
    private String lawyer;
    private LocalDateTime createTime;
    @Column(length = 500)
    private String content;
    private int value = 0;
    private Boolean label1;
    private Boolean label2;
    private Boolean label3;
    private Boolean label4;
    private Boolean label5;
    private Boolean label6;
    private Boolean visible;

    @JsonIgnore
    public Order getOrder() {
        return order;
    }

    @JsonProperty
    public void setOrder(Order order) {
        this.order = order;
    }

    @JsonProperty
    public Long getOrderId() {
        if (order == null || order.getId() == null) return null;
        return order.getId();
    }
}
