package io.qozz.qozzbank.domain.enumeration;

import lombok.Getter;

/**
 * TransactionStatus represents the current state of a bank transaction.
 * It is used to track processing, completion, or failure of operations.
 */
@Getter
public enum TransactionStatus {
    PENDING("Pending"),         // Transaction created but not yet processed.
    PROCESSING("Processing"),   // Transaction is being executed / validated.
    COMPLETED("Completed"),     // Transaction successfully completed.
    FAILED("Failed"),           // Transaction failed due to error or rejection.
    CANCELED("Canceled");       // Transaction canceled by user or system.

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the enum constant matching the given human-readable value.
     *
     * @param value human-readable name of the transaction status
     * @return TransactionStatus enum constant
     * @throws IllegalArgumentException if no match found
     */
    public static TransactionStatus fromValue(String value) {
        for (TransactionStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown TransactionStatus value: " + value);
    }
}
