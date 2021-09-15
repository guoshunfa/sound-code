package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

public class FastInfosetUtil {
   public static XMLStreamReader createFIStreamReader(InputStream in) {
      if (FastInfosetReflection.fiStAXDocumentParser_new == null) {
         throw new XMLReaderException("fastinfoset.noImplementation", new Object[0]);
      } else {
         try {
            Object sdp = FastInfosetReflection.fiStAXDocumentParser_new.newInstance();
            FastInfosetReflection.fiStAXDocumentParser_setStringInterning.invoke(sdp, Boolean.TRUE);
            FastInfosetReflection.fiStAXDocumentParser_setInputStream.invoke(sdp, in);
            return (XMLStreamReader)sdp;
         } catch (Exception var2) {
            throw new XMLStreamReaderException(var2);
         }
      }
   }
}
