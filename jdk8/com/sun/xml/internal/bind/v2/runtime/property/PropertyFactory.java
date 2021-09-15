package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class PropertyFactory {
   private static final Constructor<? extends Property>[] propImpls;

   private PropertyFactory() {
   }

   public static Property create(JAXBContextImpl grammar, RuntimePropertyInfo info) {
      PropertyKind kind = info.kind();
      switch(kind) {
      case ATTRIBUTE:
         return new AttributeProperty(grammar, (RuntimeAttributePropertyInfo)info);
      case VALUE:
         return new ValueProperty(grammar, (RuntimeValuePropertyInfo)info);
      case ELEMENT:
         if (((RuntimeElementPropertyInfo)info).isValueList()) {
            return new ListElementProperty(grammar, (RuntimeElementPropertyInfo)info);
         }
      case REFERENCE:
      case MAP:
         break;
      default:
         assert false;
      }

      boolean isCollection = info.isCollection();
      boolean isLeaf = isLeaf(info);
      Constructor c = propImpls[(isLeaf ? 0 : 6) + (isCollection ? 3 : 0) + kind.propertyIndex];

      try {
         return (Property)c.newInstance(grammar, info);
      } catch (InstantiationException var8) {
         throw new InstantiationError(var8.getMessage());
      } catch (IllegalAccessException var9) {
         throw new IllegalAccessError(var9.getMessage());
      } catch (InvocationTargetException var10) {
         Throwable t = var10.getCause();
         if (t instanceof Error) {
            throw (Error)t;
         } else if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
         } else {
            throw new AssertionError(t);
         }
      }
   }

   static boolean isLeaf(RuntimePropertyInfo info) {
      Collection<? extends RuntimeTypeInfo> types = info.ref();
      if (types.size() != 1) {
         return false;
      } else {
         RuntimeTypeInfo rti = (RuntimeTypeInfo)types.iterator().next();
         if (!(rti instanceof RuntimeNonElement)) {
            return false;
         } else if (info.id() == ID.IDREF) {
            return true;
         } else if (((RuntimeNonElement)rti).getTransducer() == null) {
            return false;
         } else {
            return info.getIndividualType().equals(rti.getType());
         }
      }
   }

   static {
      Class<? extends Property>[] implClasses = new Class[]{SingleElementLeafProperty.class, null, null, ArrayElementLeafProperty.class, null, null, SingleElementNodeProperty.class, SingleReferenceNodeProperty.class, SingleMapNodeProperty.class, ArrayElementNodeProperty.class, ArrayReferenceNodeProperty.class, null};
      propImpls = new Constructor[implClasses.length];

      for(int i = 0; i < propImpls.length; ++i) {
         if (implClasses[i] != null) {
            propImpls[i] = implClasses[i].getConstructors()[0];
         }
      }

   }
}
