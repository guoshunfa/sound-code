package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class PropertyImpl<BeanT> implements Property<BeanT> {
   protected final String fieldName;
   private RuntimePropertyInfo propertyInfo = null;
   private boolean hiddenByOverride = false;

   public PropertyImpl(JAXBContextImpl context, RuntimePropertyInfo prop) {
      this.fieldName = prop.getName();
      if (context.retainPropertyInfo) {
         this.propertyInfo = prop;
      }

   }

   public RuntimePropertyInfo getInfo() {
      return this.propertyInfo;
   }

   public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
   }

   public void serializeURIs(BeanT o, XMLSerializer w) throws SAXException, AccessorException {
   }

   public boolean hasSerializeURIAction() {
      return false;
   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      return null;
   }

   public void wrapUp() {
   }

   public boolean isHiddenByOverride() {
      return this.hiddenByOverride;
   }

   public void setHiddenByOverride(boolean hidden) {
      this.hiddenByOverride = hidden;
   }

   public String getFieldName() {
      return this.fieldName;
   }
}
