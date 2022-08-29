package org.dice_research.serial.maps;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * This class handles the deserialization of complex Map classes. Note that it will use
 * {@link HashMap} to instantiate the serialized map.
 * 
 * This main deserialization method implements a finite automaton. States are as follows:
 * <ul>
 * <li>0 = end of map reached</li>
 * <li>1 = within the JSON object representing the map</li>
 * <li>2 = found main key class field</li>
 * <li>3 = found main value class field</li>
 * <li>4 = found array of map elements</li>
 * <li>5 = found undefined field. It will be ignored</><li>6 = array of map elements started</><li>6
 * = found start of the array of map elements</li>
 * </ul>
 * 
 * The deserialization method for single map elements implements a finite automaton. States are as
 * follows:
 * <ul>
 * <li>1 = within the JSON object representing the element</li>
 * <li>2 = found main key class field</li>
 * <li>3 = found main value class field</li>
 * <li>4 = found main key field</li>
 * <li>5 = found main value field</li>
 * <li>6 = found undefined field. It will be ignored</><li>6 = array of map elements started</>
 * </ul>
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public abstract class AbstractComplexMapDeserializer<T extends Map<Object, Object>>
    extends StdDeserializer<T> {

  private static final long serialVersionUID = 1L;

  private Supplier<T> mapFactory;

  public AbstractComplexMapDeserializer(Supplier<T> mapFactory) {
    this(mapFactory, null);
  }

  public AbstractComplexMapDeserializer(Supplier<T> mapFactory, Class<?> t) {
    super(t);
    this.mapFactory = mapFactory;
  }

  @Override
  public T deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    T resultMap = mapFactory.get();
    int state = 1;
    Class<?> mainKeyClass = null;
    Class<?> mainValueClass = null;
    while (state > 0) {
      JsonToken token = parser.nextToken();
      switch (token) {
        case END_ARRAY:
          if (state == 6) {
            state = 1;
          } else {
            throw new IOException("Saw an unexpected end of a JSON array (state=" + state + ").");
          }
          break;
        case END_OBJECT:
          if (state == 1) {
            state = 0;
          } else {
            throw new IOException("Saw an unexpected end of a JSON object (state=" + state + ").");
          }
          break;
        case FIELD_NAME:
          if (state == 1) {
            switch (parser.getCurrentName()) {
              case ComplexMapSerializer.KEY_TYPE_FIELD:
                state = 2;
                break;
              case ComplexMapSerializer.VALUE_TYPE_FIELD:
                state = 3;
                break;
              case ComplexMapSerializer.ARRAY_FIELD:
                state = 4;
                break;
              default:
                state = 5;
                break;
            }
          } else {
            throw new IOException("Found a field with the name " + parser.getCurrentName()
                + " in an unexpected position (state=" + state + ").");
          }
          break;
        case START_ARRAY:
          if (state == 4) {
            state = 6;
          } else {
            throw new IOException("Saw an unexpected start of a JSON array (state=" + state + ").");
          }
          break;
        case START_OBJECT:
          if (state == 6) {
            parseElement(parser, mainKeyClass, mainValueClass, resultMap);
          } else {
            throw new IOException(
                "Saw an unexpected start of a JSON object (state=" + state + ").");
          }
          break;
        case VALUE_STRING:
          if (state == 2) {
            mainKeyClass = loadClass(parser.getText());
          } else if (state == 3) {
            mainValueClass = loadClass(parser.getText());
            // } else {
            // Unexpected value will be ignored
            // throw new IOException("Saw an unexpected String value (state=" + state + ").");
          }
          state = 1;
          break;
        default:
          // NOT_AVAILABLE, VALUE_NUMBER_FLOAT, VALUE_NUMBER_INT, VALUE_FALSE, VALUE_TRUE,
          // VALUE_EMBEDDED_OBJECT, VALUE_NULL
          throw new IOException("Saw an unexpected JSON token: " + parser.currentToken()
              + " (state = " + state + ").");
      }
    }
    return resultMap;
  }

  protected void parseElement(JsonParser parser, Class<?> mainKeyClass, Class<?> mainValueClass,
      Map<Object, Object> resultMap) throws IOException {
    Class<?> localKeyClass = mainKeyClass;
    Class<?> localValueClass = mainValueClass;
    Object key = null;
    Object value = null;
    int state = 1;
    while (true) {
      JsonToken token = parser.nextToken();
      switch (token) {
        case END_OBJECT:
          if (state == 1) {
            resultMap.put(key, value);
            return;
          } else {
            throw new IOException("Saw an unexpected end of a JSON object (state=" + state + ").");
          }
        case FIELD_NAME:
          if (state == 1) {
            switch (parser.getCurrentName()) {
              case ComplexMapSerializer.KEY_TYPE_FIELD:
                state = 2;
                break;
              case ComplexMapSerializer.VALUE_TYPE_FIELD:
                state = 3;
                break;
              case ComplexMapSerializer.KEY_FIELD:
                state = 4;
                break;
              case ComplexMapSerializer.VALUE_FIELD:
                state = 5;
                break;
              default:
                state = 6;
                break;
            }
          } else {
            throw new IOException("Found a field with the name " + parser.getCurrentName()
                + " in an unexpected position (state=" + state + ").");
          }
          break;
        case START_OBJECT:
          if (state == 4) {
            key = parser.readValueAs(localKeyClass);
          } else if (state == 5) {
            value = parser.readValueAs(localValueClass);
          } else {
            throw new IOException(
                "Saw an unexpected start of a JSON object (state=" + state + ").");
          }
          state = 1;
          break;
        case VALUE_STRING:
          switch (state) {
            case 2:
              localKeyClass = loadClass(parser.getText());
              break;
            case 3:
              localValueClass = loadClass(parser.getText());
              break;
            case 4:
              key = parser.readValueAs(localKeyClass);
              break;
            case 5:
              value = parser.readValueAs(localValueClass);
              break;
            default:
              /* Unexpected value will be ignored */ break;
          }
          state = 1;
          break;
        default:
          // NOT_AVAILABLE, VALUE_NUMBER_FLOAT, VALUE_NUMBER_INT, VALUE_FALSE, VALUE_TRUE,
          // VALUE_EMBEDDED_OBJECT, VALUE_NULL, START_ARRAY, END_ARRAY
          throw new IOException("Saw an unexpected JSON token: " + parser.currentToken()
              + " (state = " + state + ").");
      }
    }
  }

  protected Class<?> loadClass(String className) throws IOException {
    try {
      return this.getClass().getClassLoader().loadClass(className);
    } catch (Exception e) {
      throw new IOException(
          "Couldn't find a class with the given class name (\"" + className + "\")", e);
    }
  }

}
