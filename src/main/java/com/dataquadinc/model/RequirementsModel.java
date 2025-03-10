package com.dataquadinc.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.aspectj.weaver.NameMangler.PREFIX;

@Entity
@Data

@Table(name = "requirements_model")
public class RequirementsModel {


    @Id
    private String jobId;

    @NotNull(message = "Job Title cannot be null")
    @Size(min = 3, max = 100, message = "Job Title must be between 3 and 100 characters")
    private String jobTitle;

    @NotNull(message = "Client Name cannot be null")
    @Size(min = 3, max = 100, message = "Client Name must be between 3 and 100 characters")
    private String clientName;

    @NotNull(message = "Job Description cannot be null")
    @Column(columnDefinition = "LONGTEXT")
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

    @Lob
    @Column(name = "job_description_blob")
    private byte[] jobDescriptionBlob;

    private LocalDateTime requirementAddedTimeStamp;

    // Storing recruiter IDs (these will be passed from UI)
    @ElementCollection
    @CollectionTable(
            name = "job_recruiters",   // Table name for recruiter IDs
            joinColumns = @JoinColumn(name = "job_id")  // Foreign key reference to jobId
    )
    @Column(name = "recruiter_id")  // Column name for recruiter IDs
    private Set<String> recruiterIds;


    private String status;
    private Set<String> recruiterName;
    private String assignedBy;


    public byte[] getJobDescriptionBlob() {
        return jobDescriptionBlob;
    }

    public void setJobDescriptionBlob(byte[] jobDescriptionBlob) {
        this.jobDescriptionBlob = jobDescriptionBlob;
    }

    public LocalDateTime getRequirementAddedTimeStamp() {
        return requirementAddedTimeStamp;
    }

    public void setRequirementAddedTimeStamp(LocalDateTime requirementAddedTimeStamp) {
        this.requirementAddedTimeStamp = requirementAddedTimeStamp;
    }
//

    // Remove EntityManager field and make prefix/initialValue static
    private static final String PREFIX = "JOB";
    private static final Integer INITIAL_VALUE = 1;

//    @PrePersist
//    public void prePersist() {
//        if (this.jobId == null || this.jobId.isEmpty()) {
//            generateJobId();
//        }
//        System.out.println("Generated Job ID: " + this.jobId);  // Debug log
//    }
//
//    // Method to generate the jobId using a random number
//    private void generateJobId() {
//        Random random = new Random();
//        int randomNumber = 1000 + random.nextInt(9000);  // Generates a random number between 1000 and 9999
//        this.jobId = "JOB" + randomNumber;  // Combine the prefix "JOB" with the random number
//    }

    @PrePersist
    public void prePersist() {
        if (this.jobId == null || this.jobId.isEmpty()) {
            generateJobId();
        }
    }

    private void generateJobId() {
        try {
            // Use EntityManager through EntityManagerFactory instead of field injection
            EntityManager em = EntityManagerFactoryListener.getEntityManager();

            Query query = em.createQuery(
                    "SELECT COALESCE(MAX(CAST(SUBSTRING(r.jobId, ?1) AS int)), ?2) FROM RequirementsModel r " +
                            "WHERE r.jobId LIKE ?3"
            );

            query.setParameter(1, PREFIX.length() + 1)
                    .setParameter(2, INITIAL_VALUE - 1)
                    .setParameter(3, PREFIX + "%");

            Integer maxNumber = (Integer) query.getSingleResult();
            if (maxNumber == null) {
                maxNumber = INITIAL_VALUE - 1;
            }

            int nextNumber = maxNumber + 1;

            // Format the number with leading zeros to ensure the length is always 3 digits
            this.jobId = PREFIX + String.format("%03d", nextNumber);  // Adjust the number of zeros as needed

        } catch (NoResultException e) {
            this.jobId = PREFIX + String.format("%03d", INITIAL_VALUE);  // Handle the case for the initial value
        } catch (Exception e) {
            throw new RuntimeException("Error generating job ID", e);
        }
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(Set<String> recruiterName) {
        this.recruiterName = recruiterName;
    }

    // Getters and setters for other fields...

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

    public Set<String> getRecruiterIds() {
        return recruiterIds;
    }

    public void setRecruiterIds(Set<String> recruiterIds) {
        if (recruiterIds != null) {
            this.recruiterIds = recruiterIds.stream()
                    .map(id -> id.replaceAll("[\"\\[\\]\\s]", ""))  // Clean the IDs when setting
                    .collect(Collectors.toSet());
        }
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
}
