package com.dataquadinc.repository;

import java.util.List;
import java.util.Optional;

import com.dataquadinc.dto.InterviewScheduledDTO;
import com.dataquadinc.dto.JobDetailsDTO;
import com.dataquadinc.dto.SubmittedCandidateDTO;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dataquadinc.model.RequirementsModel;

@Repository
public interface RequirementsDao extends JpaRepository<RequirementsModel, String>
{
    @Query("SELECT r FROM RequirementsModel r WHERE :recruiterId MEMBER OF r.recruiterIds")
    List<RequirementsModel> findJobsByRecruiterId(String recruiterId);
    // Fetch recruiters for a given jobId
    @Query("SELECT r FROM RequirementsModel r WHERE r.jobId = :jobId")
    Optional<RequirementsModel> findRecruitersByJobId(@Param("jobId") String jobId);

    @Query(value = "SELECT * FROM candidates_prod WHERE job_id = :jobId AND user_id = :recruiterId", nativeQuery = true)
    List<Tuple> findCandidatesByJobIdAndRecruiterId(@Param("jobId") String jobId, @Param("recruiterId") String recruiterId);

    @Query(value = "SELECT * FROM candidates_prod WHERE job_id = :jobId AND user_id = :recruiterId AND interview_status = 'Scheduled'", nativeQuery = true)
    List<Tuple> findInterviewScheduledCandidatesByJobIdAndRecruiterId(@Param("jobId") String jobId, @Param("recruiterId") String recruiterId);

    @Query(value = "SELECT email, user_name FROM user_details_prod WHERE user_id = :userId AND status != 'inactive'", nativeQuery = true)
    Tuple findUserEmailAndUsernameByUserId(@Param("userId") String userId);


    @Query(value = """
    SELECT u.user_id, u.user_name, r.name AS role_name, u.email, 
           u.designation, u.joining_date, u.gender, u.dob, 
           u.phone_number, u.personalemail, u.status, b.client_name 
    FROM user_details_prod u 
    LEFT JOIN user_roles_prod ur ON u.user_id = ur.user_id 
    LEFT JOIN roles_prod r ON ur.role_id = r.id
    LEFT JOIN bdm_client_prod b ON u.user_id = b.on_boarded_by
    WHERE r.name = 'BDM' AND u.user_id = :userId
    """, nativeQuery = true)
    List<Tuple> findBdmEmployeeByUserId(@Param("userId") String userId);


    // Get Clients onboarded by BDM (based on userId)
    @Query(value = """
    SELECT id, client_name, on_boarded_by, client_address, 
           JSON_UNQUOTE(JSON_EXTRACT(client_spoc_name, '$')) AS client_spoc_name,
           JSON_UNQUOTE(JSON_EXTRACT(client_spoc_emailid, '$')) AS client_spoc_emailid,
           JSON_UNQUOTE(JSON_EXTRACT(client_spoc_mobile_number, '$')) AS client_spoc_mobile_number
    FROM bdm_client_prod 
    WHERE on_boarded_by = (SELECT user_name FROM user_details_prod WHERE user_id = :userId)
""", nativeQuery = true)
    List<Tuple> findClientsByBdmUserId(@Param("userId") String userId);

    // Get all job IDs and client names onboarded by BDM
    @Query(value = """
        SELECT r.job_id, r.job_title, b.client_name
        FROM requirements_model_prod r
        JOIN bdm_client_prod b ON r.client_name = b.client_name
        WHERE b.on_boarded_by = (SELECT user_name FROM user_details_prod WHERE user_id = :userId)
    """, nativeQuery = true)
    List<Tuple> findJobsByBdmUserId(@Param("userId") String userId);

    // Fetch all submissions for a client across ALL job IDs
    @Query(value = """
        SELECT c.candidate_id, c.full_name, c.candidate_email_id AS candidateEmailId, 
               c.contact_number, c.qualification, c.skills, c.overall_feedback, c.user_id,
               r.job_id, r.job_title, b.client_name
        FROM candidates_prod c
        JOIN requirements_model_prod r ON c.job_id = r.job_id
        JOIN bdm_client_prod b ON r.client_name = b.client_name
        WHERE b.client_name = :clientName
        """, nativeQuery = true)
    List<Tuple> findAllSubmissionsByClientName(@Param("clientName") String clientName);

    // Fetch all interview scheduled candidates for a client
    @Query(value = """
    SELECT c.candidate_id, 
           c.full_name, 
           c.candidate_email_id AS candidateEmailId, 
           c.contact_number, 
           c.qualification, 
           c.skills, 
           -- ✅ Extract the latest status from JSON or direct string
           CASE 
               WHEN JSON_VALID(c.interview_status) 
               THEN JSON_UNQUOTE(JSON_EXTRACT(c.interview_status, '$[0].status')) 
               ELSE c.interview_status 
           END AS interview_status, 
           c.interview_level, 
           c.interview_date_time, 
           r.job_id, 
           r.job_title, 
           b.client_name
    FROM (
        SELECT candidate_id, full_name, candidate_email_id, contact_number, qualification, 
               skills, interview_status, interview_level, interview_date_time, job_id, 
               ROW_NUMBER() OVER (PARTITION BY candidate_id ORDER BY interview_date_time DESC) AS rn 
        FROM candidates_prod 
    ) c
    JOIN requirements_model_prod r ON c.job_id = r.job_id
    LEFT JOIN bdm_client_prod b ON r.client_name = b.client_name
    WHERE (b.client_name = :clientName OR r.client_name = :clientName 
           OR (:clientName IS NULL AND EXISTS (
                SELECT 1 FROM candidates_prod c2 
                WHERE c2.job_id = r.job_id
           )) )
    AND c.rn = 1  -- ✅ Fetch only the latest interview status per candidate
    -- ✅ Ensure only "Scheduled" candidates are included
    AND b.client_name IS NOT NULL 
    AND c.interview_date_time IS NOT NULL
""", nativeQuery = true)
    List<Tuple> findAllInterviewsByClientName(@Param("clientName") String clientName);




    // Fetch all placements for a client across ALL job IDs
    @Query(value = """
    SELECT c.candidate_id, 
           c.full_name, 
           c.candidate_email_id AS candidateEmailId,  
           r.job_id, 
           r.job_title, 
           b.client_name
    FROM candidates_prod c
    JOIN requirements_model_prod r ON c.job_id = r.job_id
    JOIN bdm_client_prod b ON r.client_name = b.client_name
    WHERE b.client_name = :clientName
    AND (
        -- ✅ Check if interview_status is a valid JSON and contains "Placed"
        (JSON_VALID(c.interview_status) 
         AND JSON_SEARCH(c.interview_status, 'one', 'Placed', NULL, '$[*].status') IS NOT NULL)
        -- ✅ OR check if interview_status is stored as plain text "Placed"
        OR UPPER(c.interview_status) = 'PLACED'
    )
""", nativeQuery = true)
    List<Tuple> findAllPlacementsByClientName(@Param("clientName") String clientName);

    @Query(value = """
    SELECT 
        u.user_id AS employeeId,
        u.user_name AS employeeName,
        u.email AS employeeEmail,
        r.name AS role,
        
        -- Subquery to count distinct submissions
        COALESCE((SELECT COUNT(DISTINCT c.candidate_id) 
                  FROM candidates_prod c 
                  WHERE c.user_id = u.user_id), 0) AS numberOfSubmissions,

        -- Subquery to count distinct interviews (we check interview status directly)
        COALESCE((SELECT SUM(CASE 
                            WHEN c.interview_status = 'Scheduled' OR c.interview_date_time IS NOT NULL 
                            THEN 1 ELSE 0 
                        END) 
                  FROM candidates_prod c 
                  WHERE c.user_id = u.user_id), 0) AS numberOfInterviews,

        -- Subquery to count distinct placements
        COALESCE((SELECT SUM(CASE 
                            WHEN c.interview_status = 'Placed' THEN 1 ELSE 0 
                        END) 
                  FROM candidates_prod c 
                  WHERE c.user_id = u.user_id), 0) +
        COALESCE((SELECT SUM(CASE 
                            WHEN JSON_VALID(c.interview_status) = 1  
                            AND JSON_SEARCH(c.interview_status, 'one', 'Placed', NULL, '$[*].status') IS NOT NULL 
                            THEN 1 ELSE 0 
                        END) 
                  FROM candidates_prod c 
                  WHERE c.user_id = u.user_id), 0) AS numberOfPlacements,
        
        -- Subquery to count distinct clients based on job_id and client_name
        COALESCE((SELECT COUNT(DISTINCT req.client_name) 
                  FROM requirements_model_prod req 
                  JOIN job_recruiters_prod jrp ON req.job_id = jrp.job_id
                  WHERE jrp.recruiter_id = u.user_id), 0) AS numberOfClients,

        -- Subquery to count distinct job_ids based on recruiter_id
        COALESCE((SELECT COUNT(DISTINCT req.job_id) 
                  FROM requirements_model_prod req 
                  JOIN job_recruiters_prod jrp ON req.job_id = jrp.job_id
                  WHERE jrp.recruiter_id = u.user_id), 0) AS numberOfRequirements

    FROM user_details_prod u
    JOIN user_roles_prod ur ON u.user_id = ur.user_id
    JOIN roles_prod r ON ur.role_id = r.id
    
    WHERE r.name IN ('Employee', 'Teamlead')
    
""", nativeQuery = true)
    List<Tuple> getEmployeeCandidateStats();



    @Query(value = """
        SELECT 
            c.candidate_id AS candidateId,
            c.full_name AS fullName,
            c.candidate_email_id AS candidateEmailId,
            c.contact_number AS contactNumber,
            c.qualification AS qualification,
            c.skills AS skills,
            c.overall_feedback AS overallFeedback,
            r.job_id AS jobId,
            r.job_title AS jobTitle
        FROM candidates_prod c
        JOIN user_details_prod u ON c.user_id = u.user_id
        JOIN requirements_model_prod r ON c.job_id = r.job_id
        WHERE u.user_id = :userId
    """, nativeQuery = true)
    List<SubmittedCandidateDTO> findSubmittedCandidatesByUserId(@Param("userId") String userId);

    @Query(value = """
        SELECT c.candidate_id, 
               c.full_name, 
               c.candidate_email_id AS candidateEmailId, 
               c.contact_number, 
               c.qualification, 
               c.skills, 
               -- ✅ Extract the latest status from JSON or direct string
               CASE 
                   WHEN JSON_VALID(c.interview_status) 
                   THEN JSON_UNQUOTE(JSON_EXTRACT(c.interview_status, '$[0].status')) 
                   ELSE c.interview_status 
               END AS interviewStatus,
               c.interview_level AS interviewLevel,
               c.interview_date_time AS interviewDateTime,
               r.job_id AS jobId,
               r.job_title AS jobTitle,
               b.client_name AS clientName
        FROM (
            SELECT candidate_id, full_name, candidate_email_id, contact_number, qualification, 
                   skills, interview_status, interview_level, interview_date_time, job_id, 
                   ROW_NUMBER() OVER (PARTITION BY candidate_id ORDER BY interview_date_time DESC) AS rn 
            FROM candidates_prod 
            WHERE user_id = :userId  -- ✅ Filter by userId
        ) c
        JOIN requirements_model_prod r ON c.job_id = r.job_id
        LEFT JOIN bdm_client_prod b ON r.client_name = b.client_name
        WHERE (
                b.client_name IS NOT NULL  -- ✅ Ensure client name is not null
              )
          AND c.rn = 1  -- ✅ Fetch only the latest interview status per candidate
          AND c.interview_status = 'Scheduled'  -- ✅ Only "Scheduled" interviews
          AND c.interview_date_time IS NOT NULL  -- ✅ Ensure interviews are scheduled (with date/time)
    """, nativeQuery = true)
    List<InterviewScheduledDTO> findScheduledInterviewsByUserId(@Param("userId") String userId);

    @Query(value = """
        SELECT 
            TRIM(r.job_id) AS jobId,  -- Trim job_id to remove leading/trailing spaces
            TRIM(r.job_title) AS jobTitle,  -- Trim job_title
            TRIM(r.client_name) AS clientName  -- Trim client_name
        FROM requirements_model_prod r
        JOIN job_recruiters_prod jr ON r.job_id = jr.job_id
        JOIN user_details_prod u ON jr.recruiter_id = u.user_id
        WHERE u.user_id = :userId
    """, nativeQuery = true)
    List<JobDetailsDTO> findJobDetailsByUserId(@Param("userId") String userId);
}
