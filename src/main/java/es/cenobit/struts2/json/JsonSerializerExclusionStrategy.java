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
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import es.cenobit.struts2.json.annotations.DontExpose;

public class JsonSerializerExclusionStrategy implements ExclusionStrategy {

    @SuppressWarnings("rawtypes")
    private final Set toExcludeClasses;
    private final Set<String> fieldsToExclude;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JsonSerializerExclusionStrategy(Set<String> fieldsToExclude, Class<?>... classes) {
        this.fieldsToExclude = fieldsToExclude;
        this.toExcludeClasses = new LinkedHashSet(Arrays.asList(classes));
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        if (f.getAnnotation(DontExpose.class) != null) {
            return true;
        }

        if (fieldsToExclude == null || fieldsToExclude.isEmpty()) {
            return false;
        }

        for (String fieldToExclude : fieldsToExclude) {
            if (fieldToExclude == null || fieldToExclude.lastIndexOf('.') == -1) {
                continue;
            }

            String[] split = fieldToExclude.split("\\.");
            if (split != null && split.length > 1) {
                String fieldClassName = (split[split.length - 2]).toLowerCase();
                String fieldAttrName = (split[split.length - 1]).toLowerCase();

                String className = (className(f)).toLowerCase();
                String classAttrName = (f.getName()).toLowerCase();

                if (className.equals(fieldClassName) && classAttrName.equals(fieldAttrName)) {
                    return true;
                }
            }
        }

        return fieldsToExclude.contains(f.getName());
    }

    private String className(FieldAttributes f) {
        String declaringClass = f.getDeclaringClass().toString();
        if (declaringClass.indexOf('$') != -1) { // Inner class
            int index = declaringClass.lastIndexOf('$');
            return declaringClass.substring(index + 1, declaringClass.length());
        } else if (declaringClass.indexOf('.') != -1) { // Public class
            int index = declaringClass.lastIndexOf('.');
            return declaringClass.substring(index + 1, declaringClass.length());
        }

        return declaringClass;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return toExcludeClasses.contains(clazz);
    }

}
