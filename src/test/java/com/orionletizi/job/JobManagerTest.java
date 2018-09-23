package com.orionletizi.job;

import com.orionletizi.job.exec.ExecutionContext;
import com.orionletizi.job.exec.ExecutionEngine;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class JobManagerTest {

  private ExecutionEngine executionEngine;
  private JobManager manager;

  @Before
  public void before() {
    executionEngine = mock(ExecutionEngine.class);
    manager = new JobManager(1, executionEngine);
  }

  @Test
  public void testBasics() {
    final Job job = manager.newJob("my description");
    assertNotNull(job);

    final Job jobById = manager.getJobById(job.getId());
    assertEquals(job, jobById);

    final List<String[]> commands = new ArrayList<>();
    commands.add(new String[] {
        "echo", "hello, world"
    });

    manager.execute(job.getId(), commands);

    final List<ExecutionContext> executions = job.getExecutionLog();
    assertEquals(1, executions.size());
    verify(executionEngine, times(1)).execute(executions.get(0));
  }

  @Test
  public void testJobRetention() {
    final Job firstJob = manager.newJob("first job");
    assertEquals(firstJob, manager.getJobById(firstJob.getId()));

    final Job secondJob = manager.newJob("second job");
    assertEquals(secondJob, manager.getJobById(secondJob.getId()));

    // the first job should have been purged, since the max retention is set to 1
    assertNull(manager.getJobById(firstJob.getId()));
  }

}