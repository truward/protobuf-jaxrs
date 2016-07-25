package com.truward.protobuf.jaxrs;

import com.google.protobuf.BytesValue;
import com.google.protobuf.StringValue;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public class ProtobufJacksonProviderTest {
  private ProtobufJacksonProvider provider = new ProtobufJacksonProvider();

  private static final Annotation[] EMPTY_ANNOTATIONS = {};

  @Test
  public void shouldNotReadIncompatibleMessages() {
    assertFalse(provider.isReadable(String.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
    assertFalse(provider.isReadable(String.class, null, EMPTY_ANNOTATIONS, MediaType.APPLICATION_JSON_TYPE));
    assertFalse(provider.isReadable(BytesValue.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
  }

  @Test
  public void shouldNotWriteIncompatibleMessages() {
    assertFalse(provider.isWriteable(String.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
    assertFalse(provider.isWriteable(String.class, null, EMPTY_ANNOTATIONS, MediaType.APPLICATION_JSON_TYPE));
    assertFalse(provider.isWriteable(BytesValue.class, null, EMPTY_ANNOTATIONS, MediaType.TEXT_PLAIN_TYPE));
  }

  @Test
  public void shouldMarshalValue() throws IOException {
    // Given:
    final StringValue val = StringValue.newBuilder().setValue("test").build();

    // When:
    final byte[] buf;
    try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      provider.writeTo(val, val.getClass(), null, EMPTY_ANNOTATIONS, MediaType.APPLICATION_JSON_TYPE, null, os);
      buf = os.toByteArray();
    }

    // Then:
    assertTrue(provider.isReadable(StringValue.class, null, EMPTY_ANNOTATIONS, MediaType.APPLICATION_JSON_TYPE));
    assertTrue(provider.isWriteable(StringValue.class, null, EMPTY_ANNOTATIONS, MediaType.APPLICATION_JSON_TYPE));

    try (final ByteArrayInputStream is = new ByteArrayInputStream(buf)) {
      @SuppressWarnings("unchecked") final Class<Object> clazz = (Class) val.getClass();
      final Object actual = provider.readFrom(clazz, null, EMPTY_ANNOTATIONS, MediaType.APPLICATION_JSON_TYPE, null, is);
      assertEquals(val, actual);
    }
  }

  @Test
  public void shouldDeserializeUtf16Message() throws IOException {
    // Given:
    final String json = "{\"value\": \"Test\"}";
    final Charset charset = Charset.forName("UTF-16");
    final byte[] jsonBytes = json.getBytes(charset);
    final MediaType mediaType = new MediaType("application", "json", charset.name());

    // When:
    final Object actual;
    try (final ByteArrayInputStream is = new ByteArrayInputStream(jsonBytes)) {
      @SuppressWarnings("unchecked") final Class<Object> clazz = (Class) StringValue.class;
      actual = provider.readFrom(clazz, null, EMPTY_ANNOTATIONS, mediaType, null, is);
    }

    // Then:
    final StringValue val = (StringValue) actual;
    assertEquals("Test", val.getValue());
  }
}
