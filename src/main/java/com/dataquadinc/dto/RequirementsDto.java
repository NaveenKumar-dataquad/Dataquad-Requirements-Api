
package com.dataquadinc.dto;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequirementsDto
{
	@Id
	private String jobId;  // No need to add @NotNull here if it's auto-generated


	@Size(min = 3, max = 100, message = "Job Title must be between 3 and 100 characters")
	private String jobTitle;

	@NotNull(message = "Client Name cannot be null")
	@Size(min = 3, max = 100, message = "Client Name must be between 3 and 100 characters")
	private String clientName;

	@NotNull(message = "Job Description cannot be null")
//	    @Size(min = 10, max = 5000, message = "Job Description must be between 10 and 1000 characters")
	private String jobDescription;

	private MultipartFile jobDescriptionFile; // This is for file upload

	private byte[]  jobDescriptionBlob;  // BLOB data for saving to database as byte array

	public byte[] getJobDescriptionBlob() {
		return jobDescriptionBlob;
	}

	public void setJobDescriptionBlob(byte[] jobDescriptionBlob) {
		this.jobDescriptionBlob = jobDescriptionBlob;
	}

	public MultipartFile getJobDescriptionFile() {
		return jobDescriptionFile;
	}

	public void setJobDescriptionFile(MultipartFile jobDescriptionFile) {
		this.jobDescriptionFile = jobDescriptionFile;
	}

	@NotNull(message = "Job Type cannot be null")
	private String jobType;

	@NotNull(message = "Location cannot be null")
	private String location;

	@NotNull(message = "Job Mode cannot be null")
	private String jobMode;

	@NotNull(message = "Experience Required cannot be null")
	private String experienceRequired;

	@NotNull(message = "Notice Period cannot be null")
	private String noticePeriod;

	@NotNull(message = "Relevant Experience cannot be null")
	private String relevantExperience;

	@NotNull(message = "Qualification cannot be null")
	private String qualification;

	private String salaryPackage;

	private int noOfPositions;

	private LocalDateTime requirementAddedTimeStamp;

	private Set<String> recruiterIds;

	private String status;
	@JsonFormat(without = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
	private Set<String> recruiterName;
	private String assignedBy;


//	private String recruiterId;   // Add recruiterId field
//	private String recruiterEmail;


	@PrePersist
	public void prePersist() {
		if (this.jobId == null || this.jobId.isEmpty()) {
			generateJobId();  // Generate a job ID before saving
		}
	}

	// Method to generate the jobId using a random number
	private void generateJobId() {
		Random random = new Random();
		int randomNumber = 1000 + random.nextInt(9000);  // Generates a random number between 1000 and 9999
		this.jobId = "JOB" + randomNumber;  // Combine the prefix "JOB" with the random number
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public @NotNull(message = "Job Title cannot be null") @Size(min = 3, max = 100, message = "Job Title must be between 3 and 100 characters") String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(@NotNull(message = "Job Title cannot be null") @Size(min = 3, max = 100, message = "Job Title must be between 3 and 100 characters") String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public @NotNull(message = "Client Name cannot be null") @Size(min = 3, max = 100, message = "Client Name must be between 3 and 100 characters") String getClientName() {
		return clientName;
	}

	public void setClientName(@NotNull(message = "Client Name cannot be null") @Size(min = 3, max = 100, message = "Client Name must be between 3 and 100 characters") String clientName) {
		this.clientName = clientName;
	}

	public @NotNull(message = "Job Description cannot be null") String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(@NotNull(message = "Job Description cannot be null") String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public @NotNull(message = "Job Type cannot be null") String getJobType() {
		return jobType;
	}

	public void setJobType(@NotNull(message = "Job Type cannot be null") String jobType) {
		this.jobType = jobType;
	}

	public @NotNull(message = "Location cannot be null") String getLocation() {
		return location;
	}

	public void setLocation(@NotNull(message = "Location cannot be null") String location) {
		this.location = location;
	}

	public @NotNull(message = "Job Mode cannot be null") String getJobMode() {
		return jobMode;
	}

	public void setJobMode(@NotNull(message = "Job Mode cannot be null") String jobMode) {
		this.jobMode = jobMode;
	}

	public @NotNull(message = "Experience Required cannot be null") String getExperienceRequired() {
		return experienceRequired;
	}

	public void setExperienceRequired(@NotNull(message = "Experience Required cannot be null") String experienceRequired) {
		this.experienceRequired = experienceRequired;
	}

	public @NotNull(message = "Notice Period cannot be null") String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(@NotNull(message = "Notice Period cannot be null") String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public @NotNull(message = "Relevant Experience cannot be null") String getRelevantExperience() {
		return relevantExperience;
	}

	public void setRelevantExperience(@NotNull(message = "Relevant Experience cannot be null") String relevantExperience) {
		this.relevantExperience = relevantExperience;
	}

	public @NotNull(message = "Qualification cannot be null") String getQualification() {
		return qualification;
	}

	public void setQualification(@NotNull(message = "Qualification cannot be null") String qualification) {
		this.qualification = qualification;
	}

	public LocalDateTime getRequirementAddedTimeStamp() {
		return requirementAddedTimeStamp;
	}

	public void setRequirementAddedTimeStamp(LocalDateTime requirementAddedTimeStamp) {
		this.requirementAddedTimeStamp = requirementAddedTimeStamp;
	}

	public Set<String> getRecruiterIds() {
		return recruiterIds;
	}

	public void setRecruiterIds(Set<String> recruiterIds) {
		this.recruiterIds = recruiterIds;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

//	public String getRecruiterId() {
//		return recruiterId;
//	}
//
//	public void setRecruiterId(String recruiterId) {
//		this.recruiterId = recruiterId;
//	}
//
//	public String getRecruiterEmail() {
//		return recruiterEmail;
//	}
//
//	public void setRecruiterEmail(String recruiterEmail) {
//		this.recruiterEmail = recruiterEmail;
//	}


	public Set<String> getRecruiterName() {
		return recruiterName;
	}

	public void setRecruiterName(Set<String> recruiterName) {
		this.recruiterName = recruiterName;
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

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}
}
