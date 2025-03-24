package io.hhplus.tdd.exception;

public class PointHistoryNotFoundException extends RuntimeException{
    public PointHistoryNotFoundException(long id){
        super("조회한 ID " + id + "에 대한 포인트 내역이 없습니다.");
    }
}
