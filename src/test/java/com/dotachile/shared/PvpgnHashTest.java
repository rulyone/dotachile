package com.dotachile.shared;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PvpgnHashTest {

    // These three vectors are regression anchors. They were captured once from
    // the current production implementation. If any of these break, PvPGN auth
    // is broken — investigate before "fixing" the test.
    private static final String VECTOR_PASSWORD_HASH = "1d0dc8ecc058e776258cdab9ff6a10ff1629248e";
    private static final String VECTOR_EMPTY_HASH = "4d3aa0ee94261d5a584a6f576b8d99601546c680";
    private static final String VECTOR_LONG_HASH = "f897d1af4bb1764c4c3d39c66829dddab8a5b0d5";

    @Test
    void hashOfKnownPasswordMatchesLockedValue() {
        String hash = PvpgnHash.GetHash("password");
        assertThat(hash).isEqualTo(VECTOR_PASSWORD_HASH);
    }

    @Test
    void hashOfEmptyStringMatchesLockedValue() {
        String hash = PvpgnHash.GetHash("");
        assertThat(hash).isEqualTo(VECTOR_EMPTY_HASH);
    }

    @Test
    void hashOfLongPasswordMatchesLockedValue() {
        String hash = PvpgnHash.GetHash("this-is-a-much-longer-password-with-some-numbers-1234567890");
        assertThat(hash).isEqualTo(VECTOR_LONG_HASH);
    }

    @Test
    void hashIsCaseInsensitiveForAscii() {
        // PvPGN hash explicitly lowercases ASCII before hashing (toLowerUnicode)
        assertThat(PvpgnHash.GetHash("PASSWORD")).isEqualTo(PvpgnHash.GetHash("password"));
        assertThat(PvpgnHash.GetHash("Password")).isEqualTo(PvpgnHash.GetHash("password"));
    }

    @Test
    void hashOutputIsAlwaysFortyHexCharacters() {
        // SHA1-based, 20 bytes = 40 hex chars (asHex pads odd-length runs)
        assertThat(PvpgnHash.GetHash("a")).hasSize(40);
        assertThat(PvpgnHash.GetHash("password")).hasSize(40);
        assertThat(PvpgnHash.GetHash("")).hasSize(40);
    }

    @Test
    void byteArrayOverloadProducesTwentyBytes() {
        byte[] result = PvpgnHash.GetHash("password".getBytes());
        assertThat(result).hasSize(20);
    }

    @Test
    void differentInputsProduceDifferentHashes() {
        assertThat(PvpgnHash.GetHash("password1"))
                .isNotEqualTo(PvpgnHash.GetHash("password2"));
    }

    @Test
    void inputOver1024BytesThrows() {
        StringBuilder huge = new StringBuilder();
        for (int i = 0; i < 1100; i++) huge.append('a');
        assertThatThrownBy(() -> PvpgnHash.GetHash(huge.toString()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
