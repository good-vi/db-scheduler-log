package io.rocketbase.extension.compatibility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.DriverDataSource;
import io.rocketbase.extension.DbUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.Properties;

@Tag("compatibility")
@Testcontainers
@Disabled
public class MysqlCompatibilityTest extends CompatibilityTest {

    @Container
    private static final MySQLContainer MY_SQL = new MySQLContainer();
    private static HikariDataSource pooledDatasource;
    
    @BeforeAll
    static void initSchema() {
        final DriverDataSource datasource = new DriverDataSource(MY_SQL.getJdbcUrl(), "com.mysql.cj.jdbc.Driver", new Properties(), MY_SQL.getUsername(), MY_SQL.getPassword());

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(datasource);
        pooledDatasource = new HikariDataSource(hikariConfig);

        // init schema
        DbUtils.runSqlResource("/mysql_tables.sql").accept(pooledDatasource);
    }

    @Override
    public DataSource getDataSource() {
        return pooledDatasource;
    }
}
