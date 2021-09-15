package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.Nullable;
import java.util.Map;

public interface DistributedPropertySet extends PropertySet {
   @Nullable
   <T extends PropertySet> T getSatellite(Class<T> var1);

   Map<Class<? extends PropertySet>, PropertySet> getSatellites();

   void addSatellite(PropertySet var1);

   void addSatellite(Class<? extends PropertySet> var1, PropertySet var2);

   void removeSatellite(PropertySet var1);

   void copySatelliteInto(MessageContext var1);
}
