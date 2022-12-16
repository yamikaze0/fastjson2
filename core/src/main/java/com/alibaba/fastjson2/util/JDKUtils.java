package com.alibaba.fastjson2.util;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteOrder;
import java.util.function.*;

public class JDKUtils {
    public static final int JVM_VERSION;
    public static final Byte LATIN1 = 0;
    public static final Byte UTF16 = 1;

    static final Field FIELD_STRING_VALUE;
    static final long FIELD_STRING_VALUE_OFFSET;
    static volatile boolean FIELD_STRING_ERROR;

    static final Class<?> CLASS_SQL_DATASOURCE;
    static final Class<?> CLASS_SQL_ROW_SET;
    public static final boolean HAS_SQL;

    // Android not support
    public static final Class CLASS_TRANSIENT;
    public static final boolean BIG_ENDIAN;

    public static final boolean UNSAFE_SUPPORT;

    // GraalVM not support
    // Android not support
    public static final BiFunction<char[], Boolean, String> STRING_CREATOR_JDK8;
    public static final BiFunction<byte[], Byte, String> STRING_CREATOR_JDK11;
    public static final ToIntFunction<String> STRING_CODER;
    public static final Function<String, byte[]> STRING_VALUE;

    static final MethodHandles.Lookup IMPL_LOOKUP;
    static final boolean OPEN_J9;
    static volatile Constructor CONSTRUCTOR_LOOKUP;
    static volatile boolean CONSTRUCTOR_LOOKUP_ERROR;
    static volatile Throwable initErrorLast;

    static {
        int jvmVersion = -1;
        boolean openj9 = false;
        try {
            String property = System.getProperty("java.specification.version");
            if (property.startsWith("1.")) {
                property = property.substring(2);
            }
            jvmVersion = Integer.parseInt(property);

            String jmvName = System.getProperty("java.vm.name");
            openj9 = jmvName.contains("OpenJ9");
            if (openj9) {
                FIELD_STRING_ERROR = true;
            }
        } catch (Throwable ignored) {
        }

        OPEN_J9 = openj9;

        boolean hasJavaSql = true;
        Class dataSourceClass = null;
        Class rowSetClass = null;
        try {
            dataSourceClass = Class.forName("javax.sql.DataSource");
            rowSetClass = Class.forName("javax.sql.RowSet");
        } catch (Throwable e) {
            hasJavaSql = false;
        }
        CLASS_SQL_DATASOURCE = dataSourceClass;
        CLASS_SQL_ROW_SET = rowSetClass;
        HAS_SQL = hasJavaSql;

        Class transientClass = null;
        try {
            transientClass = Class.forName("java.beans.Transient");
        } catch (Throwable ignored) {
        }
        CLASS_TRANSIENT = transientClass;

        JVM_VERSION = jvmVersion;

        if (JVM_VERSION == 8) {
            Field field = null;
            long fieldOffset = -1;
            try {
                field = String.class.getDeclaredField("value");
                field.setAccessible(true);
                fieldOffset = UnsafeUtils.objectFieldOffset(field);
            } catch (Exception ignored) {
                FIELD_STRING_ERROR = true;
            }

            FIELD_STRING_VALUE = field;
            FIELD_STRING_VALUE_OFFSET = fieldOffset;
        } else {
            FIELD_STRING_ERROR = true;
            FIELD_STRING_VALUE = null;
            FIELD_STRING_VALUE_OFFSET = -1;
        }

        boolean unsafeSupport;
        unsafeSupport = ((Predicate) o -> {
            try {
                return UnsafeUtils.UNSAFE != null;
            } catch (Throwable ignored) {
                return false;
            }
        }).test(null);
        UNSAFE_SUPPORT = unsafeSupport;

        BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

        BiFunction<char[], Boolean, String> stringCreatorJDK8 = null;
        BiFunction<byte[], Byte, String> stringCreatorJDK11 = null;
        ToIntFunction<String> stringCoder = null;
        Function<String, byte[]> stringValue = null;

        MethodHandles.Lookup trustedLookup = null;
        {
            try {
                Class lookupClass = MethodHandles.Lookup.class;
                Field implLookup = lookupClass.getDeclaredField("IMPL_LOOKUP");
                long fieldOffset = UnsafeUtils.UNSAFE.staticFieldOffset(implLookup);
                trustedLookup = (MethodHandles.Lookup) UnsafeUtils.UNSAFE.getObject(lookupClass, fieldOffset);
            } catch (Throwable ignored) {
                // ignored
            }
            IMPL_LOOKUP = trustedLookup;
        }

        Boolean compact_strings = null;
        try {
            if (JVM_VERSION == 8 && trustedLookup != null) {
                MethodHandles.Lookup caller = trustedLookup(String.class);

                MethodHandle handle = caller.findConstructor(
                        String.class, MethodType.methodType(void.class, char[].class, boolean.class)
                );

                CallSite callSite = LambdaMetafactory.metafactory(
                        caller,
                        "apply",
                        MethodType.methodType(BiFunction.class),
                        handle.type().generic(),
                        handle,
                        handle.type()
                );
                stringCreatorJDK8 = (BiFunction<char[], Boolean, String>) callSite.getTarget().invokeExact();
                stringCoder = (str) -> 1;
            }

            boolean lookupLambda = false;
            if (JVM_VERSION > 8 && trustedLookup != null) {
                try {
                    Field compact_strings_field = String.class.getDeclaredField("COMPACT_STRINGS");
                    if (compact_strings_field != null) {
                        if (UNSAFE_SUPPORT) {
                            long fieldOffset = UnsafeUtils.UNSAFE.staticFieldOffset(compact_strings_field);
                            compact_strings = UnsafeUtils.UNSAFE.getBoolean(String.class, fieldOffset);
                        } else {
                            compact_strings_field.setAccessible(true);
                            compact_strings = (Boolean) compact_strings_field.get(null);
                        }
                    }
                } catch (Throwable e) {
                    initErrorLast = e;
                    // ignored
                }
                lookupLambda = compact_strings != null && compact_strings.booleanValue();
            }

            if (lookupLambda) {
                MethodHandles.Lookup caller = trustedLookup.in(String.class);
                MethodHandle handle = caller.findConstructor(
                        String.class, MethodType.methodType(void.class, byte[].class, byte.class)
                );
                CallSite callSite = LambdaMetafactory.metafactory(
                        caller,
                        "apply",
                        MethodType.methodType(BiFunction.class),
                        handle.type().generic(),
                        handle,
                        MethodType.methodType(String.class, byte[].class, Byte.class)
                );
                stringCreatorJDK11 = (BiFunction<byte[], Byte, String>) callSite.getTarget().invokeExact();

                MethodHandles.Lookup stringCaller = trustedLookup.in(String.class);
                MethodHandle coder = stringCaller.findSpecial(
                        String.class,
                        "coder",
                        MethodType.methodType(byte.class),
                        String.class
                );
                CallSite applyAsInt = LambdaMetafactory.metafactory(
                        stringCaller,
                        "applyAsInt",
                        MethodType.methodType(ToIntFunction.class),
                        MethodType.methodType(int.class, Object.class),
                        coder,
                        coder.type()
                );
                stringCoder = (ToIntFunction<String>) applyAsInt.getTarget().invokeExact();

                MethodHandle value = stringCaller.findSpecial(
                        String.class,
                        "value",
                        MethodType.methodType(byte[].class),
                        String.class
                );
                CallSite apply = LambdaMetafactory.metafactory(
                        stringCaller,
                        "apply",
                        MethodType.methodType(Function.class),
                        value.type().generic(),
                        value,
                        value.type()
                );
                stringValue = (Function<String, byte[]>) apply.getTarget().invokeExact();
            }
        } catch (Throwable e) {
            initErrorLast = e;
            e.printStackTrace();
            // ignored
        }

        STRING_CREATOR_JDK8 = stringCreatorJDK8;
        STRING_CREATOR_JDK11 = stringCreatorJDK11;
        STRING_CODER = stringCoder;
        STRING_VALUE = stringValue;
    }

    public static boolean isSQLDataSourceOrRowSet(Class<?> type) {
        return (CLASS_SQL_DATASOURCE != null && CLASS_SQL_DATASOURCE.isAssignableFrom(type))
                || (CLASS_SQL_ROW_SET != null && CLASS_SQL_ROW_SET.isAssignableFrom(type));
    }

    public static char[] getCharArray(String str) {
        // GraalVM not support
        // Android not support
        if (!FIELD_STRING_ERROR) {
            try {
                return (char[]) UnsafeUtils.UNSAFE.getObject(str, FIELD_STRING_VALUE_OFFSET);
            } catch (Exception ignored) {
                FIELD_STRING_ERROR = true;
            }
        }

        return str.toCharArray();
    }

    public static MethodHandles.Lookup trustedLookup(Class objectClass) {
        if (OPEN_J9 && JVM_VERSION < 17 && !CONSTRUCTOR_LOOKUP_ERROR) {
            Constructor constructor = CONSTRUCTOR_LOOKUP;
            try {
                if (constructor == null) {
                    constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                    constructor.setAccessible(true);
                    CONSTRUCTOR_LOOKUP = constructor;
                }
                final int FULL_ACCESS_MASK = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | 0x8 | 0x10;
                return (MethodHandles.Lookup) constructor.newInstance(objectClass, FULL_ACCESS_MASK);
            } catch (Throwable ignored) {
                CONSTRUCTOR_LOOKUP_ERROR = true;
            }
        }

        MethodHandles.Lookup implLookup = IMPL_LOOKUP;
        MethodHandles.Lookup lookup;
        if (implLookup != null) {
            lookup = implLookup.in(objectClass);
        } else {
            lookup = MethodHandles.lookup();
        }
        return lookup;
    }
}
