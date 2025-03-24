package io.hhplus.tdd.exception;

public class ZeroIdException extends RuntimeException {
    public ZeroIdException(){
        super("ID 값은 0보다 커야 합니다.");
    }
}
