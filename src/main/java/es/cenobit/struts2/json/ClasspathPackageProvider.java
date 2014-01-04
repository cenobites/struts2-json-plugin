package es.cenobit.struts2.json;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

/**
 * <p>
 * This class is a configuration provider for the XWork configuration system.
 * This is really the only way to truly handle loading of the packages, actions
 * and results correctly. This doesn't contain any logic and instead delegates
 * to the configured instance of the {@link ActionConfigBuilder} interface.
 * </p>
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
