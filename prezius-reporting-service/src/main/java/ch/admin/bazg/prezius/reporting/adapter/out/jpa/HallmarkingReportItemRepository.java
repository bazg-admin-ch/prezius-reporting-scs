package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HallmarkingReportItemRepository extends JpaRepository<HallmarkingReportItem, UUID> {
    List<HallmarkingReportItem> findAll(Specification<HallmarkingReportItem> spec);

    List<HallmarkingReportItem> findAllByOrderId(UUID orderID);

    void deleteAllByOrderId(UUID orderId);

}
