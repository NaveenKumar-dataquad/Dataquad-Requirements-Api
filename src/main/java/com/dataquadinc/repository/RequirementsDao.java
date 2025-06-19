package com.dataquadinc.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dataquadinc.dto.*;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dataquadinc.model.RequirementsModel;

@Repository
public interface RequirementsDao extends JpaRepository<RequirementsModel, String> {

    @Query("SELECT r FROM RequirementsModel r WHERE r.jobId = :jobId")
    Optional<RequirementsModel> findRecruitersByJobId(@Param("jobId") String jobId);



    @Query(value = """

    SELECT cs.*, cd.*, u.user_name AS recruiterName 

    FROM candidate_submissions cs

    JOIN candidates cd ON cs.candidate_id = cd.candidate_id

    JOIN user_details u ON cd.user_email = u.email

    WHERE cs.job_id = :jobId

    """, nativeQuery = true)

    List<Tuple> findCandidatesByJobId(@Param("jobId") String jobId);



    @Query(value = """
    SELECT cs.*, 
           cd.*, 
           u.user_name AS recruiterName, 
           id.interview_date_time AS interviewDateTime,
           id.full_name AS candidateName,      -- Candidate name from interview_details table
           id.candidate_email_id AS email,     -- Candidate email from interview_details table
           id.interview_level AS interviewLevel -- Interview level from interview_details table
    FROM candidate_submissions cs
    JOIN candidates cd ON cs.candidate_id = cd.candidate_id
    JOIN user_details u ON cd.user_email = u.email
    JOIN interview_details id ON cs.candidate_id = id.candidate_id
    WHERE cs.job_id = :jobId
      AND id.interview_date_time IS NOT NULL
      AND cs.job_id = id.job_id           -- Ensure job_id is present in both tables
""", nativeQuery = true)
    List<Tuple> findInterviewScheduledCandidatesByJobId(@Param("jobId") String jobId);

    @Query(value = "SELECT email, user_name FROM user_details WHERE user_id = :userId AND status != 'inactive'", nativeQuery = true)
    Tuple findUserEmailAndUsernameByUserId(@Param("userId") String userId);

    @Query(value = "SELECT email, user_name FROM user_details WHERE LOWER(TRIM(user_name)) = LOWER(TRIM(:assignedBy)) AND status != 'inactive'", nativeQuery = true)
    Tuple findUserEmailAndUsernameByAssignedBy(@Param("assignedBy") String assignedBy);


    @Query(value = """
            SELECT u.user_id, u.user_name, r.name AS role_name, u.email, 
                   u.designation, u.joining_date, u.gender, u.dob, 
                   u.phone_number, u.personalemail, u.status, b.client_name 
            FROM user_details u 
            LEFT JOIN user_roles ur ON u.user_id = ur.user_id 
            LEFT JOIN roles r ON ur.role_id = r.id
            LEFT JOIN bdm_client b ON u.user_id = b.on_boarded_by
            WHERE r.name = 'BDM' AND u.user_id = :userId
            """, nativeQuery = true)
    List<Tuple> findBdmEmployeeByUserId(@Param("userId") String userId);

    @Query(value = """
            SELECT id, client_name, on_boarded_by, client_address, 
                   JSON_UNQUOTE(JSON_EXTRACT(client_spoc_name, '$')) AS client_spoc_name,
                   JSON_UNQUOTE(JSON_EXTRACT(client_spoc_emailid, '$')) AS client_spoc_emailid,
                   JSON_UNQUOTE(JSON_EXTRACT(client_spoc_mobile_number, '$')) AS client_spoc_mobile_number
            FROM bdm_client 
            WHERE on_boarded_by = (SELECT user_name FROM user_details WHERE user_id = :userId)
            """, nativeQuery = true)
    List<Tuple> findClientsByBdmUserId(@Param("userId") String userId);

    @Query(value = """
        SELECT id, client_name, on_boarded_by, client_address, 
               JSON_UNQUOTE(JSON_EXTRACT(client_spoc_name, '$')) AS client_spoc_name,
               JSON_UNQUOTE(JSON_EXTRACT(client_spoc_emailid, '$')) AS client_spoc_emailid,
               JSON_UNQUOTE(JSON_EXTRACT(client_spoc_mobile_number, '$')) AS client_spoc_mobile_number
        FROM bdm_client 
        WHERE on_boarded_by = (SELECT user_name FROM user_details WHERE user_id = :userId)
          AND DATE(created_at) BETWEEN :startDate AND :endDate
        """, nativeQuery = true)
    List<Tuple> findClientsByBdmUserIdAndCreatedAtBetween(@Param("userId") String userId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);


    @Query(value = """
                SELECT r.job_id, r.job_title, b.client_name
                FROM requirements_model r
                JOIN bdm_client b ON r.client_name = b.client_name
                WHERE b.on_boarded_by = (SELECT user_name FROM user_details WHERE user_id = :userId)
            """, nativeQuery = true)
    List<Tuple> findJobsByBdmUserId(@Param("userId") String userId);

    @Query(value = """
            SELECT u.user_name AS recruiter_name, 
                   r.client_name, 
                   r.job_id, 
                   r.job_title, 
                   r.assigned_by, 
                   r.location, 
                   r.notice_period
            FROM requirements_model r
            JOIN job_recruiters jr 
                ON r.job_id = jr.job_id
            JOIN user_details u 
                ON jr.recruiter_id = u.user_id
            JOIN bdm_client b 
                ON TRIM(UPPER(r.client_name)) COLLATE utf8mb4_bin = TRIM(UPPER(b.client_name)) COLLATE utf8mb4_bin
            WHERE TRIM(UPPER(b.client_name)) COLLATE utf8mb4_bin = TRIM(UPPER(:clientName)) COLLATE utf8mb4_bin
            AND r.job_id IS NOT NULL
            """, nativeQuery = true)
    List<Tuple> findRequirementsByClientName(@Param("clientName") String clientName);

    @Query(value = """
        SELECT u.user_name AS recruiter_name, 
               r.client_name, 
               r.job_id, 
               r.job_title, 
               r.assigned_by, 
               r.location, 
               r.notice_period
        FROM requirements_model r
        JOIN job_recruiters jr 
            ON r.job_id = jr.job_id
        JOIN user_details u 
            ON jr.recruiter_id = u.user_id
        JOIN bdm_client b 
            ON TRIM(UPPER(r.client_name)) COLLATE utf8mb4_bin = TRIM(UPPER(b.client_name)) COLLATE utf8mb4_bin
        WHERE TRIM(UPPER(b.client_name)) COLLATE utf8mb4_bin = TRIM(UPPER(:clientName)) COLLATE utf8mb4_bin
          AND r.job_id IS NOT NULL
          AND DATE(r.requirement_added_time_stamp) BETWEEN :startDate AND :endDate
        """, nativeQuery = true)
    List<Tuple> findRequirementsByClientNameDateFilter(
            @Param("clientName") String clientName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
        SELECT 
            cd.candidate_id, 
            cd.full_name, 
            cd.candidate_email_id AS candidateEmailId, 
            cd.contact_number, 
            cd.qualification, 
            cs.skills, 
            cs.overall_feedback, 
            cd.user_id,
            r.job_id, 
            r.job_title, 
            b.client_name
        FROM candidate_submissions cs
        JOIN candidates cd ON cs.candidate_id = cd.candidate_id
        JOIN requirements_model r ON cs.job_id = r.job_id
        JOIN bdm_client b ON r.client_name = b.client_name
        WHERE b.client_name = :clientName
        """, nativeQuery = true)
    List<Tuple> findAllSubmissionsByClientName(@Param("clientName") String clientName);

    @Query(value = """
        SELECT 
            cd.candidate_id, 
            cd.full_name, 
            cd.candidate_email_id AS candidateEmailId, 
            cd.contact_number, 
            cd.qualification, 
            cs.skills, 
            cs.overall_feedback, 
            cd.user_id,
            r.job_id, 
            r.job_title, 
            b.client_name
        FROM candidate_submissions cs
        JOIN candidates cd ON cs.candidate_id = cd.candidate_id
        JOIN requirements_model r ON cs.job_id = r.job_id
        JOIN bdm_client b ON r.client_name = b.client_name
        WHERE b.client_name = :clientName
          AND DATE(cs.submitted_at) BETWEEN :startDate AND :endDate
        """, nativeQuery = true)
    List<Tuple> findAllSubmissionsByClientNameAndSubmittedAtBetween(
            @Param("clientName") String clientName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        cd.candidate_id, 
        cd.full_name, 
        cd.candidate_email_id AS candidateEmailId, 
        cd.contact_number, 
        cd.qualification, 
        cs.skills, 
        CASE 
            WHEN JSON_VALID(li.interview_status) 
            THEN JSON_UNQUOTE(JSON_EXTRACT(li.interview_status, '$[0].status')) 
            ELSE li.interview_status 
        END AS interview_status, 
        li.interview_level, 
        li.interview_date_time, 
        r.job_id, 
        r.job_title, 
        b.client_name
    FROM (
        SELECT 
            idt.candidate_id,
            idt.interview_status,
            idt.interview_level,
            idt.interview_date_time,
            cs.job_id,
            ROW_NUMBER() OVER (PARTITION BY idt.candidate_id ORDER BY idt.interview_date_time DESC) AS rn 
        FROM interview_details idt
        JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
    ) li
    JOIN candidate_submissions cs ON li.job_id = cs.job_id
    JOIN candidates cd ON cs.candidate_id = cd.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    LEFT JOIN bdm_client b ON r.client_name = b.client_name
    WHERE (b.client_name = :clientName OR r.client_name = :clientName 
           OR (:clientName IS NULL AND EXISTS (
                SELECT 1 FROM candidate_submissions cs2 
                WHERE cs2.job_id = r.job_id
           )) )
    AND li.rn = 1
    AND b.client_name IS NOT NULL 
    AND li.interview_date_time IS NOT NULL
    """, nativeQuery = true)
    List<Tuple> findAllInterviewsByClientName(@Param("clientName") String clientName);

    @Query(value = """
    SELECT 
        cd.candidate_id, 
        cd.full_name, 
        cd.candidate_email_id AS candidateEmailId, 
        cd.contact_number, 
        cd.qualification, 
        cs.skills, 
        CASE 
            WHEN JSON_VALID(li.interview_status) 
            THEN JSON_UNQUOTE(JSON_EXTRACT(li.interview_status, '$[0].status')) 
            ELSE li.interview_status 
        END AS interview_status, 
        li.interview_level, 
        li.interview_date_time, 
        r.job_id, 
        r.job_title, 
        b.client_name
    FROM (
        SELECT 
            idt.candidate_id,
            idt.interview_status,
            idt.interview_level,
            idt.interview_date_time,
            idt.timestamp,
            cs.job_id,
            ROW_NUMBER() OVER (PARTITION BY idt.candidate_id ORDER BY idt.interview_date_time DESC) AS rn 
        FROM interview_details idt
        JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
    ) li
    JOIN candidate_submissions cs ON li.job_id = cs.job_id
    JOIN candidates cd ON cs.candidate_id = cd.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    LEFT JOIN bdm_client b ON r.client_name = b.client_name
    WHERE (b.client_name = :clientName OR r.client_name = :clientName 
           OR (:clientName IS NULL AND EXISTS (
                SELECT 1 FROM candidate_submissions cs2 
                WHERE cs2.job_id = r.job_id
           )) )
    AND li.rn = 1
    AND b.client_name IS NOT NULL 
    AND li.interview_date_time IS NOT NULL
    AND DATE(li.timestamp) BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    List<Tuple> findAllInterviewsByClientNameDateFilter(
            @Param("clientName") String clientName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query(value = """
        SELECT 
            cd.candidate_id, 
            cd.full_name, 
            cd.candidate_email_id AS candidateEmailId,  
            r.job_id, 
            r.job_title, 
            b.client_name
        FROM interview_details idt
        JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
        JOIN candidates cd ON cs.candidate_id = cd.candidate_id
        JOIN requirements_model r ON cs.job_id = r.job_id
        JOIN bdm_client b ON r.client_name = b.client_name
        WHERE b.client_name = :clientName
          AND (
            (JSON_VALID(idt.interview_status) 
             AND JSON_SEARCH(idt.interview_status, 'one', 'PLACED', NULL, '$[*].status') IS NOT NULL)
            OR UPPER(idt.interview_status) = 'PLACED'
          )
        """, nativeQuery = true)
    List<Tuple> findAllPlacementsByClientName(@Param("clientName") String clientName);

    @Query(value = """
    SELECT 
        cd.candidate_id, 
        cd.full_name, 
        cd.candidate_email_id AS candidateEmailId,  
        r.job_id, 
        r.job_title, 
        b.client_name
    FROM interview_details idt
    JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
    JOIN candidates cd ON cs.candidate_id = cd.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    JOIN bdm_client b ON r.client_name = b.client_name
    WHERE b.client_name = :clientName
      AND (
        (JSON_VALID(idt.interview_status) 
         AND JSON_SEARCH(idt.interview_status, 'one', 'PLACED', NULL, '$[*].status') IS NOT NULL)
        OR UPPER(idt.interview_status) = 'PLACED'
      )
      AND DATE(idt.timestamp) BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    List<Tuple> findAllPlacementsByClientNameDateFilter(
            @Param("clientName") String clientName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Fetch employee candidate statistics
    @Query(value = """
    SELECT 
        u.user_id AS employeeId,
        u.user_name AS employeeName,
        u.email AS employeeEmail,
        r.name AS role,
        COALESCE((
            SELECT COUNT(DISTINCT cd.candidate_id) 
            FROM candidates cd
            JOIN candidate_submissions cs ON cd.candidate_id = cs.candidate_id
            WHERE cd.user_id = u.user_id
        ), 0) AS numberOfSubmissions,
        
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id = u.user_id
            AND idt.interview_date_time IS NOT NULL
        ), 0) AS numberOfInterviews,
        
            COALESCE((
                        SELECT COUNT(DISTINCT idt.interview_id)
                        FROM interview_details idt
                        JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
                        JOIN candidates cd ON cs.candidate_id = cd.candidate_id
                        WHERE cd.user_id = u.user_id
                          AND idt.interview_date_time IS NOT NULL -- ✅ interview must be scheduled
                          AND (
                              -- CASE 1: Direct string match
                              idt.interview_status = 'PlACED'
            
                              -- CASE 2: JSON with latest status = 'PLACED'
                              OR (
                                  JSON_VALID(idt.interview_status)
                                  AND JSON_UNQUOTE(
                                      JSON_EXTRACT(
                                          idt.interview_status,
                                          CONCAT('$[', JSON_LENGTH(idt.interview_status) - 1, '].status')
                                      )
                                  ) = 'PLACED'  -- CASE SENSITIVE match
                              )
                          )
                    ), 0) AS numberOfPlacements,
            
            
        
        COALESCE((
            SELECT COUNT(DISTINCT req.client_name) 
            FROM requirements_model req 
            JOIN job_recruiters jrp ON req.job_id = jrp.job_id
            WHERE jrp.recruiter_id = u.user_id
        ), 0) AS numberOfClients,
        
        COALESCE((
            SELECT COUNT(DISTINCT req.job_id) 
            FROM requirements_model req 
            JOIN job_recruiters jrp ON req.job_id = jrp.job_id
            WHERE jrp.recruiter_id = u.user_id
        ), 0) AS numberOfRequirements
        
    FROM user_details u
    JOIN user_roles ur ON u.user_id = ur.user_id
    JOIN roles r ON ur.role_id = r.id
    WHERE r.name = 'Employee'
    """, nativeQuery = true)
    List<Tuple> getEmployeeCandidateStats();

    // Teamlead candidate statistics
    @Query(value = """
    SELECT 
        u.user_id AS employeeId,
        u.user_name AS employeeName,
        u.email AS employeeEmail,
        'TEAMLEAD' AS role,
        COALESCE((
            SELECT COUNT(DISTINCT r2.client_name)
            FROM requirements_model r2
            WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
        ), 0) AS numberOfClients,
        
        COALESCE((
            SELECT COUNT(DISTINCT r2.job_id)
            FROM requirements_model r2
            WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
        ), 0) AS numberOfRequirements,
        
        -- Self Submissions
        COALESCE((
            SELECT COUNT(*)
            FROM candidates cd
            JOIN candidate_submissions cs ON cd.candidate_id = cs.candidate_id
            WHERE cd.user_id = u.user_id
        ), 0) AS selfSubmissions,
        
        -- Self Interviews
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id = u.user_id
            AND idt.interview_date_time >= NOW()
            AND cs.job_id IN (
                SELECT job_id FROM requirements_model r2
                WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            )
        ), 0) AS selfInterviews,
        
        -- Self Placements
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id = u.user_id
            AND (
                idt.interview_status = 'Placed'
                OR (
                    JSON_VALID(idt.interview_status)
                    AND JSON_UNQUOTE(JSON_EXTRACT(
                        idt.interview_status,
                        CONCAT('$[', JSON_LENGTH(idt.interview_status)-1, '].status')
                    )) = 'PLACED'
                )
            )
        ), 0) AS selfPlacements,
        
        -- Team Submissions
        COALESCE((
            SELECT COUNT(*)
            FROM candidate_submissions cs
            JOIN requirements_model r2 ON cs.job_id = r2.job_id
            WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            AND cs.candidate_id IN (
                SELECT candidate_id FROM candidates 
                WHERE user_id != u.user_id
            )
        ), 0) AS teamSubmissions,
        
        -- Team Interviews
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id != u.user_id
            AND idt.interview_date_time IS NOT NULL
            AND cs.job_id IN (
                SELECT job_id FROM requirements_model r2
                WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            )
        ), 0) AS teamInterviews,
        
   -- Team Placements
               COALESCE((
                   SELECT COUNT(DISTINCT idt.interview_id)
                   FROM interview_details idt
                   JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
                   JOIN candidates cd ON cs.candidate_id = cd.candidate_id
                   JOIN requirements_model r2 ON cs.job_id = r2.job_id
                   WHERE REPLACE(REPLACE(r2.assigned_by, '"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '"', ''), '"', '')
                     AND cd.user_id != u.user_id
                     AND idt.interview_date_time IS NOT NULL
                     AND (
                         -- Case 1: Direct string match
                         idt.interview_status = 'PLACED'
            
                         -- Case 2: JSON with latest status = 'PLACED'
                         OR (
                             JSON_VALID(idt.interview_status)
                             AND JSON_UNQUOTE(
                                 JSON_EXTRACT(
                                     idt.interview_status,
                                     CONCAT('$[', JSON_LENGTH(idt.interview_status) - 1, '].status')
                                 )
                             ) = 'PLACED'
                         )
                     )
               ), 0) AS teamPlacements
            
        
    FROM user_details u
    WHERE EXISTS (
        SELECT 1 FROM user_roles ur 
        JOIN roles rl ON ur.role_id = rl.id 
        WHERE ur.user_id = u.user_id AND rl.name = 'Teamlead'
    )
    GROUP BY u.user_id, u.user_name, u.email
""", nativeQuery = true)
    List<Tuple> getTeamleadCandidateStats();


    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        cs.overall_feedback AS overallFeedback,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        r.client_name AS clientName
    FROM interview_details idt
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE idt.user_id = :userId
""", nativeQuery = true)
    List<SubmittedCandidateDTO> findSubmittedCandidatesByUserId(@Param("userId") String userId);

    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        CASE 
            WHEN JSON_VALID(idt.interview_status) = 1 
            THEN JSON_UNQUOTE(JSON_EXTRACT(idt.interview_status, '$[0].status')) 
            ELSE idt.interview_status 
        END AS interviewStatus,
        idt.interview_level AS interviewLevel,
        idt.interview_date_time AS interviewDateTime,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        idt.client_name AS clientName
    FROM interview_details idt
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE idt.user_id = :userId
      AND idt.interview_date_time IS NOT NULL
      AND idt.client_name IS NOT NULL
""", nativeQuery = true)
    List<InterviewScheduledDTO> findScheduledInterviewsByUserId(@Param("userId") String userId);

    @Query(value = """
    SELECT 
        r.job_id AS jobId,
        TRIM(r.job_title) AS jobTitle,
        TRIM(r.client_name) AS clientName,
        TRIM(BOTH '\\"' FROM r.assigned_by) AS assignedBy,
        r.status AS status,
        r.no_of_positions AS noOfPositions,
        r.qualification AS qualification,
        r.job_type AS jobType,
        r.job_mode AS jobMode,
        r.requirement_added_time_stamp AS postedDate
    FROM requirements_model r
    JOIN job_recruiters jr ON r.job_id = jr.job_id
    JOIN user_details u ON jr.recruiter_id = u.user_id
    WHERE u.user_id = :userId
""", nativeQuery = true)
    List<JobDetailsDTO> findJobDetailsByUserId(@Param("userId") String userId);

    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        CASE 
            WHEN JSON_VALID(idt.interview_status) 
            THEN JSON_UNQUOTE(JSON_EXTRACT(idt.interview_status, '$[0].status')) 
            ELSE idt.interview_status 
        END AS interviewStatus,
        idt.interview_level AS interviewLevel,
        idt.interview_date_time AS interviewDateTime,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        idt.client_name AS clientName
    FROM interview_details idt
    JOIN user_details u ON idt.user_id = u.user_id
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE u.user_id = :userId
      AND (
          (JSON_VALID(idt.interview_status) 
           AND JSON_UNQUOTE(JSON_EXTRACT(idt.interview_status, '$[0].status')) = 'PLACED')
          OR UPPER(idt.interview_status) = 'PLACED'
      )
""", nativeQuery = true)
    List<PlacementDetailsDTO> findPlacementCandidatesByUserId(@Param("userId") String userId);


    @Query(value = """
            SELECT DISTINCT
                b.id AS clientId,  
                b.client_name AS clientName,  
                b.client_address AS clientAddress,  
                b.on_boarded_by AS onBoardedBy,  
                REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_name, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"', '') AS clientSpocName,  
                REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_mobile_number, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"', '') AS clientSpocMobileNumber
            FROM bdm_client b
            JOIN requirements_model r ON LOWER(b.client_name) = LOWER(r.client_name)  
            JOIN job_recruiters jr ON r.job_id = jr.job_id  
            JOIN user_details u ON jr.recruiter_id = u.user_id  
            WHERE u.user_id = :userId
            """, nativeQuery = true)
    List<ClientDetailsDTO> findClientDetailsByUserId(@Param("userId") String userId);

    @Query(value = """
                SELECT 
                    u.user_id AS employeeId,
                    u.user_name AS employeeName,                
                    r.name AS role,
                    u.email AS employeeEmail,
                    u.designation AS designation,
                    DATE_FORMAT(u.joining_date, '%Y-%m-%d') AS joiningDate, 
                    u.gender AS gender, 
                    DATE_FORMAT(u.dob, '%Y-%m-%d') AS dob, 
                    u.phone_number AS phoneNumber, 
                    u.personalemail AS personalEmail, 
                    u.status AS status
                FROM user_details u
                JOIN user_roles ur ON u.user_id = ur.user_id
                JOIN roles r ON ur.role_id = r.id
                WHERE r.name IN ('Employee', 'Teamlead')
                AND u.user_id = :userId
            """, nativeQuery = true)
    List<Tuple> getEmployeeDetailsByUserId(@Param("userId") String userId);

    @Query(value = """
            SELECT COUNT(DISTINCT cs.candidate_id)
            FROM candidate_submissions cs
            JOIN requirements_model req ON cs.job_id = req.job_id
            WHERE cs.job_id = :jobId
            """, nativeQuery = true)
    Integer getNumberOfSubmissionsByJobId(@Param("jobId") String jobId);


    @Query(value = """
            SELECT COALESCE(SUM(CASE 
                                  WHEN idt.interview_date_time IS NOT NULL 
                                  THEN 1 ELSE 0 
                               END), 0)
            FROM interview_details idt
            JOIN requirements_model req ON idt.job_id = req.job_id
            WHERE req.job_id = :jobId
            """, nativeQuery = true)
    Integer getNumberOfInterviewsByJobId(@Param("jobId") String jobId);


    // RequirementsDao.java
    @Query("SELECT r FROM RequirementsModel r WHERE DATE(r.requirementAddedTimeStamp) BETWEEN :startDate AND :endDate")
    List<RequirementsModel> findByRequirementAddedTimeStampBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = "SELECT * FROM requirements_model WHERE status <> 'Closed'", nativeQuery = true)
    List<RequirementsModel> findAllActiveRequirements();



    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        s.skills AS skills,
        s.overall_feedback AS overallFeedback,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        r.client_name AS clientName
    FROM candidates cd
    JOIN candidate_submissions s ON cd.candidate_id = s.candidate_id
    JOIN requirements_model r ON s.job_id = r.job_id
    WHERE TRIM(BOTH '\"' FROM r.assigned_by) = :username
""", nativeQuery = true)
    List<SubmittedCandidateDTO> findSubmittedCandidatesByAssignedBy(@Param("username") String username);

    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        s.skills AS skills,
        CASE 
            WHEN JSON_VALID(i.interview_status) = 1 
            THEN JSON_UNQUOTE(JSON_EXTRACT(i.interview_status, '$[0].status')) 
            ELSE i.interview_status 
        END AS interviewStatus,
        i.interview_level AS interviewLevel,
        i.interview_date_time AS interviewDateTime,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        r.client_name AS clientName
    FROM candidates cd
    JOIN candidate_submissions s ON cd.candidate_id = s.candidate_id
    JOIN interview_details i ON i.candidate_id = s.candidate_id
    JOIN requirements_model r ON s.job_id = r.job_id
    WHERE TRIM(BOTH '\"' FROM r.assigned_by) = :username
      AND i.interview_date_time IS NOT NULL
""", nativeQuery = true)
    List<InterviewScheduledDTO> findScheduledInterviewsByAssignedBy(@Param("username") String username);

    @Query(value = """
    SELECT 
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        r.client_name AS clientName,
        TRIM(BOTH '\"' FROM r.assigned_by) AS assignedBy,
        r.status AS status,
        r.no_of_positions AS noOfPositions,
        r.qualification AS qualification,
        r.job_type AS jobType,
        r.job_mode AS jobMode,
        r.requirement_added_time_stamp AS postedDate
    FROM requirements_model r
    WHERE TRIM(BOTH '\"' FROM r.assigned_by) = :username
""", nativeQuery = true)
    List<JobDetailsDTO> findJobDetailsByAssignedBy(@Param("username") String username);

    @Query(value = """
SELECT 
    cd.candidate_id AS candidateId,
    cd.full_name AS fullName,
    cd.candidate_email_id AS candidateEmailId,
    cd.contact_number AS contactNumber,
    cd.qualification AS qualification,
    s.skills AS skills,
    CASE 
        WHEN JSON_VALID(i.interview_status) 
        THEN JSON_UNQUOTE(
            JSON_EXTRACT(
                i.interview_status, 
                CONCAT(
                    '$[', 
                    JSON_LENGTH(i.interview_status) - 1, 
                    '].status'
                )
            )
        )
        ELSE i.interview_status 
    END AS interviewStatus,
    i.interview_level AS interviewLevel,
    i.interview_date_time AS interviewDateTime,
    r.job_id AS jobId,
    r.job_title AS jobTitle,
    r.client_name AS clientName
FROM candidates cd
JOIN candidate_submissions s ON cd.candidate_id = s.candidate_id
JOIN interview_details i ON i.candidate_id = s.candidate_id
JOIN requirements_model r ON s.job_id = r.job_id
WHERE TRIM(BOTH '\"' FROM r.assigned_by) = :username
  AND (
      (JSON_VALID(i.interview_status) 
       AND JSON_UNQUOTE(
           JSON_EXTRACT(
               i.interview_status, 
               CONCAT(
                   '$[', 
                   JSON_LENGTH(i.interview_status) - 1, 
                   '].status'
               )
           )
       ) = 'PLACED'
      )
      OR i.interview_status = 'PLACED'
  )
""", nativeQuery = true)
    List<PlacementDetailsDTO> findPlacementCandidatesByAssignedBy(@Param("username") String username);

    @Query(value = """
    SELECT DISTINCT
        b.id AS clientId,
        b.client_name AS clientName,
        b.client_address AS clientAddress,
        b.on_boarded_by AS onBoardedBy,
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_name, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"' , '') AS clientSpocName,
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_mobile_number, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"' , '') AS clientSpocMobileNumber
    FROM bdm_client b
    JOIN requirements_model r ON LOWER(b.client_name) = LOWER(r.client_name)
    WHERE REPLACE(REPLACE(r.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(:username, '\"', ''), '"', '')
""", nativeQuery = true)
    List<ClientDetailsDTO> findClientDetailsByAssignedBy(@Param("username") String username);


    @Query(value = """
    SELECT 
        u.user_id AS employeeId,
        u.user_name AS employeeName,                
        r.name AS role,
        u.email AS employeeEmail,
        u.designation AS designation,
        DATE_FORMAT(u.joining_date, '%Y-%m-%d') AS joiningDate, 
        u.gender AS gender, 
        DATE_FORMAT(u.dob, '%Y-%m-%d') AS dob, 
        u.phone_number AS phoneNumber, 
        u.personalemail AS personalEmail, 
        u.status AS status
    FROM user_details u
    JOIN user_roles ur ON u.user_id = ur.user_id
    JOIN roles r ON ur.role_id = r.id
    WHERE r.name = 'Teamlead'
    AND u.user_id = :userId
    """, nativeQuery = true)
    List<Tuple> getTeamleadDetailsByUserId(@Param("userId") String userId);


    @Query(value = """
    SELECT 
        u.user_id AS userId,
        u.user_name AS userName,
        r.name AS role
    FROM user_details u
    JOIN user_roles ur ON u.user_id = ur.user_id
    JOIN roles r ON ur.role_id = r.id
    WHERE u.user_id = :userId
""", nativeQuery = true)
    Tuple getUserRoleAndUsername(@Param("userId") String userId);


    @Query("SELECT r FROM RequirementsModel r " +
            "WHERE :recruiterId MEMBER OF r.recruiterIds " +
            "AND r.requirementAddedTimeStamp BETWEEN :startDate AND :endDate")
    List<RequirementsModel> findJobsByRecruiterIdAndDateRange(@Param("recruiterId") String recruiterId,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM user_details WHERE user_id = :userId", nativeQuery = true)
    int countByUserId(@Param("userId") String userId);


    // Native query to validate if the username exists in user_details_prod
    @Query(value = "SELECT user_name FROM user_details WHERE user_id = :userId", nativeQuery = true)
    String findUserNameByUserId(@Param("userId") String userId);


    @Query(value = "SELECT * FROM requirements_model " +
            "WHERE assigned_by = :assignedBy " +
            "AND requirement_added_time_stamp BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<RequirementsModel> findJobsAssignedByNameAndDateRange(
            @Param("assignedBy") String assignedBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query(value = """
    SELECT 
        cd.candidate_id, 
        id.full_name AS candidateName, 
        id.candidate_email_id AS email, 
        id.contact_number AS contactNumber, 
        cd.qualification AS qualification,
        cs.overall_feedback AS overallFeedback,
        u.user_name AS recruiterName
    FROM candidate_submissions cs
    JOIN candidates cd ON cs.candidate_id = cd.candidate_id
    JOIN user_details u ON cd.user_email = u.email
    JOIN interview_details id ON cs.candidate_id = id.candidate_id
    WHERE cs.job_id = :jobId
      AND id.is_placed = true
      AND cs.job_id = id.job_id
""", nativeQuery = true)
    List<Tuple> findPlacementsByJobId(@Param("jobId") String jobId);

    Optional<RequirementsModel> findByJobId(String jobId);


    @Query(value = """
    SELECT 
        u.user_id AS employeeId,
        u.user_name AS employeeName,
        u.email AS employeeEmail,
        'TEAMLEAD' AS role,
        COALESCE((
            SELECT COUNT(DISTINCT r2.client_name)
            FROM requirements_model r2
            WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            AND DATE(r2.requirement_added_time_stamp) BETWEEN :startDate AND :endDate
        ), 0) AS numberOfClients,
        
        COALESCE((
            SELECT COUNT(DISTINCT r2.job_id)
            FROM requirements_model r2
            WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            AND DATE(r2.requirement_added_time_stamp) BETWEEN :startDate AND :endDate
        ), 0) AS numberOfRequirements,
        
        -- Self Submissions (filter by profile_received_date)
        COALESCE((
            SELECT COUNT(*)
            FROM candidates cd
            JOIN candidate_submissions cs ON cd.candidate_id = cs.candidate_id
            WHERE cd.user_id = u.user_id
            AND DATE(cs.profile_received_date) BETWEEN :startDate AND :endDate
        ), 0) AS selfSubmissions,
        
        -- Self Interviews (filter by interview_date_time)
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id = u.user_id
            AND DATE(idt.interview_date_time) BETWEEN :startDate AND :endDate
            AND cs.job_id IN (
                SELECT job_id FROM requirements_model r2
                WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            )
        ), 0) AS selfInterviews,
        
        -- Self Placements (filter by created_at)
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id = u.user_id
            AND DATE(idt.interview_date_time) BETWEEN :startDate AND :endDate
            AND (
                idt.interview_status = 'PLACED'
                OR (
                    JSON_VALID(idt.interview_status)
                    AND JSON_UNQUOTE(JSON_EXTRACT(
                        idt.interview_status,
                        CONCAT('$[', JSON_LENGTH(idt.interview_status)-1, '].status')
                    )) = 'PLACED'
                )
            )
        ), 0) AS selfPlacements,
        
        -- Team Submissions (filter by profile_received_date)
        COALESCE((
            SELECT COUNT(*)
            FROM candidate_submissions cs
            JOIN requirements_model r2 ON cs.job_id = r2.job_id
            WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            AND DATE(cs.profile_received_date) BETWEEN :startDate AND :endDate
            AND cs.candidate_id IN (
                SELECT candidate_id FROM candidates 
                WHERE user_id != u.user_id
            )
        ), 0) AS teamSubmissions,
        
        -- Team Interviews (filter by interview_date_time)
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id != u.user_id
            AND DATE(idt.interview_date_time) BETWEEN :startDate AND :endDate
            AND cs.job_id IN (
                SELECT job_id FROM requirements_model r2
                WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            )
        ), 0) AS teamInterviews,
        
        -- Team Placements (filter by created_at)
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            JOIN requirements_model r2 ON cs.job_id = r2.job_id
            WHERE REPLACE(REPLACE(r2.assigned_by, '\"', ''), '"', '') = REPLACE(REPLACE(u.user_name, '\"', ''), '"', '')
            AND cd.user_id != u.user_id
            AND DATE(idt.timestamp) BETWEEN :startDate AND :endDate
            AND (
                idt.interview_status = 'PLACED'
                OR (
                    JSON_VALID(idt.interview_status)
                    AND JSON_UNQUOTE(
                        JSON_EXTRACT(
                            idt.interview_status,
                            CONCAT('$[', JSON_LENGTH(idt.interview_status) - 1, '].status')
                        )
                    ) = 'PLACED'
                )
            )
        ), 0) AS teamPlacements
        
    FROM user_details u
    WHERE EXISTS (
        SELECT 1 FROM user_roles ur 
        JOIN roles rl ON ur.role_id = rl.id 
        WHERE ur.user_id = u.user_id AND rl.name = 'Teamlead'
    )
    GROUP BY u.user_id, u.user_name, u.email
""", nativeQuery = true)
    List<Tuple> getTeamleadCandidateStats(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    @Query(value = """
    SELECT 
        u.user_id AS employeeId,
        u.user_name AS employeeName,
        u.email AS employeeEmail,
        r.name AS role,
        COALESCE((
            SELECT COUNT(DISTINCT cd.candidate_id) 
            FROM candidates cd
            JOIN candidate_submissions cs ON cd.candidate_id = cs.candidate_id
            WHERE cd.user_id = u.user_id 
            AND DATE(cs.profile_received_date) BETWEEN :startDate AND :endDate
        ), 0) AS numberOfSubmissions,
        
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id = u.user_id
            AND idt.interview_date_time IS NOT NULL
            AND DATE(idt.interview_date_time) BETWEEN :startDate AND :endDate
        ), 0) AS numberOfInterviews,
        
        COALESCE((
            SELECT COUNT(DISTINCT idt.interview_id)
            FROM interview_details idt
            JOIN candidate_submissions cs ON idt.candidate_id = cs.candidate_id
            JOIN candidates cd ON cs.candidate_id = cd.candidate_id
            WHERE cd.user_id = u.user_id
            AND idt.interview_date_time IS NOT NULL
            AND (
                idt.interview_status = 'PLACED'
                OR
                (
                    JSON_VALID(idt.interview_status)
                    AND JSON_UNQUOTE(
                        JSON_EXTRACT(
                            idt.interview_status,
                            CONCAT('$[', JSON_LENGTH(idt.interview_status) - 1, '].status')
                        )
                    ) = 'PLACED'
                )
            )
            AND DATE(idt.timestamp) BETWEEN :startDate AND :endDate
        ), 0) AS numberOfPlacements,
        
        COALESCE((
            SELECT COUNT(DISTINCT req.client_name) 
            FROM requirements_model req 
            JOIN job_recruiters jrp ON req.job_id = jrp.job_id
            WHERE jrp.recruiter_id = u.user_id
            AND DATE(req.requirement_added_time_stamp) BETWEEN :startDate AND :endDate
        ), 0) AS numberOfClients,
        
        COALESCE((
            SELECT COUNT(DISTINCT req.job_id) 
            FROM requirements_model req 
            JOIN job_recruiters jrp ON req.job_id = jrp.job_id
            WHERE jrp.recruiter_id = u.user_id
            AND DATE(req.requirement_added_time_stamp) BETWEEN :startDate AND :endDate
        ), 0) AS numberOfRequirements
       
    FROM user_details u
    JOIN user_roles ur ON u.user_id = ur.user_id
    JOIN roles r ON ur.role_id = r.id
    WHERE r.name = 'Employee'
    """, nativeQuery = true)
    List<Tuple> getEmployeeCandidateStats(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);


    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        cs.overall_feedback AS overallFeedback,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        r.client_name AS clientName
    FROM interview_details idt
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE idt.user_id = :userId
      AND cs.submitted_at BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<SubmittedCandidateDTO> findSubmittedCandidatesByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        CASE 
            WHEN JSON_VALID(idt.interview_status) = 1 
              AND JSON_LENGTH(idt.interview_status) > 0
            THEN JSON_UNQUOTE(
                JSON_EXTRACT(
                    idt.interview_status,
                    CONCAT(
                        '$[',
                        CAST(JSON_LENGTH(idt.interview_status) - 1 AS CHAR),
                        '].status'
                    )
                )
            )
            ELSE NULL
        END AS interviewStatus,
        CASE 
            WHEN JSON_VALID(idt.interview_status) = 1 
              AND JSON_LENGTH(idt.interview_status) > 0
            THEN JSON_UNQUOTE(
                JSON_EXTRACT(
                    idt.interview_status,
                    CONCAT(
                        '$[',
                        CAST(JSON_LENGTH(idt.interview_status) - 1 AS CHAR),
                        '].interviewLevel'
                    )
                )
            )
            ELSE NULL
        END AS interviewLevel,
        idt.interview_date_time AS interviewDateTime,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        idt.client_name AS clientName
    FROM interview_details idt
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE idt.user_id = :userId
      AND idt.interview_date_time IS NOT NULL
      AND idt.client_name IS NOT NULL
      AND idt.interview_date_time BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<InterviewScheduledDTO> findScheduledInterviewsByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    @Query(value = """
    SELECT 
        r.job_id AS jobId,
        TRIM(r.job_title) AS jobTitle,
        TRIM(r.client_name) AS clientName,
        TRIM(BOTH '\\"' FROM r.assigned_by) AS assignedBy,
        r.status AS status,
        r.no_of_positions AS noOfPositions,
        r.qualification AS qualification,
        r.job_type AS jobType,
        r.job_mode AS jobMode,
        r.requirement_added_time_stamp AS postedDate
    FROM requirements_model r
    JOIN job_recruiters jr ON r.job_id = jr.job_id
    JOIN user_details u ON jr.recruiter_id = u.user_id
    WHERE u.user_id = :userId
      AND r.requirement_added_time_stamp BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<JobDetailsDTO> findJobDetailsByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        CASE 
            WHEN JSON_VALID(idt.interview_status) 
            THEN JSON_UNQUOTE(JSON_EXTRACT(idt.interview_status, '$[0].status')) 
            ELSE idt.interview_status 
        END AS interviewStatus,
        idt.interview_level AS interviewLevel,
        idt.interview_date_time AS interviewDateTime,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        idt.client_name AS clientName
    FROM interview_details idt
    JOIN user_details u ON idt.user_id = u.user_id
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE u.user_id = :userId
      AND (
          (JSON_VALID(idt.interview_status) 
           AND JSON_UNQUOTE(JSON_EXTRACT(idt.interview_status, '$[0].status')) = 'PLACED')
          OR UPPER(idt.interview_status) = 'PLACED'
      )
      AND idt.timestamp BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<PlacementDetailsDTO> findPlacementCandidatesByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query(value = """
    SELECT DISTINCT
        b.id AS clientId,  
        b.client_name AS clientName,  
        b.client_address AS clientAddress,  
        b.on_boarded_by AS onBoardedBy,  
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_name, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"', '') AS clientSpocName,  
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_mobile_number, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"', '') AS clientSpocMobileNumber
    FROM bdm_client b
    JOIN requirements_model r ON LOWER(b.client_name) = LOWER(r.client_name)  
    JOIN job_recruiters jr ON r.job_id = jr.job_id  
    JOIN user_details u ON jr.recruiter_id = u.user_id  
    WHERE u.user_id = :userId
      AND r.requirement_added_time_stamp BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<ClientDetailsDTO> findClientDetailsByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        cs.overall_feedback AS overallFeedback,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        r.client_name AS clientName
    FROM candidate_submissions cs
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE r.assigned_by = :assignedBy
      AND cs.submitted_at BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<SubmittedCandidateDTO> findSubmittedCandidatesByAssignedByAndDateRange(
            @Param("assignedBy") String assignedBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        CASE 
            WHEN JSON_VALID(idt.interview_status) = 1 
              AND JSON_LENGTH(idt.interview_status) > 0
            THEN JSON_UNQUOTE(
                JSON_EXTRACT(
                    idt.interview_status,
                    CONCAT(
                        '$[',
                        CAST(JSON_LENGTH(idt.interview_status) - 1 AS CHAR),
                        '].status'
                    )
                )
            )
            ELSE NULL
        END AS interviewStatus,
        CASE 
            WHEN JSON_VALID(idt.interview_status) = 1 
              AND JSON_LENGTH(idt.interview_status) > 0
            THEN JSON_UNQUOTE(
                JSON_EXTRACT(
                    idt.interview_status,
                    CONCAT(
                        '$[',
                        CAST(JSON_LENGTH(idt.interview_status) - 1 AS CHAR),
                        '].interviewLevel'
                    )
                )
            )
            ELSE NULL
        END AS interviewLevel,
        idt.interview_date_time AS interviewDateTime,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        idt.client_name AS clientName
    FROM interview_details idt
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE r.assigned_by = :assignedBy
      AND idt.interview_date_time IS NOT NULL
      AND idt.interview_date_time BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<InterviewScheduledDTO> findScheduledInterviewsByAssignedByAndDateRange(
            @Param("assignedBy") String assignedBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query(value = """
    SELECT 
        r.job_id AS jobId,
        TRIM(r.job_title) AS jobTitle,
        TRIM(r.client_name) AS clientName,
        TRIM(BOTH '\\"' FROM r.assigned_by) AS assignedBy,
        r.status AS status,
        r.no_of_positions AS noOfPositions,
        r.qualification AS qualification,
        r.job_type AS jobType,
        r.job_mode AS jobMode,
        r.requirement_added_time_stamp AS postedDate
    FROM requirements_model r
    WHERE r.assigned_by = :assignedBy
      AND r.requirement_added_time_stamp BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<JobDetailsDTO> findJobDetailsByAssignedByAndDateRange(
            @Param("assignedBy") String assignedBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        cd.candidate_id AS candidateId,
        cd.full_name AS fullName,
        cd.candidate_email_id AS candidateEmailId,
        cd.contact_number AS contactNumber,
        cd.qualification AS qualification,
        cs.skills AS skills,
        CASE 
            WHEN JSON_VALID(idt.interview_status) 
            THEN JSON_UNQUOTE(JSON_EXTRACT(idt.interview_status, '$[0].status')) 
            ELSE idt.interview_status 
        END AS interviewStatus,
        idt.interview_level AS interviewLevel,
        idt.interview_date_time AS interviewDateTime,
        r.job_id AS jobId,
        r.job_title AS jobTitle,
        idt.client_name AS clientName
    FROM interview_details idt
    JOIN candidate_submissions cs ON cs.candidate_id = idt.candidate_id
    JOIN candidates cd ON cd.candidate_id = cs.candidate_id
    JOIN requirements_model r ON cs.job_id = r.job_id
    WHERE r.assigned_by = :assignedBy
      AND (
          (JSON_VALID(idt.interview_status) 
           AND JSON_UNQUOTE(JSON_EXTRACT(idt.interview_status, '$[0].status')) = 'PLACED')
          OR UPPER(idt.interview_status) = 'PLACED'
      )
      AND idt.timestamp BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<PlacementDetailsDTO> findPlacementCandidatesByAssignedByAndDateRange(
            @Param("assignedBy") String assignedBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT DISTINCT
        b.id AS clientId,  
        b.client_name AS clientName,  
        b.client_address AS clientAddress,  
        b.on_boarded_by AS onBoardedBy,  
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_name, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"', '') AS clientSpocName,  
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(JSON_UNQUOTE(JSON_EXTRACT(b.client_spoc_mobile_number, '$')), '[\"', ''), '\"]', ''), '\\\\"', ''), '\\\\', ''), '"', '') AS clientSpocMobileNumber
    FROM bdm_client b
    JOIN requirements_model r ON LOWER(b.client_name) = LOWER(r.client_name)
    WHERE r.assigned_by = :assignedBy
      AND r.requirement_added_time_stamp BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    List<ClientDetailsDTO> findClientDetailsByAssignedByAndDateRange(
            @Param("assignedBy") String assignedBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
