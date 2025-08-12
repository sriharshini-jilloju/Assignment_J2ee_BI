package com.digital.evidence.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digital.evidence.api.dto.EvidenceAiSummaryDTO;
import com.digital.evidence.service.EvidenceSummaryService;

@Service
public class EvidenceSummaryApiServiceImpl implements EvidenceSummaryApiService {

    private final EvidenceSummaryService evidenceSummaryService;
    
    @Autowired
    public EvidenceSummaryApiServiceImpl(EvidenceSummaryService evidenceSummaryService) {
        this.evidenceSummaryService = evidenceSummaryService;
    }
	
	@Override
	public EvidenceAiSummaryDTO  getEvidenceSummary(Long evidenceId) {
		// TODO Auto-generated method stub
		String text = evidenceSummaryService.generateEvidenceSummary(evidenceId);
        return new EvidenceAiSummaryDTO(evidenceId, text);
	}

}
