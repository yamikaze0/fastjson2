package com.alibaba.json.bvt.issue_3600;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

public class Issue3602 extends TestCase {
    private UnsupportedEncodingException exception = new UnsupportedEncodingException();
    public void test_for_issue() throws Exception {
        VO vo = new VO();
//        vo.id = 1L;

        JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue);
    }

    public static class VO {
        @JSONField(serializeUsing = VOSer.class)
        public Long id;
    }

    public static class VOSer implements ObjectSerializer {
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            throw new UnsupportedEncodingException();
        }
    }

}
