package com.dotachile.auth.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarseMBTest {

    @Test
    void passwordsMatchReturnsTrueWhenBothFieldsEqual() {
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPassword("hunter2");
        mb.setPasswordConfirm("hunter2");
        assertThat(mb.passwordsMatch()).isTrue();
    }

    @Test
    void passwordsMatchReturnsFalseWhenFieldsDifferByCase() {
        // The actual CapsLock case the feature exists to catch.
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPassword("hunter2");
        mb.setPasswordConfirm("Hunter2");
        assertThat(mb.passwordsMatch()).isFalse();
    }

    @Test
    void passwordsMatchReturnsFalseWhenConfirmIsNull() {
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPassword("hunter2");
        assertThat(mb.passwordsMatch()).isFalse();
    }

    @Test
    void passwordsMatchReturnsFalseWhenPasswordIsNull() {
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPasswordConfirm("hunter2");
        assertThat(mb.passwordsMatch()).isFalse();
    }

    @Test
    void passwordsMatchReturnsFalseWhenBothNull() {
        RegistrarseMB mb = new RegistrarseMB();
        assertThat(mb.passwordsMatch()).isFalse();
    }
}
