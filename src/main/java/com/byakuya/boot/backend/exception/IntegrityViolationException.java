package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by 田伯光 at 2022/10/23 15:53
 */
public class IntegrityViolationException extends BackendException {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    public IntegrityViolationException(DataIntegrityViolationException cause) {
        super(ErrorStatus.DB_INTEGRITY_VIOLATION, cause);
    }

    @Override
    public String getMessage() {
        DataIntegrityViolationException violation = (DataIntegrityViolationException) getCause();
        Throwable cause = violation.getRootCause();
        if (cause == null) {
            cause = violation;
        }
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            switch (((SQLIntegrityConstraintViolationException) cause).getErrorCode()) {
                case 1062:
                    return "error.db.record.duplicate";
                case 1452:
                    return "error.db.rel.record.not.found";
                default:
            }
        }
        return super.getMessage();
    }
}
