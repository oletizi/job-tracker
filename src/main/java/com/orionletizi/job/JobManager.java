package com.orionletizi.job;

import com.orionletizi.job.exec.Task;
import com.orionletizi.job.exec.ExecutionContext;
import com.orionletizi.job.exec.ExecutionEngine;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"WeakerAccess", "unused"})
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
    synchronized (jobsById) {
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

  public Job getJobById(final String id) {
    synchronized (jobsById) {
      return jobsById.get(id);
    }
  }

  public void execute(final String jobId, String[] command) {
    executionEngine.execute(prepareNewExecutionContext(jobId, new ExecutionContext(nextId(), command)));
  }

  public void execute(final String jobId, final List<String[]> commands) {
    for (String[] command : commands) {
      execute(jobId, command);
    }
  }

  public void run(final String jobId, final Task task) {
    executionEngine.run(prepareNewExecutionContext(jobId, new ExecutionContext(nextId(), task)));
  }

  public void run(final String jobId, final List<Task> tasks) {
    for (Task task : tasks) {
      run(jobId, task);
    }
  }

  private ExecutionContext prepareNewExecutionContext(final String jobId, final ExecutionContext ctxt) {
    final Job job = getJobById(jobId);
    if (job == null) {
      throw new RuntimeException("No such job: " + jobId);
    }
    job.addExecutionContext(ctxt);
    return ctxt;
  }
}
