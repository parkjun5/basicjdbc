package jdbc.test.basicjdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class UnCheckedAppTest {

    @Test
    void unCheckedTest() {
        //given
        Controller controller = new Controller();
        //when + then
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(RunTimeSQLException.class);
    }

    static class RunTimeSQLException extends RuntimeException {
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
                throw new RunTimeSQLException(e);
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

}
