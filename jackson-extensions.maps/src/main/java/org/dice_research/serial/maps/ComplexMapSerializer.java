package org.dice_research.serial.maps;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings({"rawtypes", "serial"})
public class ComplexMapSerializer extends StdSerializer<Map> {

  public static final String KEY_FIELD = "k";
  public static final String VALUE_FIELD = "v";
  public static final String KEY_TYPE_FIELD = "a";
  public static final String VALUE_TYPE_FIELD = "b";
  public static final String ARRAY_FIELD = "c";

  public ComplexMapSerializer() {
    this(null);
  }

  public ComplexMapSerializer(Class<Map> t) {
    super(t);
  }

  @Override
  public void serialize(Map map, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeStartObject();
    if (map.isEmpty()) {
      // nothing to do...
      gen.writeEndObject();
      return;
    }
    // Determine main types
    Class<?> mainKeyClass = determineKeyClass(map);
    Class<?> mainValueClass = determineValueClass(map);
    // Write the main classes into the header of our object
    writeType(mainKeyClass, true, gen);
    writeType(mainValueClass, false, gen);
    // Write the single elements
    gen.writeFieldName(ARRAY_FIELD);
    gen.writeStartArray();
    for (Object key : map.keySet()) {
      writeElement(key, mainKeyClass, map.get(key), mainValueClass, gen);
    }
    gen.writeEndArray();
    gen.writeEndObject();
  }

  protected Class<?> determineKeyClass(Map map) {
    return determineMainClass(map.keySet().stream());
  }

  protected Class<?> determineValueClass(Map map) {
    return determineMainClass(map.values().stream());
  }

  protected Class<?> determineMainClass(Stream<?> stream) {
    Map<Class<?>, Integer> histogram = new HashMap<>();
    stream.forEach(obj -> histogram.compute(obj.getClass(), (k, v) -> v == null ? 1 : v + 1));
    // Search for maximum
    int maxValue = 0;
    Class<?> clazz = null;
    for (Entry<Class<?>, Integer> entry : histogram.entrySet()) {
      if (entry.getValue() > maxValue) {
        maxValue = entry.getValue();
        clazz = entry.getKey();
      }
    }
    return clazz;
  }

  protected void writeType(Class<?> clazz, boolean isKeyType, JsonGenerator gen)
      throws IOException {
    gen.writeFieldName(isKeyType ? KEY_TYPE_FIELD : VALUE_TYPE_FIELD);
    gen.writeString(clazz.getCanonicalName());
    //gen.writeStringField(isKeyType ? KEY_TYPE_FIELD : VALUE_TYPE_FIELD, clazz.getCanonicalName());
  }

  protected void writeElement(Object key, Class mainKeyClass, Object value, Class mainValueClass,
      JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    // Write key class if it is not the same as the main class
    if ((key != null) && (!mainKeyClass.equals(key.getClass()))) {
      writeType(key.getClass(), true, gen);
    }
    // Write value class if it is not the same as the main class
    if ((value != null) && (!mainValueClass.equals(value.getClass()))) {
      writeType(value.getClass(), true, gen);
    }
    // Write key object
    gen.writeObjectField(KEY_FIELD, key);
    // Write value object
    gen.writeObjectField(VALUE_FIELD, value);
    gen.writeEndObject();
  }

}
