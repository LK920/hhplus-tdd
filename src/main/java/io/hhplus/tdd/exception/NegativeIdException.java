package io.hhplus.tdd.exception;

public class NegativeIdException extends RuntimeException{
    public NegativeIdException(){
        super("ID 값은 음수가 될 수 없습니다.");
    }
}
