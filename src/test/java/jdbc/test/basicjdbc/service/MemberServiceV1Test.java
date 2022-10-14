package jdbc.test.basicjdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepositoryV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

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

    @AfterEach
    void afterEach() {
        memberRepositoryV1.deleteMember(MEMBER_A);
        memberRepositoryV1.deleteMember(MEMBER_B);
        memberRepositoryV1.deleteMember(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체 ")
    void transferTest() throws SQLException {
        memberRepositoryV1.save(new Member(MEMBER_A, 10000));
        memberRepositoryV1.save(new Member(MEMBER_B, 10000));

        memberServiceV1.accountTransfer(MEMBER_A, MEMBER_B, 1000);


        Member memberA = memberRepositoryV1.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(9000);

        Member memberB = memberRepositoryV1.findById(MEMBER_B);
        assertThat(memberB.getMoney()).isEqualTo(11000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void transferException() throws SQLException {
        memberRepositoryV1.save(new Member(MEMBER_A, 10000));
        memberRepositoryV1.save(new Member(MEMBER_EX, 10000));

        assertThatThrownBy(() ->
                memberServiceV1.accountTransfer(MEMBER_A, MEMBER_EX, 1000))
                .isInstanceOf(IllegalStateException.class);
        Member memberA = memberRepositoryV1.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(9000);

        Member memberEx = memberRepositoryV1.findById(MEMBER_EX);
        assertThat(memberEx.getMoney()).isEqualTo(10000);
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