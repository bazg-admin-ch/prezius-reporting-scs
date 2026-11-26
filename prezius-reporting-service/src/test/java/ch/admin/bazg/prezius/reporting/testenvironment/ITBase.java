package ch.admin.bazg.prezius.reporting.testenvironment;

import ch.admin.bazg.prezius.reporting.adapter.out.jpa.AnalysisReportItemRepository;
import ch.admin.bazg.prezius.reporting.adapter.out.jpa.BusinessPartnerRepository;
import ch.admin.bazg.prezius.reporting.adapter.out.jpa.HallmarkingReportItemRepository;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.AnalysisReportItemGenerator;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.BusinessPartnerGenerator;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.HallmarkingReportItemGenerator;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.StorageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_DATAHUB;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_JEAPRESTCLIENT;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_JOB_SCHEDULERS;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_MESSAGESENDER;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.TEST;

@ActiveProfiles({TEST, MOCK_MESSAGESENDER, MOCK_JEAPRESTCLIENT, MOCK_JOB_SCHEDULERS, MOCK_DATAHUB})
@SpringBootTest(classes = ITMockConfiguration.class)
@Sql(scripts = {"/cleanup.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ITBase {

    @Autowired
    protected StorageHelper storageHelper;

    @Autowired
    protected HallmarkingReportItemRepository hallmarkingReportItemRepository;

    @Autowired
    protected AnalysisReportItemRepository analysisReportItemRepository;

    @Autowired
    protected BusinessPartnerRepository businessPartnerRepository;

    protected HallmarkingReportItemGenerator hallmarkingReportItemGenerator = new HallmarkingReportItemGenerator();
    protected AnalysisReportItemGenerator analysisReportItemGenerator = new AnalysisReportItemGenerator();
    protected BusinessPartnerGenerator businessPartnerGenerator = new BusinessPartnerGenerator();
}
