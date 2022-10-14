package jdbc.test.basicjdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepositoryV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;

@Slf4j
class MemberServiceV1Test {

    public static String MEMBER_A = "memberA";
    public static String MEMBER_B = "memberB";
    public static String MEMBER_EX = "memberEX";

    private MemberRepositoryV1 memberRepositoryV1;
    private MemberServiceV1 memberServiceV1;

    @BeforeEach
    void beforeEach() {
        HikariDataSource dataSource = createHikariDataSource();
        memberRepositoryV1 = new MemberRepositoryV1(dataSource);
        memberServiceV1 = new MemberServiceV1(memberRepositoryV1);
    }

    @Test
    @DisplayName("정상 이체 ")
    void transferTest() throws SQLException {
        memberRepositoryV1.save(new Member(MEMBER_A, 10000));
        memberRepositoryV1.save(new Member(MEMBER_B, 10000));

        memberServiceV1.accountTransfer(MEMBER_A, MEMBER_B, 1000);

        Member memberA = memberRepositoryV1.findById(MEMBER_A);
        log.info("member A = {} ", memberA);
        Member memberB = memberRepositoryV1.findById(MEMBER_B);
        log.info("member B = {} ", memberB);
    }

    private HikariDataSource createHikariDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }
}