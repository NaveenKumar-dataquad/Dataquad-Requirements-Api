package com.dataquadinc.dto;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RecruiterRequirementsDto {

	private String jobId;

	private String jobTitle;

	private String clientName;

	private String jobDescription;

	private String jobType;

	private String location;

	private String jobMode;

	private String experienceRequired;

	private String noticePeriod;

	private String relevantExperience;

	private String qualification;

	private LocalDateTime requirementAddedTimeStamp;

	private String status;

	private String remark;

}
