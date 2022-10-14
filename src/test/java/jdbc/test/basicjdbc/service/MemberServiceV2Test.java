package jdbc.test.basicjdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepositoryV1;
import jdbc.test.basicjdbc.repository.MemberRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberServiceV2Test {

    public static String MEMBER_A = "memberA";
    public static String MEMBER_B = "memberB";
    public static String MEMBER_EX = "memberEX";

    private MemberRepositoryV2 memberRepositoryV2;
    private MemberServiceV2 memberServiceV2;

    @BeforeEach
    void beforeEach() {
        HikariDataSource dataSource = createHikariDataSource();
        memberRepositoryV2 = new MemberRepositoryV2(dataSource);
        memberServiceV2 = new MemberServiceV2(memberRepositoryV2, dataSource);
    }

    @AfterEach
    void afterEach() {
        memberRepositoryV2.deleteMember(MEMBER_A);
        memberRepositoryV2.deleteMember(MEMBER_B);
        memberRepositoryV2.deleteMember(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체 ")
    void transferTest() throws SQLException {
        memberRepositoryV2.save(new Member(MEMBER_A, 10000));
        memberRepositoryV2.save(new Member(MEMBER_B, 10000));

        memberServiceV2.accountTransfer(MEMBER_A, MEMBER_B, 1000);


        Member memberA = memberRepositoryV2.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(9000);

        Member memberB = memberRepositoryV2.findById(MEMBER_B);
        assertThat(memberB.getMoney()).isEqualTo(11000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void transferException() throws SQLException {
        memberRepositoryV2.save(new Member(MEMBER_A, 10000));
        memberRepositoryV2.save(new Member(MEMBER_EX, 10000));

        assertThatThrownBy(() ->
                memberServiceV2.accountTransfer(MEMBER_A, MEMBER_EX, 1000))
                .isInstanceOf(IllegalStateException.class);
        Member memberA = memberRepositoryV2.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(10000);

        Member memberEx = memberRepositoryV2.findById(MEMBER_EX);
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