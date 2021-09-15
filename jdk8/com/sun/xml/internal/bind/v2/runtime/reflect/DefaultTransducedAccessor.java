package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class DefaultTransducedAccessor<T> extends TransducedAccessor<T> {
   public abstract String print(T var1) throws AccessorException, SAXException;

   public void writeLeafElement(XMLSerializer w, Name tagName, T o, String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
      w.leafElement(tagName, this.print(o), fieldName);
   }

   public void writeText(XMLSerializer w, T o, String fieldName) throws AccessorException, SAXException, IOException, XMLStreamException {
      w.text(this.print(o), fieldName);
   }
}
