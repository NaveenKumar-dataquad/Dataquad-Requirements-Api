package com.dataquadinc.dto;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AssignedRequirementsDto {

    private String jobId;  // No need to add @NotNull here if it's auto-generated

    @Size(min = 3, max = 100, message = "Job Title must be between 3 and 100 characters")
    private String jobTitle;

    @NotNull(message = "Client Name cannot be null")
    @Size(min = 3, max = 100, message = "Client Name must be between 3 and 100 characters")
    private String clientName;

    @NotNull(message = "Job Description cannot be null")
    private String jobDescription;

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
    private Set<String> recruiterName;

    // Removed jobDescriptionFile and jobDescriptionBlob

}