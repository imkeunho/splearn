package tobyspring.splearn.adapter.integration;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import tobyspring.splearn.domain.shared.Email;

import static org.assertj.core.api.Assertions.assertThat;

class DummyEmailSenderTest {
    @Test
    @StdIo
    void dummyEmailSender(StdOut stdOut) {
        //given
        DummyEmailSender dummyEmailSender = new DummyEmailSender();
        //when
        dummyEmailSender.send(new Email("imkeunho@naver.com"), "subject", "body");
        //then
        assertThat(stdOut.capturedLines()[0])
                .isEqualTo("DummyEmailSending Email: Email[address=imkeunho@naver.com]");
    }
}