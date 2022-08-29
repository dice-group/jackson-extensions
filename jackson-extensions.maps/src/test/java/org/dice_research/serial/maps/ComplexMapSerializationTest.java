package org.dice_research.serial.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@RunWith(Parameterized.class)
public class ComplexMapSerializationTest {

    private Map<ComplexObject, ComplexObject> map;

    public ComplexMapSerializationTest(Map<ComplexObject, ComplexObject> map) {
        super();
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws JsonMappingException, JsonProcessingException {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Map.class, new ComplexMapSerializer());
        module.addDeserializer(Map.class, new ComplexHashMapDeserializer());
        module.addSerializer(HashMap.class, new ComplexMapSerializer());
        module.addDeserializer(HashMap.class, new ComplexHashMapDeserializer());
        ObjectMapper mapper = new ObjectMapper().registerModule(module);

        String json = mapper.writeValueAsString(map);

        Map<ComplexObject, ComplexObject> readMap = null;
        try {
        readMap = mapper.readValue(json, Map.class);
        } catch(Exception e) {
            System.err.print("Exception while reading map from JSON. JSON String: ");
            System.err.println(json);
            throw e;
        }

        for (ComplexObject key : map.keySet()) {
            Assert.assertTrue("Maps is missing key " + key.toString() + ". JSON String: " + json,
                    readMap.containsKey(key));
            Assert.assertEquals("Maps have different values for key " + key.toString() + ". JSON String: " + json,
                    map.get(key), readMap.get(key));
        }
        Assert.assertEquals("Maps differ after serialization. JSON String: " + json, map.size(), readMap.size());
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();

        // Create some objects that we can use for our test cases
        ComplexObject key1 = new ComplexObject("key1", "1key");
        ComplexObject key2 = new ComplexObject("key2", "2key");
        ComplexObject key3 = new ComplexObject("key3", "3key");
        ComplexObject keyNull = new ComplexObject(null, "1key");
        ComplexObject value1 = new ComplexObject("value1", "1value");
        ComplexObject value2 = new ComplexObject("value2", "2value");
        ComplexObject value3 = new ComplexObject("value3", "3value");
        ComplexObject valueNull = new ComplexObject("value3", null);
        ExtendedObject extObj1 = new ExtendedObject("ext1", "object1", 1);
        ExtendedObject extObj2 = new ExtendedObject("ext2", "object2", 2);

        Map<ComplexObject, ComplexObject> map;
        // empty map
        map = new HashMap<>();
        testConfigs.add(new Object[] { map });
        // single k->v pair
        map = new HashMap<>();
        map.put(key1, value1);
        testConfigs.add(new Object[] { map });
        // 3 k->v pairs
        map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        testConfigs.add(new Object[] { map });
        // 3 k->v pairs, majority complex object
        map = new HashMap<>();
        map.put(key1, value1);
        map.put(extObj1, extObj2);
        map.put(key3, value3);
        testConfigs.add(new Object[] { map });
        // 3 k->v pairs, majority of keys is extended
        map = new HashMap<>();
        map.put(extObj1, value1);
        map.put(key2, value2);
        map.put(extObj2, value3);
        testConfigs.add(new Object[] { map });
        // 3 k->v pairs, majority of keys is extended
        map = new HashMap<>();
        map.put(key1, extObj1);
        map.put(key2, value2);
        map.put(key3, extObj2);
        testConfigs.add(new Object[] { map });

        // 3 k->v pairs, different map type
        map = new TreeMap<>(new ComplexObjectComparator());
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        testConfigs.add(new Object[] { map });

        // 4 k->v pairs, null values
        map = new HashMap<>();
        map.put(keyNull, value1);
        map.put(key2, valueNull);
        map.put(key3, null);
        map.put(null, value3);

        return testConfigs;
    }

    public static class ComplexObject {
        private String attribute1;
        private String attribute2;

        public ComplexObject() {
            super();
        }

        public ComplexObject(String attribute1, String attribute2) {
            super();
            this.attribute1 = attribute1;
            this.attribute2 = attribute2;
        }

        /**
         * @return the attribute1
         */
        public String getAttribute1() {
            return attribute1;
        }

        /**
         * @param attribute1 the attribute1 to set
         */
        public void setAttribute1(String attribute1) {
            this.attribute1 = attribute1;
        }

        /**
         * @return the attribute2
         */
        public String getAttribute2() {
            return attribute2;
        }

        /**
         * @param attribute2 the attribute2 to set
         */
        public void setAttribute2(String attribute2) {
            this.attribute2 = attribute2;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((attribute1 == null) ? 0 : attribute1.hashCode());
            result = prime * result + ((attribute2 == null) ? 0 : attribute2.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ComplexObject other = (ComplexObject) obj;
            if (attribute1 == null) {
                if (other.attribute1 != null)
                    return false;
            } else if (!attribute1.equals(other.attribute1))
                return false;
            if (attribute2 == null) {
                if (other.attribute2 != null)
                    return false;
            } else if (!attribute2.equals(other.attribute2))
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ComplexObject [attribute1=");
            builder.append(attribute1);
            builder.append(", attribute2=");
            builder.append(attribute2);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class ExtendedObject extends ComplexObject {
        private double attribute3;

        public ExtendedObject() {
            super();
        }

        public ExtendedObject(String attribute1, String attribute2, double attribute3) {
            super(attribute1, attribute2);
            this.attribute3 = attribute3;
        }

        /**
         * @return the attribute3
         */
        public double getAttribute3() {
            return attribute3;
        }

        /**
         * @param attribute3 the attribute3 to set
         */
        public void setAttribute3(double attribute3) {
            this.attribute3 = attribute3;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            long temp;
            temp = Double.doubleToLongBits(attribute3);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            ExtendedObject other = (ExtendedObject) obj;
            if (Double.doubleToLongBits(attribute3) != Double.doubleToLongBits(other.attribute3))
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ExtendedObject [attribute3=");
            builder.append(attribute3);
            builder.append(", getAttribute1()=");
            builder.append(getAttribute1());
            builder.append(", getAttribute2()=");
            builder.append(getAttribute2());
            builder.append("]");
            return builder.toString();
        }
    }

    public static class ComplexObjectComparator implements Comparator<ComplexObject> {
        @Override
        public int compare(ComplexObject o1, ComplexObject o2) {
            int diff = o1.getAttribute1().compareTo(o2.getAttribute1());
            if (diff == 0) {
                o1.getAttribute2().compareTo(o2.getAttribute2());
            }
            return diff;
        }
    }
}
