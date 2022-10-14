package jdbc.test.basicjdbc.repository;

import jdbc.test.basicjdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    void saveTest() throws SQLException {
        String memberId = "TESTER3";
        Member tester = new Member(memberId, 1000);
        Member save = memberRepositoryV0.save(tester);
        assertThat(save).isEqualTo(tester);

        Member findMember = memberRepositoryV0.findById(save.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(save).isEqualTo(findMember);

        int updateIdx = memberRepositoryV0.updateMember(memberId);
        assertThat(updateIdx).isEqualTo(1);
        Member findChangedMember = memberRepositoryV0.findById(memberId);
        assertThat(findChangedMember.getMoney()).isEqualTo(5000);

        int deleteIdx = memberRepositoryV0.deleteMember(memberId);
        assertThat(deleteIdx).isEqualTo(1);
        assertThrows(NoSuchElementException.class,
                () -> memberRepositoryV0.findById(memberId));
    }
}