package com.sun.xml.internal.ws.spi.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public final class TypeInfo {
   public final QName tagName;
   public Type type;
   public final Annotation[] annotations;
   private Map<String, Object> properties = new HashMap();
   private boolean isGlobalElement = true;
   private TypeInfo parentCollectionType;
   private Type genericType;
   private boolean nillable = true;

   public TypeInfo(QName tagName, Type type, Annotation... annotations) {
      if (tagName != null && type != null && annotations != null) {
         this.tagName = new QName(tagName.getNamespaceURI().intern(), tagName.getLocalPart().intern(), tagName.getPrefix());
         this.type = type;
         if (type instanceof Class && ((Class)type).isPrimitive()) {
            this.nillable = false;
         }

         this.annotations = annotations;
      } else {
         String nullArgs = "";
         if (tagName == null) {
            nullArgs = "tagName";
         }

         if (type == null) {
            nullArgs = nullArgs + (nullArgs.length() > 0 ? ", type" : "type");
         }

         if (annotations == null) {
            nullArgs = nullArgs + (nullArgs.length() > 0 ? ", annotations" : "annotations");
         }

         throw new IllegalArgumentException("Argument(s) \"" + nullArgs + "\" can''t be null.)");
      }
   }

   public <A extends Annotation> A get(Class<A> annotationType) {
      Annotation[] var2 = this.annotations;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation a = var2[var4];
         if (a.annotationType() == annotationType) {
            return (Annotation)annotationType.cast(a);
         }
      }

      return null;
   }

   public TypeInfo toItemType() {
      Type t = this.genericType != null ? this.genericType : this.type;
      Type base = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(t, Collection.class);
      return base == null ? this : new TypeInfo(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(base, 0), new Annotation[0]);
   }

   public Map<String, Object> properties() {
      return this.properties;
   }

   public boolean isGlobalElement() {
      return this.isGlobalElement;
   }

   public void setGlobalElement(boolean isGlobalElement) {
      this.isGlobalElement = isGlobalElement;
   }

   public TypeInfo getParentCollectionType() {
      return this.parentCollectionType;
   }

   public void setParentCollectionType(TypeInfo parentCollectionType) {
      this.parentCollectionType = parentCollectionType;
   }

   public boolean isRepeatedElement() {
      return this.parentCollectionType != null;
   }

   public Type getGenericType() {
      return this.genericType;
   }

   public void setGenericType(Type genericType) {
      this.genericType = genericType;
   }

   public boolean isNillable() {
      return this.nillable;
   }

   public void setNillable(boolean nillable) {
      this.nillable = nillable;
   }

   public String toString() {
      return "TypeInfo: Type = " + this.type + ", tag = " + this.tagName;
   }

   public TypeInfo getItemType() {
      Type genericComponentType;
      if (this.type instanceof Class && ((Class)this.type).isArray() && !byte[].class.equals(this.type)) {
         Type componentType = ((Class)this.type).getComponentType();
         genericComponentType = null;
         if (this.genericType != null && this.genericType instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)this.type;
            genericComponentType = arrayType.getGenericComponentType();
            componentType = arrayType.getGenericComponentType();
         }

         TypeInfo ti = new TypeInfo(this.tagName, (Type)componentType, this.annotations);
         if (genericComponentType != null) {
            ti.setGenericType(genericComponentType);
         }

         return ti;
      } else {
         Type t = this.genericType != null ? this.genericType : this.type;
         genericComponentType = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(t, Collection.class);
         return genericComponentType != null ? new TypeInfo(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(genericComponentType, 0), this.annotations) : null;
      }
   }
}
