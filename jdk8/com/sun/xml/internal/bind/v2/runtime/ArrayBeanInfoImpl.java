package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ArrayBeanInfoImpl extends JaxBeanInfo {
   private final Class itemType;
   private final JaxBeanInfo itemBeanInfo;
   private Loader loader;

   public ArrayBeanInfoImpl(JAXBContextImpl owner, RuntimeArrayInfo rai) {
      super(owner, rai, rai.getType(), (QName)rai.getTypeName(), false, true, false);
      this.itemType = this.jaxbType.getComponentType();
      this.itemBeanInfo = owner.getOrCreate((RuntimeTypeInfo)rai.getItemType());
   }

   protected void link(JAXBContextImpl grammar) {
      this.getLoader(grammar, false);
      super.link(grammar);
   }

   protected Object toArray(List list) {
      int len = list.size();
      Object array = Array.newInstance(this.itemType, len);

      for(int i = 0; i < len; ++i) {
         Array.set(array, i, list.get(i));
      }

      return array;
   }

   public void serializeBody(Object array, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
      int len = Array.getLength(array);

      for(int i = 0; i < len; ++i) {
         Object item = Array.get(array, i);
         target.startElement("", "item", (String)null, (Object)null);
         if (item == null) {
            target.writeXsiNilTrue();
         } else {
            target.childAsXsiType(item, "arrayItem", this.itemBeanInfo, false);
         }

         target.endElement();
      }

   }

   public final String getElementNamespaceURI(Object array) {
      throw new UnsupportedOperationException();
   }

   public final String getElementLocalName(Object array) {
      throw new UnsupportedOperationException();
   }

   public final Object createInstance(UnmarshallingContext context) {
      return new ArrayList();
   }

   public final boolean reset(Object array, UnmarshallingContext context) {
      return false;
   }

   public final String getId(Object array, XMLSerializer target) {
      return null;
   }

   public final void serializeAttributes(Object array, XMLSerializer target) {
   }

   public final void serializeRoot(Object array, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
      target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(array.getClass().getName()), (ValidationEventLocator)null, (Throwable)null));
   }

   public final void serializeURIs(Object array, XMLSerializer target) {
   }

   public final Transducer getTransducer() {
      return null;
   }

   public final Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
      if (this.loader == null) {
         this.loader = new ArrayBeanInfoImpl.ArrayLoader(context);
      }

      return this.loader;
   }

   private final class ArrayLoader extends Loader implements Receiver {
      private final Loader itemLoader;

      public ArrayLoader(JAXBContextImpl owner) {
         super(false);
         this.itemLoader = ArrayBeanInfoImpl.this.itemBeanInfo.getLoader(owner, true);
      }

      public void startElement(UnmarshallingContext.State state, TagName ea) {
         state.setTarget(new ArrayList());
      }

      public void leaveElement(UnmarshallingContext.State state, TagName ea) {
         state.setTarget(ArrayBeanInfoImpl.this.toArray((List)state.getTarget()));
      }

      public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         if (ea.matches("", "item")) {
            state.setLoader(this.itemLoader);
            state.setReceiver(this);
         } else {
            super.childElement(state, ea);
         }

      }

      public Collection<QName> getExpectedChildElements() {
         return Collections.singleton(new QName("", "item"));
      }

      public void receive(UnmarshallingContext.State state, Object o) {
         ((List)state.getTarget()).add(o);
      }
   }
}
