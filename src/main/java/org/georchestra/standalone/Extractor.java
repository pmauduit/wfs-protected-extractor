package org.georchestra.standalone;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Envelope;


public class Extractor {

	public static final int MAX_LINE_LENGTH = 80;
	
	public static void main(String argv[]) throws IOException {
	    System.out.println("\nstandalone WFS extractor started.\n");
	    
	    String getCapabilities = "http://sdi.georchestra.org/geoserver/wfs?REQUEST=GetCapabilities&VERSION=1.0.0";

		Map connectionParameters = new HashMap();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilities);

		// Step 2 - connection
		DataStore data = DataStoreFinder.getDataStore(connectionParameters);

		// Step 3 - discouvery
		String typeNames[] = data.getTypeNames();
		String typeName = argv.length > 1 ?argv[1] : typeNames[new Random().nextInt(typeNames.length)] ;

		// Step 4 - target
		FeatureSource<SimpleFeatureType, SimpleFeature> source = data.getFeatureSource(typeName);
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures();

		FeatureIterator<SimpleFeature> iterator = features.features();
		try {
			while (iterator.hasNext()) {
				Feature feature = (Feature) iterator.next();
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
				System.out.println(String.format("\t%s: %s...", p.getName(), p.getValue().toString().substring(0, MAX_LINE_LENGTH - 3)));
			} else {
				System.out.println(String.format("\t%s: %s", p.getName(), p.getValue()));
			}
		}
		System.out.println("\n");
	}
}
