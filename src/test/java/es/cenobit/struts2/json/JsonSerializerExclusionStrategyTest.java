package es.cenobit.struts2.json;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonSerializerExclusionStrategyTest {

    final static String DECLARING_CLASS = "class es.cenobit.struts2.json.TestObject";
    final static String DECLARING_INNER_CLASS = "class es.cenobit.struts2.json.JsonSerializerTest$TestObject";

    @Test
    public void shouldClassName() {
        if (DECLARING_CLASS.indexOf('.') != -1) {
            int index = DECLARING_CLASS.lastIndexOf('.');
            String className = DECLARING_CLASS.substring(index + 1, DECLARING_CLASS.length());
            Assert.assertEquals("TestObject", className);
        }
    }

    @Test
    public void shouldInnerClassName() {
        if (DECLARING_INNER_CLASS.indexOf('$') != -1) {
            int index = DECLARING_INNER_CLASS.lastIndexOf('$');
            String className = DECLARING_INNER_CLASS.substring(index + 1, DECLARING_INNER_CLASS.length());
            Assert.assertEquals("TestObject", className);
        }
    }
}
