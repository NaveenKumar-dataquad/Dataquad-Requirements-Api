package com.dataquadinc.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecruiterRequirementsDto {

	private String jobId;

	private String jobTitle;

	private String clientName;

	private String jobDescription;

//	private byte[] jobDescriptionBlob;  // Add the jobDescriptionBlob field as byte array

	private String jobType;

	private String location;

	private String jobMode;

	private String experienceRequired;

	private String noticePeriod;

	private String relevantExperience;

	private String qualification;

	private String salaryPackage;

	private int  noOfPositions;

	private LocalDateTime requirementAddedTimeStamp;

	private String status;
	private String assignedBy;
	public String getAge() {
		if (requirementAddedTimeStamp == null) {
			return "N/A";
		}

		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(requirementAddedTimeStamp, now);

		long days = duration.toDays();
		long hours = duration.toHours() % 24;

		return days + " days " + hours + " hours";
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getJobMode() {
		return jobMode;
	}

	public void setJobMode(String jobMode) {
		this.jobMode = jobMode;
	}

	public String getExperienceRequired() {
		return experienceRequired;
	}

	public void setExperienceRequired(String experienceRequired) {
		this.experienceRequired = experienceRequired;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public String getRelevantExperience() {
		return relevantExperience;
	}

	public void setRelevantExperience(String relevantExperience) {
		this.relevantExperience = relevantExperience;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getSalaryPackage() {
		return salaryPackage;
	}

	public void setSalaryPackage(String salaryPackage) {
		this.salaryPackage = salaryPackage;
	}

	public int getNoOfPositions() {
		return noOfPositions;
	}

	public void setNoOfPositions(int noOfPositions) {
		this.noOfPositions = noOfPositions;
	}

	public LocalDateTime getRequirementAddedTimeStamp() {
		return requirementAddedTimeStamp;
	}

	public void setRequirementAddedTimeStamp(LocalDateTime requirementAddedTimeStamp) {
		this.requirementAddedTimeStamp = requirementAddedTimeStamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}
}
