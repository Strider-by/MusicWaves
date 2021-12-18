package by.musicwaves.controller.util;

import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;

import java.util.EnumSet;
import java.util.Optional;

import static by.musicwaves.entity.Role.*;
import static java.util.EnumSet.noneOf;
import static java.util.EnumSet.of;

/**
 * Represents access levels that can be used to control if some user can get access to some Command or Page with some
 * access restrictions.
 */
public enum AccessLevelEnum {

    ALL(noneOf(Role.class)) {
        @Override
        protected boolean containsRole(Role role) {
            return true;
        }

        @Override
        public boolean isAccessGranted(User user) {
            return true;
        }
    },
    USER_PLUS(of(USER, MUSIC_CURATOR, ADMINISTRATOR)),
    MUSIC_CURATOR_PLUS(of(MUSIC_CURATOR, ADMINISTRATOR)),
    ADMINISTRATOR_ONLY(of(ADMINISTRATOR));

    private final EnumSet<Role> includedRoles;

    AccessLevelEnum(EnumSet<Role> includedRoles) {
        this.includedRoles = includedRoles;
    }

    protected boolean containsRole(Role role) {
        return this.includedRoles.contains(role);
    }

    /**
     * Checks if given user is allowed to perform actions granted by this AccessLevel instance.
     * The check is based on the Role field set in User instance.
     * Method is NPE-safe, it works correctly even when user == null or it's Role field isn't set (Role role == null).
     *
     * @param user - user to be checked. Can be null.
     * @return if access is granted.
     */
    public boolean isAccessGranted(User user) {
        return Optional.ofNullable(user)
                .map(User::getRole)
                .map(this::containsRole)
                .orElse(false);
    }

    /**
     * Checks if given user is NOT allowed to perform actions granted by this AccessLevel instance.
     * The check is based on the Role field set in User instance.
     * Method is NPE-safe, it works correctly even when user == null or it's Role field isn't set (Role role == null).
     *
     * @param user - user to be checked. Can be null.
     * @return if access is forbidden.
     */
    public boolean isAccessForbidden(User user) {
        return !isAccessGranted(user);
    }
}
