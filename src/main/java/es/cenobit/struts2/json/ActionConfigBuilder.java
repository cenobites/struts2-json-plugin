package es.cenobit.struts2.json;

public interface ActionConfigBuilder {

    void buildActionConfigs();

    boolean needsReload();

    void destroy();
}