package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerBase;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLSignatureInput {
   private InputStream inputOctetStreamProxy = null;
   private Set<Node> inputNodeSet = null;
   private Node subNode = null;
   private Node excludeNode = null;
   private boolean excludeComments = false;
   private boolean isNodeSet = false;
   private byte[] bytes = null;
   private String mimeType = null;
   private String sourceURI = null;
   private List<NodeFilter> nodeFilters = new ArrayList();
   private boolean needsToBeExpanded = false;
   private OutputStream outputStream = null;
   private DocumentBuilderFactory dfactory;

   public XMLSignatureInput(byte[] var1) {
      this.bytes = var1;
   }

   public XMLSignatureInput(InputStream var1) {
      this.inputOctetStreamProxy = var1;
   }

   public XMLSignatureInput(Node var1) {
      this.subNode = var1;
   }

   public XMLSignatureInput(Set<Node> var1) {
      this.inputNodeSet = var1;
   }

   public boolean isNeedsToBeExpanded() {
      return this.needsToBeExpanded;
   }

   public void setNeedsToBeExpanded(boolean var1) {
      this.needsToBeExpanded = var1;
   }

   public Set<Node> getNodeSet() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
      return this.getNodeSet(false);
   }

   public Set<Node> getInputNodeSet() {
      return this.inputNodeSet;
   }

   public Set<Node> getNodeSet(boolean var1) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
      if (this.inputNodeSet != null) {
         return this.inputNodeSet;
      } else if (this.inputOctetStreamProxy == null && this.subNode != null) {
         if (var1) {
            XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.subNode));
         }

         this.inputNodeSet = new LinkedHashSet();
         XMLUtils.getSet(this.subNode, this.inputNodeSet, this.excludeNode, this.excludeComments);
         return this.inputNodeSet;
      } else if (this.isOctetStream()) {
         this.convertToNodes();
         LinkedHashSet var2 = new LinkedHashSet();
         XMLUtils.getSet(this.subNode, var2, (Node)null, false);
         return var2;
      } else {
         throw new RuntimeException("getNodeSet() called but no input data present");
      }
   }

   public InputStream getOctetStream() throws IOException {
      if (this.inputOctetStreamProxy != null) {
         return this.inputOctetStreamProxy;
      } else if (this.bytes != null) {
         this.inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
         return this.inputOctetStreamProxy;
      } else {
         return null;
      }
   }

   public InputStream getOctetStreamReal() {
      return this.inputOctetStreamProxy;
   }

   public byte[] getBytes() throws IOException, CanonicalizationException {
      byte[] var1 = this.getBytesFromInputStream();
      if (var1 != null) {
         return var1;
      } else {
         Canonicalizer20010315OmitComments var2 = new Canonicalizer20010315OmitComments();
         this.bytes = var2.engineCanonicalize(this);
         return this.bytes;
      }
   }

   public boolean isNodeSet() {
      return this.inputOctetStreamProxy == null && this.inputNodeSet != null || this.isNodeSet;
   }

   public boolean isElement() {
      return this.inputOctetStreamProxy == null && this.subNode != null && this.inputNodeSet == null && !this.isNodeSet;
   }

   public boolean isOctetStream() {
      return (this.inputOctetStreamProxy != null || this.bytes != null) && this.inputNodeSet == null && this.subNode == null;
   }

   public boolean isOutputStreamSet() {
      return this.outputStream != null;
   }

   public boolean isByteArray() {
      return this.bytes != null && this.inputNodeSet == null && this.subNode == null;
   }

   public boolean isInitialized() {
      return this.isOctetStream() || this.isNodeSet();
   }

   public String getMIMEType() {
      return this.mimeType;
   }

   public void setMIMEType(String var1) {
      this.mimeType = var1;
   }

   public String getSourceURI() {
      return this.sourceURI;
   }

   public void setSourceURI(String var1) {
      this.sourceURI = var1;
   }

   public String toString() {
      if (this.isNodeSet()) {
         return "XMLSignatureInput/NodeSet/" + this.inputNodeSet.size() + " nodes/" + this.getSourceURI();
      } else if (this.isElement()) {
         return "XMLSignatureInput/Element/" + this.subNode + " exclude " + this.excludeNode + " comments:" + this.excludeComments + "/" + this.getSourceURI();
      } else {
         try {
            return "XMLSignatureInput/OctetStream/" + this.getBytes().length + " octets/" + this.getSourceURI();
         } catch (IOException var2) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
         } catch (CanonicalizationException var3) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
         }
      }
   }

   public String getHTMLRepresentation() throws XMLSignatureException {
      XMLSignatureInputDebugger var1 = new XMLSignatureInputDebugger(this);
      return var1.getHTMLRepresentation();
   }

   public String getHTMLRepresentation(Set<String> var1) throws XMLSignatureException {
      XMLSignatureInputDebugger var2 = new XMLSignatureInputDebugger(this, var1);
      return var2.getHTMLRepresentation();
   }

   public Node getExcludeNode() {
      return this.excludeNode;
   }

   public void setExcludeNode(Node var1) {
      this.excludeNode = var1;
   }

   public Node getSubNode() {
      return this.subNode;
   }

   public boolean isExcludeComments() {
      return this.excludeComments;
   }

   public void setExcludeComments(boolean var1) {
      this.excludeComments = var1;
   }

   public void updateOutputStream(OutputStream var1) throws CanonicalizationException, IOException {
      this.updateOutputStream(var1, false);
   }

   public void updateOutputStream(OutputStream var1, boolean var2) throws CanonicalizationException, IOException {
      if (var1 != this.outputStream) {
         if (this.bytes != null) {
            var1.write(this.bytes);
         } else if (this.inputOctetStreamProxy == null) {
            Object var3 = null;
            if (var2) {
               var3 = new Canonicalizer11_OmitComments();
            } else {
               var3 = new Canonicalizer20010315OmitComments();
            }

            ((CanonicalizerBase)var3).setWriter(var1);
            ((CanonicalizerBase)var3).engineCanonicalize(this);
         } else {
            byte[] var7 = new byte[4096];
            boolean var4 = false;

            int var8;
            try {
               while((var8 = this.inputOctetStreamProxy.read(var7)) != -1) {
                  var1.write(var7, 0, var8);
               }
            } catch (IOException var6) {
               this.inputOctetStreamProxy.close();
               throw var6;
            }
         }

      }
   }

   public void setOutputStream(OutputStream var1) {
      this.outputStream = var1;
   }

   private byte[] getBytesFromInputStream() throws IOException {
      if (this.bytes != null) {
         return this.bytes;
      } else if (this.inputOctetStreamProxy == null) {
         return null;
      } else {
         try {
            this.bytes = JavaUtils.getBytesFromStream(this.inputOctetStreamProxy);
         } finally {
            this.inputOctetStreamProxy.close();
         }

         return this.bytes;
      }
   }

   public void addNodeFilter(NodeFilter var1) {
      if (this.isOctetStream()) {
         try {
            this.convertToNodes();
         } catch (Exception var3) {
            throw new XMLSecurityRuntimeException("signature.XMLSignatureInput.nodesetReference", var3);
         }
      }

      this.nodeFilters.add(var1);
   }

   public List<NodeFilter> getNodeFilters() {
      return this.nodeFilters;
   }

   public void setNodeSet(boolean var1) {
      this.isNodeSet = var1;
   }

   void convertToNodes() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
      if (this.dfactory == null) {
         this.dfactory = DocumentBuilderFactory.newInstance();
         this.dfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
         this.dfactory.setValidating(false);
         this.dfactory.setNamespaceAware(true);
      }

      DocumentBuilder var1 = this.dfactory.newDocumentBuilder();

      try {
         var1.setErrorHandler(new IgnoreAllErrorHandler());
         Document var2 = var1.parse(this.getOctetStream());
         this.subNode = var2;
      } catch (SAXException var9) {
         ByteArrayOutputStream var3 = new ByteArrayOutputStream();
         var3.write("<container>".getBytes("UTF-8"));
         var3.write(this.getBytes());
         var3.write("</container>".getBytes("UTF-8"));
         byte[] var4 = var3.toByteArray();
         Document var5 = var1.parse((InputStream)(new ByteArrayInputStream(var4)));
         this.subNode = var5.getDocumentElement().getFirstChild().getFirstChild();
      } finally {
         if (this.inputOctetStreamProxy != null) {
            this.inputOctetStreamProxy.close();
         }

         this.inputOctetStreamProxy = null;
         this.bytes = null;
      }

   }
}
