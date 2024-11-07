package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ATMTest {

    private Bank mockBank;
    private ATM atm;
    private BankCard bankCard;

    @BeforeEach
    void setUp() {
        mockBank = mock(Bank.class);
        atm = new ATM(mockBank);
        bankCard = new BankCard(1234, "1234");
    }

    @Test
    @DisplayName("Checking if the card is not locked and present")
    public void testIsCardValid(){
        when(mockBank.isCardLocked(1234)).thenReturn(false);
        atm.insertCard(bankCard);
        assertTrue(atm.isCardPresent(), "The card should be present");
    }

    @Test
    @DisplayName("Checking if the card is locked and therefore should fail")
    public void testIsCardInvalid(){
        when(mockBank.isCardLocked(1234)).thenReturn(true);
        atm.insertCard(bankCard);
        assertTrue(atm.isCardLocked(), "The card should not be accepted since it is locked");
    }

    @Test
    @DisplayName("Testing if the PinCode is processed correctly if correct pin is submitted")
    public void testCheckPinCode() {
        atm.insertCard(bankCard);
        when(mockBank.validatePin(1234, "1234")).thenReturn(true);
        assertTrue(atm.submitPin("1234"), "PinCode should be correct");
    }


    @ParameterizedTest
    @DisplayName("Testing different pincodes to check if theyre denied as theyre the wrong ones")
    @ValueSource(strings =  {"5678", "2345", "8912"})
    public void testWrongPinCodes(String wrongPinCodes){
        atm.insertCard(bankCard);
        when(mockBank.validatePin(1234, wrongPinCodes)).thenReturn(false);
        assertFalse(atm.submitPin(wrongPinCodes), "PinCodes should not be correct");
    }

    @Test
    @DisplayName("Making sure card is locked after 3 failed attempts on pincode")
    public void testLockCardAfterThreeFailedAttempts(){
        atm.insertCard(bankCard);
        when(mockBank.isCardLocked(1234)).thenReturn(true);
        //Using loop to try 3 inputs on the same card
        for(int i = 0; i < 2; i++){
            atm.submitPin("4567");
        }
        assertFalse(atm.submitPin("4657"), "Should be locked after 3 failed attempts");
    }

    @Test
    @DisplayName("Testing balance retrieval from ATM")
    public void testCheckBalance() {
        atm.insertCard(bankCard);
        when(mockBank.getBalance(1234)).thenReturn(1000.0);

        double balance = atm.checkBalance();

        assertEquals(1000.0, balance, "The balance should be 1000.0");
    }


    @Test
    @DisplayName("Testing deposit functionality in ATM")
    public void testDeposit() {
        atm.insertCard(bankCard);
        double depositAmount = 500.0;

        atm.deposit(bankCard.getCardId(), depositAmount);
        verify(mockBank).deposit(bankCard.getCardId(), depositAmount);
    }


    @Test
    @DisplayName("Testing successful withdrawal from ATM")
    public void testWithdrawSuccess() {
        atm.insertCard(bankCard);
        when(mockBank.getBalance(1234)).thenReturn(1000.0);
        double withdrawAmount = 200.0;

        atm.withdraw(bankCard.getCardId(), withdrawAmount);
        verify(mockBank).withdraw(bankCard.getCardId(), withdrawAmount);
    }

    @Test
    @DisplayName("Testing unsuccessful withdrawal due to insufficient balance")
    public void testWithdrawFailure() {
        atm.insertCard(bankCard);
        when(mockBank.getBalance(1234)).thenReturn(100.0);
        double withdrawAmount = 200.0;

        atm.withdraw(bankCard.getCardId(), withdrawAmount);
        verify(mockBank, never()).withdraw(bankCard.getCardId(), withdrawAmount);
    }


    @Test
    @DisplayName("Testing bank identification with mocked static method")
    public void testBankIdentification() {
        try (MockedStatic<Bank> bankMockedStatic = mockStatic(Bank.class)) {
            bankMockedStatic.when(Bank::getBankName).thenReturn("Test Bank");
            assertEquals("Test Bank", Bank.getBankName(), "The bank name should be 'Test Bank'");
        }
    }

    @Test
    @DisplayName("Testing transaction cancellation")
    public void testCancelTransaction() {
        atm.insertCard(bankCard);
        atm.cancelTransaction();
        assertFalse(atm.isCardPresent(), "Card should be ejected after canceling the transaction");
    }

}