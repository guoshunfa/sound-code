package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;

enum GroupKind {
   ALL("all"),
   SEQUENCE("sequence"),
   CHOICE("choice");

   private final String name;

   private GroupKind(String name) {
      this.name = name;
   }

   Particle write(ContentModelContainer parent) {
      return (Particle)parent._element(this.name, Particle.class);
   }
}
