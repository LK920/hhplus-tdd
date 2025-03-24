package io.hhplus.tdd.exception;

public class NegativePointException extends RuntimeException{
    public NegativePointException(){
        super("포인트는 음수일 수 없습니다.");
    }
}
