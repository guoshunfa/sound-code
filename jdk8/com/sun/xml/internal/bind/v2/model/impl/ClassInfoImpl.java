package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

public class ClassInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements ClassInfo<T, C>, Element<T, C> {
   protected final C clazz;
   private final QName elementName;
   private final QName typeName;
   private FinalArrayList<PropertyInfoImpl<T, C, F, M>> properties;
   private String[] propOrder;
   private ClassInfoImpl<T, C, F, M> baseClass;
   private boolean baseClassComputed = false;
   private boolean hasSubClasses = false;
   protected PropertySeed<T, C, F, M> attributeWildcard;
   private M factoryMethod = null;
   private static final ClassInfoImpl.SecondaryAnnotation[] SECONDARY_ANNOTATIONS = ClassInfoImpl.SecondaryAnnotation.values();
   private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
   private static final HashMap<Class, Integer> ANNOTATION_NUMBER_MAP = new HashMap();
   private static final String[] DEFAULT_ORDER;

   ClassInfoImpl(ModelBuilder<T, C, F, M> builder, Locatable upstream, C clazz) {
      super(builder, upstream);
      this.clazz = clazz;

      assert clazz != null;

      this.elementName = this.parseElementName(clazz);
      XmlType t = (XmlType)this.reader().getClassAnnotation(XmlType.class, clazz, this);
      this.typeName = this.parseTypeName(clazz, t);
      if (t != null) {
         String[] propOrder = t.propOrder();
         if (propOrder.length == 0) {
            this.propOrder = null;
         } else if (propOrder[0].length() == 0) {
            this.propOrder = DEFAULT_ORDER;
         } else {
            this.propOrder = propOrder;
         }
      } else {
         this.propOrder = DEFAULT_ORDER;
      }

      XmlAccessorOrder xao = (XmlAccessorOrder)this.reader().getPackageAnnotation(XmlAccessorOrder.class, clazz, this);
      if (xao != null && xao.value() == XmlAccessOrder.UNDEFINED) {
         this.propOrder = null;
      }

      xao = (XmlAccessorOrder)this.reader().getClassAnnotation(XmlAccessorOrder.class, clazz, this);
      if (xao != null && xao.value() == XmlAccessOrder.UNDEFINED) {
         this.propOrder = null;
      }

      if (this.nav().isInterface(clazz)) {
         builder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INTERFACE.format(this.nav().getClassName(clazz)), this));
      }

      if (!this.hasFactoryConstructor(t) && !this.nav().hasDefaultConstructor(clazz)) {
         if (this.nav().isInnerClass(clazz)) {
            builder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INNER_CLASS.format(this.nav().getClassName(clazz)), this));
         } else if (this.elementName != null) {
            builder.reportError(new IllegalAnnotationException(Messages.NO_DEFAULT_CONSTRUCTOR.format(this.nav().getClassName(clazz)), this));
         }
      }

   }

   public ClassInfoImpl<T, C, F, M> getBaseClass() {
      if (!this.baseClassComputed) {
         C s = this.nav().getSuperClass(this.clazz);
         if (s != null && s != this.nav().asDecl(Object.class)) {
            NonElement<T, C> b = this.builder.getClassInfo(s, true, this);
            if (b instanceof ClassInfoImpl) {
               this.baseClass = (ClassInfoImpl)b;
               this.baseClass.hasSubClasses = true;
            } else {
               this.baseClass = null;
            }
         } else {
            this.baseClass = null;
         }

         this.baseClassComputed = true;
      }

      return this.baseClass;
   }

   public final Element<T, C> getSubstitutionHead() {
      ClassInfoImpl c;
      for(c = this.getBaseClass(); c != null && !c.isElement(); c = c.getBaseClass()) {
      }

      return c;
   }

   public final C getClazz() {
      return this.clazz;
   }

   /** @deprecated */
   public ClassInfoImpl<T, C, F, M> getScope() {
      return null;
   }

   public final T getType() {
      return this.nav().use(this.clazz);
   }

   public boolean canBeReferencedByIDREF() {
      Iterator var1 = this.getProperties().iterator();

      PropertyInfo p;
      do {
         if (!var1.hasNext()) {
            ClassInfoImpl<T, C, F, M> base = this.getBaseClass();
            if (base != null) {
               return base.canBeReferencedByIDREF();
            }

            return false;
         }

         p = (PropertyInfo)var1.next();
      } while(p.id() != ID.ID);

      return true;
   }

   public final String getName() {
      return this.nav().getClassName(this.clazz);
   }

   public <A extends Annotation> A readAnnotation(Class<A> a) {
      return this.reader().getClassAnnotation(a, this.clazz, this);
   }

   public Element<T, C> asElement() {
      return this.isElement() ? this : null;
   }

   public List<? extends PropertyInfo<T, C>> getProperties() {
      if (this.properties != null) {
         return this.properties;
      } else {
         XmlAccessType at = this.getAccessType();
         this.properties = new FinalArrayList();
         this.findFieldProperties(this.clazz, at);
         this.findGetterSetterProperties(at);
         if (this.propOrder != DEFAULT_ORDER && this.propOrder != null) {
            ClassInfoImpl<T, C, F, M>.PropertySorter sorter = new ClassInfoImpl.PropertySorter();
            Iterator var3 = this.properties.iterator();

            while(var3.hasNext()) {
               PropertyInfoImpl p = (PropertyInfoImpl)var3.next();
               sorter.checkedGet(p);
            }

            Collections.sort(this.properties, sorter);
            sorter.checkUnusedProperties();
         } else {
            XmlAccessOrder ao = this.getAccessorOrder();
            if (ao == XmlAccessOrder.ALPHABETICAL) {
               Collections.sort(this.properties);
            }
         }

         PropertyInfoImpl vp = null;
         PropertyInfoImpl ep = null;
         Iterator var9 = this.properties.iterator();

         while(var9.hasNext()) {
            PropertyInfoImpl p = (PropertyInfoImpl)var9.next();
            switch(p.kind()) {
            case ELEMENT:
            case REFERENCE:
            case MAP:
               ep = p;
               break;
            case VALUE:
               if (vp != null) {
                  this.builder.reportError(new IllegalAnnotationException(Messages.MULTIPLE_VALUE_PROPERTY.format(), vp, p));
               }

               if (this.getBaseClass() != null) {
                  this.builder.reportError(new IllegalAnnotationException(Messages.XMLVALUE_IN_DERIVED_TYPE.format(), p));
               }

               vp = p;
            case ATTRIBUTE:
               break;
            default:
               assert false;
            }
         }

         if (ep != null && vp != null) {
            this.builder.reportError(new IllegalAnnotationException(Messages.ELEMENT_AND_VALUE_PROPERTY.format(), vp, ep));
         }

         return this.properties;
      }
   }

   private void findFieldProperties(C c, XmlAccessType at) {
      C sc = this.nav().getSuperClass(c);
      if (this.shouldRecurseSuperClass(sc)) {
         this.findFieldProperties(sc, at);
      }

      Iterator var4 = this.nav().getDeclaredFields(c).iterator();

      while(true) {
         while(var4.hasNext()) {
            F f = var4.next();
            Annotation[] annotations = this.reader().getAllFieldAnnotations(f, this);
            boolean isDummy = this.reader().hasFieldAnnotation(OverrideAnnotationOf.class, f);
            if (this.nav().isTransient(f)) {
               if (hasJAXBAnnotation(annotations)) {
                  this.builder.reportError(new IllegalAnnotationException(Messages.TRANSIENT_FIELD_NOT_BINDABLE.format(this.nav().getFieldName(f)), getSomeJAXBAnnotation(annotations)));
               }
            } else if (this.nav().isStaticField(f)) {
               if (hasJAXBAnnotation(annotations)) {
                  this.addProperty(this.createFieldSeed(f), annotations, false);
               }
            } else {
               if (at == XmlAccessType.FIELD || at == XmlAccessType.PUBLIC_MEMBER && this.nav().isPublicField(f) || hasJAXBAnnotation(annotations)) {
                  if (isDummy) {
                     Object top;
                     for(top = this.getBaseClass(); top != null && ((ClassInfo)top).getProperty("content") == null; top = ((ClassInfo)top).getBaseClass()) {
                     }

                     DummyPropertyInfo prop = (DummyPropertyInfo)((ClassInfo)top).getProperty("content");
                     PropertySeed seed = this.createFieldSeed(f);
                     prop.addType(this.createReferenceProperty(seed));
                  } else {
                     this.addProperty(this.createFieldSeed(f), annotations, false);
                  }
               }

               this.checkFieldXmlLocation(f);
            }
         }

         return;
      }
   }

   public final boolean hasValueProperty() {
      ClassInfoImpl<T, C, F, M> bc = this.getBaseClass();
      if (bc != null && bc.hasValueProperty()) {
         return true;
      } else {
         Iterator var2 = this.getProperties().iterator();

         PropertyInfo p;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            p = (PropertyInfo)var2.next();
         } while(!(p instanceof ValuePropertyInfo));

         return true;
      }
   }

   public PropertyInfo<T, C> getProperty(String name) {
      Iterator var2 = this.getProperties().iterator();

      PropertyInfo p;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         p = (PropertyInfo)var2.next();
      } while(!p.getName().equals(name));

      return p;
   }

   protected void checkFieldXmlLocation(F f) {
   }

   private <T extends Annotation> T getClassOrPackageAnnotation(Class<T> type) {
      T t = this.reader().getClassAnnotation(type, this.clazz, this);
      return t != null ? t : this.reader().getPackageAnnotation(type, this.clazz, this);
   }

   private XmlAccessType getAccessType() {
      XmlAccessorType xat = (XmlAccessorType)this.getClassOrPackageAnnotation(XmlAccessorType.class);
      return xat != null ? xat.value() : XmlAccessType.PUBLIC_MEMBER;
   }

   private XmlAccessOrder getAccessorOrder() {
      XmlAccessorOrder xao = (XmlAccessorOrder)this.getClassOrPackageAnnotation(XmlAccessorOrder.class);
      return xao != null ? xao.value() : XmlAccessOrder.UNDEFINED;
   }

   public boolean hasProperties() {
      return !this.properties.isEmpty();
   }

   private static <T> T pickOne(T... args) {
      Object[] var1 = args;
      int var2 = args.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         T arg = var1[var3];
         if (arg != null) {
            return arg;
         }
      }

      return null;
   }

   private static <T> List<T> makeSet(T... args) {
      List<T> l = new FinalArrayList();
      Object[] var2 = args;
      int var3 = args.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         T arg = var2[var4];
         if (arg != null) {
            l.add(arg);
         }
      }

      return l;
   }

   private void checkConflict(Annotation a, Annotation b) throws ClassInfoImpl.DuplicateException {
      assert b != null;

      if (a != null) {
         throw new ClassInfoImpl.DuplicateException(a, b);
      }
   }

   private void addProperty(PropertySeed<T, C, F, M> seed, Annotation[] annotations, boolean dummy) {
      XmlTransient t = null;
      XmlAnyAttribute aa = null;
      XmlAttribute a = null;
      XmlValue v = null;
      XmlElement e1 = null;
      XmlElements e2 = null;
      XmlElementRef r1 = null;
      XmlElementRefs r2 = null;
      XmlAnyElement xae = null;
      XmlMixed mx = null;
      OverrideAnnotationOf ov = null;
      int secondaryAnnotations = 0;

      try {
         Annotation[] var16 = annotations;
         int groupCount = annotations.length;

         for(int var18 = 0; var18 < groupCount; ++var18) {
            Annotation ann = var16[var18];
            Integer index = (Integer)ANNOTATION_NUMBER_MAP.get(ann.annotationType());
            if (index != null) {
               switch(index) {
               case 0:
                  this.checkConflict(t, ann);
                  t = (XmlTransient)ann;
                  break;
               case 1:
                  this.checkConflict(aa, ann);
                  aa = (XmlAnyAttribute)ann;
                  break;
               case 2:
                  this.checkConflict(a, ann);
                  a = (XmlAttribute)ann;
                  break;
               case 3:
                  this.checkConflict(v, ann);
                  v = (XmlValue)ann;
                  break;
               case 4:
                  this.checkConflict(e1, ann);
                  e1 = (XmlElement)ann;
                  break;
               case 5:
                  this.checkConflict(e2, ann);
                  e2 = (XmlElements)ann;
                  break;
               case 6:
                  this.checkConflict(r1, ann);
                  r1 = (XmlElementRef)ann;
                  break;
               case 7:
                  this.checkConflict(r2, ann);
                  r2 = (XmlElementRefs)ann;
                  break;
               case 8:
                  this.checkConflict(xae, ann);
                  xae = (XmlAnyElement)ann;
                  break;
               case 9:
                  this.checkConflict(mx, ann);
                  mx = (XmlMixed)ann;
                  break;
               case 10:
                  this.checkConflict(ov, ann);
                  ov = (OverrideAnnotationOf)ann;
                  break;
               default:
                  secondaryAnnotations |= 1 << index - 20;
               }
            }
         }

         ClassInfoImpl.PropertyGroup group = null;
         groupCount = 0;
         if (t != null) {
            group = ClassInfoImpl.PropertyGroup.TRANSIENT;
            ++groupCount;
         }

         if (aa != null) {
            group = ClassInfoImpl.PropertyGroup.ANY_ATTRIBUTE;
            ++groupCount;
         }

         if (a != null) {
            group = ClassInfoImpl.PropertyGroup.ATTRIBUTE;
            ++groupCount;
         }

         if (v != null) {
            group = ClassInfoImpl.PropertyGroup.VALUE;
            ++groupCount;
         }

         if (e1 != null || e2 != null) {
            group = ClassInfoImpl.PropertyGroup.ELEMENT;
            ++groupCount;
         }

         if (r1 != null || r2 != null || xae != null || mx != null || ov != null) {
            group = ClassInfoImpl.PropertyGroup.ELEMENT_REF;
            ++groupCount;
         }

         if (groupCount > 1) {
            List<Annotation> err = makeSet(t, aa, a, v, (Annotation)pickOne(e1, e2), (Annotation)pickOne(r1, r2, xae));
            throw new ClassInfoImpl.ConflictException(err);
         }

         if (group == null) {
            assert groupCount == 0;

            if (this.nav().isSubClassOf(seed.getRawType(), this.nav().ref(Map.class)) && !seed.hasAnnotation(XmlJavaTypeAdapter.class)) {
               group = ClassInfoImpl.PropertyGroup.MAP;
            } else {
               group = ClassInfoImpl.PropertyGroup.ELEMENT;
            }
         } else if (group.equals(ClassInfoImpl.PropertyGroup.ELEMENT) && this.nav().isSubClassOf(seed.getRawType(), this.nav().ref(Map.class)) && !seed.hasAnnotation(XmlJavaTypeAdapter.class)) {
            group = ClassInfoImpl.PropertyGroup.MAP;
         }

         if ((secondaryAnnotations & group.allowedsecondaryAnnotations) != 0) {
            ClassInfoImpl.SecondaryAnnotation[] var31 = SECONDARY_ANNOTATIONS;
            int var33 = var31.length;
            int var34 = 0;

            while(true) {
               if (var34 >= var33) {
                  assert false;
                  break;
               }

               ClassInfoImpl.SecondaryAnnotation sa = var31[var34];
               if (!group.allows(sa)) {
                  Class[] var22 = sa.members;
                  int var23 = var22.length;

                  for(int var24 = 0; var24 < var23; ++var24) {
                     Class<? extends Annotation> m = var22[var24];
                     Annotation offender = seed.readAnnotation(m);
                     if (offender != null) {
                        this.builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_NOT_ALLOWED.format(m.getSimpleName()), offender));
                        return;
                     }
                  }
               }

               ++var34;
            }
         }

         switch(group) {
         case TRANSIENT:
            return;
         case ANY_ATTRIBUTE:
            if (this.attributeWildcard != null) {
               this.builder.reportError(new IllegalAnnotationException(Messages.TWO_ATTRIBUTE_WILDCARDS.format(this.nav().getClassName(this.getClazz())), aa, this.attributeWildcard));
               return;
            }

            this.attributeWildcard = seed;
            if (this.inheritsAttributeWildcard()) {
               this.builder.reportError(new IllegalAnnotationException(Messages.SUPER_CLASS_HAS_WILDCARD.format(), aa, this.getInheritedAttributeWildcard()));
               return;
            }

            if (!this.nav().isSubClassOf(seed.getRawType(), this.nav().ref(Map.class))) {
               this.builder.reportError(new IllegalAnnotationException(Messages.INVALID_ATTRIBUTE_WILDCARD_TYPE.format(this.nav().getTypeName(seed.getRawType())), aa, this.getInheritedAttributeWildcard()));
               return;
            }

            return;
         case ATTRIBUTE:
            this.properties.add(this.createAttributeProperty(seed));
            return;
         case VALUE:
            this.properties.add(this.createValueProperty(seed));
            return;
         case ELEMENT:
            this.properties.add(this.createElementProperty(seed));
            return;
         case ELEMENT_REF:
            this.properties.add(this.createReferenceProperty(seed));
            return;
         case MAP:
            this.properties.add(this.createMapProperty(seed));
            return;
         default:
            assert false;
         }
      } catch (ClassInfoImpl.ConflictException var27) {
         List<Annotation> err = var27.annotations;
         this.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.getClazz()) + '#' + seed.getName(), ((Annotation)err.get(0)).annotationType().getName(), ((Annotation)err.get(1)).annotationType().getName()), (Annotation)err.get(0), (Annotation)err.get(1)));
      } catch (ClassInfoImpl.DuplicateException var28) {
         this.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(var28.a1.annotationType().getName()), var28.a1, var28.a2));
      }

   }

   protected ReferencePropertyInfoImpl<T, C, F, M> createReferenceProperty(PropertySeed<T, C, F, M> seed) {
      return new ReferencePropertyInfoImpl(this, seed);
   }

   protected AttributePropertyInfoImpl<T, C, F, M> createAttributeProperty(PropertySeed<T, C, F, M> seed) {
      return new AttributePropertyInfoImpl(this, seed);
   }

   protected ValuePropertyInfoImpl<T, C, F, M> createValueProperty(PropertySeed<T, C, F, M> seed) {
      return new ValuePropertyInfoImpl(this, seed);
   }

   protected ElementPropertyInfoImpl<T, C, F, M> createElementProperty(PropertySeed<T, C, F, M> seed) {
      return new ElementPropertyInfoImpl(this, seed);
   }

   protected MapPropertyInfoImpl<T, C, F, M> createMapProperty(PropertySeed<T, C, F, M> seed) {
      return new MapPropertyInfoImpl(this, seed);
   }

   private void findGetterSetterProperties(XmlAccessType at) {
      Map<String, M> getters = new LinkedHashMap();
      Map<String, M> setters = new LinkedHashMap();
      Object c = this.clazz;

      do {
         this.collectGetterSetters(this.clazz, getters, setters);
         c = this.nav().getSuperClass(c);
      } while(this.shouldRecurseSuperClass(c));

      Set<String> complete = new TreeSet(getters.keySet());
      complete.retainAll(setters.keySet());
      this.resurrect(getters, complete);
      this.resurrect(setters, complete);
      Iterator var6 = complete.iterator();

      while(true) {
         while(true) {
            Object getter;
            Object setter;
            Annotation[] ga;
            Annotation[] sa;
            boolean hasAnnotation;
            boolean isOverriding;
            do {
               if (!var6.hasNext()) {
                  getters.keySet().removeAll(complete);
                  setters.keySet().removeAll(complete);
                  return;
               }

               String name = (String)var6.next();
               getter = getters.get(name);
               setter = setters.get(name);
               ga = getter != null ? this.reader().getAllMethodAnnotations(getter, new MethodLocatable(this, getter, this.nav())) : EMPTY_ANNOTATIONS;
               sa = setter != null ? this.reader().getAllMethodAnnotations(setter, new MethodLocatable(this, setter, this.nav())) : EMPTY_ANNOTATIONS;
               hasAnnotation = hasJAXBAnnotation(ga) || hasJAXBAnnotation(sa);
               isOverriding = false;
               if (!hasAnnotation) {
                  isOverriding = getter != null && this.nav().isOverriding(getter, c) && setter != null && this.nav().isOverriding(setter, c);
               }
            } while((at != XmlAccessType.PROPERTY || isOverriding) && (at != XmlAccessType.PUBLIC_MEMBER || !this.isConsideredPublic(getter) || !this.isConsideredPublic(setter) || isOverriding) && !hasAnnotation);

            if (getter != null && setter != null && !this.nav().isSameType(this.nav().getReturnType(getter), this.nav().getMethodParameters(setter)[0])) {
               this.builder.reportError(new IllegalAnnotationException(Messages.GETTER_SETTER_INCOMPATIBLE_TYPE.format(this.nav().getTypeName(this.nav().getReturnType(getter)), this.nav().getTypeName(this.nav().getMethodParameters(setter)[0])), new MethodLocatable(this, getter, this.nav()), new MethodLocatable(this, setter, this.nav())));
            } else {
               Annotation[] r;
               if (ga.length == 0) {
                  r = sa;
               } else if (sa.length == 0) {
                  r = ga;
               } else {
                  r = new Annotation[ga.length + sa.length];
                  System.arraycopy(ga, 0, r, 0, ga.length);
                  System.arraycopy(sa, 0, r, ga.length, sa.length);
               }

               this.addProperty(this.createAccessorSeed(getter, setter), r, false);
            }
         }
      }
   }

   private void collectGetterSetters(C c, Map<String, M> getters, Map<String, M> setters) {
      C sc = this.nav().getSuperClass(c);
      if (this.shouldRecurseSuperClass(sc)) {
         this.collectGetterSetters(sc, getters, setters);
      }

      Collection<? extends M> methods = this.nav().getDeclaredMethods(c);
      Map<String, List<M>> allSetters = new LinkedHashMap();
      Iterator var7 = methods.iterator();

      while(var7.hasNext()) {
         M method = var7.next();
         boolean used = false;
         if (!this.nav().isBridgeMethod(method)) {
            String name = this.nav().getMethodName(method);
            int arity = this.nav().getMethodParameters(method).length;
            if (this.nav().isStaticMethod(method)) {
               this.ensureNoAnnotation(method);
            } else {
               String propName = getPropertyNameFromGetMethod(name);
               if (propName != null && arity == 0) {
                  getters.put(propName, method);
                  used = true;
               }

               propName = getPropertyNameFromSetMethod(name);
               if (propName != null && arity == 1) {
                  List<M> propSetters = (List)allSetters.get(propName);
                  if (null == propSetters) {
                     propSetters = new ArrayList();
                     allSetters.put(propName, propSetters);
                  }

                  ((List)propSetters).add(method);
                  used = true;
               }

               if (!used) {
                  this.ensureNoAnnotation(method);
               }
            }
         }
      }

      var7 = getters.entrySet().iterator();

      while(true) {
         while(true) {
            String propName;
            Object getter;
            List propSetters;
            do {
               Map.Entry e;
               if (!var7.hasNext()) {
                  var7 = allSetters.entrySet().iterator();

                  while(var7.hasNext()) {
                     e = (Map.Entry)var7.next();
                     setters.put(e.getKey(), ((List)e.getValue()).get(0));
                  }

                  return;
               }

               e = (Map.Entry)var7.next();
               propName = (String)e.getKey();
               getter = e.getValue();
               propSetters = (List)allSetters.remove(propName);
            } while(null == propSetters);

            T getterType = this.nav().getReturnType(getter);
            Iterator var21 = propSetters.iterator();

            while(var21.hasNext()) {
               M setter = var21.next();
               T setterType = this.nav().getMethodParameters(setter)[0];
               if (this.nav().isSameType(setterType, getterType)) {
                  setters.put(propName, setter);
                  break;
               }
            }
         }
      }
   }

   private boolean shouldRecurseSuperClass(C sc) {
      return sc != null && (this.builder.isReplaced(sc) || this.reader().hasClassAnnotation(sc, XmlTransient.class));
   }

   private boolean isConsideredPublic(M m) {
      return m == null || this.nav().isPublicMethod(m);
   }

   private void resurrect(Map<String, M> methods, Set<String> complete) {
      Iterator var3 = methods.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, M> e = (Map.Entry)var3.next();
         if (!complete.contains(e.getKey()) && hasJAXBAnnotation(this.reader().getAllMethodAnnotations(e.getValue(), this))) {
            complete.add(e.getKey());
         }
      }

   }

   private void ensureNoAnnotation(M method) {
      Annotation[] annotations = this.reader().getAllMethodAnnotations(method, this);
      Annotation[] var3 = annotations;
      int var4 = annotations.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Annotation a = var3[var5];
         if (isJAXBAnnotation(a)) {
            this.builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_ON_WRONG_METHOD.format(), a));
            return;
         }
      }

   }

   private static boolean isJAXBAnnotation(Annotation a) {
      return ANNOTATION_NUMBER_MAP.containsKey(a.annotationType());
   }

   private static boolean hasJAXBAnnotation(Annotation[] annotations) {
      return getSomeJAXBAnnotation(annotations) != null;
   }

   private static Annotation getSomeJAXBAnnotation(Annotation[] annotations) {
      Annotation[] var1 = annotations;
      int var2 = annotations.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Annotation a = var1[var3];
         if (isJAXBAnnotation(a)) {
            return a;
         }
      }

      return null;
   }

   private static String getPropertyNameFromGetMethod(String name) {
      if (name.startsWith("get") && name.length() > 3) {
         return name.substring(3);
      } else {
         return name.startsWith("is") && name.length() > 2 ? name.substring(2) : null;
      }
   }

   private static String getPropertyNameFromSetMethod(String name) {
      return name.startsWith("set") && name.length() > 3 ? name.substring(3) : null;
   }

   protected PropertySeed<T, C, F, M> createFieldSeed(F f) {
      return new FieldPropertySeed(this, f);
   }

   protected PropertySeed<T, C, F, M> createAccessorSeed(M getter, M setter) {
      return new GetterSetterPropertySeed(this, getter, setter);
   }

   public final boolean isElement() {
      return this.elementName != null;
   }

   public boolean isAbstract() {
      return this.nav().isAbstract(this.clazz);
   }

   public boolean isOrdered() {
      return this.propOrder != null;
   }

   public final boolean isFinal() {
      return this.nav().isFinal(this.clazz);
   }

   public final boolean hasSubClasses() {
      return this.hasSubClasses;
   }

   public final boolean hasAttributeWildcard() {
      return this.declaresAttributeWildcard() || this.inheritsAttributeWildcard();
   }

   public final boolean inheritsAttributeWildcard() {
      return this.getInheritedAttributeWildcard() != null;
   }

   public final boolean declaresAttributeWildcard() {
      return this.attributeWildcard != null;
   }

   private PropertySeed<T, C, F, M> getInheritedAttributeWildcard() {
      for(ClassInfoImpl c = this.getBaseClass(); c != null; c = c.getBaseClass()) {
         if (c.attributeWildcard != null) {
            return c.attributeWildcard;
         }
      }

      return null;
   }

   public final QName getElementName() {
      return this.elementName;
   }

   public final QName getTypeName() {
      return this.typeName;
   }

   public final boolean isSimpleType() {
      List<? extends PropertyInfo> props = this.getProperties();
      if (props.size() != 1) {
         return false;
      } else {
         return ((PropertyInfo)props.get(0)).kind() == PropertyKind.VALUE;
      }
   }

   void link() {
      this.getProperties();
      Map<String, PropertyInfoImpl> names = new HashMap();
      Iterator var2 = this.properties.iterator();

      while(var2.hasNext()) {
         PropertyInfoImpl<T, C, F, M> p = (PropertyInfoImpl)var2.next();
         p.link();
         PropertyInfoImpl old = (PropertyInfoImpl)names.put(p.getName(), p);
         if (old != null) {
            this.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_COLLISION.format(p.getName()), p, old));
         }
      }

      super.link();
   }

   public Location getLocation() {
      return this.nav().getClassLocation(this.clazz);
   }

   private boolean hasFactoryConstructor(XmlType t) {
      if (t == null) {
         return false;
      } else {
         String method = t.factoryMethod();
         T fClass = this.reader().getClassValue(t, "factoryClass");
         if (method.length() > 0) {
            if (this.nav().isSameType(fClass, this.nav().ref(XmlType.DEFAULT.class))) {
               fClass = this.nav().use(this.clazz);
            }

            Iterator var4 = this.nav().getDeclaredMethods(this.nav().asDecl(fClass)).iterator();

            while(var4.hasNext()) {
               M m = var4.next();
               if (this.nav().getMethodName(m).equals(method) && this.nav().isSameType(this.nav().getReturnType(m), this.nav().use(this.clazz)) && this.nav().getMethodParameters(m).length == 0 && this.nav().isStaticMethod(m)) {
                  this.factoryMethod = m;
                  break;
               }
            }

            if (this.factoryMethod == null) {
               this.builder.reportError(new IllegalAnnotationException(Messages.NO_FACTORY_METHOD.format(this.nav().getClassName(this.nav().asDecl(fClass)), method), this));
            }
         } else if (!this.nav().isSameType(fClass, this.nav().ref(XmlType.DEFAULT.class))) {
            this.builder.reportError(new IllegalAnnotationException(Messages.FACTORY_CLASS_NEEDS_FACTORY_METHOD.format(this.nav().getClassName(this.nav().asDecl(fClass))), this));
         }

         return this.factoryMethod != null;
      }
   }

   public Method getFactoryMethod() {
      return (Method)this.factoryMethod;
   }

   public String toString() {
      return "ClassInfo(" + this.clazz + ')';
   }

   static {
      Class[] annotations = new Class[]{XmlTransient.class, XmlAnyAttribute.class, XmlAttribute.class, XmlValue.class, XmlElement.class, XmlElements.class, XmlElementRef.class, XmlElementRefs.class, XmlAnyElement.class, XmlMixed.class, OverrideAnnotationOf.class};
      HashMap<Class, Integer> m = ANNOTATION_NUMBER_MAP;
      Class[] var2 = annotations;
      int var3 = annotations.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         Class c = var2[var4];
         m.put(c, m.size());
      }

      int index = 20;
      ClassInfoImpl.SecondaryAnnotation[] var12 = SECONDARY_ANNOTATIONS;
      var4 = var12.length;

      for(int var13 = 0; var13 < var4; ++var13) {
         ClassInfoImpl.SecondaryAnnotation sa = var12[var13];
         Class[] var7 = sa.members;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Class member = var7[var9];
            m.put(member, index);
         }

         ++index;
      }

      DEFAULT_ORDER = new String[0];
   }

   private static enum PropertyGroup {
      TRANSIENT(new boolean[]{false, false, false, false, false, false}),
      ANY_ATTRIBUTE(new boolean[]{true, false, false, false, false, false}),
      ATTRIBUTE(new boolean[]{true, true, true, false, true, true}),
      VALUE(new boolean[]{true, true, true, false, true, true}),
      ELEMENT(new boolean[]{true, true, true, true, true, true}),
      ELEMENT_REF(new boolean[]{true, false, false, true, false, false}),
      MAP(new boolean[]{false, false, false, true, false, false});

      final int allowedsecondaryAnnotations;

      private PropertyGroup(boolean... bits) {
         int mask = 0;

         assert bits.length == ClassInfoImpl.SECONDARY_ANNOTATIONS.length;

         for(int i = 0; i < bits.length; ++i) {
            if (bits[i]) {
               mask |= ClassInfoImpl.SECONDARY_ANNOTATIONS[i].bitMask;
            }
         }

         this.allowedsecondaryAnnotations = ~mask;
      }

      boolean allows(ClassInfoImpl.SecondaryAnnotation a) {
         return (this.allowedsecondaryAnnotations & a.bitMask) == 0;
      }
   }

   private static enum SecondaryAnnotation {
      JAVA_TYPE(1, new Class[]{XmlJavaTypeAdapter.class}),
      ID_IDREF(2, new Class[]{XmlID.class, XmlIDREF.class}),
      BINARY(4, new Class[]{XmlInlineBinaryData.class, XmlMimeType.class, XmlAttachmentRef.class}),
      ELEMENT_WRAPPER(8, new Class[]{XmlElementWrapper.class}),
      LIST(16, new Class[]{XmlList.class}),
      SCHEMA_TYPE(32, new Class[]{XmlSchemaType.class});

      final int bitMask;
      final Class<? extends Annotation>[] members;

      private SecondaryAnnotation(int bitMask, Class<? extends Annotation>... members) {
         this.bitMask = bitMask;
         this.members = members;
      }
   }

   private static final class DuplicateException extends Exception {
      final Annotation a1;
      final Annotation a2;

      public DuplicateException(Annotation a1, Annotation a2) {
         this.a1 = a1;
         this.a2 = a2;
      }
   }

   private static final class ConflictException extends Exception {
      final List<Annotation> annotations;

      public ConflictException(List<Annotation> one) {
         this.annotations = one;
      }
   }

   private final class PropertySorter extends HashMap<String, Integer> implements Comparator<PropertyInfoImpl> {
      PropertyInfoImpl[] used;
      private Set<String> collidedNames;

      PropertySorter() {
         super(ClassInfoImpl.this.propOrder.length);
         this.used = new PropertyInfoImpl[ClassInfoImpl.this.propOrder.length];
         String[] var2 = ClassInfoImpl.this.propOrder;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String name = var2[var4];
            if (this.put(name, this.size()) != null) {
               ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ENTRY_IN_PROP_ORDER.format(name), ClassInfoImpl.this));
            }
         }

      }

      public int compare(PropertyInfoImpl o1, PropertyInfoImpl o2) {
         int lhs = this.checkedGet(o1);
         int rhs = this.checkedGet(o2);
         return lhs - rhs;
      }

      private int checkedGet(PropertyInfoImpl p) {
         Integer i = (Integer)this.get(p.getName());
         if (i == null) {
            if (p.kind().isOrdered) {
               ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_MISSING_FROM_ORDER.format(p.getName()), p));
            }

            i = this.size();
            this.put(p.getName(), i);
         }

         int ii = i;
         if (ii < this.used.length) {
            if (this.used[ii] != null && this.used[ii] != p) {
               if (this.collidedNames == null) {
                  this.collidedNames = new HashSet();
               }

               if (this.collidedNames.add(p.getName())) {
                  ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_PROPERTIES.format(p.getName()), p, this.used[ii]));
               }
            }

            this.used[ii] = p;
         }

         return i;
      }

      public void checkUnusedProperties() {
         for(int i = 0; i < this.used.length; ++i) {
            if (this.used[i] == null) {
               String unusedName = ClassInfoImpl.this.propOrder[i];
               String nearest = EditDistance.findNearest(unusedName, (Collection)(new AbstractList<String>() {
                  public String get(int index) {
                     return ((PropertyInfoImpl)ClassInfoImpl.this.properties.get(index)).getName();
                  }

                  public int size() {
                     return ClassInfoImpl.this.properties.size();
                  }
               }));
               boolean isOverriding = i > ClassInfoImpl.this.properties.size() - 1 ? false : ((PropertyInfoImpl)ClassInfoImpl.this.properties.get(i)).hasAnnotation(OverrideAnnotationOf.class);
               if (!isOverriding) {
                  ClassInfoImpl.this.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_ORDER_CONTAINS_UNUSED_ENTRY.format(unusedName, nearest), ClassInfoImpl.this));
               }
            }
         }

      }
   }
}
