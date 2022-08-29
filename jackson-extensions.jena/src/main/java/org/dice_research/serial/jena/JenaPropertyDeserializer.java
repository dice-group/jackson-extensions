package org.dice_research.serial.jena;

import java.io.IOException;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JenaPropertyDeserializer extends StdDeserializer<Property> {

  private static final long serialVersionUID = 1L;

  public JenaPropertyDeserializer() { 
      this(null); 
  } 

  public JenaPropertyDeserializer(Class<Property> t) { 
      super(t); 
  }

  @Override
  public Property deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    String value = parser.getText();
    return ResourceFactory.createProperty(value);
  }
}