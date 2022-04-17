package com.alibaba.fastjson2.writer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;

final class FieldWriterObjectFuncFinal<T> extends FieldWriterObjectFinal<T> {
    final Method method;
    final Function function;
    final boolean isArray;

    protected FieldWriterObjectFuncFinal(String name, int ordinal, long featues, String format, Type fieldType, Class fieldClass, Method method, Function function) {
        super(name, ordinal, featues, format, fieldType, fieldClass);
        this.method = method;
        this.function = function;
        isArray = fieldClass == AtomicIntegerArray.class
                || fieldClass == AtomicLongArray.class
                || fieldClass == AtomicReferenceArray.class
                || fieldClass.isArray();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public Object getFieldValue(Object object) {
        return function.apply(object);
    }
}
