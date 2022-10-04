package jdbc.test.basicjdbc.repository;

import jdbc.test.basicjdbc.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryV0Test {

    @Test
    void saveTest() throws SQLException {
        MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();
        Member tester = new Member("TESTER3", 1000);
        Member save = memberRepositoryV0.save(tester);
        Assertions.assertThat(save).isEqualTo(tester);
    }
}