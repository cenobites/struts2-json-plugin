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
package es.cenobit.struts2.json.osgi;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import es.cenobit.struts2.json.ActionConfigBuilder;
import es.cenobit.struts2.json.JsonConstants;

/**
 * <p>
 * This class is a configuration provider for the XWork configuration system.
 * This is really the only way to truly handle loading of the packages, actions
 * and results correctly. This doesn't contain any logic and instead delegates
 * to the configured instance of the {@link ActionConfigBuilder} interface.
 * </p>
 * 
 * <b>Support for OSGi</b>
 */
public class ClasspathPackageProvider implements PackageProvider {

	private ActionConfigBuilder actionConfigBuilder;

	@Inject
	public ClasspathPackageProvider(Container container) {
		this.actionConfigBuilder = container.getInstance(ActionConfigBuilder.class,
				container.getInstance(String.class, JsonConstants.JSON_ACTION_CONFIG_BUILDER));
	}

	public void init(Configuration configuration) throws ConfigurationException {
	}

	public boolean needsReload() {
		return actionConfigBuilder.needsReload();
	}

	public void loadPackages() throws ConfigurationException {
		actionConfigBuilder.buildActionConfigs();
	}
}
