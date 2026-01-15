package io.qozz.qozzbank.enumeration;

import lombok.Getter;

/**
 * CardStatus represents the current state of a card (physical or virtual).
 * It is used to control which operations are allowed with the card.
 */
@Getter
public enum CardStatus {
    INACTIVE("Inactive"),   // Card is created but not yet active.
    ACTIVE("Active"),       // Card is active and can be used for payments.
    BLOCKED("Blocked"),     // Card blocked due to fraud, lost/stolen or security reasons.
    EXPIRED("Expired"),     // Card past its expiry date.
    SUSPENDED("Suspended"); // Temporarily suspended by bank or user.

    private final String value;

    CardStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the enum constant matching the given human-readable value.
     *
     * @param value human-readable name of the card status
     * @return CardStatus enum constant
     * @throws IllegalArgumentException if no match found
     */
    public static CardStatus fromValue(String value) {
        for (CardStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown CardStatus value: " + value);
    }
}
