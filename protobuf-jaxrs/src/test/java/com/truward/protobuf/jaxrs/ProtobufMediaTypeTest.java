package com.truward.protobuf.jaxrs;

import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public class ProtobufMediaTypeTest {

  @Test
  public void shouldMimeTypeMatchMediaType() {
    assertEquals(MediaType.valueOf(ProtobufMediaType.MIME), ProtobufMediaType.MEDIA_TYPE);
    assertTrue(ProtobufMediaType.MEDIA_TYPE.isCompatible(MediaType.valueOf(ProtobufMediaType.MIME)));
  }
}
