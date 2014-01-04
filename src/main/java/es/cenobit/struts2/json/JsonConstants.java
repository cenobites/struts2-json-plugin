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

public class JsonConstants {
	public static final String JSON_ACTION_CONFIG_BUILDER = "struts.json.actionConfigBuilder";
	public static final String JSON_SERVICES = "struts.json.jsonServices";

	// Support for OSGi and Convention integration
	public static final String CONVENTION_ACTION_CONFIG_BUILDER = "struts.convention.actionConfigBuilder";
	public static final String CONVENTION_ACTION_CONFIG_BUILDER_CLASS = "org.apache.struts2.convention.ActionConfigBuilder";
	public static final String CONVENTION_PACKAGE_PROVIDER_CLASS = "org.apache.struts2.convention.ClasspathPackageProvider";
}
