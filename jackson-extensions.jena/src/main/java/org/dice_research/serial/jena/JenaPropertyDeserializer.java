package org.dice_research.serial.jena;

import java.io.IOException;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * A simple deserializer which reads the given JSON token as text and uses it as
 * IRI to create a {@link Property} object using the static
 * {@link ResourceFactory}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class JenaPropertyDeserializer extends StdDeserializer<Property> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JenaPropertyDeserializer() {
        this(Property.class);
    }

    /**
     * Constructor taking additional type that this deserializer can process.
     * 
     * @param t Type of values this deserializer handles: sometimes exact types,
     *          other time most specific supertype of types deserializer handles
     *          (which may be as generic as {@link Object} in some case)
     */
    public JenaPropertyDeserializer(Class<?> t) {
        super(t);
    }

    @Override
    public Property deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = parser.getText();
        return ResourceFactory.createProperty(value);
    }
}