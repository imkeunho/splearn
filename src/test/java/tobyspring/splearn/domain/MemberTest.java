package tobyspring.splearn.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tobyspring.splearn.domain.MemberFixture.createMemberRegisterRequest;
import static tobyspring.splearn.domain.MemberFixture.createPasswordEncoder;

class MemberTest {
    Member member;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = createPasswordEncoder();

        member = Member.register(createMemberRegisterRequest(), passwordEncoder);
    }

    @Test
    void registerMember() {
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
        assertThat(member.verifyPassword("verysecret", passwordEncoder)).isTrue();
        assertThat(member.verifyPassword("hello", passwordEncoder)).isFalse();
    }

    @Test
    void changeNickname() {
        //given
        assertThat(member.getNickname()).isEqualTo("imkeunho");
        //when
        member.changeNickname("charlie");
        //then
        assertThat(member.getNickname()).isEqualTo("charlie");
    }

    @Test
    void changePassword() {
        //given
        member.changePassword("verysecret1", passwordEncoder);
        //when

        //then
        assertThat(member.verifyPassword("verysecret1", passwordEncoder)).isTrue();
    }

    @Test
    void isActive() {
        //given
        assertThat(member.isActive()).isFalse();
        //when
        member.activate();
        //then
        assertThat(member.isActive()).isTrue();

        member.deactivate();
        assertThat(member.isActive()).isFalse();

    }
    
    @Test
    void invalidEmail() {
        assertThatThrownBy(() -> {
            Member.register(createMemberRegisterRequest("invalid email"), passwordEncoder);
        }).isInstanceOf(IllegalArgumentException.class);

        Member.register(new MemberRegisterRequest("imkeunho@naver.com", "Toby", "secret"), passwordEncoder);
    }
}