package com.seek.api.controllers;

import com.seek.api.dto.ApplicationDTO;
import com.seek.api.model.Application;
import com.seek.api.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by langley on 8/5/17.
 */
@RestController
@RequestMapping("/api/apply")
public class ApplyController {

    private final JobService jobService;

    @Autowired
    public ApplyController(JobService jobService) {
        this.jobService = jobService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllApplications() {
        List<Application> applications =  jobService.findAllApplication();
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Application> getApplicationById(@PathVariable Long id) {
        Application application = jobService.findApplicationByID(id);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Application> addNewApplication(@RequestBody Application application) {
        jobService.addApplication(application);
        return new ResponseEntity<>(application, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Application> updateApplication(@RequestBody Application application) {
        jobService.updateApplication(application);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @RequestMapping(value = "/combo", method = RequestMethod.GET)
    public ResponseEntity<?> getAllApplicationCombo(@RequestParam(value = "username", required = true) String username) {
        List<ApplicationDTO> applicationDTOS = jobService.findApplicationComboByPublisher(username);   // TODO
        return new ResponseEntity<>(applicationDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/my", method = RequestMethod.GET)
    public ResponseEntity<?> getMyApplications(@RequestParam(value = "username", required = true) String username) {
        List<Application> applications =  jobService.findApplicationByApplicant(username);   // TODO
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

}
