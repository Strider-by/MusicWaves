package by.musicwaves.entity;

import java.util.Arrays;

public enum Role
{
    /** dummy role to be used when we cannot find role by provided id */
    UNKNOWN(0),
    
    /** user that needs to prove to be the owner of the e-mail used for registration */
    MAIL_CONFIRMATION_REQUIRED(1),
    
    /** user with basic rights */
    USER(2),
    
    /** user can add new and alter already existing music related entities */
    MUSIC_CURATOR(3),
    
    /** user can change other users' roles (excepr for other administrators') */
    ADMINISTRATOR(4),
    
    /** user that can change all other users' roles */
    SUPER_ADMINISTRATOR(5),
    
    /** temporaly banned user (no saved playlists and favourite items were deleted) */
    SUSPENDED(6),
    
    /** constantly banned user (saved playlists and favourite items are deleted */
    BANNED(7);
    
    
    int databaseId;
    
    // check that used id-s are unique
    static
    {
        int rolesCount = values().length;
        long uniqueRoleIdCount = Arrays.stream(values())
                .mapToInt(role -> role.databaseId)
                .distinct()
                .count();
        
        if (rolesCount != uniqueRoleIdCount)
        {
            throw new RuntimeException("Non-unique database id found during Role enum initialization");
        }
                
    }
    
    
    private Role(int databaseId) 
    {
        this.databaseId = databaseId;
    }
    
    public static Role getByDatabaseId(int id)
    {
        return Arrays.stream(values())
                .filter(role -> role.databaseId == id)
                .findAny()
                .orElse(Role.UNKNOWN);
    }
    
    public int getDatabaseId()
    {
        return this.databaseId;
    }
    
    
    
}
