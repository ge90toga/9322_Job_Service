
package com.seek.api.commandline;

import com.seek.api.model.*;
import com.seek.api.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * initial data at application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    JobService jobService;

    @Override
    public void run(String... arg0) throws Exception {
//        testJobs();
    }

    private void testJobs() {
        Job job1 = new Job("295046974@qq.com", "Java Dev", "full-time", "Google", "hello", JobStatus.OPEN);
        jobService.addJob(job1);
//        Job job2 = new Job("Frank1", "Java Dev", "full-time", "Google", "hello", JobStatus.OPEN);
//        jobService.addJob(job2);
//        System.out.println("exist job: " + jobService.existJob(2L));
//        Job job1 = jobService.findJobByID(2L);
//        System.out.println(job1);
//        job1.setStatus(JobStatus.CLOSED);
        Reviewer reviewer1 = new Reviewer("review1@gmail.com", "review1", job1);
        Reviewer reviewer2 = new Reviewer("review2@gmail.com", "review2", job1);
        List<Reviewer> reviewerList = new ArrayList<>();
        reviewerList.add(reviewer1);
        reviewerList.add(reviewer2);
        job1.setReviewers(reviewerList);
        jobService.addJob(job1);
        System.out.println(job1);
        Application application = new Application("1", "1", "12xx@qq.com", "123123", ApplicationStatus.WAITING);
        jobService.addApplication(application);
        Review review = new Review("1", "1","1", "xiaomeimei", "hellllo", true);
        jobService.addReview(review);

//        List<Job> jobList = jobService.findAllJob();
//        System.out.println(jobList.size());
//        Job j = jobService.findJobByID(1L);
//        System.out.println(j);
    }

}