package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ArrayElementLeafProperty<BeanT, ListT, ItemT> extends ArrayElementProperty<BeanT, ListT, ItemT> {
   private final Transducer<ItemT> xducer;

   public ArrayElementLeafProperty(JAXBContextImpl p, RuntimeElementPropertyInfo prop) {
      super(p, prop);

      assert prop.getTypes().size() == 1;

      this.xducer = ((RuntimeTypeRef)prop.getTypes().get(0)).getTransducer();

      assert this.xducer != null;

   }

   public void serializeItem(JaxBeanInfo bi, ItemT item, XMLSerializer w) throws SAXException, AccessorException, IOException, XMLStreamException {
      this.xducer.declareNamespace(item, w);
      w.endNamespaceDecls(item);
      w.endAttributes();
      this.xducer.writeText(w, item, this.fieldName);
   }
}
