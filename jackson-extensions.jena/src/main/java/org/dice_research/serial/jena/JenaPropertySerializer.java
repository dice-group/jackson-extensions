package org.dice_research.serial.jena;

import java.io.IOException;

import org.apache.jena.rdf.model.Property;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * A simple serializer which represents the given {@link Property} instance as
 * java string comprising only the property's IRI.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class JenaPropertySerializer extends StdSerializer<Property> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JenaPropertySerializer() {
        this(Property.class);
    }

    /**
     * Constructor taking additional type that this serializer can process.
     * 
     * @param t Nominal type supported, usually declared type of property for which
     *          serializer is used.
     * 
     */
    public JenaPropertySerializer(Class<Property> t) {
        super(t);
    }

    @Override
    public void serialize(Property value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getURI());
    }
}
