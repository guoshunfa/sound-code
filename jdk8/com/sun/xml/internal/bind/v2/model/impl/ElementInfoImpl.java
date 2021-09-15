package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.activation.MimeType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

class ElementInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements ElementInfo<T, C> {
   private final QName tagName;
   private final NonElement<T, C> contentType;
   private final T tOfJAXBElementT;
   private final T elementType;
   private final ClassInfo<T, C> scope;
   private final XmlElementDecl anno;
   private ElementInfoImpl<T, C, F, M> substitutionHead;
   private FinalArrayList<ElementInfoImpl<T, C, F, M>> substitutionMembers;
   private final M method;
   private final Adapter<T, C> adapter;
   private final boolean isCollection;
   private final ID id;
   private final ElementInfoImpl<T, C, F, M>.PropertyImpl property;
   private final MimeType expectedMimeType;
   private final boolean inlineBinary;
   private final QName schemaType;

   public ElementInfoImpl(ModelBuilder<T, C, F, M> builder, RegistryInfoImpl<T, C, F, M> registry, M m) throws IllegalAnnotationException {
      super(builder, registry);
      this.method = m;
      this.anno = (XmlElementDecl)this.reader().getMethodAnnotation(XmlElementDecl.class, m, this);

      assert this.anno != null;

      assert this.anno instanceof Locatable;

      this.elementType = this.nav().getReturnType(m);
      T baseClass = this.nav().getBaseClass(this.elementType, this.nav().asDecl(JAXBElement.class));
      if (baseClass == null) {
         throw new IllegalAnnotationException(Messages.XML_ELEMENT_MAPPING_ON_NON_IXMLELEMENT_METHOD.format(this.nav().getMethodName(m)), this.anno);
      } else {
         this.tagName = this.parseElementName(this.anno);
         T[] methodParams = this.nav().getMethodParameters(m);
         Adapter<T, C> a = null;
         if (methodParams.length > 0) {
            XmlJavaTypeAdapter adapter = (XmlJavaTypeAdapter)this.reader().getMethodAnnotation(XmlJavaTypeAdapter.class, m, this);
            if (adapter != null) {
               a = new Adapter(adapter, this.reader(), this.nav());
            } else {
               XmlAttachmentRef xsa = (XmlAttachmentRef)this.reader().getMethodAnnotation(XmlAttachmentRef.class, m, this);
               if (xsa != null) {
                  TODO.prototype("in Annotation Processing swaRefAdapter isn't avaialble, so this returns null");
                  a = new Adapter(this.owner.nav.asDecl(SwaRefAdapter.class), this.owner.nav);
               }
            }
         }

         this.adapter = a;
         this.tOfJAXBElementT = methodParams.length > 0 ? methodParams[0] : this.nav().getTypeArgument(baseClass, 0);
         Object s;
         if (this.adapter == null) {
            s = this.nav().getBaseClass(this.tOfJAXBElementT, this.nav().asDecl(List.class));
            if (s == null) {
               this.isCollection = false;
               this.contentType = builder.getTypeInfo(this.tOfJAXBElementT, this);
            } else {
               this.isCollection = true;
               this.contentType = builder.getTypeInfo(this.nav().getTypeArgument(s, 0), this);
            }
         } else {
            this.contentType = builder.getTypeInfo(this.adapter.defaultType, this);
            this.isCollection = false;
         }

         s = this.reader().getClassValue(this.anno, "scope");
         if (this.nav().isSameType(s, this.nav().ref(XmlElementDecl.GLOBAL.class))) {
            this.scope = null;
         } else {
            NonElement<T, C> scp = builder.getClassInfo(this.nav().asDecl(s), this);
            if (!(scp instanceof ClassInfo)) {
               throw new IllegalAnnotationException(Messages.SCOPE_IS_NOT_COMPLEXTYPE.format(this.nav().getTypeName(s)), this.anno);
            }

            this.scope = (ClassInfo)scp;
         }

         this.id = this.calcId();
         this.property = this.createPropertyImpl();
         this.expectedMimeType = Util.calcExpectedMediaType(this.property, builder);
         this.inlineBinary = this.reader().hasMethodAnnotation(XmlInlineBinaryData.class, this.method);
         this.schemaType = Util.calcSchemaType(this.reader(), this.property, registry.registryClass, this.getContentInMemoryType(), this);
      }
   }

   final QName parseElementName(XmlElementDecl e) {
      String local = e.name();
      String nsUri = e.namespace();
      if (nsUri.equals("##default")) {
         XmlSchema xs = (XmlSchema)this.reader().getPackageAnnotation(XmlSchema.class, this.nav().getDeclaringClassForMethod(this.method), this);
         if (xs != null) {
            nsUri = xs.namespace();
         } else {
            nsUri = this.builder.defaultNsUri;
         }
      }

      return new QName(nsUri.intern(), local.intern());
   }

   protected ElementInfoImpl<T, C, F, M>.PropertyImpl createPropertyImpl() {
      return new ElementInfoImpl.PropertyImpl();
   }

   public ElementPropertyInfo<T, C> getProperty() {
      return this.property;
   }

   public NonElement<T, C> getContentType() {
      return this.contentType;
   }

   public T getContentInMemoryType() {
      return this.adapter == null ? this.tOfJAXBElementT : this.adapter.customType;
   }

   public QName getElementName() {
      return this.tagName;
   }

   public T getType() {
      return this.elementType;
   }

   /** @deprecated */
   public final boolean canBeReferencedByIDREF() {
      return false;
   }

   private ID calcId() {
      if (this.reader().hasMethodAnnotation(XmlID.class, this.method)) {
         return ID.ID;
      } else {
         return this.reader().hasMethodAnnotation(XmlIDREF.class, this.method) ? ID.IDREF : ID.NONE;
      }
   }

   public ClassInfo<T, C> getScope() {
      return this.scope;
   }

   public ElementInfo<T, C> getSubstitutionHead() {
      return this.substitutionHead;
   }

   public Collection<? extends ElementInfoImpl<T, C, F, M>> getSubstitutionMembers() {
      return (Collection)(this.substitutionMembers == null ? Collections.emptyList() : this.substitutionMembers);
   }

   void link() {
      if (this.anno.substitutionHeadName().length() != 0) {
         QName name = new QName(this.anno.substitutionHeadNamespace(), this.anno.substitutionHeadName());
         this.substitutionHead = this.owner.getElementInfo((Object)null, name);
         if (this.substitutionHead == null) {
            this.builder.reportError(new IllegalAnnotationException(Messages.NON_EXISTENT_ELEMENT_MAPPING.format(name.getNamespaceURI(), name.getLocalPart()), this.anno));
         } else {
            this.substitutionHead.addSubstitutionMember(this);
         }
      } else {
         this.substitutionHead = null;
      }

      super.link();
   }

   private void addSubstitutionMember(ElementInfoImpl<T, C, F, M> child) {
      if (this.substitutionMembers == null) {
         this.substitutionMembers = new FinalArrayList();
      }

      this.substitutionMembers.add(child);
   }

   public Location getLocation() {
      return this.nav().getMethodLocation(this.method);
   }

   protected class PropertyImpl implements ElementPropertyInfo<T, C>, TypeRef<T, C>, AnnotationSource {
      public NonElement<T, C> getTarget() {
         return ElementInfoImpl.this.contentType;
      }

      public QName getTagName() {
         return ElementInfoImpl.this.tagName;
      }

      public List<? extends TypeRef<T, C>> getTypes() {
         return Collections.singletonList(this);
      }

      public List<? extends NonElement<T, C>> ref() {
         return Collections.singletonList(ElementInfoImpl.this.contentType);
      }

      public QName getXmlName() {
         return ElementInfoImpl.this.tagName;
      }

      public boolean isCollectionRequired() {
         return false;
      }

      public boolean isCollectionNillable() {
         return true;
      }

      public boolean isNillable() {
         return true;
      }

      public String getDefaultValue() {
         String v = ElementInfoImpl.this.anno.defaultValue();
         return v.equals("\u0000") ? null : v;
      }

      public ElementInfoImpl<T, C, F, M> parent() {
         return ElementInfoImpl.this;
      }

      public String getName() {
         return "value";
      }

      public String displayName() {
         return "JAXBElement#value";
      }

      public boolean isCollection() {
         return ElementInfoImpl.this.isCollection;
      }

      public boolean isValueList() {
         return ElementInfoImpl.this.isCollection;
      }

      public boolean isRequired() {
         return true;
      }

      public PropertyKind kind() {
         return PropertyKind.ELEMENT;
      }

      public Adapter<T, C> getAdapter() {
         return ElementInfoImpl.this.adapter;
      }

      public ID id() {
         return ElementInfoImpl.this.id;
      }

      public MimeType getExpectedMimeType() {
         return ElementInfoImpl.this.expectedMimeType;
      }

      public QName getSchemaType() {
         return ElementInfoImpl.this.schemaType;
      }

      public boolean inlineBinaryData() {
         return ElementInfoImpl.this.inlineBinary;
      }

      public PropertyInfo<T, C> getSource() {
         return this;
      }

      public <A extends Annotation> A readAnnotation(Class<A> annotationType) {
         return ElementInfoImpl.this.reader().getMethodAnnotation(annotationType, ElementInfoImpl.this.method, ElementInfoImpl.this);
      }

      public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
         return ElementInfoImpl.this.reader().hasMethodAnnotation(annotationType, ElementInfoImpl.this.method);
      }
   }
}
