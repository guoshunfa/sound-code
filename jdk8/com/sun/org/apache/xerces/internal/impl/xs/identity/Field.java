package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.impl.xs.util.ShortListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class Field {
   protected Field.XPath fXPath;
   protected IdentityConstraint fIdentityConstraint;

   public Field(Field.XPath xpath, IdentityConstraint identityConstraint) {
      this.fXPath = xpath;
      this.fIdentityConstraint = identityConstraint;
   }

   public com.sun.org.apache.xerces.internal.impl.xpath.XPath getXPath() {
      return this.fXPath;
   }

   public IdentityConstraint getIdentityConstraint() {
      return this.fIdentityConstraint;
   }

   public XPathMatcher createMatcher(FieldActivator activator, ValueStore store) {
      return new Field.Matcher(this.fXPath, activator, store);
   }

   public String toString() {
      return this.fXPath.toString();
   }

   protected class Matcher extends XPathMatcher {
      protected FieldActivator fFieldActivator;
      protected ValueStore fStore;

      public Matcher(Field.XPath xpath, FieldActivator activator, ValueStore store) {
         super(xpath);
         this.fFieldActivator = activator;
         this.fStore = store;
      }

      protected void matched(Object actualValue, short valueType, ShortList itemValueType, boolean isNil) {
         super.matched(actualValue, valueType, itemValueType, isNil);
         if (isNil && Field.this.fIdentityConstraint.getCategory() == 1) {
            String code = "KeyMatchesNillable";
            this.fStore.reportError(code, new Object[]{Field.this.fIdentityConstraint.getElementName(), Field.this.fIdentityConstraint.getIdentityConstraintName()});
         }

         this.fStore.addValue(Field.this, actualValue, this.convertToPrimitiveKind(valueType), this.convertToPrimitiveKind(itemValueType));
         this.fFieldActivator.setMayMatch(Field.this, Boolean.FALSE);
      }

      private short convertToPrimitiveKind(short valueType) {
         if (valueType <= 20) {
            return valueType;
         } else if (valueType <= 29) {
            return 2;
         } else {
            return valueType <= 42 ? 4 : valueType;
         }
      }

      private ShortList convertToPrimitiveKind(ShortList itemValueType) {
         if (itemValueType != null) {
            int length = itemValueType.getLength();

            int i;
            for(i = 0; i < length; ++i) {
               short type = itemValueType.item(i);
               if (type != this.convertToPrimitiveKind(type)) {
                  break;
               }
            }

            if (i != length) {
               short[] arr = new short[length];

               for(int j = 0; j < i; ++j) {
                  arr[j] = itemValueType.item(j);
               }

               while(i < length) {
                  arr[i] = this.convertToPrimitiveKind(itemValueType.item(i));
                  ++i;
               }

               return new ShortListImpl(arr, arr.length);
            }
         }

         return itemValueType;
      }

      protected void handleContent(XSTypeDefinition type, boolean nillable, Object actualValue, short valueType, ShortList itemValueType) {
         if (type == null || type.getTypeCategory() == 15 && ((XSComplexTypeDefinition)type).getContentType() != 1) {
            this.fStore.reportError("cvc-id.3", new Object[]{Field.this.fIdentityConstraint.getName(), Field.this.fIdentityConstraint.getElementName()});
         }

         this.fMatchedString = actualValue;
         this.matched(this.fMatchedString, valueType, itemValueType, nillable);
      }
   }

   public static class XPath extends com.sun.org.apache.xerces.internal.impl.xpath.XPath {
      public XPath(String xpath, SymbolTable symbolTable, NamespaceContext context) throws XPathException {
         super(!xpath.trim().startsWith("/") && !xpath.trim().startsWith(".") ? "./" + xpath : xpath, symbolTable, context);

         for(int i = 0; i < this.fLocationPaths.length; ++i) {
            for(int j = 0; j < this.fLocationPaths[i].steps.length; ++j) {
               com.sun.org.apache.xerces.internal.impl.xpath.XPath.Axis axis = this.fLocationPaths[i].steps[j].axis;
               if (axis.type == 2 && j < this.fLocationPaths[i].steps.length - 1) {
                  throw new XPathException("c-fields-xpaths");
               }
            }
         }

      }
   }
}
