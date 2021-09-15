package java.beans;

import com.sun.beans.TypeResolver;
import com.sun.beans.WeakCache;
import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.MethodFinder;
import java.awt.Component;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.TreeMap;
import sun.reflect.misc.ReflectUtil;

public class Introspector {
   public static final int USE_ALL_BEANINFO = 1;
   public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
   public static final int IGNORE_ALL_BEANINFO = 3;
   private static final WeakCache<Class<?>, Method[]> declaredMethodCache = new WeakCache();
   private Class<?> beanClass;
   private BeanInfo explicitBeanInfo;
   private BeanInfo superBeanInfo;
   private BeanInfo[] additionalBeanInfo;
   private boolean propertyChangeSource = false;
   private static Class<EventListener> eventListenerType = EventListener.class;
   private String defaultEventName;
   private String defaultPropertyName;
   private int defaultEventIndex = -1;
   private int defaultPropertyIndex = -1;
   private Map<String, MethodDescriptor> methods;
   private Map<String, PropertyDescriptor> properties;
   private Map<String, EventSetDescriptor> events;
   private static final EventSetDescriptor[] EMPTY_EVENTSETDESCRIPTORS = new EventSetDescriptor[0];
   static final String ADD_PREFIX = "add";
   static final String REMOVE_PREFIX = "remove";
   static final String GET_PREFIX = "get";
   static final String SET_PREFIX = "set";
   static final String IS_PREFIX = "is";
   private HashMap<String, List<PropertyDescriptor>> pdStore = new HashMap();

   public static BeanInfo getBeanInfo(Class<?> var0) throws IntrospectionException {
      if (!ReflectUtil.isPackageAccessible(var0)) {
         return (new Introspector(var0, (Class)null, 1)).getBeanInfo();
      } else {
         ThreadGroupContext var1 = ThreadGroupContext.getContext();
         BeanInfo var2;
         synchronized(declaredMethodCache) {
            var2 = var1.getBeanInfo(var0);
         }

         if (var2 == null) {
            var2 = (new Introspector(var0, (Class)null, 1)).getBeanInfo();
            synchronized(declaredMethodCache) {
               var1.putBeanInfo(var0, var2);
            }
         }

         return var2;
      }
   }

   public static BeanInfo getBeanInfo(Class<?> var0, int var1) throws IntrospectionException {
      return getBeanInfo(var0, (Class)null, var1);
   }

   public static BeanInfo getBeanInfo(Class<?> var0, Class<?> var1) throws IntrospectionException {
      return getBeanInfo(var0, var1, 1);
   }

   public static BeanInfo getBeanInfo(Class<?> var0, Class<?> var1, int var2) throws IntrospectionException {
      BeanInfo var3;
      if (var1 == null && var2 == 1) {
         var3 = getBeanInfo(var0);
      } else {
         var3 = (new Introspector(var0, var1, var2)).getBeanInfo();
      }

      return var3;
   }

   public static String decapitalize(String var0) {
      if (var0 != null && var0.length() != 0) {
         if (var0.length() > 1 && Character.isUpperCase(var0.charAt(1)) && Character.isUpperCase(var0.charAt(0))) {
            return var0;
         } else {
            char[] var1 = var0.toCharArray();
            var1[0] = Character.toLowerCase(var1[0]);
            return new String(var1);
         }
      } else {
         return var0;
      }
   }

   public static String[] getBeanInfoSearchPath() {
      return ThreadGroupContext.getContext().getBeanInfoFinder().getPackages();
   }

   public static void setBeanInfoSearchPath(String[] var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPropertiesAccess();
      }

      ThreadGroupContext.getContext().getBeanInfoFinder().setPackages(var0);
   }

   public static void flushCaches() {
      synchronized(declaredMethodCache) {
         ThreadGroupContext.getContext().clearBeanInfoCache();
         declaredMethodCache.clear();
      }
   }

   public static void flushFromCaches(Class<?> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         synchronized(declaredMethodCache) {
            ThreadGroupContext.getContext().removeBeanInfo(var0);
            declaredMethodCache.put(var0, (Object)null);
         }
      }
   }

   private Introspector(Class<?> var1, Class<?> var2, int var3) throws IntrospectionException {
      this.beanClass = var1;
      if (var2 != null) {
         boolean var4 = false;

         for(Class var5 = var1.getSuperclass(); var5 != null; var5 = var5.getSuperclass()) {
            if (var5 == var2) {
               var4 = true;
            }
         }

         if (!var4) {
            throw new IntrospectionException(var2.getName() + " not superclass of " + var1.getName());
         }
      }

      if (var3 == 1) {
         this.explicitBeanInfo = findExplicitBeanInfo(var1);
      }

      Class var6 = var1.getSuperclass();
      if (var6 != var2) {
         int var7 = var3;
         if (var3 == 2) {
            var7 = 1;
         }

         this.superBeanInfo = getBeanInfo(var6, var2, var7);
      }

      if (this.explicitBeanInfo != null) {
         this.additionalBeanInfo = this.explicitBeanInfo.getAdditionalBeanInfo();
      }

      if (this.additionalBeanInfo == null) {
         this.additionalBeanInfo = new BeanInfo[0];
      }

   }

   private BeanInfo getBeanInfo() throws IntrospectionException {
      BeanDescriptor var1 = this.getTargetBeanDescriptor();
      MethodDescriptor[] var2 = this.getTargetMethodInfo();
      EventSetDescriptor[] var3 = this.getTargetEventInfo();
      PropertyDescriptor[] var4 = this.getTargetPropertyInfo();
      int var5 = this.getTargetDefaultEventIndex();
      int var6 = this.getTargetDefaultPropertyIndex();
      return new GenericBeanInfo(var1, var3, var5, var4, var6, var2, this.explicitBeanInfo);
   }

   private static BeanInfo findExplicitBeanInfo(Class<?> var0) {
      return (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(var0);
   }

   private PropertyDescriptor[] getTargetPropertyInfo() {
      PropertyDescriptor[] var1 = null;
      if (this.explicitBeanInfo != null) {
         var1 = this.getPropertyDescriptors(this.explicitBeanInfo);
      }

      if (var1 == null && this.superBeanInfo != null) {
         this.addPropertyDescriptors(this.getPropertyDescriptors(this.superBeanInfo));
      }

      for(int var2 = 0; var2 < this.additionalBeanInfo.length; ++var2) {
         this.addPropertyDescriptors(this.additionalBeanInfo[var2].getPropertyDescriptors());
      }

      int var3;
      if (var1 != null) {
         this.addPropertyDescriptors(var1);
      } else {
         Method[] var13 = getPublicDeclaredMethods(this.beanClass);

         for(var3 = 0; var3 < var13.length; ++var3) {
            Method var4 = var13[var3];
            if (var4 != null) {
               int var5 = var4.getModifiers();
               if (!Modifier.isStatic(var5)) {
                  String var6 = var4.getName();
                  Class[] var7 = var4.getParameterTypes();
                  Class var8 = var4.getReturnType();
                  int var9 = var7.length;
                  Object var10 = null;
                  if (var6.length() > 3 || var6.startsWith("is")) {
                     try {
                        if (var9 == 0) {
                           if (var6.startsWith("get")) {
                              var10 = new PropertyDescriptor(this.beanClass, var6.substring(3), var4, (Method)null);
                           } else if (var8 == Boolean.TYPE && var6.startsWith("is")) {
                              var10 = new PropertyDescriptor(this.beanClass, var6.substring(2), var4, (Method)null);
                           }
                        } else if (var9 == 1) {
                           if (Integer.TYPE.equals(var7[0]) && var6.startsWith("get")) {
                              var10 = new IndexedPropertyDescriptor(this.beanClass, var6.substring(3), (Method)null, (Method)null, var4, (Method)null);
                           } else if (Void.TYPE.equals(var8) && var6.startsWith("set")) {
                              var10 = new PropertyDescriptor(this.beanClass, var6.substring(3), (Method)null, var4);
                              if (this.throwsException(var4, PropertyVetoException.class)) {
                                 ((PropertyDescriptor)var10).setConstrained(true);
                              }
                           }
                        } else if (var9 == 2 && Void.TYPE.equals(var8) && Integer.TYPE.equals(var7[0]) && var6.startsWith("set")) {
                           var10 = new IndexedPropertyDescriptor(this.beanClass, var6.substring(3), (Method)null, (Method)null, (Method)null, var4);
                           if (this.throwsException(var4, PropertyVetoException.class)) {
                              ((PropertyDescriptor)var10).setConstrained(true);
                           }
                        }
                     } catch (IntrospectionException var12) {
                        var10 = null;
                     }

                     if (var10 != null) {
                        if (this.propertyChangeSource) {
                           ((PropertyDescriptor)var10).setBound(true);
                        }

                        this.addPropertyDescriptor((PropertyDescriptor)var10);
                     }
                  }
               }
            }
         }
      }

      this.processPropertyDescriptors();
      PropertyDescriptor[] var14 = (PropertyDescriptor[])this.properties.values().toArray(new PropertyDescriptor[this.properties.size()]);
      if (this.defaultPropertyName != null) {
         for(var3 = 0; var3 < var14.length; ++var3) {
            if (this.defaultPropertyName.equals(var14[var3].getName())) {
               this.defaultPropertyIndex = var3;
            }
         }
      }

      return var14;
   }

   private void addPropertyDescriptor(PropertyDescriptor var1) {
      String var2 = ((PropertyDescriptor)var1).getName();
      Object var3 = (List)this.pdStore.get(var2);
      if (var3 == null) {
         var3 = new ArrayList();
         this.pdStore.put(var2, var3);
      }

      if (this.beanClass != ((PropertyDescriptor)var1).getClass0()) {
         Method var4 = ((PropertyDescriptor)var1).getReadMethod();
         Method var5 = ((PropertyDescriptor)var1).getWriteMethod();
         boolean var6 = true;
         if (var4 != null) {
            var6 = var6 && var4.getGenericReturnType() instanceof Class;
         }

         if (var5 != null) {
            var6 = var6 && var5.getGenericParameterTypes()[0] instanceof Class;
         }

         if (var1 instanceof IndexedPropertyDescriptor) {
            IndexedPropertyDescriptor var7 = (IndexedPropertyDescriptor)var1;
            Method var8 = var7.getIndexedReadMethod();
            Method var9 = var7.getIndexedWriteMethod();
            if (var8 != null) {
               var6 = var6 && var8.getGenericReturnType() instanceof Class;
            }

            if (var9 != null) {
               var6 = var6 && var9.getGenericParameterTypes()[1] instanceof Class;
            }

            if (!var6) {
               var1 = new IndexedPropertyDescriptor(var7);
               ((PropertyDescriptor)var1).updateGenericsFor(this.beanClass);
            }
         } else if (!var6) {
            var1 = new PropertyDescriptor((PropertyDescriptor)var1);
            ((PropertyDescriptor)var1).updateGenericsFor(this.beanClass);
         }
      }

      ((List)var3).add(var1);
   }

   private void addPropertyDescriptors(PropertyDescriptor[] var1) {
      if (var1 != null) {
         PropertyDescriptor[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PropertyDescriptor var5 = var2[var4];
            this.addPropertyDescriptor(var5);
         }
      }

   }

   private PropertyDescriptor[] getPropertyDescriptors(BeanInfo var1) {
      PropertyDescriptor[] var2 = var1.getPropertyDescriptors();
      int var3 = var1.getDefaultPropertyIndex();
      if (0 <= var3 && var3 < var2.length) {
         this.defaultPropertyName = var2[var3].getName();
      }

      return var2;
   }

   private void processPropertyDescriptors() {
      if (this.properties == null) {
         this.properties = new TreeMap();
      }

      Iterator var8 = this.pdStore.values().iterator();

      while(var8.hasNext()) {
         PropertyDescriptor var2 = null;
         PropertyDescriptor var3 = null;
         PropertyDescriptor var4 = null;
         IndexedPropertyDescriptor var5 = null;
         IndexedPropertyDescriptor var6 = null;
         IndexedPropertyDescriptor var7 = null;
         List var1 = (List)var8.next();

         int var9;
         for(var9 = 0; var9 < var1.size(); ++var9) {
            var2 = (PropertyDescriptor)var1.get(var9);
            if (var2 instanceof IndexedPropertyDescriptor) {
               var5 = (IndexedPropertyDescriptor)var2;
               if (var5.getIndexedReadMethod() != null) {
                  if (var6 != null) {
                     var6 = new IndexedPropertyDescriptor(var6, var5);
                  } else {
                     var6 = var5;
                  }
               }
            } else if (var2.getReadMethod() != null) {
               String var10 = var2.getReadMethod().getName();
               if (var3 != null) {
                  String var11 = var3.getReadMethod().getName();
                  if (var11.equals(var10) || !var11.startsWith("is")) {
                     var3 = new PropertyDescriptor(var3, var2);
                  }
               } else {
                  var3 = var2;
               }
            }
         }

         for(var9 = 0; var9 < var1.size(); ++var9) {
            var2 = (PropertyDescriptor)var1.get(var9);
            if (var2 instanceof IndexedPropertyDescriptor) {
               var5 = (IndexedPropertyDescriptor)var2;
               if (var5.getIndexedWriteMethod() != null) {
                  if (var6 != null) {
                     if (isAssignable(var6.getIndexedPropertyType(), var5.getIndexedPropertyType())) {
                        if (var7 != null) {
                           var7 = new IndexedPropertyDescriptor(var7, var5);
                        } else {
                           var7 = var5;
                        }
                     }
                  } else if (var7 != null) {
                     var7 = new IndexedPropertyDescriptor(var7, var5);
                  } else {
                     var7 = var5;
                  }
               }
            } else if (var2.getWriteMethod() != null) {
               if (var3 != null) {
                  if (isAssignable(var3.getPropertyType(), var2.getPropertyType())) {
                     if (var4 != null) {
                        var4 = new PropertyDescriptor(var4, var2);
                     } else {
                        var4 = var2;
                     }
                  }
               } else if (var4 != null) {
                  var4 = new PropertyDescriptor(var4, var2);
               } else {
                  var4 = var2;
               }
            }
         }

         Object var12 = null;
         var5 = null;
         if (var6 != null && var7 != null) {
            if (var3 != var4 && var3 != null) {
               if (var4 == null) {
                  var2 = var3;
               } else if (var4 instanceof IndexedPropertyDescriptor) {
                  var2 = this.mergePropertyWithIndexedProperty(var3, (IndexedPropertyDescriptor)var4);
               } else if (var3 instanceof IndexedPropertyDescriptor) {
                  var2 = this.mergePropertyWithIndexedProperty(var4, (IndexedPropertyDescriptor)var3);
               } else {
                  var2 = this.mergePropertyDescriptor(var3, var4);
               }
            } else {
               var2 = var4;
            }

            if (var6 == var7) {
               var5 = var6;
            } else {
               var5 = this.mergePropertyDescriptor(var6, var7);
            }

            if (var2 == null) {
               var12 = var5;
            } else {
               Class var13 = var2.getPropertyType();
               Class var14 = var5.getIndexedPropertyType();
               if (var13.isArray() && var13.getComponentType() == var14) {
                  var12 = var2.getClass0().isAssignableFrom(var5.getClass0()) ? new IndexedPropertyDescriptor(var2, var5) : new IndexedPropertyDescriptor(var5, var2);
               } else if (var2.getClass0().isAssignableFrom(var5.getClass0())) {
                  var12 = var2.getClass0().isAssignableFrom(var5.getClass0()) ? new PropertyDescriptor(var2, var5) : new PropertyDescriptor(var5, var2);
               } else {
                  var12 = var5;
               }
            }
         } else if (var3 != null && var4 != null) {
            if (var6 != null) {
               var3 = this.mergePropertyWithIndexedProperty(var3, var6);
            }

            if (var7 != null) {
               var4 = this.mergePropertyWithIndexedProperty(var4, var7);
            }

            if (var3 == var4) {
               var12 = var3;
            } else if (var4 instanceof IndexedPropertyDescriptor) {
               var12 = this.mergePropertyWithIndexedProperty(var3, (IndexedPropertyDescriptor)var4);
            } else if (var3 instanceof IndexedPropertyDescriptor) {
               var12 = this.mergePropertyWithIndexedProperty(var4, (IndexedPropertyDescriptor)var3);
            } else {
               var12 = this.mergePropertyDescriptor(var3, var4);
            }
         } else if (var7 != null) {
            var12 = var7;
            if (var4 != null) {
               var12 = this.mergePropertyDescriptor(var7, var4);
            }

            if (var3 != null) {
               var12 = this.mergePropertyDescriptor(var7, var3);
            }
         } else if (var6 != null) {
            var12 = var6;
            if (var3 != null) {
               var12 = this.mergePropertyDescriptor(var6, var3);
            }

            if (var4 != null) {
               var12 = this.mergePropertyDescriptor(var6, var4);
            }
         } else if (var4 != null) {
            var12 = var4;
         } else if (var3 != null) {
            var12 = var3;
         }

         if (var12 instanceof IndexedPropertyDescriptor) {
            var5 = (IndexedPropertyDescriptor)var12;
            if (var5.getIndexedReadMethod() == null && var5.getIndexedWriteMethod() == null) {
               var12 = new PropertyDescriptor(var5);
            }
         }

         if (var12 == null && var1.size() > 0) {
            var12 = (PropertyDescriptor)var1.get(0);
         }

         if (var12 != null) {
            this.properties.put(((PropertyDescriptor)var12).getName(), var12);
         }
      }

   }

   private static boolean isAssignable(Class<?> var0, Class<?> var1) {
      return var0 != null && var1 != null ? var0.isAssignableFrom(var1) : var0 == var1;
   }

   private PropertyDescriptor mergePropertyWithIndexedProperty(PropertyDescriptor var1, IndexedPropertyDescriptor var2) {
      Class var3 = var1.getPropertyType();
      if (var3.isArray() && var3.getComponentType() == var2.getIndexedPropertyType()) {
         return var1.getClass0().isAssignableFrom(var2.getClass0()) ? new IndexedPropertyDescriptor(var1, var2) : new IndexedPropertyDescriptor(var2, var1);
      } else {
         return var1;
      }
   }

   private PropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor var1, PropertyDescriptor var2) {
      Object var3 = null;
      Class var4 = var2.getPropertyType();
      Class var5 = var1.getIndexedPropertyType();
      if (var4.isArray() && var4.getComponentType() == var5) {
         if (var2.getClass0().isAssignableFrom(var1.getClass0())) {
            var3 = new IndexedPropertyDescriptor(var2, var1);
         } else {
            var3 = new IndexedPropertyDescriptor(var1, var2);
         }
      } else if (var1.getReadMethod() == null && var1.getWriteMethod() == null) {
         if (var2.getClass0().isAssignableFrom(var1.getClass0())) {
            var3 = new PropertyDescriptor(var2, var1);
         } else {
            var3 = new PropertyDescriptor(var1, var2);
         }
      } else if (var2.getClass0().isAssignableFrom(var1.getClass0())) {
         var3 = var1;
      } else {
         var3 = var2;
         Method var6 = var2.getWriteMethod();
         Method var7 = var2.getReadMethod();
         if (var7 == null && var6 != null) {
            var7 = findMethod(var2.getClass0(), "get" + NameGenerator.capitalize(var2.getName()), 0);
            if (var7 != null) {
               try {
                  ((PropertyDescriptor)var3).setReadMethod(var7);
               } catch (IntrospectionException var10) {
               }
            }
         }

         if (var6 == null && var7 != null) {
            var6 = findMethod(var2.getClass0(), "set" + NameGenerator.capitalize(var2.getName()), 1, new Class[]{FeatureDescriptor.getReturnType(var2.getClass0(), var7)});
            if (var6 != null) {
               try {
                  ((PropertyDescriptor)var3).setWriteMethod(var6);
               } catch (IntrospectionException var9) {
               }
            }
         }
      }

      return (PropertyDescriptor)var3;
   }

   private PropertyDescriptor mergePropertyDescriptor(PropertyDescriptor var1, PropertyDescriptor var2) {
      return var1.getClass0().isAssignableFrom(var2.getClass0()) ? new PropertyDescriptor(var1, var2) : new PropertyDescriptor(var2, var1);
   }

   private IndexedPropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor var1, IndexedPropertyDescriptor var2) {
      return var1.getClass0().isAssignableFrom(var2.getClass0()) ? new IndexedPropertyDescriptor(var1, var2) : new IndexedPropertyDescriptor(var2, var1);
   }

   private EventSetDescriptor[] getTargetEventInfo() throws IntrospectionException {
      if (this.events == null) {
         this.events = new HashMap();
      }

      EventSetDescriptor[] var1 = null;
      int var2;
      if (this.explicitBeanInfo != null) {
         var1 = this.explicitBeanInfo.getEventSetDescriptors();
         var2 = this.explicitBeanInfo.getDefaultEventIndex();
         if (var2 >= 0 && var2 < var1.length) {
            this.defaultEventName = var1[var2].getName();
         }
      }

      int var3;
      EventSetDescriptor[] var17;
      if (var1 == null && this.superBeanInfo != null) {
         var17 = this.superBeanInfo.getEventSetDescriptors();

         for(var3 = 0; var3 < var17.length; ++var3) {
            this.addEvent(var17[var3]);
         }

         var3 = this.superBeanInfo.getDefaultEventIndex();
         if (var3 >= 0 && var3 < var17.length) {
            this.defaultEventName = var17[var3].getName();
         }
      }

      for(var2 = 0; var2 < this.additionalBeanInfo.length; ++var2) {
         EventSetDescriptor[] var18 = this.additionalBeanInfo[var2].getEventSetDescriptors();
         if (var18 != null) {
            for(int var4 = 0; var4 < var18.length; ++var4) {
               this.addEvent(var18[var4]);
            }
         }
      }

      if (var1 != null) {
         for(var2 = 0; var2 < var1.length; ++var2) {
            this.addEvent(var1[var2]);
         }
      } else {
         Method[] var19 = getPublicDeclaredMethods(this.beanClass);
         HashMap var20 = null;
         HashMap var21 = null;
         HashMap var5 = null;

         Class var12;
         for(int var6 = 0; var6 < var19.length; ++var6) {
            Method var7 = var19[var6];
            if (var7 != null) {
               int var8 = var7.getModifiers();
               if (!Modifier.isStatic(var8)) {
                  String var9 = var7.getName();
                  if (var9.startsWith("add") || var9.startsWith("remove") || var9.startsWith("get")) {
                     Class var10;
                     Type[] var11;
                     String var13;
                     if (var9.startsWith("add")) {
                        var10 = var7.getReturnType();
                        if (var10 == Void.TYPE) {
                           var11 = var7.getGenericParameterTypes();
                           if (var11.length == 1) {
                              var12 = TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, var11[0]));
                              if (isSubclass(var12, eventListenerType)) {
                                 var13 = var9.substring(3);
                                 if (var13.length() > 0 && var12.getName().endsWith(var13)) {
                                    if (var20 == null) {
                                       var20 = new HashMap();
                                    }

                                    var20.put(var13, var7);
                                 }
                              }
                           }
                        }
                     } else if (var9.startsWith("remove")) {
                        var10 = var7.getReturnType();
                        if (var10 == Void.TYPE) {
                           var11 = var7.getGenericParameterTypes();
                           if (var11.length == 1) {
                              var12 = TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, var11[0]));
                              if (isSubclass(var12, eventListenerType)) {
                                 var13 = var9.substring(6);
                                 if (var13.length() > 0 && var12.getName().endsWith(var13)) {
                                    if (var21 == null) {
                                       var21 = new HashMap();
                                    }

                                    var21.put(var13, var7);
                                 }
                              }
                           }
                        }
                     } else if (var9.startsWith("get")) {
                        Class[] var26 = var7.getParameterTypes();
                        if (var26.length == 0) {
                           Class var28 = FeatureDescriptor.getReturnType(this.beanClass, var7);
                           if (var28.isArray()) {
                              var12 = var28.getComponentType();
                              if (isSubclass(var12, eventListenerType)) {
                                 var13 = var9.substring(3, var9.length() - 1);
                                 if (var13.length() > 0 && var12.getName().endsWith(var13)) {
                                    if (var5 == null) {
                                       var5 = new HashMap();
                                    }

                                    var5.put(var13, var7);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (var20 != null && var21 != null) {
            Iterator var22 = var20.keySet().iterator();

            label161:
            while(true) {
               String var23;
               do {
                  do {
                     if (!var22.hasNext()) {
                        break label161;
                     }

                     var23 = (String)var22.next();
                  } while(var21.get(var23) == null);
               } while(!var23.endsWith("Listener"));

               String var24 = decapitalize(var23.substring(0, var23.length() - 8));
               Method var25 = (Method)var20.get(var23);
               Method var27 = (Method)var21.get(var23);
               Method var29 = null;
               if (var5 != null) {
                  var29 = (Method)var5.get(var23);
               }

               var12 = FeatureDescriptor.getParameterTypes(this.beanClass, var25)[0];
               Method[] var30 = getPublicDeclaredMethods(var12);
               ArrayList var14 = new ArrayList(var30.length);

               for(int var15 = 0; var15 < var30.length; ++var15) {
                  if (var30[var15] != null && this.isEventHandler(var30[var15])) {
                     var14.add(var30[var15]);
                  }
               }

               Method[] var31 = (Method[])var14.toArray(new Method[var14.size()]);
               EventSetDescriptor var16 = new EventSetDescriptor(var24, var12, var31, var25, var27, var29);
               if (this.throwsException(var25, TooManyListenersException.class)) {
                  var16.setUnicast(true);
               }

               this.addEvent(var16);
            }
         }
      }

      if (this.events.size() == 0) {
         var17 = EMPTY_EVENTSETDESCRIPTORS;
      } else {
         var17 = new EventSetDescriptor[this.events.size()];
         var17 = (EventSetDescriptor[])this.events.values().toArray(var17);
         if (this.defaultEventName != null) {
            for(var3 = 0; var3 < var17.length; ++var3) {
               if (this.defaultEventName.equals(var17[var3].getName())) {
                  this.defaultEventIndex = var3;
               }
            }
         }
      }

      return var17;
   }

   private void addEvent(EventSetDescriptor var1) {
      String var2 = var1.getName();
      if (var1.getName().equals("propertyChange")) {
         this.propertyChangeSource = true;
      }

      EventSetDescriptor var3 = (EventSetDescriptor)this.events.get(var2);
      if (var3 == null) {
         this.events.put(var2, var1);
      } else {
         EventSetDescriptor var4 = new EventSetDescriptor(var3, var1);
         this.events.put(var2, var4);
      }
   }

   private MethodDescriptor[] getTargetMethodInfo() {
      if (this.methods == null) {
         this.methods = new HashMap(100);
      }

      MethodDescriptor[] var1 = null;
      if (this.explicitBeanInfo != null) {
         var1 = this.explicitBeanInfo.getMethodDescriptors();
      }

      MethodDescriptor[] var2;
      int var3;
      if (var1 == null && this.superBeanInfo != null) {
         var2 = this.superBeanInfo.getMethodDescriptors();

         for(var3 = 0; var3 < var2.length; ++var3) {
            this.addMethod(var2[var3]);
         }
      }

      int var6;
      for(var6 = 0; var6 < this.additionalBeanInfo.length; ++var6) {
         MethodDescriptor[] var7 = this.additionalBeanInfo[var6].getMethodDescriptors();
         if (var7 != null) {
            for(int var4 = 0; var4 < var7.length; ++var4) {
               this.addMethod(var7[var4]);
            }
         }
      }

      if (var1 != null) {
         for(var6 = 0; var6 < var1.length; ++var6) {
            this.addMethod(var1[var6]);
         }
      } else {
         Method[] var8 = getPublicDeclaredMethods(this.beanClass);

         for(var3 = 0; var3 < var8.length; ++var3) {
            Method var9 = var8[var3];
            if (var9 != null) {
               MethodDescriptor var5 = new MethodDescriptor(var9);
               this.addMethod(var5);
            }
         }
      }

      var2 = new MethodDescriptor[this.methods.size()];
      var2 = (MethodDescriptor[])this.methods.values().toArray(var2);
      return var2;
   }

   private void addMethod(MethodDescriptor var1) {
      String var2 = var1.getName();
      MethodDescriptor var3 = (MethodDescriptor)this.methods.get(var2);
      if (var3 == null) {
         this.methods.put(var2, var1);
      } else {
         String[] var4 = var1.getParamNames();
         String[] var5 = var3.getParamNames();
         boolean var6 = false;
         if (var4.length == var5.length) {
            var6 = true;

            for(int var7 = 0; var7 < var4.length; ++var7) {
               if (var4[var7] != var5[var7]) {
                  var6 = false;
                  break;
               }
            }
         }

         if (var6) {
            MethodDescriptor var10 = new MethodDescriptor(var3, var1);
            this.methods.put(var2, var10);
         } else {
            String var9 = makeQualifiedMethodName(var2, var4);
            var3 = (MethodDescriptor)this.methods.get(var9);
            if (var3 == null) {
               this.methods.put(var9, var1);
            } else {
               MethodDescriptor var8 = new MethodDescriptor(var3, var1);
               this.methods.put(var9, var8);
            }
         }
      }
   }

   private static String makeQualifiedMethodName(String var0, String[] var1) {
      StringBuffer var2 = new StringBuffer(var0);
      var2.append('=');

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2.append(':');
         var2.append(var1[var3]);
      }

      return var2.toString();
   }

   private int getTargetDefaultEventIndex() {
      return this.defaultEventIndex;
   }

   private int getTargetDefaultPropertyIndex() {
      return this.defaultPropertyIndex;
   }

   private BeanDescriptor getTargetBeanDescriptor() {
      if (this.explicitBeanInfo != null) {
         BeanDescriptor var1 = this.explicitBeanInfo.getBeanDescriptor();
         if (var1 != null) {
            return var1;
         }
      }

      return new BeanDescriptor(this.beanClass, findCustomizerClass(this.beanClass));
   }

   private static Class<?> findCustomizerClass(Class<?> var0) {
      String var1 = var0.getName() + "Customizer";

      try {
         var0 = ClassFinder.findClass(var1, var0.getClassLoader());
         if (Component.class.isAssignableFrom(var0) && Customizer.class.isAssignableFrom(var0)) {
            return var0;
         }
      } catch (Exception var3) {
      }

      return null;
   }

   private boolean isEventHandler(Method var1) {
      Type[] var2 = var1.getGenericParameterTypes();
      return var2.length != 1 ? false : isSubclass(TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, var2[0])), EventObject.class);
   }

   private static Method[] getPublicDeclaredMethods(Class<?> var0) {
      if (!ReflectUtil.isPackageAccessible(var0)) {
         return new Method[0];
      } else {
         synchronized(declaredMethodCache) {
            Method[] var2 = (Method[])declaredMethodCache.get(var0);
            if (var2 == null) {
               var2 = var0.getMethods();

               for(int var3 = 0; var3 < var2.length; ++var3) {
                  Method var4 = var2[var3];
                  if (!var4.getDeclaringClass().equals(var0)) {
                     var2[var3] = null;
                  } else {
                     try {
                        var4 = MethodFinder.findAccessibleMethod(var4);
                        Class var5 = var4.getDeclaringClass();
                        var2[var3] = !var5.equals(var0) && !var5.isInterface() ? null : var4;
                     } catch (NoSuchMethodException var7) {
                     }
                  }
               }

               declaredMethodCache.put(var0, var2);
            }

            return var2;
         }
      }
   }

   private static Method internalFindMethod(Class<?> var0, String var1, int var2, Class[] var3) {
      Method var4 = null;

      for(Class var5 = var0; var5 != null; var5 = var5.getSuperclass()) {
         Method[] var6 = getPublicDeclaredMethods(var5);

         for(int var7 = 0; var7 < var6.length; ++var7) {
            var4 = var6[var7];
            if (var4 != null && var4.getName().equals(var1)) {
               Type[] var8 = var4.getGenericParameterTypes();
               if (var8.length == var2) {
                  if (var3 == null) {
                     return var4;
                  }

                  boolean var9 = false;
                  if (var2 <= 0) {
                     return var4;
                  }

                  for(int var10 = 0; var10 < var2; ++var10) {
                     if (TypeResolver.erase(TypeResolver.resolveInClass(var0, var8[var10])) != var3[var10]) {
                        var9 = true;
                     }
                  }

                  if (!var9) {
                     return var4;
                  }
               }
            }
         }
      }

      var4 = null;
      Class[] var11 = var0.getInterfaces();

      for(int var12 = 0; var12 < var11.length; ++var12) {
         var4 = internalFindMethod(var11[var12], var1, var2, (Class[])null);
         if (var4 != null) {
            break;
         }
      }

      return var4;
   }

   static Method findMethod(Class<?> var0, String var1, int var2) {
      return findMethod(var0, var1, var2, (Class[])null);
   }

   static Method findMethod(Class<?> var0, String var1, int var2, Class[] var3) {
      return var1 == null ? null : internalFindMethod(var0, var1, var2, var3);
   }

   static boolean isSubclass(Class<?> var0, Class<?> var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         for(Class var2 = var0; var2 != null; var2 = var2.getSuperclass()) {
            if (var2 == var1) {
               return true;
            }

            if (var1.isInterface()) {
               Class[] var3 = var2.getInterfaces();

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  if (isSubclass(var3[var4], var1)) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean throwsException(Method var1, Class<?> var2) {
      Class[] var3 = var1.getExceptionTypes();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4] == var2) {
            return true;
         }
      }

      return false;
   }

   static Object instantiate(Class<?> var0, String var1) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
      ClassLoader var2 = var0.getClassLoader();
      Class var3 = ClassFinder.findClass(var1, var2);
      return var3.newInstance();
   }
}
