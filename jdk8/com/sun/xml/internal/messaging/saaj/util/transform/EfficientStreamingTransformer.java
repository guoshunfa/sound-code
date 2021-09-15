package com.sun.xml.internal.messaging.saaj.util.transform;

import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

public class EfficientStreamingTransformer extends Transformer {
   private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
   private Transformer m_realTransformer = null;
   private Object m_fiDOMDocumentParser = null;
   private Object m_fiDOMDocumentSerializer = null;

   private EfficientStreamingTransformer() {
   }

   private void materialize() throws TransformerException {
      if (this.m_realTransformer == null) {
         this.m_realTransformer = this.transformerFactory.newTransformer();
      }

   }

   public void clearParameters() {
      if (this.m_realTransformer != null) {
         this.m_realTransformer.clearParameters();
      }

   }

   public ErrorListener getErrorListener() {
      try {
         this.materialize();
         return this.m_realTransformer.getErrorListener();
      } catch (TransformerException var2) {
         return null;
      }
   }

   public Properties getOutputProperties() {
      try {
         this.materialize();
         return this.m_realTransformer.getOutputProperties();
      } catch (TransformerException var2) {
         return null;
      }
   }

   public String getOutputProperty(String str) throws IllegalArgumentException {
      try {
         this.materialize();
         return this.m_realTransformer.getOutputProperty(str);
      } catch (TransformerException var3) {
         return null;
      }
   }

   public Object getParameter(String str) {
      try {
         this.materialize();
         return this.m_realTransformer.getParameter(str);
      } catch (TransformerException var3) {
         return null;
      }
   }

   public URIResolver getURIResolver() {
      try {
         this.materialize();
         return this.m_realTransformer.getURIResolver();
      } catch (TransformerException var2) {
         return null;
      }
   }

   public void setErrorListener(ErrorListener errorListener) throws IllegalArgumentException {
      try {
         this.materialize();
         this.m_realTransformer.setErrorListener(errorListener);
      } catch (TransformerException var3) {
      }

   }

   public void setOutputProperties(Properties properties) throws IllegalArgumentException {
      try {
         this.materialize();
         this.m_realTransformer.setOutputProperties(properties);
      } catch (TransformerException var3) {
      }

   }

   public void setOutputProperty(String str, String str1) throws IllegalArgumentException {
      try {
         this.materialize();
         this.m_realTransformer.setOutputProperty(str, str1);
      } catch (TransformerException var4) {
      }

   }

   public void setParameter(String str, Object obj) {
      try {
         this.materialize();
         this.m_realTransformer.setParameter(str, obj);
      } catch (TransformerException var4) {
      }

   }

   public void setURIResolver(URIResolver uRIResolver) {
      try {
         this.materialize();
         this.m_realTransformer.setURIResolver(uRIResolver);
      } catch (TransformerException var3) {
      }

   }

   private InputStream getInputStreamFromSource(StreamSource s) throws TransformerException {
      InputStream stream = s.getInputStream();
      if (stream != null) {
         return stream;
      } else if (s.getReader() != null) {
         return null;
      } else {
         String systemId = s.getSystemId();
         if (systemId != null) {
            try {
               String fileURL = systemId;
               if (systemId.startsWith("file:///")) {
                  String absolutePath = systemId.substring(7);
                  boolean hasDriveDesignator = absolutePath.indexOf(":") > 0;
                  if (hasDriveDesignator) {
                     String driveDesignatedPath = absolutePath.substring(1);
                     fileURL = driveDesignatedPath;
                  } else {
                     fileURL = absolutePath;
                  }
               }

               try {
                  return new FileInputStream(new File(new URI(fileURL)));
               } catch (URISyntaxException var8) {
                  throw new TransformerException(var8);
               }
            } catch (IOException var9) {
               throw new TransformerException(var9.toString());
            }
         } else {
            throw new TransformerException("Unexpected StreamSource object");
         }
      }
   }

   public void transform(Source source, Result result) throws TransformerException {
      if (source instanceof StreamSource && result instanceof StreamResult) {
         try {
            StreamSource streamSource = (StreamSource)source;
            InputStream is = this.getInputStreamFromSource(streamSource);
            OutputStream os = ((StreamResult)result).getOutputStream();
            if (os == null) {
               throw new TransformerException("Unexpected StreamResult object contains null OutputStream");
            }

            if (is != null) {
               if (is.markSupported()) {
                  is.mark(Integer.MAX_VALUE);
               }

               byte[] b = new byte[8192];

               int num;
               while((num = is.read(b)) != -1) {
                  os.write(b, 0, num);
               }

               if (is.markSupported()) {
                  is.reset();
               }

               return;
            }

            Reader reader = streamSource.getReader();
            if (reader != null) {
               if (reader.markSupported()) {
                  reader.mark(Integer.MAX_VALUE);
               }

               PushbackReader pushbackReader = new PushbackReader(reader, 4096);
               XMLDeclarationParser ev = new XMLDeclarationParser(pushbackReader);

               try {
                  ev.parse();
               } catch (Exception var12) {
                  throw new TransformerException("Unable to run the JAXP transformer on a stream " + var12.getMessage());
               }

               Writer writer = new OutputStreamWriter(os);
               ev.writeTo(writer);
               char[] ac = new char[8192];

               int num;
               while((num = pushbackReader.read(ac)) != -1) {
                  writer.write((char[])ac, 0, num);
               }

               writer.flush();
               if (reader.markSupported()) {
                  reader.reset();
               }

               return;
            }
         } catch (IOException var15) {
            var15.printStackTrace();
            throw new TransformerException(var15.toString());
         }

         throw new TransformerException("Unexpected StreamSource object");
      } else if (FastInfosetReflection.isFastInfosetSource(source) && result instanceof DOMResult) {
         try {
            if (this.m_fiDOMDocumentParser == null) {
               this.m_fiDOMDocumentParser = FastInfosetReflection.DOMDocumentParser_new();
            }

            FastInfosetReflection.DOMDocumentParser_parse(this.m_fiDOMDocumentParser, (Document)((DOMResult)result).getNode(), FastInfosetReflection.FastInfosetSource_getInputStream(source));
         } catch (Exception var13) {
            throw new TransformerException(var13);
         }
      } else if (source instanceof DOMSource && FastInfosetReflection.isFastInfosetResult(result)) {
         try {
            if (this.m_fiDOMDocumentSerializer == null) {
               this.m_fiDOMDocumentSerializer = FastInfosetReflection.DOMDocumentSerializer_new();
            }

            FastInfosetReflection.DOMDocumentSerializer_setOutputStream(this.m_fiDOMDocumentSerializer, FastInfosetReflection.FastInfosetResult_getOutputStream(result));
            FastInfosetReflection.DOMDocumentSerializer_serialize(this.m_fiDOMDocumentSerializer, ((DOMSource)source).getNode());
         } catch (Exception var14) {
            throw new TransformerException(var14);
         }
      } else {
         this.materialize();
         this.m_realTransformer.transform(source, result);
      }
   }

   public static Transformer newTransformer() {
      return new EfficientStreamingTransformer();
   }
}
