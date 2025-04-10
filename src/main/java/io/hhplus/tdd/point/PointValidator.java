package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.NegativeIdException;
import io.hhplus.tdd.exception.NegativePointException;
import io.hhplus.tdd.exception.ZeroIdException;

public class PointValidator {
    public void validateId(long id){
        if(id < 0){
            throw new NegativeIdException();
        }
        if(id == 0){
            throw  new ZeroIdException();
        }
    }

    public void validateAmount(long amount){
        if(amount < 0){
            throw new NegativePointException();
        }
    }
}
