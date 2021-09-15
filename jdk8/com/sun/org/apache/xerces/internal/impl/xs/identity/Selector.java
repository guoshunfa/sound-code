package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class Selector {
   protected final Selector.XPath fXPath;
   protected final IdentityConstraint fIdentityConstraint;
   protected IdentityConstraint fIDConstraint;

   public Selector(Selector.XPath xpath, IdentityConstraint identityConstraint) {
      this.fXPath = xpath;
      this.fIdentityConstraint = identityConstraint;
   }

   public com.sun.org.apache.xerces.internal.impl.xpath.XPath getXPath() {
      return this.fXPath;
   }

   public IdentityConstraint getIDConstraint() {
      return this.fIdentityConstraint;
   }

   public XPathMatcher createMatcher(FieldActivator activator, int initialDepth) {
      return new Selector.Matcher(this.fXPath, activator, initialDepth);
   }

   public String toString() {
      return this.fXPath.toString();
   }

   public class Matcher extends XPathMatcher {
      protected final FieldActivator fFieldActivator;
      protected final int fInitialDepth;
      protected int fElementDepth;
      protected int fMatchedDepth;

      public Matcher(Selector.XPath xpath, FieldActivator activator, int initialDepth) {
         super(xpath);
         this.fFieldActivator = activator;
         this.fInitialDepth = initialDepth;
      }

      public void startDocumentFragment() {
         super.startDocumentFragment();
         this.fElementDepth = 0;
         this.fMatchedDepth = -1;
      }

      public void startElement(QName element, XMLAttributes attributes) {
         super.startElement(element, attributes);
         ++this.fElementDepth;
         if (this.isMatched()) {
            this.fMatchedDepth = this.fElementDepth;
            this.fFieldActivator.startValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
            int count = Selector.this.fIdentityConstraint.getFieldCount();

            for(int i = 0; i < count; ++i) {
               Field field = Selector.this.fIdentityConstraint.getFieldAt(i);
               XPathMatcher matcher = this.fFieldActivator.activateField(field, this.fInitialDepth);
               matcher.startElement(element, attributes);
            }
         }

      }

      public void endElement(QName element, XSTypeDefinition type, boolean nillable, Object actualValue, short valueType, ShortList itemValueType) {
         super.endElement(element, type, nillable, actualValue, valueType, itemValueType);
         if (this.fElementDepth-- == this.fMatchedDepth) {
            this.fMatchedDepth = -1;
            this.fFieldActivator.endValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
         }

      }

      public IdentityConstraint getIdentityConstraint() {
         return Selector.this.fIdentityConstraint;
      }

      public int getInitialDepth() {
         return this.fInitialDepth;
      }
   }

   public static class XPath extends com.sun.org.apache.xerces.internal.impl.xpath.XPath {
      public XPath(String xpath, SymbolTable symbolTable, NamespaceContext context) throws XPathException {
         super(normalize(xpath), symbolTable, context);

         for(int i = 0; i < this.fLocationPaths.length; ++i) {
            com.sun.org.apache.xerces.internal.impl.xpath.XPath.Axis axis = this.fLocationPaths[i].steps[this.fLocationPaths[i].steps.length - 1].axis;
            if (axis.type == 2) {
               throw new XPathException("c-selector-xpath");
            }
         }

      }

      private static String normalize(String xpath) {
         StringBuffer modifiedXPath = new StringBuffer(xpath.length() + 5);
         boolean var2 = true;

         while(true) {
            if (!XMLChar.trim(xpath).startsWith("/") && !XMLChar.trim(xpath).startsWith(".")) {
               modifiedXPath.append("./");
            }

            int unionIndex = xpath.indexOf(124);
            if (unionIndex == -1) {
               modifiedXPath.append(xpath);
               return modifiedXPath.toString();
            }

            modifiedXPath.append(xpath.substring(0, unionIndex + 1));
            xpath = xpath.substring(unionIndex + 1, xpath.length());
         }
      }
   }
}
