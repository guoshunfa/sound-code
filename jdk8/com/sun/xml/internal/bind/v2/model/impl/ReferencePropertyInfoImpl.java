package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;

class ReferencePropertyInfoImpl<T, C, F, M> extends ERPropertyInfoImpl<T, C, F, M> implements ReferencePropertyInfo<T, C>, DummyPropertyInfo<T, C, F, M> {
   private Set<Element<T, C>> types;
   private Set<ReferencePropertyInfoImpl<T, C, F, M>> subTypes = new LinkedHashSet();
   private final boolean isMixed;
   private final WildcardMode wildcard;
   private final C domHandler;
   private Boolean isRequired;
   private static boolean is2_2 = true;

   public ReferencePropertyInfoImpl(ClassInfoImpl<T, C, F, M> classInfo, PropertySeed<T, C, F, M> seed) {
      super(classInfo, seed);
      this.isMixed = seed.readAnnotation(XmlMixed.class) != null;
      XmlAnyElement xae = (XmlAnyElement)seed.readAnnotation(XmlAnyElement.class);
      if (xae == null) {
         this.wildcard = null;
         this.domHandler = null;
      } else {
         this.wildcard = xae.lax() ? WildcardMode.LAX : WildcardMode.SKIP;
         this.domHandler = this.nav().asDecl(this.reader().getClassValue(xae, "value"));
      }

   }

   public Set<? extends Element<T, C>> ref() {
      return this.getElements();
   }

   public PropertyKind kind() {
      return PropertyKind.REFERENCE;
   }

   public Set<? extends Element<T, C>> getElements() {
      if (this.types == null) {
         this.calcTypes(false);
      }

      assert this.types != null;

      return this.types;
   }

   private void calcTypes(boolean last) {
      this.types = new LinkedHashSet();
      XmlElementRefs refs = (XmlElementRefs)this.seed.readAnnotation(XmlElementRefs.class);
      XmlElementRef ref = (XmlElementRef)this.seed.readAnnotation(XmlElementRef.class);
      if (refs != null && ref != null) {
         this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), ref.annotationType().getName(), refs.annotationType().getName()), ref, refs));
      }

      XmlElementRef[] ann;
      if (refs != null) {
         ann = refs.value();
      } else if (ref != null) {
         ann = new XmlElementRef[]{ref};
      } else {
         ann = null;
      }

      this.isRequired = !this.isCollection();
      if (ann != null) {
         Navigator<T, C, F, M> nav = this.nav();
         AnnotationReader<T, C, F, M> reader = this.reader();
         T defaultType = nav.ref(XmlElementRef.DEFAULT.class);
         C je = nav.asDecl(JAXBElement.class);
         XmlElementRef[] var9 = ann;
         int var10 = ann.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            XmlElementRef r = var9[var11];
            T type = reader.getClassValue(r, "type");
            if (this.nav().isSameType(type, defaultType)) {
               type = nav.erasure(this.getIndividualType());
            }

            boolean yield;
            if (nav.getBaseClass(type, je) != null) {
               yield = this.addGenericElement(r);
            } else {
               yield = this.addAllSubtypes(type);
            }

            if (this.isRequired && !this.isRequired(r)) {
               this.isRequired = false;
            }

            if (last && !yield) {
               if (this.nav().isSameType(type, nav.ref(JAXBElement.class))) {
                  this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(this.getEffectiveNamespaceFor(r), r.name()), this));
               } else {
                  this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(type), this));
               }

               return;
            }
         }
      }

      Iterator var18 = this.subTypes.iterator();

      while(true) {
         ReferencePropertyInfoImpl info;
         do {
            if (!var18.hasNext()) {
               this.types = Collections.unmodifiableSet(this.types);
               return;
            }

            info = (ReferencePropertyInfoImpl)var18.next();
            PropertySeed sd = info.seed;
            refs = (XmlElementRefs)sd.readAnnotation(XmlElementRefs.class);
            ref = (XmlElementRef)sd.readAnnotation(XmlElementRef.class);
            if (refs != null && ref != null) {
               this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), ref.annotationType().getName(), refs.annotationType().getName()), ref, refs));
            }

            if (refs != null) {
               ann = refs.value();
            } else if (ref != null) {
               ann = new XmlElementRef[]{ref};
            } else {
               ann = null;
            }
         } while(ann == null);

         Navigator<T, C, F, M> nav = this.nav();
         AnnotationReader<T, C, F, M> reader = this.reader();
         T defaultType = nav.ref(XmlElementRef.DEFAULT.class);
         C je = nav.asDecl(JAXBElement.class);
         XmlElementRef[] var25 = ann;
         int var26 = ann.length;

         for(int var27 = 0; var27 < var26; ++var27) {
            XmlElementRef r = var25[var27];
            T type = reader.getClassValue(r, "type");
            if (this.nav().isSameType(type, defaultType)) {
               type = nav.erasure(this.getIndividualType());
            }

            boolean yield;
            if (nav.getBaseClass(type, je) != null) {
               yield = this.addGenericElement(r, info);
            } else {
               yield = this.addAllSubtypes(type);
            }

            if (last && !yield) {
               if (this.nav().isSameType(type, nav.ref(JAXBElement.class))) {
                  this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(this.getEffectiveNamespaceFor(r), r.name()), this));
               } else {
                  this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(), this));
               }

               return;
            }
         }
      }
   }

   public boolean isRequired() {
      if (this.isRequired == null) {
         this.calcTypes(false);
      }

      return this.isRequired;
   }

   private boolean isRequired(XmlElementRef ref) {
      if (!is2_2) {
         return true;
      } else {
         try {
            return ref.required();
         } catch (LinkageError var3) {
            is2_2 = false;
            return true;
         }
      }
   }

   private boolean addGenericElement(XmlElementRef r) {
      String nsUri = this.getEffectiveNamespaceFor(r);
      return this.addGenericElement((ElementInfo)this.parent.owner.getElementInfo(this.parent.getClazz(), new QName(nsUri, r.name())));
   }

   private boolean addGenericElement(XmlElementRef r, ReferencePropertyInfoImpl<T, C, F, M> info) {
      String nsUri = info.getEffectiveNamespaceFor(r);
      ElementInfo ei = this.parent.owner.getElementInfo(info.parent.getClazz(), new QName(nsUri, r.name()));
      this.types.add(ei);
      return true;
   }

   private String getEffectiveNamespaceFor(XmlElementRef r) {
      String nsUri = r.namespace();
      XmlSchema xs = (XmlSchema)this.reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
      if (xs != null && xs.attributeFormDefault() == XmlNsForm.QUALIFIED && nsUri.length() == 0) {
         nsUri = this.parent.builder.defaultNsUri;
      }

      return nsUri;
   }

   private boolean addGenericElement(ElementInfo<T, C> ei) {
      if (ei == null) {
         return false;
      } else {
         this.types.add(ei);
         Iterator var2 = ei.getSubstitutionMembers().iterator();

         while(var2.hasNext()) {
            ElementInfo<T, C> subst = (ElementInfo)var2.next();
            this.addGenericElement(subst);
         }

         return true;
      }
   }

   private boolean addAllSubtypes(T type) {
      Navigator<T, C, F, M> nav = this.nav();
      NonElement<T, C> t = this.parent.builder.getClassInfo(nav.asDecl(type), this);
      if (!(t instanceof ClassInfo)) {
         return false;
      } else {
         boolean result = false;
         ClassInfo<T, C> c = (ClassInfo)t;
         if (c.isElement()) {
            this.types.add(c.asElement());
            result = true;
         }

         Iterator var6 = this.parent.owner.beans().values().iterator();

         while(var6.hasNext()) {
            ClassInfo<T, C> ci = (ClassInfo)var6.next();
            if (ci.isElement() && nav.isSubClassOf(ci.getType(), type)) {
               this.types.add(ci.asElement());
               result = true;
            }
         }

         var6 = this.parent.owner.getElementMappings((Object)null).values().iterator();

         while(var6.hasNext()) {
            ElementInfo<T, C> ei = (ElementInfo)var6.next();
            if (nav.isSubClassOf(ei.getType(), type)) {
               this.types.add(ei);
               result = true;
            }
         }

         return result;
      }
   }

   protected void link() {
      super.link();
      this.calcTypes(true);
   }

   public final void addType(PropertyInfoImpl<T, C, F, M> info) {
      this.subTypes.add((ReferencePropertyInfoImpl)info);
   }

   public final boolean isMixed() {
      return this.isMixed;
   }

   public final WildcardMode getWildcard() {
      return this.wildcard;
   }

   public final C getDOMHandler() {
      return this.domHandler;
   }
}
