package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;

public interface XMLComponentManager {
   boolean getFeature(String var1) throws XMLConfigurationException;

   boolean getFeature(String var1, boolean var2);

   Object getProperty(String var1) throws XMLConfigurationException;

   Object getProperty(String var1, Object var2);

   FeatureState getFeatureState(String var1);

   PropertyState getPropertyState(String var1);
}
