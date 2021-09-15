package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;

public abstract class Quick implements Annotation, Locatable, Location {
   private final Locatable upstream;

   protected Quick(Locatable upstream) {
      this.upstream = upstream;
   }

   protected abstract Annotation getAnnotation();

   protected abstract Quick newInstance(Locatable var1, Annotation var2);

   public final Location getLocation() {
      return this;
   }

   public final Locatable getUpstream() {
      return this.upstream;
   }

   public final String toString() {
      return this.getAnnotation().toString();
   }
}
