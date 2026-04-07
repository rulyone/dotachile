package com.dotachile.shared;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTest {

    // ----- cambiarSlashes -----

    @Test
    void cambiarSlashesReplacesBackslashesWithForwardSlashes() {
        assertThat(Util.cambiarSlashes("c:\\foo\\bar.txt")).isEqualTo("c:/foo/bar.txt");
    }

    @Test
    void cambiarSlashesIsIdentityForStringWithoutBackslashes() {
        assertThat(Util.cambiarSlashes("/already/clean")).isEqualTo("/already/clean");
    }

    @Test
    void cambiarSlashesPreservesEmptyString() {
        assertThat(Util.cambiarSlashes("")).isEqualTo("");
    }

    // ----- hashPassword(String, String) -----

    @Test
    void hashPasswordIsDeterministic() {
        String h1 = Util.hashPassword("secret", "alice");
        String h2 = Util.hashPassword("secret", "alice");
        assertThat(h1).isEqualTo(h2);
    }

    @Test
    void hashPasswordDiffersWhenUsernameDiffers() {
        assertThat(Util.hashPassword("secret", "alice"))
                .isNotEqualTo(Util.hashPassword("secret", "bob"));
    }

    @Test
    void hashPasswordDiffersWhenPasswordDiffers() {
        assertThat(Util.hashPassword("secretA", "alice"))
                .isNotEqualTo(Util.hashPassword("secretB", "alice"));
    }

    @Test
    void hashPasswordOutputIsLowercaseHex32Chars() {
        String h = Util.hashPassword("secret", "alice");
        assertThat(h).hasSize(32);
        assertThat(h).matches("[0-9a-f]{32}");
    }

    // ----- dateSinMillis -----

    @Test
    void dateSinMillisZeroesTheMillisecondField() {
        Calendar cal = new GregorianCalendar(2026, Calendar.APRIL, 6, 14, 30, 45);
        cal.set(Calendar.MILLISECOND, 789);
        Date stripped = Util.dateSinMillis(cal.getTime());

        Calendar check = new GregorianCalendar();
        check.setTime(stripped);
        assertThat(check.get(Calendar.MILLISECOND)).isZero();
        assertThat(check.get(Calendar.SECOND)).isEqualTo(45);
        assertThat(check.get(Calendar.MINUTE)).isEqualTo(30);
        assertThat(check.get(Calendar.HOUR_OF_DAY)).isEqualTo(14);
    }

    // ----- dateSinTime -----

    @Test
    void dateSinTimeZeroesAllTimeFields() {
        Calendar cal = new GregorianCalendar(2026, Calendar.APRIL, 6, 14, 30, 45);
        cal.set(Calendar.MILLISECOND, 789);
        Date stripped = Util.dateSinTime(cal.getTime());

        Calendar check = new GregorianCalendar();
        check.setTime(stripped);
        assertThat(check.get(Calendar.MILLISECOND)).isZero();
        assertThat(check.get(Calendar.SECOND)).isZero();
        assertThat(check.get(Calendar.MINUTE)).isZero();
        assertThat(check.get(Calendar.HOUR_OF_DAY)).isZero();
        assertThat(check.get(Calendar.DAY_OF_MONTH)).isEqualTo(6);
        assertThat(check.get(Calendar.MONTH)).isEqualTo(Calendar.APRIL);
        assertThat(check.get(Calendar.YEAR)).isEqualTo(2026);
    }

    // ----- caracteresValidosPvpgn -----

    @Test
    void caracteresValidosPvpgnAcceptsLettersDigitsAndAllowedSymbols() {
        assertThat(Util.caracteresValidosPvpgn("Player_1")).isTrue();
        assertThat(Util.caracteresValidosPvpgn("a-b_c^d.e[f]g")).isTrue();
        assertThat(Util.caracteresValidosPvpgn("ABCxyz123")).isTrue();
    }

    @Test
    void caracteresValidosPvpgnRejectsSpaces() {
        assertThat(Util.caracteresValidosPvpgn("hello world")).isFalse();
    }

    @Test
    void caracteresValidosPvpgnRejectsPunctuationOutsideAllowList() {
        assertThat(Util.caracteresValidosPvpgn("hi!")).isFalse();
        assertThat(Util.caracteresValidosPvpgn("name@host")).isFalse();
        assertThat(Util.caracteresValidosPvpgn("a/b")).isFalse();
    }

    @Test
    void caracteresValidosPvpgnReturnsTrueForEmptyString() {
        // Vacuously true: the loop never runs.
        assertThat(Util.caracteresValidosPvpgn("")).isTrue();
    }
}
