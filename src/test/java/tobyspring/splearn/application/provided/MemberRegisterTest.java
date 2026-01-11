package tobyspring.splearn.application.provided;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.*;

import static org.assertj.core.api.Assertions.assertThat;
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
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
            .isInstanceOf(DuplicateEmailException.class);
        //when

        //then
    }
    
    @Test
    void activate() {
        //given
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();
        //when
        member = memberRegister.activate(member.getId());
        entityManager.flush();
        //then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        
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

    private void checkValidation(MemberRegisterRequest invalid) {
        assertThatThrownBy(() -> memberRegister.register(invalid))
            .isInstanceOf(ConstraintViolationException.class);
    }
}
