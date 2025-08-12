package com.digital.evidence.api.service;

import com.digital.evidence.api.dto.EvidenceAiSummaryDTO;

public interface EvidenceSummaryApiService {
	EvidenceAiSummaryDTO  getEvidenceSummary(Long evidenceId);
}
