package com.dataquadinc.model;



import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
public class RequirementsModel {

    @Id
    @NotNull(message = "Job ID cannot be null")
    private String jobId;

    @NotNull(message = "Job Title cannot be null")
    @Size(min = 3, max = 100, message = "Job Title must be between 3 and 100 characters")
    private String jobTitle;

    @NotNull(message = "Client Name cannot be null")
    @Size(min = 3, max = 100, message = "Client Name must be between 3 and 100 characters")
    private String clientName;

    @NotNull(message = "Job Description cannot be null")
    @Size(min = 10, max = 15000, message = "Job Description must be between 10 and 1000 characters")
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
    
    private LocalDateTime requirementAddedTimeStamp;

   
    @ElementCollection
    @CollectionTable(
        name = "job_recruiters",  
        joinColumns = @JoinColumn(name = "job_id")
    )
    @Column(name = "recruiter_id")  
    private Set<String> recruiterIds;

    private String status;
    
    private String remark;
    
}
