package jdbc.test.basicjdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepository;
import jdbc.test.basicjdbc.repository.MemberRepositoryV4_1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    public static String MEMBER_A = "memberA";
    public static String MEMBER_B = "memberB";
    public static String MEMBER_EX = "memberEX";

    @Autowired
    MemberRepository memberRepository;
    @Autowired MemberServiceV4 memberServiceV4;

    @TestConfiguration
    static class TestConfig {

        private final DataSource dataSource;

        TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepository() {
            return new MemberRepositoryV4_1(dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(memberRepository());
        }
    }

    @AfterEach
    void afterEach() {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    void AopCheck() {
        log.info("memberService class={}", memberServiceV4.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());

        assertThat(AopUtils.isAopProxy(memberServiceV4)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체 ")
    void transferTest() {
        memberRepository.save(new Member(MEMBER_A, 10000));
        memberRepository.save(new Member(MEMBER_B, 10000));

        memberServiceV4.accountTransfer(MEMBER_A, MEMBER_B, 1000);


        Member memberA = memberRepository.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(9000);

        Member memberB = memberRepository.findById(MEMBER_B);
        assertThat(memberB.getMoney()).isEqualTo(11000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void transferException() {
        memberRepository.save(new Member(MEMBER_A, 10000));
        memberRepository.save(new Member(MEMBER_EX, 10000));

        assertThatThrownBy(() ->
                memberServiceV4.accountTransfer(MEMBER_A, MEMBER_EX, 1000))
                .isInstanceOf(IllegalStateException.class);
        Member memberA = memberRepository.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(10000);

        Member memberEx = memberRepository.findById(MEMBER_EX);
        assertThat(memberEx.getMoney()).isEqualTo(10000);
    }

}