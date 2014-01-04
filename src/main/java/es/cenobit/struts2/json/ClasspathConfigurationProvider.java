package es.cenobit.struts2.json;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherListener;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.location.LocatableProperties;

/**
 * <p>
 * Xwork will only reload configurations, if one ContainerProvider needs
 * reloading, that's all this class does
 * </p>
 */
public class ClasspathConfigurationProvider implements ConfigurationProvider, DispatcherListener {

    private ActionConfigBuilder actionConfigBuilder;
    private boolean devMode;
    private boolean reload;
    private boolean listeningToDispatcher;

    @Inject
    public ClasspathConfigurationProvider(Container container) {
        this.actionConfigBuilder = container.getInstance(ActionConfigBuilder.class,
                container.getInstance(String.class, JsonConstants.JSON_ACTION_CONFIG_BUILDER));
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = "true".equals(mode);
    }

    @Inject("struts.json.classes.reload")
    public void setReload(String reload) {
        this.reload = "true".equals(reload);
    }

    /**
     * Not used.
     */
    public void destroy() {
        if (this.listeningToDispatcher)
            Dispatcher.removeDispatcherListener(this);
        actionConfigBuilder.destroy();
    }

    /**
     * Not used.
     */
    public void init(Configuration configuration) {
        if (devMode && reload && !listeningToDispatcher) {
            // this is the only way I found to be able to get added to to
            // ConfigurationProvider list
            // listening to events in Dispatcher
            listeningToDispatcher = true;
            Dispatcher.addDispatcherListener(this);
        }
    }

    /**
     * Does nothing.
     */
    public void register(ContainerBuilder containerBuilder, LocatableProperties locatableProperties)
            throws ConfigurationException {
    }

    /**
     * Loads the packages using the {@link ActionConfigBuilder}.
     * 
     * @throws ConfigurationException
     */
    public void loadPackages() throws ConfigurationException {
    }

    /**
     * Depends on devMode, relead and actionConfigBuilder.needsReload()
     * 
     * @return Always false.
     */
    public boolean needsReload() {
        return devMode && reload && actionConfigBuilder.needsReload();
    }

    public void dispatcherInitialized(Dispatcher du) {
        du.getConfigurationManager().addContainerProvider(this);
    }

    public void dispatcherDestroyed(Dispatcher du) {
    }
}