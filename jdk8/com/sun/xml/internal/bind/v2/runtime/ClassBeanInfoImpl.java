package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.StructureLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public final class ClassBeanInfoImpl<BeanT> extends JaxBeanInfo<BeanT> implements AttributeAccessor<BeanT> {
   public final Property<BeanT>[] properties;
   private Property<? super BeanT> idProperty;
   private Loader loader;
   private Loader loaderWithTypeSubst;
   private RuntimeClassInfo ci;
   private final Accessor<? super BeanT, Map<QName, String>> inheritedAttWildcard;
   private final Transducer<BeanT> xducer;
   public final ClassBeanInfoImpl<? super BeanT> superClazz;
   private final Accessor<? super BeanT, Locator> xmlLocatorField;
   private final Name tagName;
   private boolean retainPropertyInfo = false;
   private AttributeProperty<BeanT>[] attributeProperties;
   private Property<BeanT>[] uriProperties;
   private final Method factoryMethod;
   private static final AttributeProperty[] EMPTY_PROPERTIES = new AttributeProperty[0];
   private static final Logger logger = Util.getClassLogger();

   ClassBeanInfoImpl(JAXBContextImpl owner, RuntimeClassInfo ci) {
      super(owner, ci, (Class)ci.getClazz(), (QName)ci.getTypeName(), ci.isElement(), false, true);
      this.ci = ci;
      this.inheritedAttWildcard = ci.getAttributeWildcard();
      this.xducer = ci.getTransducer();
      this.factoryMethod = ci.getFactoryMethod();
      this.retainPropertyInfo = owner.retainPropertyInfo;
      if (this.factoryMethod != null) {
         int classMod = this.factoryMethod.getDeclaringClass().getModifiers();
         if (!Modifier.isPublic(classMod) || !Modifier.isPublic(this.factoryMethod.getModifiers())) {
            try {
               this.factoryMethod.setAccessible(true);
            } catch (SecurityException var9) {
               logger.log(Level.FINE, (String)("Unable to make the method of " + this.factoryMethod + " accessible"), (Throwable)var9);
               throw var9;
            }
         }
      }

      if (ci.getBaseClass() == null) {
         this.superClazz = null;
      } else {
         this.superClazz = owner.getOrCreate(ci.getBaseClass());
      }

      if (this.superClazz != null && this.superClazz.xmlLocatorField != null) {
         this.xmlLocatorField = this.superClazz.xmlLocatorField;
      } else {
         this.xmlLocatorField = ci.getLocatorField();
      }

      Collection<? extends RuntimePropertyInfo> ps = ci.getProperties();
      this.properties = new Property[ps.size()];
      int idx = 0;
      boolean elementOnly = true;
      Iterator var6 = ps.iterator();

      while(var6.hasNext()) {
         RuntimePropertyInfo info = (RuntimePropertyInfo)var6.next();
         Property p = PropertyFactory.create(owner, info);
         if (info.id() == ID.ID) {
            this.idProperty = p;
         }

         this.properties[idx++] = p;
         elementOnly &= info.elementOnlyContent();
         this.checkOverrideProperties(p);
      }

      this.hasElementOnlyContentModel(elementOnly);
      if (ci.isElement()) {
         this.tagName = owner.nameBuilder.createElementName(ci.getElementName());
      } else {
         this.tagName = null;
      }

      this.setLifecycleFlags();
   }

   private void checkOverrideProperties(Property p) {
      ClassBeanInfoImpl bi = this;

      while((bi = bi.superClazz) != null) {
         Property[] props = bi.properties;
         if (props == null) {
            break;
         }

         Property[] var4 = props;
         int var5 = props.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Property superProperty = var4[var6];
            if (superProperty != null) {
               String spName = superProperty.getFieldName();
               if (spName != null && spName.equals(p.getFieldName())) {
                  superProperty.setHiddenByOverride(true);
               }
            }
         }
      }

   }

   protected void link(JAXBContextImpl grammar) {
      if (this.uriProperties == null) {
         super.link(grammar);
         if (this.superClazz != null) {
            this.superClazz.link(grammar);
         }

         this.getLoader(grammar, true);
         if (this.superClazz != null) {
            if (this.idProperty == null) {
               this.idProperty = this.superClazz.idProperty;
            }

            if (!this.superClazz.hasElementOnlyContentModel()) {
               this.hasElementOnlyContentModel(false);
            }
         }

         List<AttributeProperty> attProps = new FinalArrayList();
         List<Property> uriProps = new FinalArrayList();

         for(ClassBeanInfoImpl bi = this; bi != null; bi = bi.superClazz) {
            for(int i = 0; i < bi.properties.length; ++i) {
               Property p = bi.properties[i];
               if (p instanceof AttributeProperty) {
                  attProps.add((AttributeProperty)p);
               }

               if (p.hasSerializeURIAction()) {
                  uriProps.add(p);
               }
            }
         }

         if (grammar.c14nSupport) {
            Collections.sort(attProps);
         }

         if (attProps.isEmpty()) {
            this.attributeProperties = EMPTY_PROPERTIES;
         } else {
            this.attributeProperties = (AttributeProperty[])attProps.toArray(new AttributeProperty[attProps.size()]);
         }

         if (uriProps.isEmpty()) {
            this.uriProperties = EMPTY_PROPERTIES;
         } else {
            this.uriProperties = (Property[])uriProps.toArray(new Property[uriProps.size()]);
         }

      }
   }

   public void wrapUp() {
      Property[] var1 = this.properties;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Property p = var1[var3];
         p.wrapUp();
      }

      this.ci = null;
      super.wrapUp();
   }

   public String getElementNamespaceURI(BeanT bean) {
      return this.tagName.nsUri;
   }

   public String getElementLocalName(BeanT bean) {
      return this.tagName.localName;
   }

   public BeanT createInstance(UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException {
      BeanT bean = null;
      if (this.factoryMethod == null) {
         bean = ClassFactory.create0(this.jaxbType);
      } else {
         Object o = ClassFactory.create(this.factoryMethod);
         if (!this.jaxbType.isInstance(o)) {
            throw new InstantiationException("The factory method didn't return a correct object");
         }

         bean = o;
      }

      if (this.xmlLocatorField != null) {
         try {
            this.xmlLocatorField.set(bean, new LocatorImpl(context.getLocator()));
         } catch (AccessorException var4) {
            context.handleError((Exception)var4);
         }
      }

      return bean;
   }

   public boolean reset(BeanT bean, UnmarshallingContext context) throws SAXException {
      try {
         if (this.superClazz != null) {
            this.superClazz.reset(bean, context);
         }

         Property[] var3 = this.properties;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Property<BeanT> p = var3[var5];
            p.reset(bean);
         }

         return true;
      } catch (AccessorException var7) {
         context.handleError((Exception)var7);
         return false;
      }
   }

   public String getId(BeanT bean, XMLSerializer target) throws SAXException {
      if (this.idProperty != null) {
         try {
            return this.idProperty.getIdValue(bean);
         } catch (AccessorException var4) {
            target.reportError((String)null, var4);
         }
      }

      return null;
   }

   public void serializeRoot(BeanT bean, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
      if (this.tagName == null) {
         Class beanClass = bean.getClass();
         String message;
         if (beanClass.isAnnotationPresent(XmlRootElement.class)) {
            message = Messages.UNABLE_TO_MARSHAL_UNBOUND_CLASS.format(beanClass.getName());
         } else {
            message = Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(beanClass.getName());
         }

         target.reportError(new ValidationEventImpl(1, message, (ValidationEventLocator)null, (Throwable)null));
      } else {
         target.startElement(this.tagName, bean);
         target.childAsSoleContent(bean, (String)null);
         target.endElement();
         if (this.retainPropertyInfo) {
            target.currentProperty.remove();
         }
      }

   }

   public void serializeBody(BeanT bean, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
      if (this.superClazz != null) {
         this.superClazz.serializeBody(bean, target);
      }

      try {
         Property[] var3 = this.properties;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Property<BeanT> p = var3[var5];
            if (this.retainPropertyInfo) {
               target.currentProperty.set(p);
            }

            boolean isThereAnOverridingProperty = p.isHiddenByOverride();
            if (isThereAnOverridingProperty && !bean.getClass().equals(this.jaxbType)) {
               if (isThereAnOverridingProperty) {
                  Class beanClass = bean.getClass();
                  if (Utils.REFLECTION_NAVIGATOR.getDeclaredField(beanClass, p.getFieldName()) == null) {
                     p.serializeBody(bean, target, (Object)null);
                  }
               }
            } else {
               p.serializeBody(bean, target, (Object)null);
            }
         }
      } catch (AccessorException var9) {
         target.reportError((String)null, var9);
      }

   }

   public void serializeAttributes(BeanT bean, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
      AttributeProperty[] var3 = this.attributeProperties;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         AttributeProperty p = var3[var5];

         try {
            if (this.retainPropertyInfo) {
               Property parentProperty = target.getCurrentProperty();
               target.currentProperty.set(p);
               p.serializeAttributes(bean, target);
               target.currentProperty.set(parentProperty);
            } else {
               p.serializeAttributes(bean, target);
            }

            if (p.attName.equals("http://www.w3.org/2001/XMLSchema-instance", "nil")) {
               this.isNilIncluded = true;
            }
         } catch (AccessorException var9) {
            target.reportError((String)null, var9);
         }
      }

      try {
         if (this.inheritedAttWildcard != null) {
            Map<QName, String> map = (Map)this.inheritedAttWildcard.get(bean);
            target.attWildcardAsAttributes(map, (String)null);
         }
      } catch (AccessorException var8) {
         target.reportError((String)null, var8);
      }

   }

   public void serializeURIs(BeanT bean, XMLSerializer target) throws SAXException {
      try {
         int var5;
         if (!this.retainPropertyInfo) {
            Property[] var9 = this.uriProperties;
            int var11 = var9.length;

            for(var5 = 0; var5 < var11; ++var5) {
               Property<BeanT> p = var9[var5];
               p.serializeURIs(bean, target);
            }
         } else {
            Property parentProperty = target.getCurrentProperty();
            Property[] var4 = this.uriProperties;
            var5 = var4.length;
            int var6 = 0;

            while(true) {
               if (var6 >= var5) {
                  target.currentProperty.set(parentProperty);
                  break;
               }

               Property<BeanT> p = var4[var6];
               target.currentProperty.set(p);
               p.serializeURIs(bean, target);
               ++var6;
            }
         }

         if (this.inheritedAttWildcard != null) {
            Map<QName, String> map = (Map)this.inheritedAttWildcard.get(bean);
            target.attWildcardAsURIs(map, (String)null);
         }
      } catch (AccessorException var8) {
         target.reportError((String)null, var8);
      }

   }

   public Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
      if (this.loader == null) {
         StructureLoader sl = new StructureLoader(this);
         this.loader = sl;
         if (this.ci.hasSubClasses()) {
            this.loaderWithTypeSubst = new XsiTypeLoader(this);
         } else {
            this.loaderWithTypeSubst = this.loader;
         }

         sl.init(context, this, this.ci.getAttributeWildcard());
      }

      return typeSubstitutionCapable ? this.loaderWithTypeSubst : this.loader;
   }

   public Transducer<BeanT> getTransducer() {
      return this.xducer;
   }
}
