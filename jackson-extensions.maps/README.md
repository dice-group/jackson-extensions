# jackson-extensions.jena

This is a small Java project that provides some (de)serializers to handle the generic `Map<K,V>` interface and its implementations in a generic way. Most descriptions of the serilization and deserialization of this class only look at cases in which the type of keys (`K`) is a simple Java object, which can be directly transformed into a String. This extension handles cases in which the key as well as the value types can be complex objects.

For implementation details, please have a look at the Javadoc comments of the serializer and deserializer classes.
