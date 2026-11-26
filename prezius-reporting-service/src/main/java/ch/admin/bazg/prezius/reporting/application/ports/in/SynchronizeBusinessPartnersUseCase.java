package ch.admin.bazg.prezius.reporting.application.ports.in;

public interface SynchronizeBusinessPartnersUseCase {

    void synchronizeOne(String businessPartnerId);

    void synchronizeAll();
}
