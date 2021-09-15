package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.ClassLocatable;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.RegistryInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

public class ModelBuilder<T, C, F, M> implements ModelBuilderI<T, C, F, M> {
   private static final Logger logger;
   final TypeInfoSetImpl<T, C, F, M> typeInfoSet;
   public final AnnotationReader<T, C, F, M> reader;
   public final Navigator<T, C, F, M> nav;
   private final Map<QName, TypeInfo> typeNames = new HashMap();
   public final String defaultNsUri;
   final Map<String, RegistryInfoImpl<T, C, F, M>> registries = new HashMap();
   private final Map<C, C> subclassReplacements;
   private ErrorHandler errorHandler;
   private boolean hadError;
   public boolean hasSwaRef;
   private final ErrorHandler proxyErrorHandler = new ErrorHandler() {
      public void error(IllegalAnnotationException e) {
         ModelBuilder.this.reportError(e);
      }
   };
   private boolean linked;

   public ModelBuilder(AnnotationReader<T, C, F, M> reader, Navigator<T, C, F, M> navigator, Map<C, C> subclassReplacements, String defaultNamespaceRemap) {
      this.reader = reader;
      this.nav = navigator;
      this.subclassReplacements = subclassReplacements;
      if (defaultNamespaceRemap == null) {
         defaultNamespaceRemap = "";
      }

      this.defaultNsUri = defaultNamespaceRemap;
      reader.setErrorHandler(this.proxyErrorHandler);
      this.typeInfoSet = this.createTypeInfoSet();
   }

   protected TypeInfoSetImpl<T, C, F, M> createTypeInfoSet() {
      return new TypeInfoSetImpl(this.nav, this.reader, BuiltinLeafInfoImpl.createLeaves(this.nav));
   }

   public NonElement<T, C> getClassInfo(C clazz, Locatable upstream) {
      return this.getClassInfo(clazz, false, upstream);
   }

   public NonElement<T, C> getClassInfo(C clazz, boolean searchForSuperClass, Locatable upstream) {
      assert clazz != null;

      NonElement<T, C> r = this.typeInfoSet.getClassInfo(clazz);
      if (r != null) {
         return r;
      } else {
         Object r;
         if (this.nav.isEnum(clazz)) {
            EnumLeafInfoImpl<T, C, F, M> li = this.createEnumLeafInfo(clazz, upstream);
            this.typeInfoSet.add(li);
            r = li;
            this.addTypeName(li);
         } else {
            boolean isReplaced = this.subclassReplacements.containsKey(clazz);
            if (isReplaced && !searchForSuperClass) {
               r = this.getClassInfo(this.subclassReplacements.get(clazz), upstream);
            } else if (!this.reader.hasClassAnnotation(clazz, XmlTransient.class) && !isReplaced) {
               ClassInfoImpl<T, C, F, M> ci = this.createClassInfo(clazz, upstream);
               this.typeInfoSet.add(ci);
               Iterator var7 = ci.getProperties().iterator();

               while(var7.hasNext()) {
                  PropertyInfo<T, C> p = (PropertyInfo)var7.next();
                  if (p.kind() == PropertyKind.REFERENCE) {
                     this.addToRegistry(clazz, (Locatable)p);
                     Class[] prmzdClasses = this.getParametrizedTypes(p);
                     if (prmzdClasses != null) {
                        Class[] var10 = prmzdClasses;
                        int var11 = prmzdClasses.length;

                        for(int var12 = 0; var12 < var11; ++var12) {
                           Class prmzdClass = var10[var12];
                           if (prmzdClass != clazz) {
                              this.addToRegistry(prmzdClass, (Locatable)p);
                           }
                        }
                     }
                  }

                  TypeInfo var22;
                  for(Iterator var20 = p.ref().iterator(); var20.hasNext(); var22 = (TypeInfo)var20.next()) {
                  }
               }

               ci.getBaseClass();
               r = ci;
               this.addTypeName(ci);
            } else {
               r = this.getClassInfo(this.nav.getSuperClass(clazz), searchForSuperClass, new ClassLocatable(upstream, clazz, this.nav));
            }
         }

         XmlSeeAlso sa = (XmlSeeAlso)this.reader.getClassAnnotation(XmlSeeAlso.class, clazz, upstream);
         if (sa != null) {
            Object[] var17 = this.reader.getClassArrayValue(sa, "value");
            int var18 = var17.length;

            for(int var19 = 0; var19 < var18; ++var19) {
               T t = var17[var19];
               this.getTypeInfo(t, (Locatable)sa);
            }
         }

         return (NonElement)r;
      }
   }

   private void addToRegistry(C clazz, Locatable p) {
      String pkg = this.nav.getPackageName(clazz);
      if (!this.registries.containsKey(pkg)) {
         C c = this.nav.loadObjectFactory(clazz, pkg);
         if (c != null) {
            this.addRegistry(c, p);
         }
      }

   }

   private Class[] getParametrizedTypes(PropertyInfo p) {
      try {
         Type pType = ((RuntimePropertyInfo)p).getIndividualType();
         if (pType instanceof ParameterizedType) {
            ParameterizedType prmzdType = (ParameterizedType)pType;
            if (prmzdType.getRawType() == JAXBElement.class) {
               Type[] actualTypes = prmzdType.getActualTypeArguments();
               Class[] result = new Class[actualTypes.length];

               for(int i = 0; i < actualTypes.length; ++i) {
                  result[i] = (Class)actualTypes[i];
               }

               return result;
            }
         }
      } catch (Exception var7) {
         logger.log(Level.FINE, "Error in ModelBuilder.getParametrizedTypes. " + var7.getMessage());
      }

      return null;
   }

   private void addTypeName(NonElement<T, C> r) {
      QName t = r.getTypeName();
      if (t != null) {
         TypeInfo old = (TypeInfo)this.typeNames.put(t, r);
         if (old != null) {
            this.reportError(new IllegalAnnotationException(Messages.CONFLICTING_XML_TYPE_MAPPING.format(r.getTypeName()), old, r));
         }

      }
   }

   public NonElement<T, C> getTypeInfo(T t, Locatable upstream) {
      NonElement<T, C> r = this.typeInfoSet.getTypeInfo(t);
      if (r != null) {
         return r;
      } else if (this.nav.isArray(t)) {
         ArrayInfoImpl<T, C, F, M> ai = this.createArrayInfo(upstream, t);
         this.addTypeName(ai);
         this.typeInfoSet.add(ai);
         return ai;
      } else {
         C c = this.nav.asDecl(t);

         assert c != null : t.toString() + " must be a leaf, but we failed to recognize it.";

         return this.getClassInfo(c, upstream);
      }
   }

   public NonElement<T, C> getTypeInfo(Ref<T, C> ref) {
      assert !ref.valueList;

      C c = this.nav.asDecl(ref.type);
      if (c != null && this.reader.getClassAnnotation(XmlRegistry.class, c, (Locatable)null) != null) {
         if (!this.registries.containsKey(this.nav.getPackageName(c))) {
            this.addRegistry(c, (Locatable)null);
         }

         return null;
      } else {
         return this.getTypeInfo(ref.type, (Locatable)null);
      }
   }

   protected EnumLeafInfoImpl<T, C, F, M> createEnumLeafInfo(C clazz, Locatable upstream) {
      return new EnumLeafInfoImpl(this, upstream, clazz, this.nav.use(clazz));
   }

   protected ClassInfoImpl<T, C, F, M> createClassInfo(C clazz, Locatable upstream) {
      return new ClassInfoImpl(this, upstream, clazz);
   }

   protected ElementInfoImpl<T, C, F, M> createElementInfo(RegistryInfoImpl<T, C, F, M> registryInfo, M m) throws IllegalAnnotationException {
      return new ElementInfoImpl(this, registryInfo, m);
   }

   protected ArrayInfoImpl<T, C, F, M> createArrayInfo(Locatable upstream, T arrayType) {
      return new ArrayInfoImpl(this, upstream, arrayType);
   }

   public RegistryInfo<T, C> addRegistry(C registryClass, Locatable upstream) {
      return new RegistryInfoImpl(this, upstream, registryClass);
   }

   public RegistryInfo<T, C> getRegistry(String packageName) {
      return (RegistryInfo)this.registries.get(packageName);
   }

   public TypeInfoSet<T, C, F, M> link() {
      assert !this.linked;

      this.linked = true;
      Iterator var1 = this.typeInfoSet.getAllElements().iterator();

      while(var1.hasNext()) {
         ElementInfoImpl ei = (ElementInfoImpl)var1.next();
         ei.link();
      }

      var1 = this.typeInfoSet.beans().values().iterator();

      while(var1.hasNext()) {
         ClassInfoImpl ci = (ClassInfoImpl)var1.next();
         ci.link();
      }

      var1 = this.typeInfoSet.enums().values().iterator();

      while(var1.hasNext()) {
         EnumLeafInfoImpl li = (EnumLeafInfoImpl)var1.next();
         li.link();
      }

      return this.hadError ? null : this.typeInfoSet;
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   public final void reportError(IllegalAnnotationException e) {
      this.hadError = true;
      if (this.errorHandler != null) {
         this.errorHandler.error(e);
      }

   }

   public boolean isReplaced(C sc) {
      return this.subclassReplacements.containsKey(sc);
   }

   public Navigator<T, C, F, M> getNavigator() {
      return this.nav;
   }

   public AnnotationReader<T, C, F, M> getReader() {
      return this.reader;
   }

   static {
      try {
         XmlSchema s = null;
         ((XmlSchema)s).location();
      } catch (NullPointerException var3) {
      } catch (NoSuchMethodError var4) {
         Messages res;
         if (SecureLoader.getClassClassLoader(XmlSchema.class) == null) {
            res = Messages.INCOMPATIBLE_API_VERSION_MUSTANG;
         } else {
            res = Messages.INCOMPATIBLE_API_VERSION;
         }

         throw new LinkageError(res.format(Which.which(XmlSchema.class), Which.which(ModelBuilder.class)));
      }

      try {
         WhiteSpaceProcessor.isWhiteSpace("xyz");
      } catch (NoSuchMethodError var2) {
         throw new LinkageError(Messages.RUNNING_WITH_1_0_RUNTIME.format(Which.which(WhiteSpaceProcessor.class), Which.which(ModelBuilder.class)));
      }

      logger = Logger.getLogger(ModelBuilder.class.getName());
   }
}
