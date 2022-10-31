package jdbc.test.basicjdbc.exception.translator;

import jdbc.test.basicjdbc.connection.ConnectionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static jdbc.test.basicjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

    }


    @Test
    void sqlExceptionErrorCode() {
        String sql = "select ABA aSDASD";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();

        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(42122);
            log.info("errorCode={}", errorCode);
            log.info("error", e);

        }

    }

    @Test
    void exceptionTranslator() {
        String sql = "select ABA aSDASD";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();

        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(42122);

            SQLErrorCodeSQLExceptionTranslator exceptionTranslator
                    = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            DataAccessException resultEx
                    = exceptionTranslator.translate("select", sql, e);

            log.info("resultEx={}", resultEx);
            log.info("resultEx", resultEx);
            assert resultEx != null;
            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);

        }
    }
}
