package org.istio.keycloak.custom.model;


import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    private static final Logger logger = Logger.getLogger(UserAdapter.class);

    private final User user;
    private final String keycloakId;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, User user) {
        super(session, realm, model);
        this.user = user;
        this.keycloakId = StorageId.keycloakId(model, user.getId());

        this.setAttribute("backend-session", Collections.singletonList("backend-session-"+user.getId()));
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return super.getAttributes();
    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        Set<RoleModel> roleMappings = super.getRoleMappings();
        RoleModel roleModelUser = KeycloakModelUtils.getRoleFromString(realm, "user");

        logger.infov("found role: {0}, {1}", roleModelUser, roleModelUser.getClass());

        if (roleModelUser!=null) {
            roleMappings.add(roleModelUser);
        }

        roleMappings.add(new AdditionalRoleModel(realm));
        return roleMappings;
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        user.setUsername(username);
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        user.setEmail(email);
    }

    @Override
    public String getFirstName() {
        return user.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        user.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        return user.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        user.setLastName(lastName);
    }
}