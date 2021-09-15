package com.sun.xml.internal.ws.spi.db;

import java.util.Map;
import javax.xml.namespace.QName;

public abstract class WrapperAccessor {
   protected Map<Object, PropertySetter> propertySetters;
   protected Map<Object, PropertyGetter> propertyGetters;
   protected boolean elementLocalNameCollision;

   protected PropertySetter getPropertySetter(QName name) {
      Object key = this.elementLocalNameCollision ? name : name.getLocalPart();
      return (PropertySetter)this.propertySetters.get(key);
   }

   protected PropertyGetter getPropertyGetter(QName name) {
      Object key = this.elementLocalNameCollision ? name : name.getLocalPart();
      return (PropertyGetter)this.propertyGetters.get(key);
   }

   public PropertyAccessor getPropertyAccessor(String ns, String name) {
      QName n = new QName(ns, name);
      final PropertySetter setter = this.getPropertySetter(n);
      final PropertyGetter getter = this.getPropertyGetter(n);
      return new PropertyAccessor() {
         public Object get(Object bean) throws DatabindingException {
            return getter.get(bean);
         }

         public void set(Object bean, Object value) throws DatabindingException {
            setter.set(bean, value);
         }
      };
   }
}
