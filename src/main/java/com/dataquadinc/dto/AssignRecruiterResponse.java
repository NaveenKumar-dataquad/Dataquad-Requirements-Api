package com.dataquadinc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class AssignRecruiterResponse
{
	private String jobId;
	private String recruiterId;
	private String message;

	public AssignRecruiterResponse(String jobId, String recruiterId) {
		this.jobId = jobId;
		this.recruiterId = recruiterId;
		this.message = "Assigned Successfully"; // Set a default message
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getRecruiterId() {
		return recruiterId;
	}

	public void setRecruiterId(String recruiterId) {
		this.recruiterId = recruiterId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public AssignRecruiterResponse(String jobId, String recruiterId, String message) {
		this.jobId = jobId;
		this.recruiterId = recruiterId;
		this.message = message;
	}

}
