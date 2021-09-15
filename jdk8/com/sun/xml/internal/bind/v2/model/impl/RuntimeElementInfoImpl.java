package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class RuntimeElementInfoImpl extends ElementInfoImpl<Type, Class, Field, Method> implements RuntimeElementInfo {
   private final Class<? extends XmlAdapter> adapterType;

   public RuntimeElementInfoImpl(RuntimeModelBuilder modelBuilder, RegistryInfoImpl registry, Method method) throws IllegalAnnotationException {
      super(modelBuilder, registry, method);
      Adapter<Type, Class> a = this.getProperty().getAdapter();
      if (a != null) {
         this.adapterType = (Class)a.adapterType;
      } else {
         this.adapterType = null;
      }

   }

   protected ElementInfoImpl<Type, Class, Field, Method>.PropertyImpl createPropertyImpl() {
      return new RuntimeElementInfoImpl.RuntimePropertyImpl();
   }

   public RuntimeElementPropertyInfo getProperty() {
      return (RuntimeElementPropertyInfo)super.getProperty();
   }

   public Class<? extends JAXBElement> getType() {
      return (Class)Utils.REFLECTION_NAVIGATOR.erasure(super.getType());
   }

   public RuntimeClassInfo getScope() {
      return (RuntimeClassInfo)super.getScope();
   }

   public RuntimeNonElement getContentType() {
      return (RuntimeNonElement)super.getContentType();
   }

   class RuntimePropertyImpl extends ElementInfoImpl<Type, Class, Field, Method>.PropertyImpl implements RuntimeElementPropertyInfo, RuntimeTypeRef {
      RuntimePropertyImpl() {
         super();
      }

      public Accessor getAccessor() {
         return RuntimeElementInfoImpl.this.adapterType == null ? Accessor.JAXB_ELEMENT_VALUE : Accessor.JAXB_ELEMENT_VALUE.adapt((Class)this.getAdapter().defaultType, RuntimeElementInfoImpl.this.adapterType);
      }

      public Type getRawType() {
         return Collection.class;
      }

      public Type getIndividualType() {
         return (Type)RuntimeElementInfoImpl.this.getContentType().getType();
      }

      public boolean elementOnlyContent() {
         return false;
      }

      public List<? extends RuntimeTypeRef> getTypes() {
         return Collections.singletonList(this);
      }

      public List<? extends RuntimeNonElement> ref() {
         return super.ref();
      }

      public RuntimeNonElement getTarget() {
         return (RuntimeNonElement)super.getTarget();
      }

      public RuntimePropertyInfo getSource() {
         return this;
      }

      public Transducer getTransducer() {
         return RuntimeModelBuilder.createTransducer(this);
      }
   }
}
