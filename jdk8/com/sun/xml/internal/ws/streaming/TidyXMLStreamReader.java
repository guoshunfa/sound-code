package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderFilter;
import java.io.Closeable;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public class TidyXMLStreamReader extends XMLStreamReaderFilter {
   private final Closeable closeableSource;

   public TidyXMLStreamReader(@NotNull XMLStreamReader reader, @Nullable Closeable closeableSource) {
      super(reader);
      this.closeableSource = closeableSource;
   }

   public void close() throws XMLStreamException {
      super.close();

      try {
         if (this.closeableSource != null) {
            this.closeableSource.close();
         }

      } catch (IOException var2) {
         throw new WebServiceException(var2);
      }
   }
}
