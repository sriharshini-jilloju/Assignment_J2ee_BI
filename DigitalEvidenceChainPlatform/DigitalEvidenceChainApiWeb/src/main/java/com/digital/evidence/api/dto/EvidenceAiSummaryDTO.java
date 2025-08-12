package com.digital.evidence.api.dto;

public class EvidenceAiSummaryDTO {
	private Long evidenceId;
	private String summary;

	public EvidenceAiSummaryDTO() {
	}

	public EvidenceAiSummaryDTO(Long evidenceId, String summary) {
		this.evidenceId = evidenceId;
		this.summary = summary;
	}

	public Long getEvidenceId() {
		return evidenceId;
	}

	public void setEvidenceId(Long evidenceId) {
		this.evidenceId = evidenceId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
