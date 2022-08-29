[![Codacy Badge](https://app.codacy.com/project/badge/Grade/4d8f3d054387415f8d0b891d8e20e256)](https://www.codacy.com/gh/dice-group/jackson-extensions/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dice-group/jackson-extensions&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/4d8f3d054387415f8d0b891d8e20e256)](https://www.codacy.com/gh/dice-group/jackson-extensions/dashboard?utm_source=github.com&utm_medium=referral&utm_content=dice-group/jackson-extensions&utm_campaign=Badge_Coverage)

# jackson-extensions

This project contains some helpful (de)serializer implementations that can ease the usage of the Jackson libraries in combination with some other classes.

## Modules

* **jackson-extensions.jena**: this module supports the handling of Apache Jena classes.
* **jackson-extensions.maps**: this module supports the handling of the generic `Map<K,V>` interface and its implementations.
* **jackson-extensions.test-report**: this module is only used to collect test reports and should be ignored by users.

## Usage

The project does not offer Jackson modules. Please have a look into the projects and decide which (de)serializers you need. Then, just create a simple module and add them. For example, when working with the `Map<K,V>` class, you can register our implementations when creating an `ObjectMapper` instance as follows:
```Java
SimpleModule module = new SimpleModule();
module.addSerializer(Map.class, new ComplexMapSerializer());
module.addDeserializer(Map.class, new ComplexHashMapDeserializer());
module.addSerializer(HashMap.class, new ComplexMapSerializer());
module.addDeserializer(HashMap.class, new ComplexHashMapDeserializer());
ObjectMapper mapper = new ObjectMapper().registerModule(module);
```
