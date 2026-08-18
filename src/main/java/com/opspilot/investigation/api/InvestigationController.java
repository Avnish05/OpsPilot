package com.opspilot.investigation.api;

import com.opspilot.investigation.application.InvestigationApplicationService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class InvestigationController {
    private final InvestigationApplicationService service;
    public InvestigationController(InvestigationApplicationService service) { this.service = service; }
    @PostMapping("/incidents/{incidentId}/investigations")
    public ResponseEntity<InvestigationResponse> create(@PathVariable UUID incidentId, @Valid @RequestBody CreateInvestigationRequest request) {
        var response = service.create(incidentId, request.question());
        return ResponseEntity.created(URI.create("/api/v1/investigations/" + response.id())).body(response);
    }
    @GetMapping("/incidents/{incidentId}/investigations") public List<InvestigationResponse> list(@PathVariable UUID incidentId) { return service.list(incidentId); }
    @GetMapping("/investigations/{investigationId}") public InvestigationResponse get(@PathVariable UUID investigationId) { return service.get(investigationId); }
}
