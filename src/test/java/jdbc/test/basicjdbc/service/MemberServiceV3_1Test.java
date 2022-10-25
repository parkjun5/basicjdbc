package jdbc.test.basicjdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberServiceV3_1Test {

    public static String MEMBER_A = "memberA";
    public static String MEMBER_B = "memberB";
    public static String MEMBER_EX = "memberEX";

    private MemberRepositoryV3 memberRepositoryV3;
    private MemberServiceV3_1 memberServiceV3;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource1 = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
//        HikariDataSource dataSource = createHikariDataSource();
        memberRepositoryV3 = new MemberRepositoryV3(dataSource1);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource1);
        memberServiceV3 = new MemberServiceV3_1(transactionManager, memberRepositoryV3);
    }

    @AfterEach
    void afterEach() {
        memberRepositoryV3.deleteMember(MEMBER_A);
        memberRepositoryV3.deleteMember(MEMBER_B);
        memberRepositoryV3.deleteMember(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체 ")
    void transferTest() throws SQLException {
        memberRepositoryV3.save(new Member(MEMBER_A, 10000));
        memberRepositoryV3.save(new Member(MEMBER_B, 10000));

        memberServiceV3.accountTransfer(MEMBER_A, MEMBER_B, 1000);


        Member memberA = memberRepositoryV3.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(9000);

        Member memberB = memberRepositoryV3.findById(MEMBER_B);
        assertThat(memberB.getMoney()).isEqualTo(11000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void transferException() throws SQLException {
        memberRepositoryV3.save(new Member(MEMBER_A, 10000));
        memberRepositoryV3.save(new Member(MEMBER_EX, 10000));

        assertThatThrownBy(() ->
                memberServiceV3.accountTransfer(MEMBER_A, MEMBER_EX, 1000))
                .isInstanceOf(IllegalStateException.class);
        Member memberA = memberRepositoryV3.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(10000);

        Member memberEx = memberRepositoryV3.findById(MEMBER_EX);
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