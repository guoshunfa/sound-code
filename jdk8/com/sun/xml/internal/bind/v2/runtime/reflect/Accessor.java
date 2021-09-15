package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedAccessorFactory;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.awt.Image;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;

public abstract class Accessor<BeanT, ValueT> implements Receiver {
   public final Class<ValueT> valueType;
   private static List<Class> nonAbstractableClasses = Arrays.asList(Object.class, Calendar.class, Duration.class, XMLGregorianCalendar.class, Image.class, DataHandler.class, Source.class, Date.class, File.class, URI.class, URL.class, Class.class, String.class, Source.class);
   private static boolean accessWarned = false;
   private static final Accessor ERROR = new Accessor<Object, Object>(Object.class) {
      public Object get(Object o) {
         return null;
      }

      public void set(Object o, Object o1) {
      }
   };
   public static final Accessor<JAXBElement, Object> JAXB_ELEMENT_VALUE = new Accessor<JAXBElement, Object>(Object.class) {
      public Object get(JAXBElement jaxbElement) {
         return jaxbElement.getValue();
      }

      public void set(JAXBElement jaxbElement, Object o) {
         jaxbElement.setValue(o);
      }
   };
   private static final Map<Class, Object> uninitializedValues = new HashMap();

   public Class<ValueT> getValueType() {
      return this.valueType;
   }

   protected Accessor(Class<ValueT> valueType) {
      this.valueType = valueType;
   }

   public Accessor<BeanT, ValueT> optimize(@Nullable JAXBContextImpl context) {
      return this;
   }

   public abstract ValueT get(BeanT var1) throws AccessorException;

   public abstract void set(BeanT var1, ValueT var2) throws AccessorException;

   public Object getUnadapted(BeanT bean) throws AccessorException {
      return this.get(bean);
   }

   public boolean isAdapted() {
      return false;
   }

   public void setUnadapted(BeanT bean, Object value) throws AccessorException {
      this.set(bean, value);
   }

   public void receive(UnmarshallingContext.State state, Object o) throws SAXException {
      try {
         this.set(state.getTarget(), o);
      } catch (AccessorException var4) {
         Loader.handleGenericException(var4, true);
      } catch (IllegalAccessError var5) {
         Loader.handleGenericError(var5);
      }

   }

   public boolean isValueTypeAbstractable() {
      return !nonAbstractableClasses.contains(this.getValueType());
   }

   public boolean isAbstractable(Class clazz) {
      return !nonAbstractableClasses.contains(clazz);
   }

   public final <T> Accessor<BeanT, T> adapt(Class<T> targetType, Class<? extends XmlAdapter<T, ValueT>> adapter) {
      return new AdaptedAccessor(targetType, this, adapter);
   }

   public final <T> Accessor<BeanT, T> adapt(Adapter<Type, Class> adapter) {
      return new AdaptedAccessor((Class)Utils.REFLECTION_NAVIGATOR.erasure(adapter.defaultType), this, (Class)adapter.adapterType);
   }

   public static <A, B> Accessor<A, B> getErrorInstance() {
      return ERROR;
   }

   static {
      uninitializedValues.put(Byte.TYPE, (byte)0);
      uninitializedValues.put(Boolean.TYPE, false);
      uninitializedValues.put(Character.TYPE, '\u0000');
      uninitializedValues.put(Float.TYPE, 0.0F);
      uninitializedValues.put(Double.TYPE, 0.0D);
      uninitializedValues.put(Integer.TYPE, 0);
      uninitializedValues.put(Long.TYPE, 0L);
      uninitializedValues.put(Short.TYPE, Short.valueOf((short)0));
   }

   public static class SetterOnlyReflection<BeanT, ValueT> extends Accessor.GetterSetterReflection<BeanT, ValueT> {
      public SetterOnlyReflection(Method setter) {
         super((Method)null, setter);
      }

      public ValueT get(BeanT bean) throws AccessorException {
         throw new AccessorException(Messages.NO_GETTER.format(this.setter.toString()));
      }
   }

   public static class GetterOnlyReflection<BeanT, ValueT> extends Accessor.GetterSetterReflection<BeanT, ValueT> {
      public GetterOnlyReflection(Method getter) {
         super(getter, (Method)null);
      }

      public void set(BeanT bean, ValueT value) throws AccessorException {
         throw new AccessorException(Messages.NO_SETTER.format(this.getter.toString()));
      }
   }

   public static class GetterSetterReflection<BeanT, ValueT> extends Accessor<BeanT, ValueT> {
      public final Method getter;
      public final Method setter;
      private static final Logger logger = Util.getClassLogger();

      public GetterSetterReflection(Method getter, Method setter) {
         super(getter != null ? getter.getReturnType() : setter.getParameterTypes()[0]);
         this.getter = getter;
         this.setter = setter;
         if (getter != null) {
            this.makeAccessible(getter);
         }

         if (setter != null) {
            this.makeAccessible(setter);
         }

      }

      private void makeAccessible(Method m) {
         if (!Modifier.isPublic(m.getModifiers()) || !Modifier.isPublic(m.getDeclaringClass().getModifiers())) {
            try {
               m.setAccessible(true);
            } catch (SecurityException var3) {
               if (!Accessor.accessWarned) {
                  logger.log(Level.WARNING, (String)Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(m.getDeclaringClass().getName(), m.getName()), (Throwable)var3);
               }

               Accessor.accessWarned = true;
            }
         }

      }

      public ValueT get(BeanT bean) throws AccessorException {
         try {
            return this.getter.invoke(bean);
         } catch (IllegalAccessException var3) {
            throw new IllegalAccessError(var3.getMessage());
         } catch (InvocationTargetException var4) {
            throw this.handleInvocationTargetException(var4);
         }
      }

      public void set(BeanT bean, ValueT value) throws AccessorException {
         try {
            if (value == null) {
               value = Accessor.uninitializedValues.get(this.valueType);
            }

            this.setter.invoke(bean, value);
         } catch (IllegalAccessException var4) {
            throw new IllegalAccessError(var4.getMessage());
         } catch (InvocationTargetException var5) {
            throw this.handleInvocationTargetException(var5);
         }
      }

      private AccessorException handleInvocationTargetException(InvocationTargetException e) {
         Throwable t = e.getTargetException();
         if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
         } else if (t instanceof Error) {
            throw (Error)t;
         } else {
            return new AccessorException(t);
         }
      }

      public Accessor<BeanT, ValueT> optimize(JAXBContextImpl context) {
         if (this.getter != null && this.setter != null) {
            if (context != null && context.fastBoot) {
               return this;
            } else {
               Accessor<BeanT, ValueT> acc = OptimizedAccessorFactory.get(this.getter, this.setter);
               return (Accessor)(acc != null ? acc : this);
            }
         } else {
            return this;
         }
      }
   }

   public static final class ReadOnlyFieldReflection<BeanT, ValueT> extends Accessor.FieldReflection<BeanT, ValueT> {
      public ReadOnlyFieldReflection(Field f, boolean supressAccessorWarnings) {
         super(f, supressAccessorWarnings);
      }

      public ReadOnlyFieldReflection(Field f) {
         super(f);
      }

      public void set(BeanT bean, ValueT value) {
      }

      public Accessor<BeanT, ValueT> optimize(JAXBContextImpl context) {
         return this;
      }
   }

   public static class FieldReflection<BeanT, ValueT> extends Accessor<BeanT, ValueT> {
      public final Field f;
      private static final Logger logger = Util.getClassLogger();

      public FieldReflection(Field f) {
         this(f, false);
      }

      public FieldReflection(Field f, boolean supressAccessorWarnings) {
         super(f.getType());
         this.f = f;
         int mod = f.getModifiers();
         if (!Modifier.isPublic(mod) || Modifier.isFinal(mod) || !Modifier.isPublic(f.getDeclaringClass().getModifiers())) {
            try {
               f.setAccessible(true);
            } catch (SecurityException var5) {
               if (!Accessor.accessWarned && !supressAccessorWarnings) {
                  logger.log(Level.WARNING, (String)Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(f.getDeclaringClass().getName(), f.getName()), (Throwable)var5);
               }

               Accessor.accessWarned = true;
            }
         }

      }

      public ValueT get(BeanT bean) {
         try {
            return this.f.get(bean);
         } catch (IllegalAccessException var3) {
            throw new IllegalAccessError(var3.getMessage());
         }
      }

      public void set(BeanT bean, ValueT value) {
         try {
            if (value == null) {
               value = Accessor.uninitializedValues.get(this.valueType);
            }

            this.f.set(bean, value);
         } catch (IllegalAccessException var4) {
            throw new IllegalAccessError(var4.getMessage());
         }
      }

      public Accessor<BeanT, ValueT> optimize(JAXBContextImpl context) {
         if (context != null && context.fastBoot) {
            return this;
         } else {
            Accessor<BeanT, ValueT> acc = OptimizedAccessorFactory.get(this.f);
            return (Accessor)(acc != null ? acc : this);
         }
      }
   }
}
