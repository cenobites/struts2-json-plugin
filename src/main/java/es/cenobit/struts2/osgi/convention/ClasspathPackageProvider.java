package es.cenobit.struts2.osgi.convention;

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
 * <b>Support for OSGi and Convention integration</b>
 */
public class ClasspathPackageProvider implements PackageProvider {

	private ActionConfigBuilder actionConfigBuilder;
	private PackageProvider packageProviderOSGi;

	@Inject
	public ClasspathPackageProvider(Container container) {
		this.actionConfigBuilder = container.getInstance(ActionConfigBuilder.class,
				container.getInstance(String.class, JsonConstants.JSON_ACTION_CONFIG_BUILDER));
		this.packageProviderOSGi = container.getInstance(PackageProvider.class,
				JsonConstants.CONVENTION_PACKAGE_PROVIDER_CLASS);
	}

	public void init(Configuration configuration) throws ConfigurationException {
		if (packageProviderOSGi != null) {
			packageProviderOSGi.init(configuration);
		}
	}

	public boolean needsReload() {
		boolean isNeedsReload = false;
		if (packageProviderOSGi != null) {
			isNeedsReload = isNeedsReload || packageProviderOSGi.needsReload();
		}
		return isNeedsReload || actionConfigBuilder.needsReload();
	}

	public void loadPackages() throws ConfigurationException {
		if (packageProviderOSGi != null) {
			packageProviderOSGi.loadPackages();
		}
		actionConfigBuilder.buildActionConfigs();
	}
}
