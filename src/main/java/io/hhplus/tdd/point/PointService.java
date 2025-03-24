package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class PointService {
    private static final Logger log = LoggerFactory.getLogger(PointService.class);
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint getUserPoint(long id) throws Exception {
        // 유저 포인트 조회
        // 숫자가 아닌 문자가 들어올 경우 -> 타입 에러
        // 빈 문자열이 들어오는 경우       -> 타입 에러
        // 아무 값도 들어오지 않을 경우     -> 경로 에러
        // long 범위 밖의 숫자가 들어오는 경우   -> 타입 에러 뜲
        // null이 들어오는 경우           -> 에초에 들어올수 없음
        // 음수가 들어오는 경우          -> 예외 처리 필요
        // 0이 들어오는 경우           -> 예외 처리 필요
        // 유저가 없는 경우 -> 새로운 유저 등록 UserPoint.empty()
        if(id < 0){throw new Exception("id 값은 음수가 될 수 없습니다.");}
        if(id == 0){throw new Exception("id 값은 0보다 커야 합니다.");}
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getPointHistory(long id) throws Exception {
        // 유저 포인트 내역 조회
        // id 0일 경우
        // id 음수일 경우
        // 내역테이블이 하나도 없을 경우 list [] 빈값 생성
        if(id < 0){throw new Exception("id 값은 음수가 될 수 없습니다.");}
        if(id == 0){throw new Exception("id 값은 0보다 커야 합니다.");}
        List<PointHistory> history = pointHistoryTable.selectAllByUserId(id);
        if(history.size() == 0){ throw new Exception("포인트 내역이 없습니다.");}
        return history;
    }

    public UserPoint chargePoint(long id, long amount) throws Exception {

        // 유저 포인트 충전
        // 유전 포인트 + 충전 포인트
        // 유저 충전 내역 추가
        // id 0일 경우
        // id 음수일 경우
        // amount 음수일 경우 -> 충전이기에 예외 처리 필요
        if(id < 0){throw new Exception("id 값은 음수가 될 수 없습니다.");}
        if(id == 0){throw new Exception("id 값은 0보다 커야 합니다.");}
        if(amount < 0){throw new Exception("amount 양은 음수일 수 없습니다.");}
        UserPoint currentPoint = userPointTable.selectById(id);
        UserPoint updatedPoint = userPointTable.insertOrUpdate(id, currentPoint.point() + amount);

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        
        return updatedPoint;
    }

    public UserPoint usePoint(long id, long amount) throws Exception {

        // 유저 포인트 사용
        // 유저 포인트 - 사용한 포인트
        // id 0일 경우
        // id 음수일 경우
        // amount 음수일 경우 -> 충전이기에 예외 처리 필요
        // 기존 포인트 보다 사용할 포인트 양이 큼 예외 처리
        if(id < 0){throw new Exception("id 값은 음수가 될 수 없습니다.");}
        if(id == 0){throw new Exception("id 값은 0보다 커야 합니다.");}
        if(amount < 0){throw new Exception("amount 양은 음수일 수 없습니다.");}

        UserPoint currentPoint = userPointTable.selectById(id);
        if (currentPoint.point() < amount) {}

        // 유저 포인트 수정
        UserPoint updatedPoint = userPointTable.insertOrUpdate(id, currentPoint.point() - amount);
        
        // 유저 포인트 내역 테이블에 추가
        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        
        return updatedPoint;
    }
}
