package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.featureflag;

import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class GetFeatureFlagsUiRestControllerIT extends ITBase {
    @Autowired
    private MockMvc mockMvc;

    private static final String API_URL = "/api/ui/features";
    private static final String JSON_CONTENT = """                                                  
                                                  {"EXAMPLE_FEATURE_FLAG":  true}""";
    private static final String JSON_NO_CONTENT = """                                                  
                                                  {}""";

    @Test
    void whenValidRequest_thenReturnRightResults() {
        assertDoesNotThrow(() -> {
            //testGet(FeatureResource.Format.DEFAULT, true);
            //testGet(FeatureResource.Format.ALL_ENABLED, true);
            testGet(GetFeatureFlagsUiRestController.Format.ALL_DISABLED, false);
        });
    }

    private void testGet(final GetFeatureFlagsUiRestController.Format format, final boolean hasContent) throws Exception {
        this.mockMvc.perform(get(API_URL).param("format", format.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(hasContent ? JSON_CONTENT : JSON_NO_CONTENT));
    }
}
