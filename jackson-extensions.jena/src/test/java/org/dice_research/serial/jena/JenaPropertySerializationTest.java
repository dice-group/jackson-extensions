package org.dice_research.serial.jena;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JenaPropertySerializationTest {

    @Test
    public void test() throws JsonProcessingException {
        Property property = ResourceFactory.createProperty("http://example.org/property/someProperty");
        
        SimpleModule module = new SimpleModule();
        module.addSerializer(Property.class, new JenaPropertySerializer());
        module.addDeserializer(Property.class, new JenaPropertyDeserializer());
        ObjectMapper mapper = new ObjectMapper().registerModule(module);
        
        String json = mapper.writeValueAsString(property);
        
        Property property2 = mapper.readValue(json, Property.class);
        
        Assert.assertEquals(property, property2);
    }
}
