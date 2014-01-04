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

import java.lang.reflect.Method;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;

import es.cenobit.struts2.json.annotations.Json;

public class JsonService implements JsonServices {

	@Override
	public String[] determineExcludedFieldsNames(Class<?> actionClass, String methodName) {
		Json jsonAnnotation = AnnotationUtils.findAnnotation(actionClass, Json.class);
		if (jsonAnnotation != null) {
			return jsonAnnotation.exclude();
		}

		Method[] methods = actionClass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Json json = method.getAnnotation(Json.class);
				return json.exclude();
			}
		}

		return new String[0];
	}

	@Override
	public String[] determineExcludedFieldsNames(ActionConfig actionConfig) {
		return determineExcludedFieldsNames(loadClass(actionConfig), actionConfig.getName());
	}

	@Override
	public Json getJsonAnnotation(Class<?> actionClass, String methodName) {
		Json jsonAnnotation = AnnotationUtils.findAnnotation(actionClass, Json.class);
		if (jsonAnnotation != null) {
			return jsonAnnotation;
		}

		Method[] methods = actionClass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Json json = method.getAnnotation(Json.class);
				if (json != null) {
					return json;
				}
			}
		}

		return null;
	}

	@Override
	public Json getJsonAnnotation(ActionConfig actionConfig) {
		return getJsonAnnotation(loadClass(actionConfig), actionConfig.getName());
	}

	private Class<?> loadClass(ActionConfig actionConfig) {
		try {
			return ClassLoaderUtil.loadClass(actionConfig.getClassName(), this.getClass());
		} catch (ClassNotFoundException e) {
			try {
				return ActionContext.getContext().getInstance(ObjectFactory.class)
						.getClassInstance(actionConfig.getClassName());
			} catch (ClassNotFoundException ex) {
				try {
					return getClassLoader().loadClass(actionConfig.getClassName());
				} catch (ClassNotFoundException exc) {
					throw new RuntimeException("Invalid action class configuration that references an unknown "
							+ "class named [" + actionConfig.getClassName() + "]", exc);
				}
			}
		}
	}

	/**
	 * this class loader interface can be used by other plugins to lookup
	 * resources from the bundles. A temporary class loader interface is set
	 * during other configuration loading as well
	 * 
	 * @return ClassLoaderInterface (BundleClassLoaderInterface)
	 */
	private ClassLoaderInterface getClassLoader() {
		Map<String, Object> application = ActionContext.getContext().getApplication();
		if (application != null) {
			return (ClassLoaderInterface) application.get(ClassLoaderInterface.CLASS_LOADER_INTERFACE);
		}
		return null;
	}
}
