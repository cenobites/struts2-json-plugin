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

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.Gson;

import es.cenobit.struts2.json.objects.TestInnerObject;
import es.cenobit.struts2.json.objects.TestObject;

@RunWith(JUnit4.class)
public class JsonSerializerTest {

    private Gson gson() {
        return JsonSerializerBuilder.create();
    }

    private Gson gson(String[] fields, Class<?>... classes) {
        return JsonSerializerBuilder.create(new HashSet<String>(Arrays.asList(fields)));
    }

    @Test
    public void shouldSerialization() {
        final Gson serializer = gson();
        final String json = serializer.toJson(new TestObject());
        Assert.assertTrue(json.contains("field1") && json.contains("field2"));
    }

    @Test
    public void shouldSerializationInInnerClass() {
        final Gson serializer = gson();
        final String json = serializer.toJson(new TestInnerObject());
        Assert.assertTrue(json.contains("field1") && json.contains("field2"));
    }

    @Test
    public void shouldSerializationWithExcludeFields() {
        final Gson serializer = gson(new String[] { "field2" });
        final String json = serializer.toJson(new TestObject());
        Assert.assertTrue(json.contains("field1"));
        Assert.assertFalse(json.contains("field2"));
    }

    @Test
    public void shouldSerializationWithExcludeFieldsAndRelationship() {
        final Gson serializer = gson(new String[] { "field2", "relationship1" });
        final String json = serializer.toJson(new TestObject());
        Assert.assertTrue(json.contains("field1") && json.contains("relationship2"));
        Assert.assertFalse(json.contains("field2") && json.contains("relationship1"));
    }

    @Test
    public void shouldSerializationWithExcludeFieldsInInnerClass() {
        final Gson serializer = gson(new String[] { "field2" });
        final String json = serializer.toJson(new TestInnerObject());
        Assert.assertTrue(json.contains("field1"));
        Assert.assertFalse(json.contains("field2"));
    }

    @Test
    public void shouldSerializationWithExcludeFieldsAndRelationshipInInnerClass() {
        final Gson serializer = gson(new String[] { "field2", "relationship1" });
        final String json = serializer.toJson(new TestInnerObject());
        Assert.assertTrue(json.contains("field1") && json.contains("relationship2"));
        Assert.assertFalse(json.contains("field2") && json.contains("relationship1"));
    }

    @Test
    public void shouldSerializationWithExcludeFieldsComplex() {
        final Gson serializer = gson(new String[] { "field2", "TestRelationship1Object.field2" });
        final String json = serializer.toJson(new TestObject());
        Assert.assertEquals(
                "{\"field1\":\"Java\",\"field3\":\"Json\","
                        + "\"relationship1\":{\"java\":\"field1\",\"json\":\"field3\"},"
                        + "\"relationship2\":{\"java\":\"field1\",\"json\":\"field3\",\"relationship1\":{\"java\":\"field1\",\"json\":\"field3\"}}}",
                json);
    }

    @Test
    public void shouldSerializationWithExcludeFieldsComplex2() {
        final Gson serializer = gson(new String[] { "field2", "TestRelationship1Object.field2",
                "TestRelationship2Object.relationship1" });
        final String json = serializer.toJson(new TestObject());
        Assert.assertEquals("{\"field1\":\"Java\",\"field3\":\"Json\","
                + "\"relationship1\":{\"java\":\"field1\",\"json\":\"field3\"},"
                + "\"relationship2\":{\"java\":\"field1\",\"json\":\"field3\"}}", json);
    }

    @Test
    public void shouldSerializationWithExcludeFieldsComplexInInnerClass() {
        final Gson serializer = gson(new String[] { "field2", "TestInnerRelationship1Object.field2" });
        final String json = serializer.toJson(new TestInnerObject());
        Assert.assertEquals(
                "{\"field1\":\"Java\",\"field3\":\"Json\","
                        + "\"relationship1\":{\"java\":\"field1\",\"json\":\"field3\"},"
                        + "\"relationship2\":{\"java\":\"field1\",\"json\":\"field3\",\"relationship1\":{\"java\":\"field1\",\"json\":\"field3\"}}}",
                json);
    }

    @Test
    public void shouldSerializationWithExcludeFieldsComplex2InInnerClass() {
        final Gson serializer = gson(new String[] { "field2", "TestInnerRelationship1Object.field2",
                "TestInnerRelationship2Object.relationship1" });
        final String json = serializer.toJson(new TestInnerObject());
        Assert.assertEquals("{\"field1\":\"Java\",\"field3\":\"Json\","
                + "\"relationship1\":{\"java\":\"field1\",\"json\":\"field3\"},"
                + "\"relationship2\":{\"java\":\"field1\",\"json\":\"field3\"}}", json);
    }

}
