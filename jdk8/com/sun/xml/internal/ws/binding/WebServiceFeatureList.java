package com.sun.xml.internal.ws.binding;

import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.api.FeatureListValidator;
import com.sun.xml.internal.ws.api.FeatureListValidatorAnnotation;
import com.sun.xml.internal.ws.api.ImpliesWebServiceFeature;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.RuntimeModelerException;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import javax.xml.ws.RespectBinding;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

public final class WebServiceFeatureList extends AbstractMap<Class<? extends WebServiceFeature>, WebServiceFeature> implements WSFeatureList {
   private Map<Class<? extends WebServiceFeature>, WebServiceFeature> wsfeatures = new HashMap();
   private boolean isValidating = false;
   @Nullable
   private WSDLFeaturedObject parent;
   private static final Logger LOGGER = Logger.getLogger(WebServiceFeatureList.class.getName());

   public static WebServiceFeatureList toList(Iterable<WebServiceFeature> features) {
      if (features instanceof WebServiceFeatureList) {
         return (WebServiceFeatureList)features;
      } else {
         WebServiceFeatureList w = new WebServiceFeatureList();
         if (features != null) {
            w.addAll(features);
         }

         return w;
      }
   }

   public WebServiceFeatureList() {
   }

   public WebServiceFeatureList(@NotNull WebServiceFeature... features) {
      if (features != null) {
         WebServiceFeature[] var2 = features;
         int var3 = features.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            WebServiceFeature f = var2[var4];
            this.addNoValidate(f);
         }
      }

   }

   public void validate() {
      if (!this.isValidating) {
         this.isValidating = true;
         Iterator var1 = this.iterator();

         while(var1.hasNext()) {
            WebServiceFeature ff = (WebServiceFeature)var1.next();
            this.validate(ff);
         }
      }

   }

   private void validate(WebServiceFeature feature) {
      FeatureListValidatorAnnotation fva = (FeatureListValidatorAnnotation)feature.getClass().getAnnotation(FeatureListValidatorAnnotation.class);
      if (fva != null) {
         Class beanClass = fva.bean();

         try {
            FeatureListValidator validator = (FeatureListValidator)beanClass.newInstance();
            validator.validate(this);
         } catch (InstantiationException var5) {
            throw new WebServiceException(var5);
         } catch (IllegalAccessException var6) {
            throw new WebServiceException(var6);
         }
      }

   }

   public WebServiceFeatureList(WebServiceFeatureList features) {
      if (features != null) {
         this.wsfeatures.putAll(features.wsfeatures);
         this.parent = features.parent;
         this.isValidating = features.isValidating;
      }

   }

   public WebServiceFeatureList(@NotNull Class<?> endpointClass) {
      this.parseAnnotations(endpointClass);
   }

   public void parseAnnotations(Iterable<Annotation> annIt) {
      Iterator var2 = annIt.iterator();

      while(var2.hasNext()) {
         Annotation ann = (Annotation)var2.next();
         WebServiceFeature feature = getFeature(ann);
         if (feature != null) {
            this.add(feature);
         }
      }

   }

   public static WebServiceFeature getFeature(Annotation a) {
      WebServiceFeature ftr = null;
      if (!a.annotationType().isAnnotationPresent(WebServiceFeatureAnnotation.class)) {
         ftr = null;
      } else if (a instanceof Addressing) {
         Addressing addAnn = (Addressing)a;

         try {
            ftr = new AddressingFeature(addAnn.enabled(), addAnn.required(), addAnn.responses());
         } catch (NoSuchMethodError var4) {
            throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(Addressing.class))), new Object[0]);
         }
      } else if (a instanceof MTOM) {
         MTOM mtomAnn = (MTOM)a;
         ftr = new MTOMFeature(mtomAnn.enabled(), mtomAnn.threshold());
      } else if (a instanceof RespectBinding) {
         RespectBinding rbAnn = (RespectBinding)a;
         ftr = new RespectBindingFeature(rbAnn.enabled());
      } else {
         ftr = getWebServiceFeatureBean(a);
      }

      return (WebServiceFeature)ftr;
   }

   public void parseAnnotations(Class<?> endpointClass) {
      Annotation[] var2 = endpointClass.getAnnotations();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation a = var2[var4];
         WebServiceFeature ftr = getFeature(a);
         if (ftr != null) {
            if (ftr instanceof MTOMFeature) {
               BindingID bindingID = BindingID.parse(endpointClass);
               MTOMFeature bindingMtomSetting = (MTOMFeature)bindingID.createBuiltinFeatureList().get(MTOMFeature.class);
               if (bindingMtomSetting != null && bindingMtomSetting.isEnabled() ^ ftr.isEnabled()) {
                  throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_MTOM_CONFLICT(bindingID, ftr.isEnabled()), new Object[0]);
               }
            }

            this.add(ftr);
         }
      }

   }

   private static String toJar(String url) {
      if (!url.startsWith("jar:")) {
         return url;
      } else {
         url = url.substring(4);
         return url.substring(0, url.lastIndexOf(33));
      }
   }

   private static WebServiceFeature getWebServiceFeatureBean(Annotation a) {
      WebServiceFeatureAnnotation wsfa = (WebServiceFeatureAnnotation)a.annotationType().getAnnotation(WebServiceFeatureAnnotation.class);
      Class<? extends WebServiceFeature> beanClass = wsfa.bean();
      Constructor ftrCtr = null;
      String[] paramNames = null;
      Constructor[] var6 = beanClass.getConstructors();
      int i = var6.length;

      for(int var8 = 0; var8 < i; ++var8) {
         Constructor con = var6[var8];
         FeatureConstructor ftrCtrAnn = (FeatureConstructor)con.getAnnotation(FeatureConstructor.class);
         if (ftrCtrAnn != null) {
            if (ftrCtr != null) {
               throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(a, beanClass));
            }

            ftrCtr = con;
            paramNames = ftrCtrAnn.value();
         }
      }

      WebServiceFeature bean;
      if (ftrCtr == null) {
         bean = getWebServiceFeatureBeanViaBuilder(a, beanClass);
         if (bean != null) {
            return bean;
         } else {
            throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(a, beanClass));
         }
      } else if (ftrCtr.getParameterTypes().length != paramNames.length) {
         throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(a, beanClass));
      } else {
         try {
            Object[] params = new Object[paramNames.length];

            for(i = 0; i < paramNames.length; ++i) {
               Method m = a.annotationType().getDeclaredMethod(paramNames[i]);
               params[i] = m.invoke(a);
            }

            bean = (WebServiceFeature)ftrCtr.newInstance(params);
            return bean;
         } catch (Exception var11) {
            throw new WebServiceException(var11);
         }
      }
   }

   private static WebServiceFeature getWebServiceFeatureBeanViaBuilder(Annotation annotation, Class<? extends WebServiceFeature> beanClass) {
      try {
         Method featureBuilderMethod = beanClass.getDeclaredMethod("builder");
         Object builder = featureBuilderMethod.invoke(beanClass);
         Method buildMethod = builder.getClass().getDeclaredMethod("build");
         Method[] var5 = builder.getClass().getDeclaredMethods();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Method builderMethod = var5[var7];
            if (!builderMethod.equals(buildMethod)) {
               String methodName = builderMethod.getName();
               Method annotationMethod = annotation.annotationType().getDeclaredMethod(methodName);
               Object annotationFieldValue = annotationMethod.invoke(annotation);
               Object[] arg = new Object[]{annotationFieldValue};
               if (!skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(builderMethod, annotationFieldValue)) {
                  builderMethod.invoke(builder, arg);
               }
            }
         }

         Object result = buildMethod.invoke(builder);
         if (result instanceof WebServiceFeature) {
            return (WebServiceFeature)result;
         } else {
            throw new WebServiceException("Not a WebServiceFeature: " + result);
         }
      } catch (NoSuchMethodException var13) {
         return null;
      } catch (IllegalAccessException var14) {
         throw new WebServiceException(var14);
      } catch (InvocationTargetException var15) {
         throw new WebServiceException(var15);
      }
   }

   private static boolean skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(Method builderMethod, Object annotationFieldValue) {
      Class<?> annotationFieldValueClass = annotationFieldValue.getClass();
      if (!annotationFieldValueClass.isEnum()) {
         return false;
      } else {
         Class<?>[] builderMethodParameterTypes = builderMethod.getParameterTypes();
         if (builderMethodParameterTypes.length != 1) {
            throw new WebServiceException("expected only 1 parameter");
         } else {
            String builderParameterTypeName = builderMethodParameterTypes[0].getName();
            return !builderParameterTypeName.startsWith("com.oracle.webservices.internal.test.features_annotations_enums.apinew") && !builderParameterTypeName.startsWith("com.oracle.webservices.internal.api") ? false : false;
         }
      }
   }

   public Iterator<WebServiceFeature> iterator() {
      return (Iterator)(this.parent != null ? new WebServiceFeatureList.MergedFeatures(this.parent.getFeatures()) : this.wsfeatures.values().iterator());
   }

   @NotNull
   public WebServiceFeature[] toArray() {
      return this.parent != null ? (new WebServiceFeatureList.MergedFeatures(this.parent.getFeatures())).toArray() : (WebServiceFeature[])this.wsfeatures.values().toArray(new WebServiceFeature[0]);
   }

   public boolean isEnabled(@NotNull Class<? extends WebServiceFeature> feature) {
      WebServiceFeature ftr = this.get(feature);
      return ftr != null && ftr.isEnabled();
   }

   public boolean contains(@NotNull Class<? extends WebServiceFeature> feature) {
      WebServiceFeature ftr = this.get(feature);
      return ftr != null;
   }

   @Nullable
   public <F extends WebServiceFeature> F get(@NotNull Class<F> featureType) {
      WebServiceFeature f = (WebServiceFeature)featureType.cast(this.wsfeatures.get(featureType));
      return f == null && this.parent != null ? this.parent.getFeatures().get(featureType) : f;
   }

   public void add(@NotNull WebServiceFeature f) {
      if (this.addNoValidate(f) && this.isValidating) {
         this.validate(f);
      }

   }

   private boolean addNoValidate(@NotNull WebServiceFeature f) {
      if (!this.wsfeatures.containsKey(f.getClass())) {
         this.wsfeatures.put(f.getClass(), f);
         if (f instanceof ImpliesWebServiceFeature) {
            ((ImpliesWebServiceFeature)f).implyFeatures(this);
         }

         return true;
      } else {
         return false;
      }
   }

   public void addAll(@NotNull Iterable<WebServiceFeature> list) {
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         WebServiceFeature f = (WebServiceFeature)var2.next();
         this.add(f);
      }

   }

   void setMTOMEnabled(boolean b) {
      this.wsfeatures.put(MTOMFeature.class, new MTOMFeature(b));
   }

   public boolean equals(Object other) {
      if (!(other instanceof WebServiceFeatureList)) {
         return false;
      } else {
         WebServiceFeatureList w = (WebServiceFeatureList)other;
         return this.wsfeatures.equals(w.wsfeatures) && this.parent == w.parent;
      }
   }

   public String toString() {
      return this.wsfeatures.toString();
   }

   public void mergeFeatures(@NotNull Iterable<WebServiceFeature> features, boolean reportConflicts) {
      Iterator var3 = features.iterator();

      while(var3.hasNext()) {
         WebServiceFeature wsdlFtr = (WebServiceFeature)var3.next();
         if (this.get(wsdlFtr.getClass()) == null) {
            this.add(wsdlFtr);
         } else if (reportConflicts && this.isEnabled(wsdlFtr.getClass()) != wsdlFtr.isEnabled()) {
            LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get(wsdlFtr.getClass()), wsdlFtr));
         }
      }

   }

   public void mergeFeatures(WebServiceFeature[] features, boolean reportConflicts) {
      WebServiceFeature[] var3 = features;
      int var4 = features.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WebServiceFeature wsdlFtr = var3[var5];
         if (this.get(wsdlFtr.getClass()) == null) {
            this.add(wsdlFtr);
         } else if (reportConflicts && this.isEnabled(wsdlFtr.getClass()) != wsdlFtr.isEnabled()) {
            LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get(wsdlFtr.getClass()), wsdlFtr));
         }
      }

   }

   public void mergeFeatures(@NotNull WSDLPort wsdlPort, boolean honorWsdlRequired, boolean reportConflicts) {
      if (!honorWsdlRequired || this.isEnabled(RespectBindingFeature.class)) {
         if (!honorWsdlRequired) {
            this.addAll(wsdlPort.getFeatures());
         } else {
            Iterator var4 = wsdlPort.getFeatures().iterator();

            while(var4.hasNext()) {
               WebServiceFeature wsdlFtr = (WebServiceFeature)var4.next();
               if (this.get(wsdlFtr.getClass()) == null) {
                  try {
                     Method m = wsdlFtr.getClass().getMethod("isRequired");

                     try {
                        boolean required = (Boolean)m.invoke(wsdlFtr);
                        if (required) {
                           this.add(wsdlFtr);
                        }
                     } catch (IllegalAccessException var8) {
                        throw new WebServiceException(var8);
                     } catch (InvocationTargetException var9) {
                        throw new WebServiceException(var9);
                     }
                  } catch (NoSuchMethodException var10) {
                     this.add(wsdlFtr);
                  }
               } else if (reportConflicts && this.isEnabled(wsdlFtr.getClass()) != wsdlFtr.isEnabled()) {
                  LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get(wsdlFtr.getClass()), wsdlFtr));
               }
            }

         }
      }
   }

   public void setParentFeaturedObject(@NotNull WSDLFeaturedObject parent) {
      this.parent = parent;
   }

   @Nullable
   public static <F extends WebServiceFeature> F getFeature(@NotNull WebServiceFeature[] features, @NotNull Class<F> featureType) {
      WebServiceFeature[] var2 = features;
      int var3 = features.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WebServiceFeature f = var2[var4];
         if (f.getClass() == featureType) {
            return f;
         }
      }

      return null;
   }

   public Set<Map.Entry<Class<? extends WebServiceFeature>, WebServiceFeature>> entrySet() {
      return this.wsfeatures.entrySet();
   }

   public WebServiceFeature put(Class<? extends WebServiceFeature> key, WebServiceFeature value) {
      return (WebServiceFeature)this.wsfeatures.put(key, value);
   }

   public static SOAPVersion getSoapVersion(WSFeatureList features) {
      EnvelopeStyleFeature env = (EnvelopeStyleFeature)features.get(EnvelopeStyleFeature.class);
      if (env != null) {
         return SOAPVersion.from(env);
      } else {
         env = (EnvelopeStyleFeature)features.get(EnvelopeStyleFeature.class);
         return env != null ? SOAPVersion.from(env) : null;
      }
   }

   public static boolean isFeatureEnabled(Class<? extends WebServiceFeature> type, WebServiceFeature[] features) {
      WebServiceFeature ftr = getFeature(features, type);
      return ftr != null && ftr.isEnabled();
   }

   public static WebServiceFeature[] toFeatureArray(WSBinding binding) {
      if (!binding.isFeatureEnabled(EnvelopeStyleFeature.class)) {
         WebServiceFeature[] f = new WebServiceFeature[]{binding.getSOAPVersion().toFeature()};
         binding.getFeatures().mergeFeatures(f, false);
      }

      return binding.getFeatures().toArray();
   }

   private final class MergedFeatures implements Iterator<WebServiceFeature> {
      private final Stack<WebServiceFeature> features = new Stack();

      public MergedFeatures(@NotNull WSFeatureList parent) {
         Iterator var3 = WebServiceFeatureList.this.wsfeatures.values().iterator();

         WebServiceFeature f;
         while(var3.hasNext()) {
            f = (WebServiceFeature)var3.next();
            this.features.push(f);
         }

         var3 = parent.iterator();

         while(var3.hasNext()) {
            f = (WebServiceFeature)var3.next();
            if (!WebServiceFeatureList.this.wsfeatures.containsKey(f.getClass())) {
               this.features.push(f);
            }
         }

      }

      public boolean hasNext() {
         return !this.features.empty();
      }

      public WebServiceFeature next() {
         if (!this.features.empty()) {
            return (WebServiceFeature)this.features.pop();
         } else {
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         if (!this.features.empty()) {
            this.features.pop();
         }

      }

      public WebServiceFeature[] toArray() {
         return (WebServiceFeature[])this.features.toArray(new WebServiceFeature[0]);
      }
   }
}
