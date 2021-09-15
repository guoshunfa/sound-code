package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.util.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.WebServiceException;

public abstract class AbstractWrapperBeanGenerator<T, C, M, A extends Comparable> {
   private static final Logger LOGGER = Logger.getLogger(AbstractWrapperBeanGenerator.class.getName());
   private static final String RETURN = "return";
   private static final String EMTPY_NAMESPACE_ID = "";
   private static final Class[] jaxbAnns = new Class[]{XmlAttachmentRef.class, XmlMimeType.class, XmlJavaTypeAdapter.class, XmlList.class, XmlElement.class};
   private static final Set<String> skipProperties = new HashSet();
   private final AnnotationReader<T, C, ?, M> annReader;
   private final Navigator<T, C, ?, M> nav;
   private final AbstractWrapperBeanGenerator.BeanMemberFactory<T, A> factory;
   private static final Map<String, String> reservedWords;

   protected AbstractWrapperBeanGenerator(AnnotationReader<T, C, ?, M> annReader, Navigator<T, C, ?, M> nav, AbstractWrapperBeanGenerator.BeanMemberFactory<T, A> factory) {
      this.annReader = annReader;
      this.nav = nav;
      this.factory = factory;
   }

   private List<Annotation> collectJAXBAnnotations(M method) {
      List<Annotation> jaxbAnnotation = new ArrayList();
      Class[] var3 = jaxbAnns;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Class jaxbClass = var3[var5];
         Annotation ann = this.annReader.getMethodAnnotation(jaxbClass, method, (Locatable)null);
         if (ann != null) {
            jaxbAnnotation.add(ann);
         }
      }

      return jaxbAnnotation;
   }

   private List<Annotation> collectJAXBAnnotations(M method, int paramIndex) {
      List<Annotation> jaxbAnnotation = new ArrayList();
      Class[] var4 = jaxbAnns;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Class jaxbClass = var4[var6];
         Annotation ann = this.annReader.getMethodParameterAnnotation(jaxbClass, method, paramIndex, (Locatable)null);
         if (ann != null) {
            jaxbAnnotation.add(ann);
         }
      }

      return jaxbAnnotation;
   }

   protected abstract T getSafeType(T var1);

   protected abstract T getHolderValueType(T var1);

   protected abstract boolean isVoidType(T var1);

   public List<A> collectRequestBeanMembers(M method) {
      List<A> requestMembers = new ArrayList();
      int paramIndex = -1;
      Object[] var4 = this.nav.getMethodParameters(method);
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         T param = var4[var6];
         ++paramIndex;
         WebParam webParam = (WebParam)this.annReader.getMethodParameterAnnotation(WebParam.class, method, paramIndex, (Locatable)null);
         if (webParam == null || !webParam.header() && !webParam.mode().equals(WebParam.Mode.OUT)) {
            T holderType = this.getHolderValueType(param);
            T paramType = holderType != null ? holderType : this.getSafeType(param);
            String paramName = webParam != null && webParam.name().length() > 0 ? webParam.name() : "arg" + paramIndex;
            String paramNamespace = webParam != null && webParam.targetNamespace().length() > 0 ? webParam.targetNamespace() : "";
            List<Annotation> jaxbAnnotation = this.collectJAXBAnnotations(method, paramIndex);
            this.processXmlElement(jaxbAnnotation, paramName, paramNamespace, paramType);
            A member = (Comparable)this.factory.createWrapperBeanMember(paramType, getPropertyName(paramName), jaxbAnnotation);
            requestMembers.add(member);
         }
      }

      return requestMembers;
   }

   public List<A> collectResponseBeanMembers(M method) {
      List<A> responseMembers = new ArrayList();
      String responseElementName = "return";
      String responseNamespace = "";
      boolean isResultHeader = false;
      WebResult webResult = (WebResult)this.annReader.getMethodAnnotation(WebResult.class, method, (Locatable)null);
      if (webResult != null) {
         if (webResult.name().length() > 0) {
            responseElementName = webResult.name();
         }

         if (webResult.targetNamespace().length() > 0) {
            responseNamespace = webResult.targetNamespace();
         }

         isResultHeader = webResult.header();
      }

      T returnType = this.getSafeType(this.nav.getReturnType(method));
      if (!this.isVoidType(returnType) && !isResultHeader) {
         List<Annotation> jaxbRespAnnotations = this.collectJAXBAnnotations(method);
         this.processXmlElement(jaxbRespAnnotations, responseElementName, responseNamespace, returnType);
         responseMembers.add(this.factory.createWrapperBeanMember(returnType, getPropertyName(responseElementName), jaxbRespAnnotations));
      }

      int paramIndex = -1;
      Object[] var9 = this.nav.getMethodParameters(method);
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         T param = var9[var11];
         ++paramIndex;
         T paramType = this.getHolderValueType(param);
         WebParam webParam = (WebParam)this.annReader.getMethodParameterAnnotation(WebParam.class, method, paramIndex, (Locatable)null);
         if (paramType != null && (webParam == null || !webParam.header())) {
            String paramName = webParam != null && webParam.name().length() > 0 ? webParam.name() : "arg" + paramIndex;
            String paramNamespace = webParam != null && webParam.targetNamespace().length() > 0 ? webParam.targetNamespace() : "";
            List<Annotation> jaxbAnnotation = this.collectJAXBAnnotations(method, paramIndex);
            this.processXmlElement(jaxbAnnotation, paramName, paramNamespace, paramType);
            A member = (Comparable)this.factory.createWrapperBeanMember(paramType, getPropertyName(paramName), jaxbAnnotation);
            responseMembers.add(member);
         }
      }

      return responseMembers;
   }

   private void processXmlElement(List<Annotation> jaxb, String elemName, String elemNS, T type) {
      XmlElement elemAnn = null;
      Iterator var6 = jaxb.iterator();

      while(var6.hasNext()) {
         Annotation a = (Annotation)var6.next();
         if (a.annotationType() == XmlElement.class) {
            elemAnn = (XmlElement)a;
            jaxb.remove(a);
            break;
         }
      }

      String name = elemAnn != null && !elemAnn.name().equals("##default") ? elemAnn.name() : elemName;
      String ns = elemAnn != null && !elemAnn.namespace().equals("##default") ? elemAnn.namespace() : elemNS;
      boolean nillable = this.nav.isArray(type) || elemAnn != null && elemAnn.nillable();
      boolean required = elemAnn != null && elemAnn.required();
      AbstractWrapperBeanGenerator.XmlElementHandler handler = new AbstractWrapperBeanGenerator.XmlElementHandler(name, ns, nillable, required);
      XmlElement elem = (XmlElement)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{XmlElement.class}, handler);
      jaxb.add(elem);
   }

   public Collection<A> collectExceptionBeanMembers(C exception) {
      return this.collectExceptionBeanMembers(exception, true);
   }

   public Collection<A> collectExceptionBeanMembers(C exception, boolean decapitalize) {
      TreeMap<String, A> fields = new TreeMap();
      this.getExceptionProperties(exception, fields, decapitalize);
      XmlType xmlType = (XmlType)this.annReader.getClassAnnotation(XmlType.class, exception, (Locatable)null);
      if (xmlType != null) {
         String[] propOrder = xmlType.propOrder();
         if (propOrder.length > 0 && propOrder[0].length() != 0) {
            List<A> list = new ArrayList();
            String[] var7 = propOrder;
            int var8 = propOrder.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String prop = var7[var9];
               A a = (Comparable)fields.get(prop);
               if (a == null) {
                  throw new WebServiceException("Exception " + exception + " has @XmlType and its propOrder contains unknown property " + prop);
               }

               list.add(a);
            }

            return list;
         }
      }

      return fields.values();
   }

   private void getExceptionProperties(C exception, TreeMap<String, A> fields, boolean decapitalize) {
      C sc = this.nav.getSuperClass(exception);
      if (sc != null) {
         this.getExceptionProperties(sc, fields, decapitalize);
      }

      Collection<? extends M> methods = this.nav.getDeclaredMethods(exception);
      Iterator var6 = methods.iterator();

      while(true) {
         Object method;
         String name;
         do {
            do {
               do {
                  do {
                     if (!var6.hasNext()) {
                        return;
                     }

                     method = var6.next();
                  } while(!this.nav.isPublicMethod(method));
               } while(this.nav.isStaticMethod(method) && this.nav.isFinalMethod(method));
            } while(!this.nav.isPublicMethod(method));

            name = this.nav.getMethodName(method);
         } while(!name.startsWith("get") && !name.startsWith("is"));

         if (!skipProperties.contains(name) && !name.equals("get") && !name.equals("is")) {
            T returnType = this.getSafeType(this.nav.getReturnType(method));
            if (this.nav.getMethodParameters(method).length == 0) {
               String fieldName = name.startsWith("get") ? name.substring(3) : name.substring(2);
               if (decapitalize) {
                  fieldName = StringUtils.decapitalize(fieldName);
               }

               fields.put(fieldName, this.factory.createWrapperBeanMember(returnType, fieldName, Collections.emptyList()));
            }
         }
      }
   }

   private static String getPropertyName(String name) {
      String propertyName = BindingHelper.mangleNameToVariableName(name);
      return getJavaReservedVarialbeName(propertyName);
   }

   @NotNull
   private static String getJavaReservedVarialbeName(@NotNull String name) {
      String reservedName = (String)reservedWords.get(name);
      return reservedName == null ? name : reservedName;
   }

   static {
      skipProperties.add("getCause");
      skipProperties.add("getLocalizedMessage");
      skipProperties.add("getClass");
      skipProperties.add("getStackTrace");
      skipProperties.add("getSuppressed");
      reservedWords = new HashMap();
      reservedWords.put("abstract", "_abstract");
      reservedWords.put("assert", "_assert");
      reservedWords.put("boolean", "_boolean");
      reservedWords.put("break", "_break");
      reservedWords.put("byte", "_byte");
      reservedWords.put("case", "_case");
      reservedWords.put("catch", "_catch");
      reservedWords.put("char", "_char");
      reservedWords.put("class", "_class");
      reservedWords.put("const", "_const");
      reservedWords.put("continue", "_continue");
      reservedWords.put("default", "_default");
      reservedWords.put("do", "_do");
      reservedWords.put("double", "_double");
      reservedWords.put("else", "_else");
      reservedWords.put("extends", "_extends");
      reservedWords.put("false", "_false");
      reservedWords.put("final", "_final");
      reservedWords.put("finally", "_finally");
      reservedWords.put("float", "_float");
      reservedWords.put("for", "_for");
      reservedWords.put("goto", "_goto");
      reservedWords.put("if", "_if");
      reservedWords.put("implements", "_implements");
      reservedWords.put("import", "_import");
      reservedWords.put("instanceof", "_instanceof");
      reservedWords.put("int", "_int");
      reservedWords.put("interface", "_interface");
      reservedWords.put("long", "_long");
      reservedWords.put("native", "_native");
      reservedWords.put("new", "_new");
      reservedWords.put("null", "_null");
      reservedWords.put("package", "_package");
      reservedWords.put("private", "_private");
      reservedWords.put("protected", "_protected");
      reservedWords.put("public", "_public");
      reservedWords.put("return", "_return");
      reservedWords.put("short", "_short");
      reservedWords.put("static", "_static");
      reservedWords.put("strictfp", "_strictfp");
      reservedWords.put("super", "_super");
      reservedWords.put("switch", "_switch");
      reservedWords.put("synchronized", "_synchronized");
      reservedWords.put("this", "_this");
      reservedWords.put("throw", "_throw");
      reservedWords.put("throws", "_throws");
      reservedWords.put("transient", "_transient");
      reservedWords.put("true", "_true");
      reservedWords.put("try", "_try");
      reservedWords.put("void", "_void");
      reservedWords.put("volatile", "_volatile");
      reservedWords.put("while", "_while");
      reservedWords.put("enum", "_enum");
   }

   private static class XmlElementHandler implements InvocationHandler {
      private String name;
      private String namespace;
      private boolean nillable;
      private boolean required;

      XmlElementHandler(String name, String namespace, boolean nillable, boolean required) {
         this.name = name;
         this.namespace = namespace;
         this.nillable = nillable;
         this.required = required;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         String methodName = method.getName();
         if (methodName.equals("name")) {
            return this.name;
         } else if (methodName.equals("namespace")) {
            return this.namespace;
         } else if (methodName.equals("nillable")) {
            return this.nillable;
         } else if (methodName.equals("required")) {
            return this.required;
         } else {
            throw new WebServiceException("Not handling " + methodName);
         }
      }
   }

   public interface BeanMemberFactory<T, A> {
      A createWrapperBeanMember(T var1, String var2, List<Annotation> var3);
   }
}
