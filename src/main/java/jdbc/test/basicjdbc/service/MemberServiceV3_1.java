package jdbc.test.basicjdbc.service;

import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepositoryV3;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            bizLogic(fromId, money, toId);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }

    }

    private void bizLogic(String fromId, int money, String toId) throws SQLException {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);
        memberRepositoryV3.updateMember(fromId,fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV3.updateMember(toId,toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("memberEX")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
