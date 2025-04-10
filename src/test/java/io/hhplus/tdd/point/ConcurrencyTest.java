package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(SpringExtension.class)
@Import({PointService.class, PointHistoryTable.class, UserPointTable.class, PointValidator.class})
public class ConcurrencyTest {
    @Autowired
    private PointService pointService;
    @Autowired
    private PointHistoryTable pointHistoryTable;
    @Autowired
    private UserPointTable userPointTable;

    @Test
    @DisplayName("동일한 아이디로 동시 포인트 충전 테스트")
    void 동일한_아이디_동시_충전_테스트() throws InterruptedException {

        long id = 1l;
        long amount = 2000;
        long threadCount = 5;
        pointService.getUserPoint(id);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++){
            Thread thread = new Thread(()->{
                pointService.chargePoint(id,amount);
            });
            threads.add(thread);
            thread.start();
        }

        for(Thread thread: threads){
            thread.join();
        }

        UserPoint userPoint = userPointTable.selectById(id);

        assertThat(userPoint)
                .isNotNull()
                .extracting("id", "point")
                .containsExactly(id, amount*threadCount);
    }

    @Test
    @DisplayName("동일한 아이디로 동시 포인트 사용 테스트")
    void 동일한_아이디_동시_포인트_사용_테스트() throws InterruptedException {
        // Given
        long userId = 1L;
        long initialCharge = 100000L;
        long useAmount = 500L;
        int threadCount = 10;  // 동시 실행할 스레드 수
        CountDownLatch latch = new CountDownLatch(threadCount);
        pointService.chargePoint(userId, initialCharge); // 초기 포인트 충전

        // When
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    pointService.usePoint(userId, useAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                    System.out.println("CountDown : " + latch.getCount());
                }
            });
            thread.start();
        }

        latch.await();  // 모든 스레드 완료 대기

        // Then
        UserPoint finalPoint = pointService.getUserPoint(userId);
        assertThat(finalPoint.point())
                .isEqualTo(initialCharge - useAmount * threadCount); // 최종 포인트 검증
    }

}
