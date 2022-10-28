package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import org.springframework.context.MessageSourceResolvable;

/**
 * Created by 田伯光 at 2022/10/22 23:15
 */
public class RecordNotFoundException extends BackendException implements MessageSourceResolvable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private final String code;
    private final Object[] args;

    public RecordNotFoundException(String code, Object... args) {
        super(ErrorStatus.DB_RECORD_NOT_FOUND);
        this.code = code;
        this.args = args;
    }

    @Override
    public String[] getCodes() {
        return new String[]{code};
    }

    @Override
    public Object[] getArguments() {
        return this.args.clone();
    }

    @Override
    public String getDefaultMessage() {
        return ErrorStatus.DB_RECORD_NOT_FOUND.code;
    }
}
