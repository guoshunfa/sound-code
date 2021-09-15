package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class InlineBinaryTransducer<V> extends FilterTransducer<V> {
   public InlineBinaryTransducer(Transducer<V> core) {
      super(core);
   }

   @NotNull
   public CharSequence print(@NotNull V o) throws AccessorException {
      XMLSerializer w = XMLSerializer.getInstance();
      boolean old = w.setInlineBinaryFlag(true);

      CharSequence var4;
      try {
         var4 = this.core.print(o);
      } finally {
         w.setInlineBinaryFlag(old);
      }

      return var4;
   }

   public void writeText(XMLSerializer w, V o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
      boolean old = w.setInlineBinaryFlag(true);

      try {
         this.core.writeText(w, o, fieldName);
      } finally {
         w.setInlineBinaryFlag(old);
      }

   }

   public void writeLeafElement(XMLSerializer w, Name tagName, V o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
      boolean old = w.setInlineBinaryFlag(true);

      try {
         this.core.writeLeafElement(w, tagName, o, fieldName);
      } finally {
         w.setInlineBinaryFlag(old);
      }

   }
}
