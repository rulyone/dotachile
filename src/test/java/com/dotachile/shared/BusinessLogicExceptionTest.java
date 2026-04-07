package com.dotachile.shared;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BusinessLogicExceptionTest {

    @Test
    void noArgConstructorHasNullMessage() {
        BusinessLogicException ex = new BusinessLogicException();
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    void messageConstructorPropagatesMessage() {
        BusinessLogicException ex = new BusinessLogicException("clan banned");
        assertThat(ex.getMessage()).isEqualTo("clan banned");
    }

    @Test
    void isACheckedException() {
        // Important: callers expect this to be checked so business rules are
        // enforced at compile time on service signatures.
        assertThat(Exception.class).isAssignableFrom(BusinessLogicException.class);
        assertThat(RuntimeException.class.isAssignableFrom(BusinessLogicException.class)).isFalse();
    }
}
