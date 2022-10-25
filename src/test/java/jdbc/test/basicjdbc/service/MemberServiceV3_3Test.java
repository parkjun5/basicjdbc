package jdbc.test.basicjdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {

    public static String MEMBER_A = "memberA";
    public static String MEMBER_B = "memberB";
    public static String MEMBER_EX = "memberEX";

    @Autowired
    MemberRepositoryV3 memberRepositoryV3;
    @Autowired MemberServiceV3_3 memberServiceV3;

    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }

    @AfterEach
    void afterEach() {
        memberRepositoryV3.deleteMember(MEMBER_A);
        memberRepositoryV3.deleteMember(MEMBER_B);
        memberRepositoryV3.deleteMember(MEMBER_EX);
    }

    @Test
    void AopCheck() {
        log.info("memberService class={}", memberServiceV3.getClass());
        log.info("memberRepository class={}", memberRepositoryV3.getClass());

        assertThat(AopUtils.isAopProxy(memberServiceV3)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepositoryV3)).isFalse();
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