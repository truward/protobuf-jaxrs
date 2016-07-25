package com.truward.protobuf.jaxrs;

import com.google.protobuf.BytesValue;
import com.google.protobuf.StringValue;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;

import static org.junit.Assert.*;

/**
 * @author Alexander Shabanov
 */
public class ProtobufProviderTest {
  private ProtobufProvider provider = new ProtobufProvider();

  private static final Annotation[] EMPTY_ANNOTATIONS = {};

  @Test
  public void shouldNotReadIncompatibleMessages() {
    assertFalse(provider.isReadable(String.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
    assertFalse(provider.isReadable(String.class, null, EMPTY_ANNOTATIONS, ProtobufMediaType.MEDIA_TYPE));
    assertFalse(provider.isReadable(BytesValue.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
  }

  @Test
  public void shouldNotWriteIncompatibleMessages() {
    assertFalse(provider.isWriteable(String.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
    assertFalse(provider.isWriteable(String.class, null, EMPTY_ANNOTATIONS, ProtobufMediaType.MEDIA_TYPE));
    assertFalse(provider.isWriteable(BytesValue.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
  }

  @Test
  public void shouldParseCompatibleTypes() throws IOException {
    // Given:
    final StringValue val = StringValue.newBuilder().setValue("test").build();

    // When:
    final byte[] buf;
    try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      provider.writeTo(val, val.getClass(), null, EMPTY_ANNOTATIONS, ProtobufMediaType.MEDIA_TYPE, null, os);
      buf = os.toByteArray();
    }

    // Then:
    assertTrue(provider.isReadable(BytesValue.class, null, EMPTY_ANNOTATIONS, ProtobufMediaType.MEDIA_TYPE));
    assertTrue(provider.isWriteable(BytesValue.class, null, EMPTY_ANNOTATIONS, ProtobufMediaType.MEDIA_TYPE));

    try (final ByteArrayInputStream is = new ByteArrayInputStream(buf)) {
      @SuppressWarnings("unchecked") final Class<Object> clazz = (Class) val.getClass();
      final Object actual = provider.readFrom(clazz, null, EMPTY_ANNOTATIONS, ProtobufMediaType.MEDIA_TYPE, null, is);
      assertEquals(val, actual);
    }
  }
}
