package com.sun.xml.internal.ws.config.metro.dev;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public interface FeatureReader<T extends WebServiceFeature> {
   QName ENABLED_ATTRIBUTE_NAME = new QName("enabled");

   T parse(XMLEventReader var1) throws WebServiceException;
}
