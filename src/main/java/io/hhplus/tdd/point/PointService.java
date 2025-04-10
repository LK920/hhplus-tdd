package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.*;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class PointService {
    private static final Logger log = LoggerFactory.getLogger(PointService.class);
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final PointValidator pointValidator;
    private static long MAX_BALANCE = 100000l;
    private final ReentrantLock lock = new ReentrantLock();

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable, PointValidator pointValidator) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
        this.pointValidator = pointValidator;
    }

    public UserPoint getUserPoint(long id)  {
        // 유저 포인트 조회
        pointValidator.validateId(id);
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getPointHistory(long id) {
        // 유저 포인트 내역 조회

        pointValidator.validateId(id);
        List<PointHistory> history = pointHistoryTable.selectAllByUserId(id);
        if(history.size() == 0){ throw new PointHistoryNotFoundException(id);}
        return history;
    }

    public UserPoint chargePoint(long id, long amount) {

        /*
        * 유저 포인트 충전
        * 유전 포인트 + 충전 포인트
        * 유저 충전 내역 추가
        */

        lock.lock();
        try{
            pointValidator.validateId(id);
            pointValidator.validateAmount(amount);
            UserPoint currentPoint = userPointTable.selectById(id);
            long sumPoint = currentPoint.point() + amount;
            if(sumPoint > MAX_BALANCE){
                throw new MaxBalanceExceededException(currentPoint.point(), MAX_BALANCE);
            }
            UserPoint updatedPoint = userPointTable.insertOrUpdate(id, sumPoint);
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            return updatedPoint;
        } finally {
            lock.unlock();
        }
    }

    public UserPoint usePoint(long id, long amount) {

        // 유저 포인트 사용
        // 유저 포인트 - 사용한 포인트
        lock.lock();
        try {
            pointValidator.validateId(id);
            pointValidator.validateAmount(amount);
            UserPoint currentPoint = userPointTable.selectById(id);
            if (currentPoint.point() < amount) {
                throw new BalanceNotEnoughException(currentPoint.point());
            }

            // 유저 포인트 수정
            UserPoint updatedPoint = userPointTable.insertOrUpdate(id, currentPoint.point() - amount);

            // 유저 포인트 내역 테이블에 추가
            pointHistoryTable.insert(id, currentPoint.point() - amount, TransactionType.USE, System.currentTimeMillis());

            return updatedPoint;
        } finally {
            lock.unlock();
        }

    }
}
