package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.NegativeIdException;
import io.hhplus.tdd.exception.NegativePointException;
import io.hhplus.tdd.exception.ZeroIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class PointValidatorTest {

    private static final PointValidator validator = new PointValidator();


    @Test
    @DisplayName("id가 음수면 예외 발생")
    void validateNegativeId(){
        assertThatThrownBy(()-> validator.validateId(-1)).isInstanceOf(NegativeIdException.class);
    }

    @Test
    @DisplayName("id가 0이면 예외 발생")
    void validateZeroId(){
        assertThatThrownBy(()-> validator.validateId(0)).isInstanceOf(ZeroIdException.class);
    }

    @Test
    @DisplayName("amount가 음수면 예외 발생")
    void validateAmount(){
        assertThatThrownBy(()-> validator.validateAmount(-1)).isInstanceOf(NegativePointException.class);
    }
}
