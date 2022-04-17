package com.alibaba.fastjson2.writer;

import com.alibaba.fastjson2.JSONException;

import java.lang.reflect.Field;

final class FieldWriterBooleanField extends FieldWriterBoolean {
    final Field field;

    protected FieldWriterBooleanField(String fieldName, int ordinal, long features, Field field, Class fieldClass) {
        super(fieldName, ordinal, features, null, fieldClass, fieldClass);
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }

    public Object getFieldValue(Object object) {
        try {
            return field.get(object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new JSONException("field.get error, " + name, e);
        }
    }

    protected Boolean getValue(Object object) {
        try {
            return (Boolean) field.get(object);
        } catch (IllegalAccessException e) {
            throw new JSONException("get field error", e);
        }
    }
}
