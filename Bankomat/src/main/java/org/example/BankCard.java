package org.example;

public class BankCard {
    private final int cardId;
    private final String pinCode;
    private final boolean locked;
    private int failedAttempts;

    public BankCard(int cardId, String pinCode) {
        this.cardId = cardId;
        this.pinCode = pinCode;
        this.locked = false;
        this.failedAttempts = 0;
    }

    public int getCardId() {
        return cardId;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }


    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }
}