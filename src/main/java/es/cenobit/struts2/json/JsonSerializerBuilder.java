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

import java.util.Date;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class JsonSerializerBuilder {

	public static GsonBuilder builder() {
		return new GsonBuilder() //
				.registerTypeAdapter(Date.class, new DateTypeAdapter());
	}

	public static Gson create() {
		return builder() //
				.create();
	}

	public static Gson create(Set<String> fieldsToExclude, Class<?>... classes) {
		return builder() //
				.setExclusionStrategies(new JsonSerializerExclusionStrategy(fieldsToExclude, classes)) //
				.create();
	}
}
