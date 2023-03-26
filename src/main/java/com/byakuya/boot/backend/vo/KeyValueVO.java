package com.byakuya.boot.backend.vo;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Created by 田伯光 at 2023/3/26 21:41
 */
public final class KeyValueVO<K, V> {
    private final K key;
    private final V value;

    private KeyValueVO(K key, V value) {

        Assert.notNull(key, "Key must not be null");
        Assert.notNull(value, "Value must not be null");

        this.key = key;
        this.value = value;
    }

    public static <K, V> KeyValueVO<K, V> of(K key, V value) {
        return new KeyValueVO<>(key, value);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(key);
        result = 31 * result + ObjectUtils.nullSafeHashCode(value);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyValueVO)) {
            return false;
        }
        KeyValueVO<?, ?> kv = (KeyValueVO<?, ?>) o;
        if (!ObjectUtils.nullSafeEquals(key, kv.key)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(value, kv.value);
    }

    @Override
    public String toString() {
        return String.format("{key:%s, value:%s}", this.key, this.value);
    }
}
