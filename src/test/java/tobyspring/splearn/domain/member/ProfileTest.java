package tobyspring.splearn.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {
    @Test
    void profile() {
        //given
        new Profile("imkeunho");
        new Profile("123123");
        new Profile("imkunhl23");
        new Profile("");
    }

    @Test
    void profileFail() {
        //given
        assertThatThrownBy(() -> new Profile("kjdnaskjdahsdkjahwdjaasdad")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("한글")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("A")).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void url() {
        Profile profile = new Profile("imkeunho");

        assertThat(profile.url()).isEqualTo("@imkeunho");
    }

}