package jdbc.test.basicjdbc.repository;

import jdbc.test.basicjdbc.domain.Member;

public interface MemberRepositoryEx {

    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);
}
