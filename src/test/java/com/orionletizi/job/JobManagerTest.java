package com.orionletizi.job;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JobManagerTest {

  @Test
  public void testBasics() {
    final JobManager manager = new JobManager();
    final Job job = manager.newJob("my description");
    assertNotNull(job);

    final Job jobById = manager.getJobById(job.getId());
    assertEquals(job, jobById);

  }

}