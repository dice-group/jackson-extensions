package org.dice_research.serial.jena;

import java.io.IOException;
import org.apache.jena.rdf.model.Property;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JenaPropertySerializer extends StdSerializer<Property> {

  private static final long serialVersionUID = 1L;

  public JenaPropertySerializer() {
    this(null);
  }

  public JenaPropertySerializer(Class<Property> t) {
    super(t);
  }

  @Override
  public void serialize(Property value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeString(value.getURI());
  }
}
