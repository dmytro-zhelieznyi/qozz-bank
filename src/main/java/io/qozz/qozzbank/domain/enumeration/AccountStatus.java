package io.qozz.qozzbank.domain.enumeration;

import lombok.Getter;

/**
 * AccountStatus represents the current state of a bank account.
 * It is used to control which operations are allowed on the account.
 */
@Getter
public enum AccountStatus {
    ACTIVE("Active"),       // Account is fully operational.
    INACTIVE("Inactive"),   // Temporarily inactive, cannot perform transactions.
    FROZEN("Frozen"),       // Blocked due to suspicious activity or compliance.
    CLOSED("Closed");       // Account permanently closed.

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the enum constant matching the given human-readable value.
     *
     * @param value human-readable name of the account status
     * @return AccountStatus enum
     * @throws IllegalArgumentException if no match found
     */
    public static AccountStatus fromValue(String value) {
        for (AccountStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown AccountStatus value: " + value);
    }
}
