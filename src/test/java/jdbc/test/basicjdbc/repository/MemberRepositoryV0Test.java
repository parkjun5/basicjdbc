package jdbc.test.basicjdbc.repository;

import jdbc.test.basicjdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryV0Test {

    @Test
    void saveTest() throws SQLException {
        MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();
        Member tester = new Member("TESTER2", 1000);
        memberRepositoryV0.save(tester);
    }
}