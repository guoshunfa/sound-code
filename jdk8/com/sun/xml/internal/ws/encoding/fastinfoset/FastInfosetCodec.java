package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;

public class FastInfosetCodec implements Codec {
   private static final int DEFAULT_INDEXED_STRING_SIZE_LIMIT = 32;
   private static final int DEFAULT_INDEXED_STRING_MEMORY_LIMIT = 4194304;
   private StAXDocumentParser _parser;
   private StAXDocumentSerializer _serializer;
   private final boolean _retainState;
   private final ContentType _contentType;

   FastInfosetCodec(boolean retainState) {
      this._retainState = retainState;
      this._contentType = retainState ? new ContentTypeImpl("application/vnd.sun.stateful.fastinfoset") : new ContentTypeImpl("application/fastinfoset");
   }

   public String getMimeType() {
      return this._contentType.getContentType();
   }

   public Codec copy() {
      return new FastInfosetCodec(this._retainState);
   }

   public ContentType getStaticContentType(Packet packet) {
      return this._contentType;
   }

   public ContentType encode(Packet packet, OutputStream out) {
      Message message = packet.getMessage();
      if (message != null && message.hasPayload()) {
         XMLStreamWriter writer = this.getXMLStreamWriter(out);

         try {
            writer.writeStartDocument();
            packet.getMessage().writePayloadTo(writer);
            writer.writeEndDocument();
            writer.flush();
         } catch (XMLStreamException var6) {
            throw new WebServiceException(var6);
         }
      }

      return this._contentType;
   }

   public ContentType encode(Packet packet, WritableByteChannel buffer) {
      throw new UnsupportedOperationException();
   }

   public void decode(InputStream in, String contentType, Packet packet) throws IOException {
      in = hasSomeData(in);
      Message message;
      if (in != null) {
         message = Messages.createUsingPayload((Source)(new FastInfosetSource(in)), SOAPVersion.SOAP_11);
      } else {
         message = Messages.createEmpty(SOAPVersion.SOAP_11);
      }

      packet.setMessage(message);
   }

   public void decode(ReadableByteChannel in, String contentType, Packet response) {
      throw new UnsupportedOperationException();
   }

   private XMLStreamWriter getXMLStreamWriter(OutputStream out) {
      if (this._serializer != null) {
         this._serializer.setOutputStream(out);
         return this._serializer;
      } else {
         return this._serializer = createNewStreamWriter(out, this._retainState);
      }
   }

   public static FastInfosetCodec create() {
      return create(false);
   }

   public static FastInfosetCodec create(boolean retainState) {
      return new FastInfosetCodec(retainState);
   }

   static StAXDocumentSerializer createNewStreamWriter(OutputStream out, boolean retainState) {
      return createNewStreamWriter(out, retainState, 32, 4194304);
   }

   static StAXDocumentSerializer createNewStreamWriter(OutputStream out, boolean retainState, int indexedStringSizeLimit, int stringsMemoryLimit) {
      StAXDocumentSerializer serializer = new StAXDocumentSerializer(out);
      if (retainState) {
         SerializerVocabulary vocabulary = new SerializerVocabulary();
         serializer.setVocabulary(vocabulary);
         serializer.setMinAttributeValueSize(0);
         serializer.setMaxAttributeValueSize(indexedStringSizeLimit);
         serializer.setMinCharacterContentChunkSize(0);
         serializer.setMaxCharacterContentChunkSize(indexedStringSizeLimit);
         serializer.setAttributeValueMapMemoryLimit(stringsMemoryLimit);
         serializer.setCharacterContentChunkMapMemoryLimit(stringsMemoryLimit);
      }

      return serializer;
   }

   static StAXDocumentParser createNewStreamReader(InputStream in, boolean retainState) {
      StAXDocumentParser parser = new StAXDocumentParser(in);
      parser.setStringInterning(true);
      if (retainState) {
         ParserVocabulary vocabulary = new ParserVocabulary();
         parser.setVocabulary(vocabulary);
      }

      return parser;
   }

   static StAXDocumentParser createNewStreamReaderRecyclable(InputStream in, boolean retainState) {
      StAXDocumentParser parser = new FastInfosetStreamReaderRecyclable(in);
      parser.setStringInterning(true);
      parser.setForceStreamClose(true);
      if (retainState) {
         ParserVocabulary vocabulary = new ParserVocabulary();
         parser.setVocabulary(vocabulary);
      }

      return parser;
   }

   private static InputStream hasSomeData(InputStream in) throws IOException {
      if (in != null && ((InputStream)in).available() < 1) {
         if (!((InputStream)in).markSupported()) {
            in = new BufferedInputStream((InputStream)in);
         }

         ((InputStream)in).mark(1);
         if (((InputStream)in).read() != -1) {
            ((InputStream)in).reset();
         } else {
            in = null;
         }
      }

      return (InputStream)in;
   }
}
