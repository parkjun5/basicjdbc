package jdbc.test.basicjdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class CheckedAppTest {

    @Test
    void checkedTest() {
        //given
        Controller controller = new Controller();
        //when + then
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(SQLException.class);
    }

    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws ConnectException, SQLException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }

}
