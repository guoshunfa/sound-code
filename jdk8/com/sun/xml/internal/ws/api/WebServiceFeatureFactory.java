package com.sun.xml.internal.ws.api;

import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.lang.annotation.Annotation;
import javax.xml.ws.WebServiceFeature;

public class WebServiceFeatureFactory {
   public static WSFeatureList getWSFeatureList(Iterable<Annotation> ann) {
      WebServiceFeatureList list = new WebServiceFeatureList();
      list.parseAnnotations(ann);
      return list;
   }

   public static WebServiceFeature getWebServiceFeature(Annotation ann) {
      return WebServiceFeatureList.getFeature(ann);
   }
}
