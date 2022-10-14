package jdbc.test.basicjdbc.service;

import jdbc.test.basicjdbc.domain.Member;
import jdbc.test.basicjdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepositoryV2;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            bizLogic(conn, fromId, money, toId);
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            releaseConnection(conn);
        }
    }

    private void bizLogic(Connection conn, String fromId, int money, String toId) throws SQLException {
        Member fromMember = memberRepositoryV2.findById(conn, fromId);
        Member toMember = memberRepositoryV2.findById(conn, toId);
        memberRepositoryV2.updateMember(conn, fromId,fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV2.updateMember(conn, toId,toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("memberEX")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
    private Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        log.info("get connection = {}, class= {}", conn, conn.getClass());
        return conn;
    }
}
