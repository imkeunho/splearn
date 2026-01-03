package tobyspring.splearn.application.required;

import org.springframework.data.repository.Repository;
import tobyspring.splearn.domain.Email;
import tobyspring.splearn.domain.Member;

import java.util.Optional;

/**
 * 회원 관련 데이터를 저장하고 조회하는 기능을 제공한다.
 */
public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByEmail(Email email);

    Optional<Member> findById(Long memberId);
}
