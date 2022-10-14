package jdbc.test.basicjdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import jdbc.test.basicjdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        // 기본 DriveManager 이용
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void saveTest() throws SQLException, InterruptedException {
        String memberId = "TESTER3";
        Member tester = new Member(memberId, 1000);
        Member save = repository.save(tester);
        assertThat(save).isEqualTo(tester);

        Member findMember = repository.findById(save.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(save).isEqualTo(findMember);

        int updateIdx = repository.updateMember(memberId, 3000);
        assertThat(updateIdx).isEqualTo(1);
        Member findChangedMember = repository.findById(memberId);
        assertThat(findChangedMember.getMoney()).isEqualTo(5000);

        int deleteIdx = repository.deleteMember(memberId);
        assertThat(deleteIdx).isEqualTo(1);
        assertThrows(NoSuchElementException.class,
                () -> repository.findById(memberId));

        Thread.sleep(1000);
    }
}