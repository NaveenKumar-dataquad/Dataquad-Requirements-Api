

package com.dataquadinc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignore null fields in JSON response
@Data
@NoArgsConstructor  // Default constructor for JSON serialization
public class BdmSubmissionDTO {
    private String candidateId;
    private String fullName;
    private String candidateEmailId;
    private String contactNumber;
    private String qualification;
    private String skills;
    private String overallFeedback;
    private String jobId;
    private String jobTitle;
    private String clientName;

    public BdmSubmissionDTO(String candidateId, String fullName, String candidateEmailId,
                            String contactNumber, String qualification, String skills,
                            String overallFeedback, String jobId, String jobTitle, String clientName) {
        this.candidateId = candidateId;
        this.fullName = fullName;
        this.candidateEmailId = candidateEmailId;
        this.contactNumber = contactNumber;
        this.qualification = qualification;
        this.skills = skills;
        this.overallFeedback = overallFeedback;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.clientName = clientName;
    }


    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCandidateEmailId() {
        return candidateEmailId;
    }

    public void setCandidateEmailId(String candidateEmailId) {
        this.candidateEmailId = candidateEmailId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getOverallFeedback() {
        return overallFeedback;
    }

    public void setOverallFeedback(String overallFeedback) {
        this.overallFeedback = overallFeedback;
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
}
