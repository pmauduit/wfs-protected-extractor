package org.georchestra.standalone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class Extractor {

    public static final String WFS_CAPABILITIES = "http://sdi.georchestra.org/geoserver/wfs?REQUEST=GetCapabilities&VERSION=1.0.0";
    public static final int MAX_LINE_LENGTH = 80;

    public static void main(String argv[]) throws Exception {
        System.out.println("\nstandalone WFS extractor started.\n");

        String wfsUsername = System.getProperty("extractor.username");
        String wfsPassword = System.getProperty("extractor.password");
        String typeName = System.getProperty("extractor.layer");

        Map connectionParameters = new HashMap();

        connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", WFS_CAPABILITIES);

        if ((wfsUsername != null) && (wfsPassword != null)) {
            connectionParameters.put("WFSDataStoreFactory:USERNAME", wfsUsername);
            connectionParameters.put("WFSDataStoreFactory:PASSWORD", wfsPassword);
        }

        DataStore data = DataStoreFinder.getDataStore(connectionParameters);

        String typeNames[] = data.getTypeNames();
        System.out.println("Available typenames:");
        for (String tn : typeNames) {
            System.out.println(String.format("\t%s", tn));
        }
        System.out.println("");

        if (typeName == null) {
            typeName = typeNames[new Random().nextInt(typeNames.length)];
            System.out.println(String.format("typeName is null, taking a random one: %s", typeName));
        } else {
            if (! Arrays.asList(typeNames).contains(typeName)) {
                System.out.println(String.format("typename \"%s\" not available in the GetCapabilities response.", typeName));
                System.out.println("standalone WFS Extractor ended.\n");
                return;
            }
        }

        FeatureSource<SimpleFeatureType, SimpleFeature> source = data.getFeatureSource(typeName);
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = null;

        try {
            features = source.getFeatures();
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("standalone WFS Extractor ended.\n");
            return;
        }


        FeatureIterator<SimpleFeature> iterator = features.features();
        try {
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                dumpFeature(feature);
            }
        } finally {
            iterator.close();
        }

        System.out.println("standalone WFS Extractor ended.\n");
    }

    private static void dumpFeature(Feature feature) {
        System.out.println(String.format("Feature [%s]:", feature.getName()));
        for (Property p : feature.getProperties()) {
            if (p.getValue() == null) {
                continue;
            }
            if (p.getValue().toString().length() > MAX_LINE_LENGTH) {
                System.out.println(String.format("\t%s: %s...", p.getName(),
                        p.getValue().toString().substring(0, MAX_LINE_LENGTH - 3)));
            } else {
                System.out.println(String.format("\t%s: %s", p.getName(), p.getValue()));
            }
        }
        System.out.println("\n");
    }
}
