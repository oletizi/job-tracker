package com.orionletizi.job;

import com.orionletizi.job.exec.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class JobManagerTest {

  private ExecutionEngine executionEngine;
  private JobManager manager;

  @Before
  public void before() {
    executionEngine = mock(ExecutionEngine.class);
    doAnswer((invocation) -> {
      final ExecutionContext ctxt = invocation.getArgumentAt(0, ExecutionContext.class);
      ctxt.getTask().run();
      return null;
    }).when(executionEngine).run(any(ExecutionContext.class));

    manager = new JobManager(10, executionEngine);
  }

  @Test
  public void testGetJobs() {
    final Collection<Job> expected = new LinkedList<>();
    expected.add(manager.newJob("job 1"));
    expected.add(manager.newJob("job 2"));

    assertEquals(expected, manager.getJobs());
  }

  @Test
  public void testBasics() {
    final Job job = manager.newJob("my description");
    assertNotNull(job);

    final Job jobById = manager.getJobById(job.getId());
    assertEquals(job, jobById);


    // Test system executables

    final List<String[]> commands = new ArrayList<>();
    commands.add(new String[] {
        "echo", "hello, world"
    });

    manager.execute(job.getId(), commands);

    List<ExecutionContext> executions = job.getExecutions();
    assertEquals(1, executions.size());
    verify(executionEngine, times(1)).execute(executions.get(0));

    // Test in-process runnable tasks
    final Task task = mock(Task.class);
    manager.run(job.getId(), task);

    executions = job.getExecutions();
    assertEquals(2, executions.size());
    verify(task, times(1)).run();

  }

  @Test
  public void testJobRetention() {
    manager = new JobManager(1, executionEngine);
    final Job firstJob = manager.newJob("first job");
    assertEquals(firstJob, manager.getJobById(firstJob.getId()));

    final Job secondJob = manager.newJob("second job");
    assertEquals(secondJob, manager.getJobById(secondJob.getId()));

    // the first job should have been purged, since the max retention is set to 1
    assertNull(manager.getJobById(firstJob.getId()));
  }

}