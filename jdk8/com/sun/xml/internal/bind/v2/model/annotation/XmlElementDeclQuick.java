package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementDecl;

final class XmlElementDeclQuick extends Quick implements XmlElementDecl {
   private final XmlElementDecl core;

   public XmlElementDeclQuick(Locatable upstream, XmlElementDecl core) {
      super(upstream);
      this.core = core;
   }

   protected Annotation getAnnotation() {
      return this.core;
   }

   protected Quick newInstance(Locatable upstream, Annotation core) {
      return new XmlElementDeclQuick(upstream, (XmlElementDecl)core);
   }

   public Class<XmlElementDecl> annotationType() {
      return XmlElementDecl.class;
   }

   public String name() {
      return this.core.name();
   }

   public Class scope() {
      return this.core.scope();
   }

   public String namespace() {
      return this.core.namespace();
   }

   public String defaultValue() {
      return this.core.defaultValue();
   }

   public String substitutionHeadNamespace() {
      return this.core.substitutionHeadNamespace();
   }

   public String substitutionHeadName() {
      return this.core.substitutionHeadName();
   }
}
