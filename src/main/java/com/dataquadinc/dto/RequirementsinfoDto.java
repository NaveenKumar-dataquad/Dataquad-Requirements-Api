
package com.dataquadinc.dto;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequirementsinfoDto {

    @Id
    private String jobId;

    @Size(min = 3, max = 100, message = "Job Title must be between 3 and 100 characters")
    private String jobTitle;

    @NotNull(message = "Client Name cannot be null")
    @Size(min = 3, max = 100, message = "Client Name must be between 3 and 100 characters")
    private String clientName;

    @NotNull(message = "Job Description cannot be null")
    private String jobDescription;

    private MultipartFile jobDescriptionFile;

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

    @PrePersist
    public void prePersist() {
        if (this.jobId == null || this.jobId.isEmpty()) {
            generateJobId();
        }
    }

    private void generateJobId() {
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000);
        this.jobId = "JOB" + randomNumber;
    }
}
