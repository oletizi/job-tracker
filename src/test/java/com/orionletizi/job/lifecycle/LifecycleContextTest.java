package com.orionletizi.job.lifecycle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class LifecycleContextTest {

  @Test
  public void testJSON() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    final LifecycleContext ctxt = new LifecycleContext();
    mapper.writeValueAsString(ctxt);

    ctxt.error(new RuntimeException("I am the proximate cause.", new RuntimeException("I'm the root cause")));

    final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ctxt);
    System.out.println(json);
  }

}