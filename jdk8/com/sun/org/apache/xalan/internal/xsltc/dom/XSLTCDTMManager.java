package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.trax.DOM2SAX;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXEvent2SAX;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXStream2SAX;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XSLTCDTMManager extends DTMManagerDefault {
   private static final boolean DUMPTREE = false;
   private static final boolean DEBUG = false;

   public static XSLTCDTMManager newInstance() {
      return new XSLTCDTMManager();
   }

   public static XSLTCDTMManager createNewDTMManagerInstance() {
      return newInstance();
   }

   public DTM getDTM(Source source, boolean unique, DTMWSFilter whiteSpaceFilter, boolean incremental, boolean doIndexing) {
      return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, false, 0, true, false);
   }

   public DTM getDTM(Source source, boolean unique, DTMWSFilter whiteSpaceFilter, boolean incremental, boolean doIndexing, boolean buildIdIndex) {
      return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, false, 0, buildIdIndex, false);
   }

   public DTM getDTM(Source source, boolean unique, DTMWSFilter whiteSpaceFilter, boolean incremental, boolean doIndexing, boolean buildIdIndex, boolean newNameTable) {
      return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, false, 0, buildIdIndex, newNameTable);
   }

   public DTM getDTM(Source source, boolean unique, DTMWSFilter whiteSpaceFilter, boolean incremental, boolean doIndexing, boolean hasUserReader, int size, boolean buildIdIndex) {
      return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, hasUserReader, size, buildIdIndex, false);
   }

   public DTM getDTM(Source source, boolean unique, DTMWSFilter whiteSpaceFilter, boolean incremental, boolean doIndexing, boolean hasUserReader, int size, boolean buildIdIndex, boolean newNameTable) {
      int dtmPos = this.getFirstFreeDTMID();
      int documentID = dtmPos << 16;
      SAXImpl dtm;
      if (null != source && source instanceof StAXSource) {
         StAXSource staxSource = (StAXSource)source;
         StAXEvent2SAX staxevent2sax = null;
         StAXStream2SAX staxStream2SAX = null;
         if (staxSource.getXMLEventReader() != null) {
            XMLEventReader xmlEventReader = staxSource.getXMLEventReader();
            staxevent2sax = new StAXEvent2SAX(xmlEventReader);
         } else if (staxSource.getXMLStreamReader() != null) {
            XMLStreamReader xmlStreamReader = staxSource.getXMLStreamReader();
            staxStream2SAX = new StAXStream2SAX(xmlStreamReader);
         }

         if (size <= 0) {
            dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, (XMLStringFactory)null, doIndexing, 512, buildIdIndex, newNameTable);
         } else {
            dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, (XMLStringFactory)null, doIndexing, size, buildIdIndex, newNameTable);
         }

         dtm.setDocumentURI(source.getSystemId());
         this.addDTM(dtm, dtmPos, 0);

         try {
            if (staxevent2sax != null) {
               staxevent2sax.setContentHandler(dtm);
               staxevent2sax.parse();
            } else if (staxStream2SAX != null) {
               staxStream2SAX.setContentHandler(dtm);
               staxStream2SAX.parse();
            }

            return dtm;
         } catch (RuntimeException var29) {
            throw var29;
         } catch (Exception var30) {
            throw new WrappedRuntimeException(var30);
         }
      } else if (null != source && source instanceof DOMSource) {
         DOMSource domsrc = (DOMSource)source;
         Node node = domsrc.getNode();
         DOM2SAX dom2sax = new DOM2SAX(node);
         if (size <= 0) {
            dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, (XMLStringFactory)null, doIndexing, 512, buildIdIndex, newNameTable);
         } else {
            dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, (XMLStringFactory)null, doIndexing, size, buildIdIndex, newNameTable);
         }

         dtm.setDocumentURI(source.getSystemId());
         this.addDTM(dtm, dtmPos, 0);
         dom2sax.setContentHandler(dtm);

         try {
            dom2sax.parse();
            return dtm;
         } catch (RuntimeException var31) {
            throw var31;
         } catch (Exception var32) {
            throw new WrappedRuntimeException(var32);
         }
      } else {
         boolean isSAXSource = null != source ? source instanceof SAXSource : true;
         boolean isStreamSource = null != source ? source instanceof StreamSource : false;
         if (!isSAXSource && !isStreamSource) {
            throw new DTMException(XMLMessages.createXMLMessage("ER_NOT_SUPPORTED", new Object[]{source}));
         } else {
            XMLReader reader;
            InputSource xmlSource;
            if (null == source) {
               xmlSource = null;
               reader = null;
               hasUserReader = false;
            } else {
               reader = this.getXMLReader(source);
               xmlSource = SAXSource.sourceToInputSource(source);
               String urlOfSource = xmlSource.getSystemId();
               if (null != urlOfSource) {
                  try {
                     urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
                  } catch (Exception var37) {
                     System.err.println("Can not absolutize URL: " + urlOfSource);
                  }

                  xmlSource.setSystemId(urlOfSource);
               }
            }

            SAXImpl dtm;
            if (size <= 0) {
               dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, (XMLStringFactory)null, doIndexing, 512, buildIdIndex, newNameTable);
            } else {
               dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, (XMLStringFactory)null, doIndexing, size, buildIdIndex, newNameTable);
            }

            this.addDTM(dtm, dtmPos, 0);
            if (null == reader) {
               return dtm;
            } else {
               reader.setContentHandler(dtm.getBuilder());
               if (!hasUserReader || null == reader.getDTDHandler()) {
                  reader.setDTDHandler(dtm);
               }

               if (!hasUserReader || null == reader.getErrorHandler()) {
                  reader.setErrorHandler(dtm);
               }

               try {
                  reader.setProperty("http://xml.org/sax/properties/lexical-handler", dtm);
               } catch (SAXNotRecognizedException var35) {
               } catch (SAXNotSupportedException var36) {
               }

               try {
                  reader.parse(xmlSource);
               } catch (RuntimeException var33) {
                  throw var33;
               } catch (Exception var34) {
                  throw new WrappedRuntimeException(var34);
               } finally {
                  if (!hasUserReader) {
                     this.releaseXMLReader(reader);
                  }

               }

               return dtm;
            }
         }
      }
   }
}
