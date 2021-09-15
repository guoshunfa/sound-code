package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_WithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerPhysical;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Canonicalizer {
   public static final String ENCODING = "UTF8";
   public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE = "(.//. | .//@* | .//namespace::*)";
   public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
   public static final String ALGO_ID_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
   public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
   public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
   public static final String ALGO_ID_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
   public static final String ALGO_ID_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
   public static final String ALGO_ID_C14N_PHYSICAL = "http://santuario.apache.org/c14n/physical";
   private static Map<String, Class<? extends CanonicalizerSpi>> canonicalizerHash = new ConcurrentHashMap();
   private final CanonicalizerSpi canonicalizerSpi;

   private Canonicalizer(String var1) throws InvalidCanonicalizerException {
      try {
         Class var2 = (Class)canonicalizerHash.get(var1);
         this.canonicalizerSpi = (CanonicalizerSpi)var2.newInstance();
         this.canonicalizerSpi.reset = true;
      } catch (Exception var4) {
         Object[] var3 = new Object[]{var1};
         throw new InvalidCanonicalizerException("signature.Canonicalizer.UnknownCanonicalizer", var3, var4);
      }
   }

   public static final Canonicalizer getInstance(String var0) throws InvalidCanonicalizerException {
      return new Canonicalizer(var0);
   }

   public static void register(String var0, String var1) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
      JavaUtils.checkRegisterPermission();
      Class var2 = (Class)canonicalizerHash.get(var0);
      if (var2 != null) {
         Object[] var3 = new Object[]{var0, var2};
         throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", var3);
      } else {
         canonicalizerHash.put(var0, Class.forName(var1));
      }
   }

   public static void register(String var0, Class<? extends CanonicalizerSpi> var1) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
      JavaUtils.checkRegisterPermission();
      Class var2 = (Class)canonicalizerHash.get(var0);
      if (var2 != null) {
         Object[] var3 = new Object[]{var0, var2};
         throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", var3);
      } else {
         canonicalizerHash.put(var0, var1);
      }
   }

   public static void registerDefaultAlgorithms() {
      canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", Canonicalizer20010315OmitComments.class);
      canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", Canonicalizer20010315WithComments.class);
      canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", Canonicalizer20010315ExclOmitComments.class);
      canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", Canonicalizer20010315ExclWithComments.class);
      canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11", Canonicalizer11_OmitComments.class);
      canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", Canonicalizer11_WithComments.class);
      canonicalizerHash.put("http://santuario.apache.org/c14n/physical", CanonicalizerPhysical.class);
   }

   public final String getURI() {
      return this.canonicalizerSpi.engineGetURI();
   }

   public boolean getIncludeComments() {
      return this.canonicalizerSpi.engineGetIncludeComments();
   }

   public byte[] canonicalize(byte[] var1) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
      ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
      InputSource var3 = new InputSource(var2);
      DocumentBuilderFactory var4 = DocumentBuilderFactory.newInstance();
      var4.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
      var4.setNamespaceAware(true);
      var4.setValidating(true);
      DocumentBuilder var5 = var4.newDocumentBuilder();
      var5.setErrorHandler(new IgnoreAllErrorHandler());
      Document var6 = var5.parse(var3);
      return this.canonicalizeSubtree(var6);
   }

   public byte[] canonicalizeSubtree(Node var1) throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeSubTree(var1);
   }

   public byte[] canonicalizeSubtree(Node var1, String var2) throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeSubTree(var1, var2);
   }

   public byte[] canonicalizeXPathNodeSet(NodeList var1) throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(var1);
   }

   public byte[] canonicalizeXPathNodeSet(NodeList var1, String var2) throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(var1, var2);
   }

   public byte[] canonicalizeXPathNodeSet(Set<Node> var1) throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(var1);
   }

   public byte[] canonicalizeXPathNodeSet(Set<Node> var1, String var2) throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(var1, var2);
   }

   public void setWriter(OutputStream var1) {
      this.canonicalizerSpi.setWriter(var1);
   }

   public String getImplementingCanonicalizerClass() {
      return this.canonicalizerSpi.getClass().getName();
   }

   public void notReset() {
      this.canonicalizerSpi.reset = false;
   }
}
