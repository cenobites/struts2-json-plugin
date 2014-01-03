/**
 * Copyright 2014 Cenobit Technologies Inc. http://cenobit.es/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
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
