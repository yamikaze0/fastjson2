package com.alibaba.fastjson2.v1issues.issue_1000;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import junit.framework.TestCase;

/**
 * Created by wenshao on 17/03/2017.
 */
public class Issue1082 extends TestCase {
    public void test_for_issue() throws Exception {
        Throwable error = null;
        try {
            Model_1082 m = (Model_1082) JSON.parseObject("{}", Model_1082.class);
        } catch (JSONException ex) {
            error = ex;
        }
        assertNotNull(error);
    }

    public void f() {

    }

    public class Model_1082 {
        public Model_1082() {
            Issue1082.this.f();
        }
    }
}
