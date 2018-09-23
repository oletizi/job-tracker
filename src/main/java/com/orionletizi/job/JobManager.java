package com.orionletizi.job;

import com.orionletizi.job.exec.ExecutionContext;
import com.orionletizi.job.exec.ExecutionEngine;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class JobManager {

  private static final AtomicLong SEQUENCE = new AtomicLong();
  private static final long CREATE_TIME = System.currentTimeMillis();

  private final Map<String, Job> jobsById = new HashMap<>();
  private final ExecutionEngine executionEngine;
  private final CircularFifoQueue<Job> retentionQueue;


  public JobManager(int maxJobRetention, final ExecutionEngine executionEngine) {
    retentionQueue = new CircularFifoQueue<>(maxJobRetention);
    this.executionEngine = executionEngine;
  }

  public Job newJob(final String description) {
    final Job job = new Job(nextId(), description);
    synchronized(jobsById) {
      jobsById.put(job.getId(), job);
      if (retentionQueue.isAtFullCapacity()) {
        final Job garbage = retentionQueue.remove();
        jobsById.remove(garbage.getId());
      }
      retentionQueue.add(job);
    }
    return job;
  }

  private String nextId() {
    return CREATE_TIME + "-" + SEQUENCE.incrementAndGet();
  }

  public Job getJobById(String id) {
    synchronized(jobsById) {
      return jobsById.get(id);
    }
  }

  public void execute(String jobId, List<String[]> commands) {
    for (String[] command : commands) {
      Job job = null;
      synchronized (jobsById) {
        job = jobsById.get(jobId);
        if (job == null) {
          throw new RuntimeException("No such job: " + jobId);
        }
      }
      final ExecutionContext ctxt = new ExecutionContext(nextId(), command);
      job.addExecutionContext(ctxt);
      executionEngine.execute(ctxt);
    }

  }
}
