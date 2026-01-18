package com.floodeer.plugins.configuration;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import org.bson.BsonDocument;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/*
 * This class is inspired by Dynamic, auto-updating bukkit configuration file (https://gist.github.com/Floodeer/5df8d7dc61a103745343a5e52d2e94ac)
 *
 * Pattern (for now) and Deprecated Codecs are not supported.
 */

public final class PluginConfiguration<T> {

    private static final Map<Class<?>, Codec<?>> CODECS = new HashMap<>();

    static {
        CODECS.put(boolean.class, Codec.BOOLEAN);
        CODECS.put(Boolean.class, Codec.BOOLEAN);

        CODECS.put(byte.class, Codec.BYTE);
        CODECS.put(Byte.class, Codec.BYTE);

        CODECS.put(short.class, Codec.SHORT);
        CODECS.put(Short.class, Codec.SHORT);

        CODECS.put(int.class, Codec.INTEGER);
        CODECS.put(Integer.class, Codec.INTEGER);

        CODECS.put(long.class, Codec.LONG);
        CODECS.put(Long.class, Codec.LONG);

        CODECS.put(float.class, Codec.FLOAT);
        CODECS.put(Float.class, Codec.FLOAT);

        CODECS.put(double.class, Codec.DOUBLE);
        CODECS.put(Double.class, Codec.DOUBLE);

        CODECS.put(String.class, Codec.STRING);

        CODECS.put(BsonDocument.class, Codec.BSON_DOCUMENT);

        CODECS.put(double[].class, Codec.DOUBLE_ARRAY);
        CODECS.put(float[].class, Codec.FLOAT_ARRAY);
        CODECS.put(int[].class, Codec.INT_ARRAY);
        CODECS.put(long[].class, Codec.LONG_ARRAY);

        CODECS.put(String[].class, new ArrayCodec<>(Codec.STRING, String[]::new));
    }

    private final BuilderCodec<T> codec;

    public PluginConfiguration(Class<T> type, Supplier<T> factory) {

        BuilderCodec.Builder<T> builder =
                BuilderCodec.builder(type, factory);

        for (Field field : type.getDeclaredFields()) {
            ConfigKey key = field.getAnnotation(ConfigKey.class);
            if (key == null) continue;

            Codec fieldCodec = CODECS.get(field.getType());
            if (fieldCodec == null) {
                throw new IllegalStateException("No codec for field type: " + field.getType().getName()
                );
            }

            field.setAccessible(true);

            builder.append(
                    new KeyedCodec(key.value(), fieldCodec),
                    (cfg, v) -> {
                        try {
                            field.set(cfg, v);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    cfg -> {
                        try {
                            return field.get(cfg);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).add();
        }

        this.codec = builder.build();
    }

    public BuilderCodec<T> codec() {
        return codec;
    }
}