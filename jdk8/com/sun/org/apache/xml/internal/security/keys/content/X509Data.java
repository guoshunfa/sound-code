package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509CRL;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Digest;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class X509Data extends SignatureElementProxy implements KeyInfoContent {
   private static Logger log = Logger.getLogger(X509Data.class.getName());

   public X509Data(Document var1) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public X509Data(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);

      for(Node var3 = this.constructionElement.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
         if (var3.getNodeType() == 1) {
            return;
         }
      }

      Object[] var4 = new Object[]{"Elements", "X509Data"};
      throw new XMLSecurityException("xml.WrongContent", var4);
   }

   public void addIssuerSerial(String var1, BigInteger var2) {
      this.add(new XMLX509IssuerSerial(this.doc, var1, var2));
   }

   public void addIssuerSerial(String var1, String var2) {
      this.add(new XMLX509IssuerSerial(this.doc, var1, var2));
   }

   public void addIssuerSerial(String var1, int var2) {
      this.add(new XMLX509IssuerSerial(this.doc, var1, var2));
   }

   public void add(XMLX509IssuerSerial var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addSKI(byte[] var1) {
      this.add(new XMLX509SKI(this.doc, var1));
   }

   public void addSKI(X509Certificate var1) throws XMLSecurityException {
      this.add(new XMLX509SKI(this.doc, var1));
   }

   public void add(XMLX509SKI var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addSubjectName(String var1) {
      this.add(new XMLX509SubjectName(this.doc, var1));
   }

   public void addSubjectName(X509Certificate var1) {
      this.add(new XMLX509SubjectName(this.doc, var1));
   }

   public void add(XMLX509SubjectName var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addCertificate(X509Certificate var1) throws XMLSecurityException {
      this.add(new XMLX509Certificate(this.doc, var1));
   }

   public void addCertificate(byte[] var1) {
      this.add(new XMLX509Certificate(this.doc, var1));
   }

   public void add(XMLX509Certificate var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addCRL(byte[] var1) {
      this.add(new XMLX509CRL(this.doc, var1));
   }

   public void add(XMLX509CRL var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addDigest(X509Certificate var1, String var2) throws XMLSecurityException {
      this.add(new XMLX509Digest(this.doc, var1, var2));
   }

   public void addDigest(byte[] var1, String var2) {
      this.add(new XMLX509Digest(this.doc, var1, var2));
   }

   public void add(XMLX509Digest var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addUnknownElement(Element var1) {
      this.constructionElement.appendChild(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public int lengthIssuerSerial() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "X509IssuerSerial");
   }

   public int lengthSKI() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "X509SKI");
   }

   public int lengthSubjectName() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "X509SubjectName");
   }

   public int lengthCertificate() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
   }

   public int lengthCRL() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "X509CRL");
   }

   public int lengthDigest() {
      return this.length("http://www.w3.org/2009/xmldsig11#", "X509Digest");
   }

   public int lengthUnknownElement() {
      int var1 = 0;

      for(Node var2 = this.constructionElement.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if (var2.getNodeType() == 1 && !var2.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
            ++var1;
         }
      }

      return var1;
   }

   public XMLX509IssuerSerial itemIssuerSerial(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509IssuerSerial", var1);
      return var2 != null ? new XMLX509IssuerSerial(var2, this.baseURI) : null;
   }

   public XMLX509SKI itemSKI(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509SKI", var1);
      return var2 != null ? new XMLX509SKI(var2, this.baseURI) : null;
   }

   public XMLX509SubjectName itemSubjectName(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509SubjectName", var1);
      return var2 != null ? new XMLX509SubjectName(var2, this.baseURI) : null;
   }

   public XMLX509Certificate itemCertificate(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509Certificate", var1);
      return var2 != null ? new XMLX509Certificate(var2, this.baseURI) : null;
   }

   public XMLX509CRL itemCRL(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509CRL", var1);
      return var2 != null ? new XMLX509CRL(var2, this.baseURI) : null;
   }

   public XMLX509Digest itemDigest(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDs11Node(this.constructionElement.getFirstChild(), "X509Digest", var1);
      return var2 != null ? new XMLX509Digest(var2, this.baseURI) : null;
   }

   public Element itemUnknownElement(int var1) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "itemUnknownElement not implemented:" + var1);
      }

      return null;
   }

   public boolean containsIssuerSerial() {
      return this.lengthIssuerSerial() > 0;
   }

   public boolean containsSKI() {
      return this.lengthSKI() > 0;
   }

   public boolean containsSubjectName() {
      return this.lengthSubjectName() > 0;
   }

   public boolean containsCertificate() {
      return this.lengthCertificate() > 0;
   }

   public boolean containsDigest() {
      return this.lengthDigest() > 0;
   }

   public boolean containsCRL() {
      return this.lengthCRL() > 0;
   }

   public boolean containsUnknownElement() {
      return this.lengthUnknownElement() > 0;
   }

   public String getBaseLocalName() {
      return "X509Data";
   }
}
