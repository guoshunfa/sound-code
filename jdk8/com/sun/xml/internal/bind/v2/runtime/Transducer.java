package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface Transducer<ValueT> {
   boolean isDefault();

   boolean useNamespace();

   void declareNamespace(ValueT var1, XMLSerializer var2) throws AccessorException;

   @NotNull
   CharSequence print(@NotNull ValueT var1) throws AccessorException;

   ValueT parse(CharSequence var1) throws AccessorException, SAXException;

   void writeText(XMLSerializer var1, ValueT var2, String var3) throws IOException, SAXException, XMLStreamException, AccessorException;

   void writeLeafElement(XMLSerializer var1, Name var2, @NotNull ValueT var3, String var4) throws IOException, SAXException, XMLStreamException, AccessorException;

   QName getTypeName(@NotNull ValueT var1);
}
