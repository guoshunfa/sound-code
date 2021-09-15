package com.sun.org.apache.xml.internal.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentSerializer extends AbstractSerializer {
   protected DocumentBuilderFactory dbf;

   public Node deserialize(byte[] var1, Node var2) throws XMLEncryptionException {
      byte[] var3 = createContext(var1, var2);
      return this.deserialize(var2, new InputSource(new ByteArrayInputStream(var3)));
   }

   public Node deserialize(String var1, Node var2) throws XMLEncryptionException {
      String var3 = createContext(var1, var2);
      return this.deserialize(var2, new InputSource(new StringReader(var3)));
   }

   private Node deserialize(Node var1, InputSource var2) throws XMLEncryptionException {
      try {
         if (this.dbf == null) {
            this.dbf = DocumentBuilderFactory.newInstance();
            this.dbf.setNamespaceAware(true);
            this.dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            this.dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
            this.dbf.setValidating(false);
         }

         DocumentBuilder var3 = this.dbf.newDocumentBuilder();
         Document var4 = var3.parse(var2);
         Document var5 = null;
         if (9 == var1.getNodeType()) {
            var5 = (Document)var1;
         } else {
            var5 = var1.getOwnerDocument();
         }

         Element var6 = (Element)var5.importNode(var4.getDocumentElement(), true);
         DocumentFragment var7 = var5.createDocumentFragment();

         for(Node var8 = var6.getFirstChild(); var8 != null; var8 = var6.getFirstChild()) {
            var6.removeChild(var8);
            var7.appendChild(var8);
         }

         return var7;
      } catch (SAXException var9) {
         throw new XMLEncryptionException("empty", var9);
      } catch (ParserConfigurationException var10) {
         throw new XMLEncryptionException("empty", var10);
      } catch (IOException var11) {
         throw new XMLEncryptionException("empty", var11);
      }
   }
}
