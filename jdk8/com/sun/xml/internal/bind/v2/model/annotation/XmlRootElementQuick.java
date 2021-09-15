package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;

final class XmlRootElementQuick extends Quick implements XmlRootElement {
   private final XmlRootElement core;

   public XmlRootElementQuick(Locatable upstream, XmlRootElement core) {
      super(upstream);
      this.core = core;
   }

   protected Annotation getAnnotation() {
      return this.core;
   }

   protected Quick newInstance(Locatable upstream, Annotation core) {
      return new XmlRootElementQuick(upstream, (XmlRootElement)core);
   }

   public Class<XmlRootElement> annotationType() {
      return XmlRootElement.class;
   }

   public String name() {
      return this.core.name();
   }

   public String namespace() {
      return this.core.namespace();
   }
}
