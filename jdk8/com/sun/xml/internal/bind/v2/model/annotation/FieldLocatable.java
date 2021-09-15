package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class FieldLocatable<F> implements Locatable {
   private final Locatable upstream;
   private final F field;
   private final Navigator<?, ?, F, ?> nav;

   public FieldLocatable(Locatable upstream, F field, Navigator<?, ?, F, ?> nav) {
      this.upstream = upstream;
      this.field = field;
      this.nav = nav;
   }

   public Locatable getUpstream() {
      return this.upstream;
   }

   public Location getLocation() {
      return this.nav.getFieldLocation(this.field);
   }
}
