package jdbc.test.basicjdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DBConnectionUtilTest {

    @Test
    void connectionTest() {
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}