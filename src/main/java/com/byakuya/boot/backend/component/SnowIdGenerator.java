package com.byakuya.boot.backend.component;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.utils.SnowFlakeUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/8/21 15:41
 */
public class SnowIdGenerator implements IdentifierGenerator {

    @Serial
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return SnowFlakeUtils.newId();
    }
}
