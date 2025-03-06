package com.dataquadinc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CandidateDto {
    private String candidateId;
    private String candidateName;
    private String email;
    private String interviewStatus;
    private String contactNumber;
    private String qualification;
    private String skills;
    private String overallFeedback;

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
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

    public CandidateDto(String candidateId, String candidateName, String email, String interviewStatus, String contactNumber, String qualification, String skills, String overallFeedback) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.email = email;
        this.interviewStatus = interviewStatus;
        this.contactNumber = contactNumber;
        this.qualification = qualification;
        this.skills = skills;
        this.overallFeedback = overallFeedback;
    }
}


