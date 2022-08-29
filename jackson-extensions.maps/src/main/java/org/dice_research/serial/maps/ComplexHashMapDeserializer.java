package org.dice_research.serial.maps;

import java.util.HashMap;

/**
 * Implementation of the {@link AbstractComplexMapDeserializer} which creates {@link HashMap}
 * instances.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class ComplexHashMapDeserializer extends AbstractComplexMapDeserializer<HashMap<Object,Object>> {

  private static final long serialVersionUID = 1L;

  public ComplexHashMapDeserializer() {
    this(null);
  }

  public ComplexHashMapDeserializer(Class<?> t) {
    super(HashMap::new, t);
  }

}
