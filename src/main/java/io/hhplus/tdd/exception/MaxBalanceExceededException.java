package io.hhplus.tdd.exception;

public class MaxBalanceExceededException extends RuntimeException {
    public MaxBalanceExceededException(long amount, long maxBalance){
        super("충전시 최대 잔고량(" + maxBalance + ")을 넘길 수 없습니다. 현재 잔고량 : " + amount);
    }
}
