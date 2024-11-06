package org.example;

public interface Bank {

    static String getBankName() {
        return "Mockito Bank";
    }

    boolean isCardLocked(int cardId);

    boolean validatePin(int cardId, String pinCode);

    boolean lockCard(int cardId);

    double getBalance(int cardId);

    void deposit(int cardId, double amount);

    void withdraw(int cardId, double amount);

}
