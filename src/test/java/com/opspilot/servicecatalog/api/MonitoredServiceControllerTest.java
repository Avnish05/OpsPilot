package com.opspilot.servicecatalog.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.opspilot.servicecatalog.application.MonitoredServiceApplicationService;
import com.opspilot.servicecatalog.application.MonitoredServiceQueryService;
import com.opspilot.servicecatalog.domain.MonitoredService;
import com.opspilot.servicecatalog.domain.MonitoredServiceNotFoundException;
import com.opspilot.servicecatalog.domain.ServiceAlreadyExistsException;
import com.opspilot.servicecatalog.domain.ServiceEnvironment;
import com.opspilot.shared.api.ApiExceptionHandler;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MonitoredServiceControllerTest {
    private final MonitoredServiceApplicationService applicationService = org.mockito.Mockito.mock(MonitoredServiceApplicationService.class);
    private final MonitoredServiceQueryService queryService = org.mockito.Mockito.mock(MonitoredServiceQueryService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MonitoredServiceController(applicationService, queryService,
                new MonitoredServiceApiMapper())).setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void createsService() throws Exception {
        UUID id = UUID.randomUUID();
        when(applicationService.create(any())).thenReturn(service(id));

        mockMvc.perform(post("/api/v1/services").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"payments\",\"environment\":\"PRODUCTION\",\"ownerTeam\":\"platform\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("payments"));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/v1/services").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void translatesDuplicateToConflict() throws Exception {
        when(applicationService.create(any())).thenThrow(new ServiceAlreadyExistsException("payments", ServiceEnvironment.PRODUCTION));

        mockMvc.perform(post("/api/v1/services").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"payments\",\"environment\":\"PRODUCTION\",\"ownerTeam\":\"platform\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void translatesMissingServiceToNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(queryService.get(eq(id))).thenThrow(new MonitoredServiceNotFoundException(id));

        mockMvc.perform(get("/api/v1/services/{id}", id))
                .andExpect(status().isNotFound());
    }

    private MonitoredService service(UUID id) {
        Instant now = Instant.parse("2026-08-05T00:00:00Z");
        return MonitoredService.reconstitute(id, "payments", null, ServiceEnvironment.PRODUCTION, "platform", true, now, now, 0);
    }
}
