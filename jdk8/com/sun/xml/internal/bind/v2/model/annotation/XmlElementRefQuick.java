package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;

final class XmlElementRefQuick extends Quick implements XmlElementRef {
   private final XmlElementRef core;

   public XmlElementRefQuick(Locatable upstream, XmlElementRef core) {
      super(upstream);
      this.core = core;
   }

   protected Annotation getAnnotation() {
      return this.core;
   }

   protected Quick newInstance(Locatable upstream, Annotation core) {
      return new XmlElementRefQuick(upstream, (XmlElementRef)core);
   }

   public Class<XmlElementRef> annotationType() {
      return XmlElementRef.class;
   }

   public String name() {
      return this.core.name();
   }

   public Class type() {
      return this.core.type();
   }

   public String namespace() {
      return this.core.namespace();
   }

   public boolean required() {
      return this.core.required();
   }
}
