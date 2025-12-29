package tobyspring.splearn.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTest {

    @Test
    void equality() {
        //given
        Email email1 = new Email("imkeunho@naver.com");
        Email email2 = new Email("imkeunho@naver.com");
        //when
        assertThat(email1).isEqualTo(email2);
        //then
        
    }

}