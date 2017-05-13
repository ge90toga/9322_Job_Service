package com.seek.api.controllers;

import com.seek.api.config.FoundITConfig;
import com.seek.api.dto.PollDTO;
import com.seek.api.dto.ReviewDTO;
import com.seek.api.model.*;
import com.seek.api.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final JobService jobService;

    @Autowired
    private FoundITConfig foundITConfig;

    @Autowired
    public AdminController(JobService jobService) {
        this.jobService = jobService;
    }

//    @ApiOperation(value = "List of Reviewers", response = ResponseEntity.class)
//    @RequestMapping(value = "/reviewers", method = RequestMethod.GET)
//    public ResponseEntity<?> getAllReviewers() {
//        List<User> reviewers =  userService.findAllReviewer();
//        return new ResponseEntity<>(reviewers, HttpStatus.OK);
//    }

    @RequestMapping(value = "/review/combo", method = RequestMethod.GET)
    public ResponseEntity<?> getAllReviewCombo(@RequestParam(value = "username", required = true) String username) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ReviewDTO> reviewDTOS = jobService.findReviewComboByUserID(username);   // TODO
        return new ResponseEntity<>(reviewDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/review/{id}", method = RequestMethod.GET)
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = jobService.findReviewByID(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public ResponseEntity<Review> addNewReview(@RequestBody Review review) {
        jobService.addReview(review);

        // update application status & job status.
        Job job = jobService.findJobByID(Long.parseLong(review.getJobID()));
        if (job != null && job.getStatus() == JobStatus.OPEN) {
            job.setStatus(JobStatus.REVIEW_PROCESSING);
            jobService.updateJob(job);
        }

        Application application = jobService.findApplicationByID(Long.parseLong(review.getApplicationID()));
        if (application != null) {
            // review approved.
            if (review.isResult()) {

                if (application.getStatus() == ApplicationStatus.WAITING) {
                    application.setStatus(ApplicationStatus.REVIEWING);
                } else if (application.getStatus() == ApplicationStatus.REVIEWING) {
                    application.setStatus(ApplicationStatus.APPROVED);
                }
            // review declined.
            } else {
                application.setStatus(ApplicationStatus.DECLINED);
            }
            jobService.updateApplication(application);
        }
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/review", method = RequestMethod.PUT)
    public ResponseEntity<Review> updateReview(@RequestBody Review review) {
        jobService.updateReview(review);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @RequestMapping(value = "/poll", method = RequestMethod.POST)
    public ResponseEntity<?> organizePoll(@RequestBody PollDTO pollDTO) {
        // step 1: update job status & app status.
        Job job = jobService.findJobByID(Long.parseLong(pollDTO.getJobID()));
        if (job != null) {
            job.setStatus(JobStatus.REVIEW_COMPLETED);
            jobService.updateJob(job);
        }

        for (String appID : pollDTO.getApplicationIDs()) {
            Application application = jobService.findApplicationByID(Long.parseLong(appID));
            if (application != null) {
                application.setStatus(ApplicationStatus.INVITATION_SEND);
                jobService.updateApplication(application);
            }

        }

        // step 2: send poll & retrieve poll link.
        String pollURL = foundITConfig.getPollServiceURL();
        System.out.println("req1:" + pollURL);

//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
//        headers.add("Content-Type", "application/json");

        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<PollDTO> httpEntity = new HttpEntity(pollDTO, headers);
        String result = restTemplate.postForObject(pollURL, pollDTO, String.class);
        System.out.println("poll url" + result + " | req:" + pollURL);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
