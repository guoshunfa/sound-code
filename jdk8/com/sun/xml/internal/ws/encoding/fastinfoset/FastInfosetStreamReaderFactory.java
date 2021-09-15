package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLStreamReader;

public final class FastInfosetStreamReaderFactory extends XMLStreamReaderFactory {
   private static final FastInfosetStreamReaderFactory factory = new FastInfosetStreamReaderFactory();
   private ThreadLocal<StAXDocumentParser> pool = new ThreadLocal();

   public static FastInfosetStreamReaderFactory getInstance() {
      return factory;
   }

   public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
      StAXDocumentParser parser = this.fetch();
      if (parser == null) {
         return FastInfosetCodec.createNewStreamReaderRecyclable(in, false);
      } else {
         parser.setInputStream(in);
         return parser;
      }
   }

   public XMLStreamReader doCreate(String systemId, Reader reader, boolean rejectDTDs) {
      throw new UnsupportedOperationException();
   }

   private StAXDocumentParser fetch() {
      StAXDocumentParser parser = (StAXDocumentParser)this.pool.get();
      this.pool.set((Object)null);
      return parser;
   }

   public void doRecycle(XMLStreamReader r) {
      if (r instanceof StAXDocumentParser) {
         this.pool.set((StAXDocumentParser)r);
      }

   }
}
