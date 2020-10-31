package org.istio.keycloak.custom.provider;

import org.istio.keycloak.custom.model.UserAdapter;
import org.jboss.logging.Logger;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendOIDCProtocolMapper extends AbstractOIDCProtocolMapper implements
        OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    private static final Logger logger = Logger.getLogger(BackendOIDCProtocolMapper.class);

    public static final String PROVIDER_ID = "oidc-backend-protocol-mapper";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "Backend mapper";
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "some help text";
    }

    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession keycloakSession,
                                            UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        UserModel user = userSession.getUser();
        List<String> attribute = user.getAttribute("backend-session");
        if (attribute!=null && !attribute.isEmpty()) {
            token.getOtherClaims().put("backend-session", attribute.get(0));
        }
        setClaim(token, mappingModel, userSession, keycloakSession, clientSessionCtx);
        return token;
    }

    public static ProtocolMapperModel create(String name,
                                             boolean accessToken, boolean idToken, boolean userInfo) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<>();
//        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, hardcodedName);
//        config.put(CLAIM_VALUE, hardcodedValue);
//        config.put(OIDCAttributeMapperHelper.JSON_TYPE, claimType);
        config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        mapper.setConfig(config);
        return mapper;
    }

}

