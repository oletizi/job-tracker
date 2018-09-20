package com.orionletizi.job;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class JobManager {

  private static final AtomicLong SEQUENCE = new AtomicLong();
  private static final long CREATE_TIME = System.currentTimeMillis();

  private final Map<String, Job> jobsById = new HashMap<>();

  public Job newJob(final String description) {
    final Job job = new Job(nextJobId(), description);
    synchronized(jobsById) {
      jobsById.put(job.getId(), job);
    }
    return job;
  }

  private String nextJobId() {
    return CREATE_TIME + "-" + SEQUENCE.incrementAndGet();
  }

  public Job getJobById(String id) {
    synchronized(jobsById) {
      return jobsById.get(id);
    }
  }
}
