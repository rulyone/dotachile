package com.dotachile.elo;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EloSystemTest {

    private static final int K = 32;

    @Test
    void winnerAtEqualRatingGainsHalfK() {
        int delta = EloSystem.calculoVariacion(1500, 1500, true, K);
        assertThat(delta).isEqualTo(16); // (int)(32 * (1 - 0.5))
    }

    @Test
    void loserAtEqualRatingLosesHalfK() {
        int delta = EloSystem.calculoVariacion(1500, 1500, false, K);
        assertThat(delta).isEqualTo(-16); // (int)(32 * (0 - 0.5))
    }

    @Test
    void winnerGainAndLoserLossAreEqualMagnitudeAtEqualRating() {
        int win = EloSystem.calculoVariacion(1500, 1500, true, K);
        int loss = EloSystem.calculoVariacion(1500, 1500, false, K);
        assertThat(win).isEqualTo(-loss);
    }

    @Test
    void favoriteWinningGainsLessThanHalfK() {
        // 200-point favorite. Expected score per table at dif 198..206 is 0.76.
        // gain = (int)(32 * (1 - 0.76)) = (int)(7.68) = 7
        int delta = EloSystem.calculoVariacion(1700, 1500, true, K);
        assertThat(delta).isEqualTo(7);
    }

    @Test
    void underdogWinningGainsMoreThanHalfK() {
        // 200-point underdog. Expected score for low side at dif 198..206 is 0.24.
        // gain = (int)(32 * (1 - 0.24)) = (int)(24.32) = 24
        int delta = EloSystem.calculoVariacion(1500, 1700, true, K);
        assertThat(delta).isEqualTo(24);
    }

    @Test
    void favoriteLosingLosesAlmostFullK() {
        // 200-point favorite losing. lose = (int)(32 * (0 - 0.76)) = (int)(-24.32) = -24
        int delta = EloSystem.calculoVariacion(1700, 1500, false, K);
        assertThat(delta).isEqualTo(-24);
    }

    @Test
    void extremeFavoriteWinsAlmostNothing() {
        // dif >= 736: expected = 1.00 for favorite. gain = (int)(32 * 0) = 0
        int delta = EloSystem.calculoVariacion(3000, 1000, true, K);
        assertThat(delta).isZero();
    }

    @Test
    void extremeUnderdogWinningGainsFullK() {
        // dif >= 736 for underdog: expected = 0.00. gain = (int)(32 * 1) = 32
        int delta = EloSystem.calculoVariacion(1000, 3000, true, K);
        assertThat(delta).isEqualTo(32);
    }

    @Test
    void extremeFavoriteLosingLosesFullK() {
        int delta = EloSystem.calculoVariacion(3000, 1000, false, K);
        assertThat(delta).isEqualTo(-32);
    }

    @Test
    void zeroKFactorAlwaysReturnsZero() {
        assertThat(EloSystem.calculoVariacion(1500, 1500, true, 0)).isZero();
        assertThat(EloSystem.calculoVariacion(1500, 1700, false, 0)).isZero();
    }

    @Test
    void smallRatingGapBelowFourBehavesLikeEqual() {
        // dif <= 3: expected = 0.50 either way
        int win = EloSystem.calculoVariacion(1500, 1502, true, K);
        assertThat(win).isEqualTo(16);
    }

    @Test
    void hopelessUnderdogLosingLosesZero() {
        // Business rule: when you were expected to lose 100% of the time,
        // you lose zero ELO for actually losing. dif=5000 -> low-side expected=0.00,
        // so (int)(32 * (0 - 0)) = 0.
        int delta = EloSystem.calculoVariacion(0, 5000, false, K);
        assertThat(delta).isZero();
    }
}
