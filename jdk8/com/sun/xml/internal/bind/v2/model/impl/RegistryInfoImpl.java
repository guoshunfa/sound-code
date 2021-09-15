package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import com.sun.xml.internal.bind.v2.model.core.RegistryInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElementDecl;

final class RegistryInfoImpl<T, C, F, M> implements Locatable, RegistryInfo<T, C> {
   final C registryClass;
   private final Locatable upstream;
   private final Navigator<T, C, F, M> nav;
   private final Set<TypeInfo<T, C>> references = new LinkedHashSet();

   RegistryInfoImpl(ModelBuilder<T, C, F, M> builder, Locatable upstream, C registryClass) {
      this.nav = builder.nav;
      this.registryClass = registryClass;
      this.upstream = upstream;
      builder.registries.put(this.getPackageName(), this);
      if (this.nav.getDeclaredField(registryClass, "_useJAXBProperties") != null) {
         builder.reportError(new IllegalAnnotationException(Messages.MISSING_JAXB_PROPERTIES.format(this.getPackageName()), this));
      } else {
         Iterator var4 = this.nav.getDeclaredMethods(registryClass).iterator();

         while(true) {
            while(var4.hasNext()) {
               M m = var4.next();
               XmlElementDecl em = (XmlElementDecl)builder.reader.getMethodAnnotation(XmlElementDecl.class, m, this);
               if (em == null) {
                  if (this.nav.getMethodName(m).startsWith("create")) {
                     this.references.add(builder.getTypeInfo(this.nav.getReturnType(m), new MethodLocatable(this, m, this.nav)));
                  }
               } else {
                  ElementInfoImpl ei;
                  try {
                     ei = builder.createElementInfo(this, m);
                  } catch (IllegalAnnotationException var9) {
                     builder.reportError(var9);
                     continue;
                  }

                  builder.typeInfoSet.add(ei, builder);
                  this.references.add(ei);
               }
            }

            return;
         }
      }
   }

   public Locatable getUpstream() {
      return this.upstream;
   }

   public Location getLocation() {
      return this.nav.getClassLocation(this.registryClass);
   }

   public Set<TypeInfo<T, C>> getReferences() {
      return this.references;
   }

   public String getPackageName() {
      return this.nav.getPackageName(this.registryClass);
   }

   public C getClazz() {
      return this.registryClass;
   }
}
