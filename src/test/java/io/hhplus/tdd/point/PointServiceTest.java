package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    // 실제 테스트 객체
    public PointService pointService;

    @Mock // 가짜 객체 생성(테스트하려는 객체의 의존성 제거)
    public PointHistoryTable pointHistoryTable;
    @Mock
    public UserPointTable userPointTable;

    @BeforeEach // @Test 시작전에 수행하는 작업
    void setUp(){
        // 모든 테스트 메서드에서 서비스 객체를 생성하는 코드 반복 작성을 막기 위해
        // 메소드 실행 전 서비스 생성되어 이전 테스트의 상태가 다음 테스트에 영향을 주지 못함 ==> 독립성 보장
        // 유지 보수성
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Test
    @DisplayName("음수 ID로 포인트 조회 시 NegativeIdException 발생")
    void 유저_포인트_조회_시_음수_id일_경우_예외_발생(){
        // assertThatThrownBy() 함수는 예외 발생 여부 검증하기 위한 메소드
        // getUserPoint함수를 실행해서 예외가 발생한다면 isInstanceOf함수로 비교하여 검증
        assertThatThrownBy(()->pointService.getUserPoint(-1)).isInstanceOf(NegativeIdException.class);
    }

    @Test
    @DisplayName("0 id로 포인트 조회시 zeroIdException 발생")
    void 유저_포인트_조회_시_id가_0일_때_예외_발생(){
        assertThatThrownBy(()->pointService.getUserPoint(0)).isInstanceOf(ZeroIdException.class);
    }

    @Test
    @DisplayName("음수 id로 포인트 내역 조회 NegativeIdException")
    void 포인트_내역_조회_시_음수_아이디일때_예외_발생(){
        assertThatThrownBy(()->pointService.getPointHistory(-1)).isInstanceOf(NegativeIdException.class);
    }

    @Test
    @DisplayName("0 id로 포인트 내역 조회 ZeroIdException")
    void 포인트_내역_조회_시_id가_0일_경우_예외(){
        assertThatThrownBy(()->pointService.getPointHistory(0)).isInstanceOf(ZeroIdException.class);
    }
    @Test
    @DisplayName("조회 시 내역이 없을 경우")
    void 포인트_내역_조회_시_내역이_없을_경우(){
        // when() : when(mock객체, 메소드(파라미터)).thenReturn(반환값)
        // when() 메소드가 호출되면 반환값을 반환해라 -> stub
        when(pointHistoryTable.selectAllByUserId(anyLong())).thenReturn(new ArrayList<>());
        // 위에서 목객체의 행위를 정의 했기에 아래에서 어떠한 값을 넣어도 에러발생
        assertThatThrownBy(()->pointService.getPointHistory(1l)).isInstanceOf(PointHistoryNotFoundException.class);
    }

    @Test
    void 포인트_충전_시_id가_0일_경우_예외_발생(){
        assertThatThrownBy(()->pointService.chargePoint(0,1)).isInstanceOf(ZeroIdException.class);
    }
    @Test
    void 포인트_충전_시_id가_음수일_경우_예외_발생(){
        assertThatThrownBy(()->pointService.chargePoint(-1,1)).isInstanceOf(NegativeIdException.class);
    }
    @Test
    void 포인트_충전_시_amount가_음수일_경우_예외_발생(){
        long id = 1;
        long amount = -1;
        assertThatThrownBy(()->pointService.chargePoint(id,amount)).isInstanceOf(NegativePointException.class);
    }
    @Test
    void 포인트_충전_시_최대잔고_초과_예외_발생(){
        UserPoint userPoint = new UserPoint(1,100000,System.currentTimeMillis());
        when(userPointTable.selectById(anyLong())).thenReturn(userPoint);
        assertThatThrownBy(()->pointService.chargePoint(1,2)).isInstanceOf(MaxBalanceExceededException.class);
    }

    @Test
    void 포인트_사용_시_id가_음수일_경우_예외_발생(){
        long id = -1;
        long amount = 9;
        assertThatThrownBy(()->pointService.usePoint(id,amount)).isInstanceOf(NegativeIdException.class);
    }
    @Test
    void 포인트_사용_시_id가_0일_경우_예외_발생(){
        assertThatThrownBy(()->pointService.usePoint(0,1)).isInstanceOf(ZeroIdException.class);
    }
    @Test
    void 포인트_사용_시_amount가_음수일_경우_예외_발생(){
        long amount = -1;
        assertThatThrownBy(()->pointService.usePoint(1,amount)).isInstanceOf(NegativePointException.class);
    }

    @Test
    void 포인트_사용_시_잔고_부족_예외_발생(){
        UserPoint userPoint = new UserPoint(1,100000,System.currentTimeMillis());
        when(userPointTable.selectById(anyLong())).thenReturn(userPoint);
        assertThatThrownBy(()->pointService.chargePoint(1,2)).isInstanceOf(MaxBalanceExceededException.class);
    }

}
