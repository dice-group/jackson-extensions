package org.dice_research.serial.maps;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * This class can serialize instances of the {@link Map} interface even if its
 * elements have complex objects as key. A serialized map object with
 * <code>[key1 -> value1, key2 -> value2]</code> may look like the following:
 * 
 * <pre>
 * { "a"="main key class",
 *   "b"="main value class",
 *   "c"=[{
 *     "k"={ key1 object },
 *     "v"={ value1 object }
 *   },{
 *     "k"={ key2 object },
 *     "v"={ value2 object }
 *   }]
 * }
 * </pre>
 * 
 * Note that the main key and value classes are determined based on the given
 * map. In case different class instances are found within the map, the class
 * that has the most instances is used as main class. All elements of the map
 * with a differing class are stored with additional information as follows:
 * 
 * <pre>
 * {
 *   "a"="class of the key (only if different from the main class)",
 *   "b"="class of the value (only if different from the main class)",
 *   "k"={ key1 object },
 *   "v"={ value1 object }
 * }
 * </pre>
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ComplexMapSerializer extends StdSerializer<Map> {

    public static final String KEY_FIELD = "k";
    public static final String VALUE_FIELD = "v";
    public static final String KEY_TYPE_FIELD = "a";
    public static final String VALUE_TYPE_FIELD = "b";
    public static final String ARRAY_FIELD = "c";

    /**
     * Constructor.
     */
    public ComplexMapSerializer() {
        this(null);
    }

    /**
     * Constructor taking additional type that this serializer can process.
     * 
     * @param t Nominal type supported, usually declared type of property for which
     *          serializer is used.
     * 
     */
    public ComplexMapSerializer(Class<Map> t) {
        super(t);
    }

    @Override
    public void serialize(Map map, JsonGenerator gen, SerializerProvider provider) throws IOException {
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

    /**
     * Method that determines the main key class.
     * 
     * @param map the map that should be serialized
     * @return the {@link Class} instance that represents the highest number of keys
     *         in the map
     */
    protected Class<?> determineKeyClass(Map map) {
        return determineMainClass(map.keySet().stream());
    }

    /**
     * Method that determines the main value class.
     * 
     * @param map the map that should be serialized
     * @return the {@link Class} instance that represents the highest number of
     *         values in the map
     */
    protected Class<?> determineValueClass(Map map) {
        return determineMainClass(map.values().stream());
    }

    /**
     * Method that determines the main class of a stream of objects.
     * 
     * @param stream a stream of objects for which the main class should be
     *               determined
     * @return the {@link Class} instance that represents the highest number of
     *         objects in the stream
     */
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

    /**
     * A simple method that writes the given class to the JSON generator.
     * 
     * @param clazz     the class that should be written
     * @param isKeyType a flag indicating whether it is a key class
     *                  (<code>true</code>) or a value class (<code>false</code>)
     * @param gen       the JSON generator instance which is used to create the JSON
     * @throws IOException if the generator throws an exception
     */
    protected void writeType(Class<?> clazz, boolean isKeyType, JsonGenerator gen) throws IOException {
        gen.writeFieldName(isKeyType ? KEY_TYPE_FIELD : VALUE_TYPE_FIELD);
        gen.writeString(clazz.getName());
    }

    /**
     * This method writes a single element from the map (i.e., a key value pair).
     * 
     * @param key            the key that should be serialized
     * @param mainKeyClass   the main class of keys in the map
     * @param value          the value that should be serialized
     * @param mainValueClass the main class of the values in the map
     * @param gen            the JSON generator instance which is used to create the
     *                       JSON
     * @throws IOException if the generator throws an exception
     */
    protected void writeElement(Object key, Class mainKeyClass, Object value, Class mainValueClass, JsonGenerator gen)
            throws IOException {
        gen.writeStartObject();
        // Write key class if it is not the same as the main class
        if ((key != null) && (!mainKeyClass.equals(key.getClass()))) {
            writeType(key.getClass(), true, gen);
        }
        // Write value class if it is not the same as the main class
        if ((value != null) && (!mainValueClass.equals(value.getClass()))) {
            writeType(value.getClass(), false, gen);
        }
        // Write key object
        gen.writeObjectField(KEY_FIELD, key);
        // Write value object
        gen.writeObjectField(VALUE_FIELD, value);
        gen.writeEndObject();
    }

}
