package com.opspilot.shared.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class OpsPilotMetrics {
    private final Counter alertsReceived; private final Counter alertsDuplicates; private final Counter alertsRejected;
    private final Counter alertsCorrelated; private final Counter incidentsCreated; private final Counter incidentsResolved;
    private final Counter aiInvestigations; private final Counter aiFailures;
    private final MeterRegistry registry; private final Timer aiDuration;
    public OpsPilotMetrics(MeterRegistry registry) {
        this.registry = registry;
        alertsReceived = Counter.builder("opspilot.alerts.received").register(registry);
        alertsDuplicates = Counter.builder("opspilot.alerts.duplicates").register(registry);
        alertsRejected = Counter.builder("opspilot.alerts.rejected").register(registry);
        alertsCorrelated = Counter.builder("opspilot.alerts.correlated").register(registry);
        incidentsCreated = Counter.builder("opspilot.incidents.created").register(registry);
        incidentsResolved = Counter.builder("opspilot.incidents.resolved").register(registry);
        aiInvestigations = Counter.builder("opspilot.ai.investigations").register(registry);
        aiFailures = Counter.builder("opspilot.ai.failures").register(registry);
        aiDuration = Timer.builder("opspilot.ai.duration").register(registry);
    }
    public void alertReceived() { alertsReceived.increment(); } public void alertDuplicate() { alertsDuplicates.increment(); }
    public void alertRejected() { alertsRejected.increment(); } public void alertCorrelated() { alertsCorrelated.increment(); }
    public void incidentCreated() { incidentsCreated.increment(); } public void incidentResolved() { incidentsResolved.increment(); }
    public void aiInvestigation() { aiInvestigations.increment(); } public void aiFailure() { aiFailures.increment(); }
    public Timer.Sample startAiTimer() { return Timer.start(registry); } public void recordAiDuration(Timer.Sample sample) { sample.stop(aiDuration); }
}
