package tobyspring.splearn.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {
    Member member;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(String password) {
                return password.toUpperCase();
            }
            @Override
            public boolean matches(String password, String passwordHash) {
                return encode(password).equals(passwordHash);
            }
        };

        member = Member.create("toby@splearn.app", "Toby", "secret", passwordEncoder);
    }

    @Test
    void createMember() {
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
        
    }

    @Test
    void activate() {
        //when
        member.activate();
        //then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void activateFail() {
        //when
        member.activate();
        //then
        assertThatThrownBy(member::activate)
            .isInstanceOf(IllegalStateException.class);
    }
    
    @Test
    void deactivate() {
        member.activate();
        //when
        member.deactivate();
        //then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @Test
    void deactivateFail() {
        //then
        assertThatThrownBy(member::deactivate)
                .isInstanceOf(IllegalStateException.class);

        member.activate();
        member.deactivate();

        assertThatThrownBy(member::deactivate)
                .isInstanceOf(IllegalStateException.class);
    }
    
    @Test
    void verifyPassword() {
        assertThat(member.verifyPassword("secret", passwordEncoder)).isTrue();
        assertThat(member.verifyPassword("hello", passwordEncoder)).isFalse();
    }

    @Test
    void changeNickname() {
        //given
        assertThat(member.getNickname()).isEqualTo("Toby");
        //when
        member.changeNickname("charlie");
        //then
        assertThat(member.getNickname()).isEqualTo("charlie");
    }

    @Test
    void changePassword() {
        //given
        member.changePassword("verysecret", passwordEncoder);
        //when

        //then
        assertThat(member.verifyPassword("verysecret", passwordEncoder)).isTrue();
    }
}