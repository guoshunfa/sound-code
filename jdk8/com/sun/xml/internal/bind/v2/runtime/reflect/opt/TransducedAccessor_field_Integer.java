package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class TransducedAccessor_field_Integer extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printInt(((Bean)o).f_int);
   }

   public void parse(Object o, CharSequence lexical) {
      ((Bean)o).f_int = DatatypeConverterImpl._parseInt(lexical);
   }

   public boolean hasValue(Object o) {
      return true;
   }

   public void writeLeafElement(XMLSerializer w, Name tagName, Object o, String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
      w.leafElement(tagName, ((Bean)o).f_int, fieldName);
   }
}
