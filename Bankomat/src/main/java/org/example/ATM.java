package org.example;

public class ATM {

    private final Bank bank;
    private BankCard bankCard;

    public ATM(Bank bank) {
        this.bank = bank;
        this.bankCard = null;
    }

    public boolean isCardPresent() {
        if (bankCard == null) {
            System.out.println("No card present. isCardPresent()");
            return false;
        }
        return true;
    }

    private boolean isCardLocked() {
        if (bank.isCardLocked(bankCard.getCardId())) {
            System.out.println("Card is locked and cannot be used. isCardLocked()");
            return true;
        }
        return false;
    }

    public void insertCard(BankCard card) {
        if (bank.isCardLocked(card.getCardId())) {
            System.out.println("Card is locked and cannot be used. insertCard()");
            this.bankCard = null;
        } else {
            System.out.println("Card is not locked and can be used. insertCard()");
            this.bankCard = card;
        }
    }

    public boolean submitPin(String pinCode) {
        if (!isCardPresent()) return false;
        isCardLocked();

        if (bank.validatePin(bankCard.getCardId(), pinCode)) {
            //If correct user get access to the functionality of the ATM and failed attempts are reset
            bankCard.resetFailedAttempts();
            System.out.println("PinCode was correct. submitPin()");
            return true;
        } else {
            //If incorrect user gets message and is locked out.
            bankCard.incrementFailedAttempts();
            int attemptsLeft = 3 - bankCard.getFailedAttempts();
            if (attemptsLeft <= 0) {
                bank.lockCard(bankCard.getCardId());
                System.out.println("Card is locked after 3 attempts and cannot be used. submitPin()");
            } else {
                System.out.println("Wrong pin, you have: " + attemptsLeft + " attempts left.");
            }
            return false;
        }

    }

    public void deposit(int cardId, double amount) {
        if (isCardPresent()) {
            isCardLocked();
            bank.deposit(cardId, amount);
        }
    }

    public void withdraw(int cardId, double amount) {
        if (isCardPresent() && bank.getBalance(cardId) >= amount) {
            isCardLocked();
            bank.withdraw(cardId, amount);
        } else {
            System.out.println("Insufficient balance for withdrawal.");
        }
    }

    public double checkBalance() {
        if (isCardPresent() && !isCardLocked()) {
            return bank.getBalance(bankCard.getCardId());
        }
        System.out.println("Cannot check balance; no card present or card is locked.");
        return -1; //Indicates an error in retrieving balance
    }

    public void cancelTransaction() {
        if (isCardPresent()) {
            System.out.println("Transaction canceled. Card ejected.");
            this.bankCard = null;
        } else {
            System.out.println("No card to eject.");
        }
    }


}
