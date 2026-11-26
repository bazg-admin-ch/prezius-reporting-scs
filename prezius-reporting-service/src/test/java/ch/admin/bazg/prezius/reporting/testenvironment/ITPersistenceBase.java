package ch.admin.bazg.prezius.reporting.testenvironment;

import ch.admin.bazg.prezius.reporting.testenvironment.generator.StorageHelper;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.TEST;

@DataJpaTest
// Use to avoid replacing the connection url by the default url of EmbeddedDatabaseConnection.H2, so this way the NON_KEYWORDS property is working
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({TEST})
@ComponentScan(basePackages = {"ch.admin.bazg.prezius.reporting.testenvironment.generator", "ch.admin.bazg.prezius.reporting.adapter.out.jpa",
        "ch.admin.bit.jeap.db.tx" /* This is required since the TransactionaReadReplica Annotation is used*/})
@Import(PersistenceTestConfiguration.class)
@Sql(scripts = {"/cleanup.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@EnableTransactionManagement(proxyTargetClass = true)
public abstract class ITPersistenceBase {

    @Autowired
    protected StorageHelper storageHelper;

    @Autowired
    protected EntityManager entityManager;
}
