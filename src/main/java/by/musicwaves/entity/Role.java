package by.musicwaves.entity;

import java.util.Arrays;

public enum Role
{
    /** dummy role to be used when we cannot find role by provided id */
    UNKNOWN(0, "unknown", false),
    
    /** user with basic rights */
    USER(1, "user", true),
    
    /** user can add new and alter already existing music related entities */
    MUSIC_CURATOR(2, "curator", true),
    
    /** user can change other users' roles and delete users */
    ADMINISTRATOR(3, "administrator", true);
    
    
    int databaseId;
    String propertyKey;
    boolean validOption;
    
    // check that used id-s are unique
    static {
        int rolesCount = values().length;
        long uniqueRoleIdCount = Arrays.stream(values())
                .mapToInt(role -> role.databaseId)
                .distinct()
                .count();
        
        if (rolesCount != uniqueRoleIdCount) {
            throw new RuntimeException("Non-unique database id found during Role enum initialization");
        }
                
    }
    
    
    Role(int databaseId, String propertyKey, boolean validOption) {
        this.databaseId = databaseId;
        this.propertyKey = propertyKey;
        this.validOption = validOption;
    }
    
    public static Role getByDatabaseId(int id) {
        return Arrays.stream(values())
                .filter(role -> role.databaseId == id)
                .findAny()
                .orElse(Role.UNKNOWN);
    }
    
    public int getDatabaseId() {
        return this.databaseId;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public boolean isValidOption() {
        return validOption;
    }
}
