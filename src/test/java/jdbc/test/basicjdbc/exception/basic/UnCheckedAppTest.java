package jdbc.test.basicjdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class UnCheckedAppTest {

    @Test
    void unCheckedTest() {
        //given
        Controller controller = new Controller();
        //when + then
        assertThatThrownBy(controller::request)
                .isInstanceOf(RunTimeSQLException.class);
    }

    @Test
    void printEx() {
        //given
        Controller co = new Controller();

        //when
        try {
            co.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }

    static class RunTimeSQLException extends RuntimeException {

        public RunTimeSQLException(String message, Throwable cause) {
            super(message, cause);
        }

        public RunTimeSQLException(Throwable cause) {
            super(cause);
        }
    }

    static class RunTimeConnectException extends RuntimeException {
        public RunTimeConnectException(String message) {
            super(message);
        }
    }

    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RunTimeConnectException("연결 실패");
        }
    }

    static class Repository {

        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RunTimeSQLException("메시지 테스트",e);
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

}
