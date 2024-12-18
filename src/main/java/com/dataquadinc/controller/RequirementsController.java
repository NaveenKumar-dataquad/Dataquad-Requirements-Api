package com.dataquadinc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dataquadinc.dto.RecruiterRequirementsDto;
import com.dataquadinc.dto.RequirementAddedResponse;
import com.dataquadinc.dto.RequirementsDto;
import com.dataquadinc.dto.StatusDto;
import com.dataquadinc.service.RequirementsService;

@RestController
@RequestMapping("/requirements")
@CrossOrigin("*")
public class RequirementsController {

	@Autowired
	private RequirementsService service;

	@PostMapping("/assignJob")
	public ResponseEntity<RequirementAddedResponse> addRequirement(@RequestBody RequirementsDto requirementsDto) {
		return new ResponseEntity<>(service.createRequirement(requirementsDto), HttpStatus.CREATED);
	}

	@GetMapping("/getAssignments")
	public ResponseEntity<?> getRequirements() {
		return new ResponseEntity<>(service.getRequirementsDetails(), HttpStatus.OK);
	}

	@GetMapping("/get/{jobId}")
	public ResponseEntity<RequirementsDto> getRequirementById(@PathVariable String jobId) {
		return new ResponseEntity<>(service.getRequirementDetailsById(jobId), HttpStatus.OK);
	}

	@PutMapping("/assign")
	public ResponseEntity<?> assignRequirement(@RequestParam String jobId, @RequestParam String recruiterId) {
		return new ResponseEntity<>(service.assignToRecruiter(jobId, recruiterId), HttpStatus.OK);
	}

	@PutMapping("/updateStatus")
	public ResponseEntity<Void> updateStatus(@RequestBody StatusDto status) {
		service.statusUpdate(status);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/recruiter/{recruiterId}")
	public ResponseEntity<List<RecruiterRequirementsDto>> getJobsByRecruiter(@PathVariable String recruiterId) {
		return new ResponseEntity<>(service.getJobsAssignedToRecruiter(recruiterId),HttpStatus.OK);
	}

}
