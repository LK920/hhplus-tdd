package io.hhplus.tdd.exception;

public class BalanceNotEnoughException extends RuntimeException{
    public BalanceNotEnoughException(long balance){
        super("잔고가 부족합니다. 현재 잔고: " + balance);
    }
}
