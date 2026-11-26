package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.urls;

import ch.admin.bazg.prezius.reporting.api.ui.api.ServiceUrlApi;
import ch.admin.bazg.prezius.reporting.api.ui.model.ServiceUrlsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class GetServiceUrlsUiRestController implements ServiceUrlApi {

    private final PreziusServicesUrlProperties preziusServicesUrlProperties;

    @Override
    public ResponseEntity<ServiceUrlsResponseDto> getServiceUrls() {
        final ServiceUrlsResponseDto dto = ServiceUrlsResponseDto.builder()
                                                                 .preziusAnalysisServiceBaseUrl(preziusServicesUrlProperties.analysisServiceRootUrl().toString())
                                                                 .preziusAuditServiceBaseUrl(preziusServicesUrlProperties.auditServiceRootUrl().toString())
                                                                 .preziusBillingServiceBaseUrl(preziusServicesUrlProperties.billingServiceRootUrl().toString())
                                                                 .preziusOrderServiceBaseUrl(preziusServicesUrlProperties.orderServiceRootUrl().toString())
                                                                 .preziusRegisterServiceBaseUrl(preziusServicesUrlProperties.registerServiceRootUrl().toString())
                                                                 .preziusApplicationServiceBaseUrl(preziusServicesUrlProperties.applicationServiceRootUrl().toString())
                                                                 .preziusMatchmakingServiceBaseUrl(preziusServicesUrlProperties.matchmakingServiceRootUrl().toString())
                                                                 .build();
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}