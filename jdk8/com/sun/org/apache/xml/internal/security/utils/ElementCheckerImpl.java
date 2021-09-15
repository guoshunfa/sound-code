package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** @deprecated */
@Deprecated
public abstract class ElementCheckerImpl implements ElementChecker {
   public boolean isNamespaceElement(Node var1, String var2, String var3) {
      return var1 != null && var3 == var1.getNamespaceURI() && var1.getLocalName().equals(var2);
   }

   public static class EmptyChecker extends ElementCheckerImpl {
      public void guaranteeThatElementInCorrectSpace(ElementProxy var1, Element var2) throws XMLSecurityException {
      }
   }

   public static class FullChecker extends ElementCheckerImpl {
      public void guaranteeThatElementInCorrectSpace(ElementProxy var1, Element var2) throws XMLSecurityException {
         String var3 = var1.getBaseLocalName();
         String var4 = var1.getBaseNamespace();
         String var5 = var2.getLocalName();
         String var6 = var2.getNamespaceURI();
         if (!var4.equals(var6) || !var3.equals(var5)) {
            Object[] var7 = new Object[]{var6 + ":" + var5, var4 + ":" + var3};
            throw new XMLSecurityException("xml.WrongElement", var7);
         }
      }
   }

   public static class InternedNsChecker extends ElementCheckerImpl {
      public void guaranteeThatElementInCorrectSpace(ElementProxy var1, Element var2) throws XMLSecurityException {
         String var3 = var1.getBaseLocalName();
         String var4 = var1.getBaseNamespace();
         String var5 = var2.getLocalName();
         String var6 = var2.getNamespaceURI();
         if (var4 != var6 || !var3.equals(var5)) {
            Object[] var7 = new Object[]{var6 + ":" + var5, var4 + ":" + var3};
            throw new XMLSecurityException("xml.WrongElement", var7);
         }
      }
   }
}
