package com.opspilot.incident.api; import jakarta.validation.constraints.*; public record ResolveIncidentRequest(@NotBlank @Size(max=2000) String summary){}
