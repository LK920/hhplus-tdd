package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(SpringExtension.class)
@Import({PointService.class, UserPointTable.class, PointHistoryTable.class, PointValidator.class})
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private PointValidator pointValidator;

    @BeforeEach
    void setUp(){
        userPointTable.selectById(1l); // 유저 생성
    }

    @Test
    @DisplayName("유저 포인트 조회")
    void getUserPointTest(){
        // given
        long id = 1l;
        // when
        UserPoint userPoint = pointService.getUserPoint(id);
        // then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("유저 내역 조회")
    void getPointHistoryTest(){
        long id = 1l;
        long amount = 500l;
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis()); // 내역 준비

        List<PointHistory> histories = pointService.getPointHistory(id);

        assertThat(histories).isNotEmpty().hasSize(1).extracting("userId", "amount", "type")
                .containsExactly(
                        tuple(id, amount, TransactionType.CHARGE)
                );
    }

    @Test
    @DisplayName("유저 포인트 충전")
    void chargePointTest(){
        // given
        long id = 1l;
        long amount = 1000l;
        UserPoint userPoint = userPointTable.selectById(id);

        // when
        UserPoint updatedPoint = pointService.chargePoint(id, amount);

        // then
        assertThat(updatedPoint.id()).isEqualTo(id);
        assertThat(updatedPoint.point()).isEqualTo(userPoint.point() + amount);

        // 포인트 저장 검증
        List<PointHistory> pointHistoryList = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistoryList)
                .isNotEmpty()
                .hasSize(1)
                .extracting("userId", "amount", "type")
                .containsExactly(
                        tuple(id, userPoint.point()+amount, TransactionType.CHARGE)
                );
    }

    @Test
    @DisplayName("유저 포인트 사용")
    void usePointTest(){
        // given
        long id = 1l;
        long charge = 10000l;
        long use = 500l;
        UserPoint chargedPoint = pointService.chargePoint(id,charge); // 포인트 준비

        // when
        UserPoint updatedPoint = pointService.usePoint(id, use);

        // then
        assertThat(updatedPoint.id()).isEqualTo(id);
        assertThat(updatedPoint.point()).isEqualTo(chargedPoint.point() - use);

        List<PointHistory> pointHistoryList = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistoryList).isNotEmpty().hasSize(2)
                .extracting("userId", "amount", "type")
                .containsExactly(
                        tuple(id, chargedPoint.point(), TransactionType.CHARGE),
                        tuple(id, updatedPoint.point(), TransactionType.USE)
                );
    }

}
