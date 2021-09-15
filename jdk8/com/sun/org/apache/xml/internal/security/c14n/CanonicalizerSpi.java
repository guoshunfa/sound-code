package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class CanonicalizerSpi {
   protected boolean reset = false;

   public byte[] engineCanonicalize(byte[] var1) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
      ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
      InputSource var3 = new InputSource(var2);
      DocumentBuilderFactory var4 = DocumentBuilderFactory.newInstance();
      var4.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
      var4.setNamespaceAware(true);
      DocumentBuilder var5 = var4.newDocumentBuilder();
      Document var6 = var5.parse(var3);
      return this.engineCanonicalizeSubTree(var6);
   }

   public byte[] engineCanonicalizeXPathNodeSet(NodeList var1) throws CanonicalizationException {
      return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(var1));
   }

   public byte[] engineCanonicalizeXPathNodeSet(NodeList var1, String var2) throws CanonicalizationException {
      return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(var1), var2);
   }

   public abstract String engineGetURI();

   public abstract boolean engineGetIncludeComments();

   public abstract byte[] engineCanonicalizeXPathNodeSet(Set<Node> var1) throws CanonicalizationException;

   public abstract byte[] engineCanonicalizeXPathNodeSet(Set<Node> var1, String var2) throws CanonicalizationException;

   public abstract byte[] engineCanonicalizeSubTree(Node var1) throws CanonicalizationException;

   public abstract byte[] engineCanonicalizeSubTree(Node var1, String var2) throws CanonicalizationException;

   public abstract void setWriter(OutputStream var1);
}
