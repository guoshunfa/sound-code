package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import java.io.IOException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class TrAXFilter extends XMLFilterImpl {
   private Templates _templates;
   private TransformerImpl _transformer;
   private TransformerHandlerImpl _transformerHandler;
   private boolean _overrideDefaultParser;

   public TrAXFilter(Templates templates) throws TransformerConfigurationException {
      this._templates = templates;
      this._transformer = (TransformerImpl)templates.newTransformer();
      this._transformerHandler = new TransformerHandlerImpl(this._transformer);
      this._overrideDefaultParser = this._transformer.overrideDefaultParser();
   }

   public Transformer getTransformer() {
      return this._transformer;
   }

   private void createParent() throws SAXException {
      XMLReader parent = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, this._transformer.isSecureProcessing());
      this.setParent(parent);
   }

   public void parse(InputSource input) throws SAXException, IOException {
      XMLReader managedReader = null;

      try {
         if (this.getParent() == null) {
            try {
               managedReader = XMLReaderManager.getInstance(this._overrideDefaultParser).getXMLReader();
               this.setParent(managedReader);
            } catch (SAXException var7) {
               throw new SAXException(var7.toString());
            }
         }

         this.getParent().parse(input);
      } finally {
         if (managedReader != null) {
            XMLReaderManager.getInstance(this._overrideDefaultParser).releaseXMLReader(managedReader);
         }

      }

   }

   public void parse(String systemId) throws SAXException, IOException {
      this.parse(new InputSource(systemId));
   }

   public void setContentHandler(ContentHandler handler) {
      this._transformerHandler.setResult(new SAXResult(handler));
      if (this.getParent() == null) {
         try {
            this.createParent();
         } catch (SAXException var3) {
            return;
         }
      }

      this.getParent().setContentHandler(this._transformerHandler);
   }

   public void setErrorListener(ErrorListener handler) {
   }
}
