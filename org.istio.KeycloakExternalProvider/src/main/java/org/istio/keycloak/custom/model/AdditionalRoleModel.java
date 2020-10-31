package org.istio.keycloak.custom.model;

import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;

import java.util.*;

/**
 *  Example class for additional role
 */
public class AdditionalRoleModel implements RoleModel  {
    protected RealmModel realm;


    public AdditionalRoleModel(RealmModel realm) {
        this.realm = realm;
    }

    @Override
    public String getName() {
        return "additional-role";
    }

    @Override
    public String getDescription() {
        return "Additional role";
    }

    @Override
    public void setDescription(String s) {
    }

    @Override
    public String getId() {
        return "additional-role-id";
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void addCompositeRole(RoleModel roleModel) {

    }

    @Override
    public void removeCompositeRole(RoleModel roleModel) {

    }

    @Override
    public Set<RoleModel> getComposites() {
        return Collections.emptySet();
    }

    @Override
    public boolean isClientRole() {
        return false;
    }

    @Override
    public String getContainerId() {
        return realm.getId();
    }

    @Override
    public RoleContainerModel getContainer() {
        return realm;
    }

    @Override
    public boolean hasRole(RoleModel roleModel) {
        return roleModel==this;
    }

    @Override
    public void setSingleAttribute(String s, String s1) {

    }

    @Override
    public void setAttribute(String s, Collection<String> collection) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public String getFirstAttribute(String s) {
        return "";
    }

    @Override
    public List<String> getAttribute(String s) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return Collections.emptyMap();
    }
}
