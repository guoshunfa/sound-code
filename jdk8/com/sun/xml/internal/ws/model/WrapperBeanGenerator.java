package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.org.objectweb.asm.AnnotationVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import com.sun.xml.internal.ws.org.objectweb.asm.FieldVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public class WrapperBeanGenerator {
   private static final Logger LOGGER = Logger.getLogger(WrapperBeanGenerator.class.getName());
   private static final WrapperBeanGenerator.FieldFactory FIELD_FACTORY = new WrapperBeanGenerator.FieldFactory();
   private static final AbstractWrapperBeanGenerator RUNTIME_GENERATOR;

   private static byte[] createBeanImage(String className, String rootName, String rootNS, String typeName, String typeNS, Collection<WrapperBeanGenerator.Field> fields) throws Exception {
      ClassWriter cw = new ClassWriter(0);
      cw.visit(49, 33, replaceDotWithSlash(className), (String)null, "java/lang/Object", (String[])null);
      AnnotationVisitor root = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
      root.visit("name", rootName);
      root.visit("namespace", rootNS);
      root.visitEnd();
      AnnotationVisitor type = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
      type.visit("name", typeName);
      type.visit("namespace", typeNS);
      if (fields.size() > 1) {
         AnnotationVisitor propVisitor = type.visitArray("propOrder");
         Iterator var10 = fields.iterator();

         while(var10.hasNext()) {
            WrapperBeanGenerator.Field field = (WrapperBeanGenerator.Field)var10.next();
            propVisitor.visit("propOrder", field.fieldName);
         }

         propVisitor.visitEnd();
      }

      type.visitEnd();
      Iterator var16 = fields.iterator();

      XmlElement xmlElem;
      while(var16.hasNext()) {
         WrapperBeanGenerator.Field field = (WrapperBeanGenerator.Field)var16.next();
         FieldVisitor fv = cw.visitField(1, field.fieldName, field.asmType.getDescriptor(), field.getSignature(), (Object)null);
         Iterator var12 = field.jaxbAnnotations.iterator();

         while(var12.hasNext()) {
            Annotation ann = (Annotation)var12.next();
            AnnotationVisitor elem;
            if (ann instanceof XmlMimeType) {
               elem = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlMimeType;", true);
               elem.visit("value", ((XmlMimeType)ann).value());
               elem.visitEnd();
            } else if (ann instanceof XmlJavaTypeAdapter) {
               elem = fv.visitAnnotation("Ljavax/xml/bind/annotation/adapters/XmlJavaTypeAdapter;", true);
               elem.visit("value", getASMType(((XmlJavaTypeAdapter)ann).value()));
               elem.visitEnd();
            } else if (ann instanceof XmlAttachmentRef) {
               elem = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlAttachmentRef;", true);
               elem.visitEnd();
            } else if (ann instanceof XmlList) {
               elem = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlList;", true);
               elem.visitEnd();
            } else {
               if (!(ann instanceof XmlElement)) {
                  throw new WebServiceException("Unknown JAXB annotation " + ann);
               }

               elem = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
               xmlElem = (XmlElement)ann;
               elem.visit("name", xmlElem.name());
               elem.visit("namespace", xmlElem.namespace());
               if (xmlElem.nillable()) {
                  elem.visit("nillable", true);
               }

               if (xmlElem.required()) {
                  elem.visit("required", true);
               }

               elem.visitEnd();
            }
         }

         fv.visitEnd();
      }

      MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V");
      mv.visitInsn(177);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
      cw.visitEnd();
      if (LOGGER.isLoggable(Level.FINE)) {
         StringBuilder sb = new StringBuilder();
         sb.append("\n");
         sb.append("@XmlRootElement(name=").append(rootName).append(", namespace=").append(rootNS).append(")");
         sb.append("\n");
         sb.append("@XmlType(name=").append(typeName).append(", namespace=").append(typeNS);
         Iterator var21;
         WrapperBeanGenerator.Field field;
         if (fields.size() > 1) {
            sb.append(", propOrder={");
            var21 = fields.iterator();

            while(var21.hasNext()) {
               field = (WrapperBeanGenerator.Field)var21.next();
               sb.append(" ");
               sb.append(field.fieldName);
            }

            sb.append(" }");
         }

         sb.append(")");
         sb.append("\n");
         sb.append("public class ").append(className).append(" {");
         var21 = fields.iterator();

         while(var21.hasNext()) {
            field = (WrapperBeanGenerator.Field)var21.next();
            sb.append("\n");
            Iterator var23 = field.jaxbAnnotations.iterator();

            while(var23.hasNext()) {
               Annotation ann = (Annotation)var23.next();
               sb.append("\n    ");
               if (ann instanceof XmlMimeType) {
                  sb.append("@XmlMimeType(value=").append(((XmlMimeType)ann).value()).append(")");
               } else if (ann instanceof XmlJavaTypeAdapter) {
                  sb.append("@XmlJavaTypeAdapter(value=").append((Object)getASMType(((XmlJavaTypeAdapter)ann).value())).append(")");
               } else if (ann instanceof XmlAttachmentRef) {
                  sb.append("@XmlAttachmentRef");
               } else if (ann instanceof XmlList) {
                  sb.append("@XmlList");
               } else {
                  if (!(ann instanceof XmlElement)) {
                     throw new WebServiceException("Unknown JAXB annotation " + ann);
                  }

                  xmlElem = (XmlElement)ann;
                  sb.append("\n    ");
                  sb.append("@XmlElement(name=").append(xmlElem.name()).append(", namespace=").append(xmlElem.namespace());
                  if (xmlElem.nillable()) {
                     sb.append(", nillable=true");
                  }

                  if (xmlElem.required()) {
                     sb.append(", required=true");
                  }

                  sb.append(")");
               }
            }

            sb.append("\n    ");
            sb.append("public ");
            if (field.getSignature() == null) {
               sb.append(field.asmType.getDescriptor());
            } else {
               sb.append(field.getSignature());
            }

            sb.append(" ");
            sb.append(field.fieldName);
         }

         sb.append("\n\n}");
         LOGGER.fine(sb.toString());
      }

      return cw.toByteArray();
   }

   private static String replaceDotWithSlash(String name) {
      return name.replace('.', '/');
   }

   static Class createRequestWrapperBean(String className, Method method, QName reqElemName, ClassLoader cl) {
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, (String)"Request Wrapper Class : {0}", (Object)className);
      }

      List requestMembers = RUNTIME_GENERATOR.collectRequestBeanMembers(method);

      byte[] image;
      try {
         image = createBeanImage(className, reqElemName.getLocalPart(), reqElemName.getNamespaceURI(), reqElemName.getLocalPart(), reqElemName.getNamespaceURI(), requestMembers);
      } catch (Exception var7) {
         throw new WebServiceException(var7);
      }

      return Injector.inject(cl, className, image);
   }

   static Class createResponseWrapperBean(String className, Method method, QName resElemName, ClassLoader cl) {
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, (String)"Response Wrapper Class : {0}", (Object)className);
      }

      List responseMembers = RUNTIME_GENERATOR.collectResponseBeanMembers(method);

      byte[] image;
      try {
         image = createBeanImage(className, resElemName.getLocalPart(), resElemName.getNamespaceURI(), resElemName.getLocalPart(), resElemName.getNamespaceURI(), responseMembers);
      } catch (Exception var7) {
         throw new WebServiceException(var7);
      }

      return Injector.inject(cl, className, image);
   }

   private static Type getASMType(java.lang.reflect.Type t) {
      assert t != null;

      if (t instanceof Class) {
         return Type.getType((Class)t);
      } else {
         if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            if (pt.getRawType() instanceof Class) {
               return Type.getType((Class)pt.getRawType());
            }
         }

         if (t instanceof GenericArrayType) {
            return Type.getType(FieldSignature.vms(t));
         } else if (t instanceof WildcardType) {
            return Type.getType(FieldSignature.vms(t));
         } else {
            if (t instanceof TypeVariable) {
               TypeVariable tv = (TypeVariable)t;
               if (tv.getBounds()[0] instanceof Class) {
                  return Type.getType((Class)tv.getBounds()[0]);
               }
            }

            throw new IllegalArgumentException("Not creating ASM Type for type = " + t);
         }
      }
   }

   static Class createExceptionBean(String className, Class exception, String typeNS, String elemName, String elemNS, ClassLoader cl) {
      return createExceptionBean(className, exception, typeNS, elemName, elemNS, cl, true);
   }

   static Class createExceptionBean(String className, Class exception, String typeNS, String elemName, String elemNS, ClassLoader cl, boolean decapitalizeExceptionBeanProperties) {
      Collection fields = RUNTIME_GENERATOR.collectExceptionBeanMembers(exception, decapitalizeExceptionBeanProperties);

      byte[] image;
      try {
         image = createBeanImage(className, elemName, elemNS, exception.getSimpleName(), typeNS, fields);
      } catch (Exception var10) {
         throw new WebServiceException(var10);
      }

      return Injector.inject(cl, className, image);
   }

   static void write(byte[] b, String className) {
      className = className.substring(className.lastIndexOf(".") + 1);

      try {
         FileOutputStream fo = new FileOutputStream(className + ".class");
         fo.write(b);
         fo.flush();
         fo.close();
      } catch (IOException var3) {
         LOGGER.log(Level.INFO, (String)"Error Writing class", (Throwable)var3);
      }

   }

   static {
      RUNTIME_GENERATOR = new WrapperBeanGenerator.RuntimeWrapperBeanGenerator(new RuntimeInlineAnnotationReader(), Utils.REFLECTION_NAVIGATOR, FIELD_FACTORY);
   }

   private static class Field implements Comparable<WrapperBeanGenerator.Field> {
      private final java.lang.reflect.Type reflectType;
      private final Type asmType;
      private final String fieldName;
      private final List<Annotation> jaxbAnnotations;

      Field(String paramName, java.lang.reflect.Type paramType, Type asmType, List<Annotation> jaxbAnnotations) {
         this.reflectType = paramType;
         this.asmType = asmType;
         this.fieldName = paramName;
         this.jaxbAnnotations = jaxbAnnotations;
      }

      String getSignature() {
         if (this.reflectType instanceof Class) {
            return null;
         } else {
            return this.reflectType instanceof TypeVariable ? null : FieldSignature.vms(this.reflectType);
         }
      }

      public int compareTo(WrapperBeanGenerator.Field o) {
         return this.fieldName.compareTo(o.fieldName);
      }
   }

   private static final class FieldFactory implements AbstractWrapperBeanGenerator.BeanMemberFactory<java.lang.reflect.Type, WrapperBeanGenerator.Field> {
      private FieldFactory() {
      }

      public WrapperBeanGenerator.Field createWrapperBeanMember(java.lang.reflect.Type paramType, String paramName, List<Annotation> jaxb) {
         return new WrapperBeanGenerator.Field(paramName, paramType, WrapperBeanGenerator.getASMType(paramType), jaxb);
      }

      // $FF: synthetic method
      FieldFactory(Object x0) {
         this();
      }
   }

   private static final class RuntimeWrapperBeanGenerator extends AbstractWrapperBeanGenerator<java.lang.reflect.Type, Class, Method, WrapperBeanGenerator.Field> {
      protected RuntimeWrapperBeanGenerator(AnnotationReader<java.lang.reflect.Type, Class, ?, Method> annReader, Navigator<java.lang.reflect.Type, Class, ?, Method> nav, AbstractWrapperBeanGenerator.BeanMemberFactory<java.lang.reflect.Type, WrapperBeanGenerator.Field> beanMemberFactory) {
         super(annReader, nav, beanMemberFactory);
      }

      protected java.lang.reflect.Type getSafeType(java.lang.reflect.Type type) {
         return type;
      }

      protected java.lang.reflect.Type getHolderValueType(java.lang.reflect.Type paramType) {
         if (paramType instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)paramType;
            if (p.getRawType().equals(Holder.class)) {
               return p.getActualTypeArguments()[0];
            }
         }

         return null;
      }

      protected boolean isVoidType(java.lang.reflect.Type type) {
         return type == Void.TYPE;
      }
   }
}
