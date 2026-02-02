package tobyspring.splearn.application.member.provided;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.member.*;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
record MemberRegisterTest(MemberRegister memberRegister, EntityManager entityManager) {

    @Test
    void register() {
        //given
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        //when

        //then
        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailFail() {
        //given
        memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
            .isInstanceOf(DuplicateEmailException.class);
        //when

        //then
    }
    
    @Test
    void activate() {
        //given
        Member member = registerMember();
        //when
        member = memberRegister.activate(member.getId());
        entityManager.flush();
        //then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDetail().getActivatedAt()).isNotNull();

    }

    @Test
    void deactivate() {
        //given
        Member member = registerMember();
        //when
        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();
        //then
        member = memberRegister.deactivate(member.getId());
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();

    }

    private Member registerMember() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();
        return member;
    }

    private Member registerMember(String email) {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest(email));
        entityManager.flush();
        entityManager.clear();
        return member;
    }

    @Test
    void memberRegisterRequestFail() {
        //given
        checkValidation(new MemberRegisterRequest("imkeunho@naver.com", "Toby", "longsecret"));
        checkValidation(new MemberRegisterRequest("imkeunho@naver.com", "imkeunho_____________________", "longsecret"));
        checkValidation(new MemberRegisterRequest("imkeunhonaver.com", "imkeunho", "longsecret"));
        //when
        //then
        
    }

    @Test
    void updateInfo() {
        //given
        Member member = registerMember();
        //when
        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        //then
        member = memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("imkeunho", "kekek", "자기소개"));
        assertThat(member.getNickname()).isEqualTo("imkeunho");
        assertThat(member.getDetail().getProfile().address()).isEqualTo("kekek");
        assertThat(member.getDetail().getIntroduction()).isEqualTo("자기소개");
    }

    @Test
    void updateInfoFail() {
        //given
        Member member = registerMember();
        memberRegister.activate(member.getId());
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("imkeunxho", "test", "자기소개"));

        Member member2 = registerMember("imkenho@naver.com");
        memberRegister.activate(member2.getId());
        entityManager.flush();
        entityManager.clear();

        //then
        assertThatThrownBy(() -> {
            memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("asddsa", "test", "자기소개"));
        }).isInstanceOf(DuplicateProfileException.class);

        memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("asddsa", "test11", "자기소개"));
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("asddsa", "", "자기소개"));

        assertThatThrownBy(() -> {
            memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("asddsa", "test11", "자기소개"));
        }).isInstanceOf(DuplicateProfileException.class);

    }

    private void checkValidation(MemberRegisterRequest invalid) {
        assertThatThrownBy(() -> memberRegister.register(invalid))
            .isInstanceOf(ConstraintViolationException.class);
    }
}
