package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.sun.istack.internal.NotNull;

/** @deprecated */
public abstract class DistributedPropertySet extends BaseDistributedPropertySet {
   /** @deprecated */
   public void addSatellite(@NotNull PropertySet satellite) {
      super.addSatellite(satellite);
   }

   /** @deprecated */
   public void addSatellite(@NotNull Class keyClass, @NotNull PropertySet satellite) {
      super.addSatellite(keyClass, satellite);
   }

   /** @deprecated */
   public void copySatelliteInto(@NotNull DistributedPropertySet r) {
      super.copySatelliteInto((com.oracle.webservices.internal.api.message.DistributedPropertySet)r);
   }

   /** @deprecated */
   public void removeSatellite(PropertySet satellite) {
      super.removeSatellite(satellite);
   }
}
