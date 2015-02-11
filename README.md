# wfs-protected-extractor
a basic project to reproduce geOrchestra bug #850 (unable to extract vector protected layers)

to be run using:
```bash
$ mvn clean install exec:java
```

## optional parameters

3 optional parameters can be passed in using java env variables:

```
extractor.username: a username to be provided for basic auth
extractor.password: a password for the underlying username
extractor.layer: a specific typename to be extracted, if not provided, a random one would be selected
```

you can tweak those directly on the command line:

```bash
$ mvn clean install exec:java -Dextractor.username=admin -Dextractor.password=password -Dextractor.layer:unkown_layer
```

