package tobyspring.splearn.domain.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tobyspring.splearn.domain.member.MemberFixture.createMemberRegisterRequest;
import static tobyspring.splearn.domain.member.MemberFixture.createPasswordEncoder;

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
        assertThat(member.getDetail().getRegisteredAt()).isNotNull();
    }

    @Test
    void activate() {
        assertThat(member.getDetail().getActivatedAt()).isNull();
        //when
        member.activate();
        //then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
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
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();

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

    @Test
    void updateInfo() {
        //given
        member.activate();
        //when
        var request = new MemberInfoUpdateRequest("llll", "kekek", "자기소개");
        member.updateInfo(request);
        //then
        assertThat(member.getNickname()).isEqualTo(request.nickname());
        assertThat(member.getDetail().getProfile().address()).isEqualTo(request.profileAddress());
        assertThat(member.getDetail().getIntroduction()).isEqualTo(request.introduction());
    }

    @Test
    void updateInfoFail() {
        //given
        assertThatThrownBy(() -> {
            var request = new MemberInfoUpdateRequest("llll", "kekek", "자기소개");
            member.updateInfo(request);
        }).isInstanceOf(IllegalStateException.class);
        //when

        //then

    }
}