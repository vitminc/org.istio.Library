package org.istio.keycloak.custom.provider;


import org.istio.keycloak.custom.model.UserRepository;
import org.jboss.logging.Logger;
// tag::keycloak-spi-provider-factory[]
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
// end::keycloak-spi-provider-factory[]
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
// tag::keycloak-spi-provider-factory[]
import org.keycloak.storage.UserStorageProviderFactory;
// end::keycloak-spi-provider-factory[]

import java.util.List;


public class BackendStorageProviderFactory implements UserStorageProviderFactory<BackendStorageProvider> {


    private static final Logger logger = Logger.getLogger(BackendStorageProvider.class);


    static final String PROVIDER_NAME = "BackendProvider";

    @Override
    public BackendStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new BackendStorageProvider(keycloakSession, componentModel, new UserRepository());
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }



    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        // this configuration is configurable in the admin-console
        return ProviderConfigurationBuilder.create()
                .property()
                .name("backendParam")
                .label("Backend Parameter")
                .helpText("Some Description")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("default value")
                .add()
                // more properties
                // .property()
                // .add()
                .build();
    }

}

