package io.beanmapper.config;

/**
 * The handler which is used to see if a Principal can have access to a @BeanRoleSecured.
 */
public interface RoleSecuredCheck {

    /**
     * Checks whether the Principal has any one of the roles
     * @param roles roles of which the Principal must have at least one
     * @return true if at least one role matches, false if not
     */
    public boolean hasRole(String... roles);

}
