package com.truward.protobuf.jaxrs;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.protobuf.Message;
import com.truward.protobuf.jackson.ProtobufJacksonUtil;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Very basic support for marshalling protobuf messages in JSON format.
 *
 * @author Alexander Shabanov
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProtobufJacksonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
  private final JsonFactory jsonFactory;

  public ProtobufJacksonProvider(@Nonnull JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }

  public ProtobufJacksonProvider() {
    this(new JsonFactory());
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return Message.class.isAssignableFrom(type) && isSupportedMediaType(mediaType);
  }

  @Override
  public Object readFrom(Class<Object> type,
                         Type genericType,
                         Annotation[] annotations,
                         MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders,
                         InputStream entityStream) throws IOException, WebApplicationException {
    // TODO: support non-UTF encodings
    final Class<? extends Message> messageClass = type.asSubclass(Message.class);
    try (final JsonParser jp = jsonFactory.createParser(entityStream)) {
      return ProtobufJacksonUtil.readJson(messageClass, jp);
    }
  }

  @Override
  public boolean isWriteable(Class<?> type,
                             Type genericType,
                             Annotation[] annotations,
                             MediaType mediaType) {
    return Message.class.isAssignableFrom(type) && isSupportedMediaType(mediaType);
  }

  @Override
  public long getSize(Object o,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Object o,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream) throws IOException, WebApplicationException {
    // TODO: support non-UTF encodings
    try (final JsonGenerator jg = jsonFactory.createGenerator(entityStream)) {
      if (o == null) {
        jg.writeNull();
        return;
      }

      ProtobufJacksonUtil.writeJson((Message) o, jg);
    }
  }

  //
  // Protected
  //

  protected boolean isSupportedMediaType(MediaType mediaType) {
    return mediaType == null || MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
  }
}
