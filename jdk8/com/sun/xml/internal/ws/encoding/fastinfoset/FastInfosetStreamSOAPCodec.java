package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public abstract class FastInfosetStreamSOAPCodec implements Codec {
   private static final FastInfosetStreamReaderFactory READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();
   private StAXDocumentParser _statefulParser;
   private StAXDocumentSerializer _serializer;
   private final StreamSOAPCodec _soapCodec;
   private final boolean _retainState;
   protected final ContentType _defaultContentType;

   FastInfosetStreamSOAPCodec(StreamSOAPCodec soapCodec, SOAPVersion soapVersion, boolean retainState, String mimeType) {
      this._soapCodec = soapCodec;
      this._retainState = retainState;
      this._defaultContentType = new ContentTypeImpl(mimeType);
   }

   FastInfosetStreamSOAPCodec(FastInfosetStreamSOAPCodec that) {
      this._soapCodec = (StreamSOAPCodec)that._soapCodec.copy();
      this._retainState = that._retainState;
      this._defaultContentType = that._defaultContentType;
   }

   public String getMimeType() {
      return this._defaultContentType.getContentType();
   }

   public ContentType getStaticContentType(Packet packet) {
      return this.getContentType(packet.soapAction);
   }

   public ContentType encode(Packet packet, OutputStream out) {
      if (packet.getMessage() != null) {
         XMLStreamWriter writer = this.getXMLStreamWriter(out);

         try {
            packet.getMessage().writeTo(writer);
            writer.flush();
         } catch (XMLStreamException var5) {
            throw new WebServiceException(var5);
         }
      }

      return this.getContentType(packet.soapAction);
   }

   public ContentType encode(Packet packet, WritableByteChannel buffer) {
      throw new UnsupportedOperationException();
   }

   public void decode(InputStream in, String contentType, Packet response) throws IOException {
      response.setMessage(this._soapCodec.decode(this.getXMLStreamReader(in)));
   }

   public void decode(ReadableByteChannel in, String contentType, Packet response) {
      throw new UnsupportedOperationException();
   }

   protected abstract StreamHeader createHeader(XMLStreamReader var1, XMLStreamBuffer var2);

   protected abstract ContentType getContentType(String var1);

   private XMLStreamWriter getXMLStreamWriter(OutputStream out) {
      if (this._serializer != null) {
         this._serializer.setOutputStream(out);
         return this._serializer;
      } else {
         return this._serializer = FastInfosetCodec.createNewStreamWriter(out, this._retainState);
      }
   }

   private XMLStreamReader getXMLStreamReader(InputStream in) {
      if (this._retainState) {
         if (this._statefulParser != null) {
            this._statefulParser.setInputStream(in);
            return this._statefulParser;
         } else {
            return this._statefulParser = FastInfosetCodec.createNewStreamReader(in, this._retainState);
         }
      } else {
         return READER_FACTORY.doCreate((String)null, (InputStream)in, false);
      }
   }

   public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec soapCodec, SOAPVersion version) {
      return create(soapCodec, version, false);
   }

   public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec soapCodec, SOAPVersion version, boolean retainState) {
      if (version == null) {
         throw new IllegalArgumentException();
      } else {
         switch(version) {
         case SOAP_11:
            return new FastInfosetStreamSOAP11Codec(soapCodec, retainState);
         case SOAP_12:
            return new FastInfosetStreamSOAP12Codec(soapCodec, retainState);
         default:
            throw new AssertionError();
         }
      }
   }
}
