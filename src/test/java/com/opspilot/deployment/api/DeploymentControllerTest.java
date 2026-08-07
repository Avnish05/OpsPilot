package com.opspilot.deployment.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.opspilot.deployment.application.DeploymentApplicationService;
import com.opspilot.deployment.application.DeploymentQueryService;
import com.opspilot.deployment.domain.Deployment;
import com.opspilot.deployment.domain.DeploymentNotFoundException;
import com.opspilot.deployment.domain.DeploymentStatus;
import com.opspilot.servicecatalog.domain.MonitoredServiceNotFoundException;
import com.opspilot.shared.api.ApiExceptionHandler;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DeploymentControllerTest {
    private final DeploymentApplicationService applicationService = org.mockito.Mockito.mock(DeploymentApplicationService.class);
    private final DeploymentQueryService queryService = org.mockito.Mockito.mock(DeploymentQueryService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DeploymentController(applicationService, queryService,
                new DeploymentApiMapper())).setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void createsDeployment() throws Exception {
        UUID serviceId = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();
        when(applicationService.create(eq(serviceId), any())).thenReturn(deployment(deploymentId, serviceId));

        mockMvc.perform(post("/api/v1/services/{serviceId}/deployments", serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"releaseVersion\":\"2.4.1\",\"status\":\"SUCCEEDED\",\"deployedAt\":\"2026-08-06T23:55:00Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(deploymentId.toString()))
                .andExpect(jsonPath("$.serviceId").value(serviceId.toString()));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/v1/services/{serviceId}/deployments", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownService() throws Exception {
        UUID serviceId = UUID.randomUUID();
        when(applicationService.create(eq(serviceId), any())).thenThrow(new MonitoredServiceNotFoundException(serviceId));

        mockMvc.perform(post("/api/v1/services/{serviceId}/deployments", serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"releaseVersion\":\"2.4.1\",\"status\":\"SUCCEEDED\",\"deployedAt\":\"2026-08-06T23:55:00Z\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundForUnknownDeployment() throws Exception {
        UUID id = UUID.randomUUID();
        when(queryService.get(id)).thenThrow(new DeploymentNotFoundException(id));

        mockMvc.perform(get("/api/v1/deployments/{deploymentId}", id)).andExpect(status().isNotFound());
    }

    private Deployment deployment(UUID id, UUID serviceId) {
        Instant deployedAt = Instant.parse("2026-08-06T23:55:00Z");
        return Deployment.reconstitute(id, serviceId, "2.4.1", DeploymentStatus.SUCCEEDED, deployedAt, deployedAt, 0);
    }
}
