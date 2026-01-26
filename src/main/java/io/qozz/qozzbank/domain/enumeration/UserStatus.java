package io.qozz.qozzbank.domain.enumeration;

import lombok.Getter;

/**
 * UserStatus represents the current state of a user account in the system.
 * It is used to control access, workflow, and business logic.
 */
@Getter
public enum UserStatus {
    ACTIVE("Active"),                               // User can fully use the system.
    INACTIVE("Inactive"),                           // Temporarily inactive (e.g., long inactivity).
    BLOCKED("Blocked"),                             // Blocked due to security or fraud concerns.
    PENDING_VERIFICATION("Pending Verification"),   // Registered but KYC or email not completed.
    SUSPENDED("Suspended"),                         // Temporarily frozen (e.g., suspicious activity).
    CLOSED("Closed"),                               // Account permanently closed by user or bank.
    LOCKED("Locked");                               // Temporarily locked (e.g., after failed login attempts).

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the enum constant matching the given human-readable value.
     *
     * @param value human-readable name of the user status
     * @return UserStatus enum
     * @throws IllegalArgumentException if no match found
     */
    public static UserStatus fromValue(String value) {
        for (UserStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown UserStatus value: " + value);
    }
}
