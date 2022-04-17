package com.alibaba.fastjson2.primitves;

import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.reader.ObjectReaders;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriters;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

public class Int64_1 {
    public int MIN_VALUE = Integer.MIN_VALUE;
    public int MAX_VALUE = Integer.MAX_VALUE;

    private long[] values = new long[100];
    private int off;

    public Int64_1() {
        values[off++] = Byte.MIN_VALUE;
        values[off++] = Byte.MAX_VALUE;
        values[off++] = Short.MIN_VALUE;
        values[off++] = Short.MAX_VALUE;
        values[off++] = JSONBTest.INT24_MIN;
        values[off++] = JSONBTest.INT24_MAX;
        values[off++] = Integer.MIN_VALUE;
        values[off++] = Integer.MAX_VALUE;
        values[off++] = Long.MIN_VALUE;
        values[off++] = Long.MAX_VALUE;
        values[off++] = 0;
        values[off++] = -1;
        values[off++] = -10;
        values[off++] = -100;
        values[off++] = -1000;
        values[off++] = -10000;
        values[off++] = -100000;
        values[off++] = -1000000;
        values[off++] = -10000000;
        values[off++] = -100000000;
        values[off++] = -1000000000;
        values[off++] = -10000000000L;
        values[off++] = -100000000000L;
        values[off++] = -1000000000000L;
        values[off++] = -10000000000000L;
        values[off++] = -100000000000000L;
        values[off++] = -1000000000000000L;
        values[off++] = -10000000000000000L;
        values[off++] = -100000000000000000L;
        values[off++] = -1000000000000000000L;
        values[off++] = 1;
        values[off++] = 10;
        values[off++] = 100;
        values[off++] = 1000;
        values[off++] = 10000;
        values[off++] = 100000;
        values[off++] = 1000000;
        values[off++] = 10000000;
        values[off++] = 100000000;
        values[off++] = 1000000000;
        values[off++] = 10000000000L;
        values[off++] = 100000000000L;
        values[off++] = 1000000000000L;
        values[off++] = 10000000000000L;
        values[off++] = 100000000000000L;
        values[off++] = 1000000000000000L;
        values[off++] = 10000000000000000L;
        values[off++] = 100000000000000000L;
        values[off++] = 1000000000000000000L;
        values[off++] = -9;
        values[off++] = -99;
        values[off++] = -999;
        values[off++] = -9999;
        values[off++] = -99999;
        values[off++] = -999999;
        values[off++] = -9999999;
        values[off++] = -99999999;
        values[off++] = -999999999;
        values[off++] = -9999999999L;
        values[off++] = -99999999999L;
        values[off++] = -999999999999L;
        values[off++] = -9999999999999L;
        values[off++] = -99999999999999L;
        values[off++] = -999999999999999L;
        values[off++] = -9999999999999999L;
        values[off++] = -99999999999999999L;
        values[off++] = -999999999999999999L;
        values[off++] = 9;
        values[off++] = 99;
        values[off++] = 999;
        values[off++] = 9999;
        values[off++] = 99999;
        values[off++] = 999999;
        values[off++] = 9999999;
        values[off++] = 99999999;
        values[off++] = 999999999;
        values[off++] = 9999999999L;
        values[off++] = 99999999999L;
        values[off++] = 999999999999L;
        values[off++] = 9999999999999L;
        values[off++] = 99999999999999L;
        values[off++] = 999999999999999L;
        values[off++] = 9999999999999999L;
        values[off++] = 99999999999999999L;
        values[off++] = 999999999999999999L;
    }

    @Test
    public void test_reflect() throws Exception {
        ObjectWriter<VO> ow = ObjectWriters.ofReflect(VO.class);
        ObjectReader<VO> oc = ObjectReaders.ofReflect(VO.class);
        for (int i = 0; i < off; i++) {
            VO vo = new VO();
            vo.value = values[i];
            JSONWriter w = JSONWriter.of();
            ow.write(w, vo);

            String json = w.toString();
            JSONReader jr = JSONReader.of(json);
            VO o = oc.readObject(jr, 0);
            assertEquals(vo.value, o.value);
        }
    }

    @Test
    public void test_lambda() throws Exception {
        ObjectWriter<VO> ow = ObjectWriters.objectWriter(VO.class);
        ObjectReader<VO> oc = ObjectReaders.of(VO.class);

        for (int i = 0; i < off; i++) {
            VO vo = new VO();
            vo.value = values[i];
            JSONWriter w = JSONWriter.of();
            ow.write(w, vo);

            String json = w.toString();
            JSONReader jr = JSONReader.of(json);
            VO o = oc.readObject(jr, 0);
            assertEquals(vo.value, o.value);
        }
    }

    @Test
    public void test_manual() throws Exception {
        ObjectWriter<VO> ow = ObjectWriters.objectWriter(ObjectWriters.fieldWriter("value", VO::getValue));
        ObjectReader<VO> oc = ObjectReaders.of(VO.class);

        for (int i = 0; i < off; i++) {
            VO vo = new VO();
            vo.value = values[i];
            JSONWriter w = JSONWriter.of();
            ow.write(w, vo);

            String json = w.toString();
            JSONReader jr = JSONReader.of(json);
            VO o = oc.readObject(jr, 0);
            assertEquals(vo.value, o.value);
        }
    }

    @Test
    public void test_0() throws Exception {
        String str = "{\"value\":\"123\"}";
        VO vo = JSON.parseObject(str, VO.class);
        assertEquals(123L, vo.getValue().longValue());
    }

    public static class VO {
        private Long value;

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}
