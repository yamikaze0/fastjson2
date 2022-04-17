package com.alibaba.fastjson2.codec;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.reader.ObjectReaderCreatorASM;
import com.alibaba.fastjson2.writer.ObjectWriterCreatorASM;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;


public class TestExternal extends TestCase {
    public void test_0 () throws Exception {
        ExtClassLoader classLoader = new ExtClassLoader();
        Class<?> clazz = classLoader.loadClass("external.VO");
        Method method = clazz.getMethod("setName", new Class[] {String.class});
        Object obj = clazz.newInstance();
        method.invoke(obj, "jobs");
        
        String text = JSON.toJSONString(obj);
        System.out.println(text);
        JSON.parseObject(text, clazz);
    }

    public void test_1 () throws Exception {
        ExtClassLoader classLoader = new ExtClassLoader();
        try {
            JSONFactory.setContextReaderCreator(new ObjectReaderCreatorASM(classLoader));
            JSONFactory.setContextWriterCreator(new ObjectWriterCreatorASM(classLoader));

            Class<?> clazz = classLoader.loadClass("external.VO");
            Method method = clazz.getMethod("setName", new Class[]{String.class});
            Object obj = clazz.newInstance();
            method.invoke(obj, "jobs");

            String text = JSON.toJSONString(obj);
            System.out.println(text);
            JSON.parseObject(text, clazz);
        } finally {
            JSONFactory.setContextReaderCreator(null);
            JSONFactory.setContextWriterCreator(null);
        }
    }
    
    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException{
            super(Thread.currentThread().getContextClassLoader());
            
            byte[] bytes;
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("external/VO.clazz");
            bytes = IOUtils.toByteArray(is);
            is.close();
            
            super.defineClass("external.VO", bytes, 0, bytes.length);
        }
    }
}
