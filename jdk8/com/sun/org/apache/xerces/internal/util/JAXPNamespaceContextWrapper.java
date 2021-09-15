package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

public final class JAXPNamespaceContextWrapper implements NamespaceContext {
   private javax.xml.namespace.NamespaceContext fNamespaceContext;
   private SymbolTable fSymbolTable;
   private List fPrefixes;
   private final Vector fAllPrefixes = new Vector();
   private int[] fContext = new int[8];
   private int fCurrentContext;

   public JAXPNamespaceContextWrapper(SymbolTable symbolTable) {
      this.setSymbolTable(symbolTable);
   }

   public void setNamespaceContext(javax.xml.namespace.NamespaceContext context) {
      this.fNamespaceContext = context;
   }

   public javax.xml.namespace.NamespaceContext getNamespaceContext() {
      return this.fNamespaceContext;
   }

   public void setSymbolTable(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
   }

   public SymbolTable getSymbolTable() {
      return this.fSymbolTable;
   }

   public void setDeclaredPrefixes(List prefixes) {
      this.fPrefixes = prefixes;
   }

   public List getDeclaredPrefixes() {
      return this.fPrefixes;
   }

   public String getURI(String prefix) {
      if (this.fNamespaceContext != null) {
         String uri = this.fNamespaceContext.getNamespaceURI(prefix);
         if (uri != null && !"".equals(uri)) {
            return this.fSymbolTable != null ? this.fSymbolTable.addSymbol(uri) : uri.intern();
         }
      }

      return null;
   }

   public String getPrefix(String uri) {
      if (this.fNamespaceContext != null) {
         if (uri == null) {
            uri = "";
         }

         String prefix = this.fNamespaceContext.getPrefix(uri);
         if (prefix == null) {
            prefix = "";
         }

         return this.fSymbolTable != null ? this.fSymbolTable.addSymbol(prefix) : prefix.intern();
      } else {
         return null;
      }
   }

   public Enumeration getAllPrefixes() {
      return Collections.enumeration(new TreeSet(this.fAllPrefixes));
   }

   public void pushContext() {
      if (this.fCurrentContext + 1 == this.fContext.length) {
         int[] contextarray = new int[this.fContext.length * 2];
         System.arraycopy(this.fContext, 0, contextarray, 0, this.fContext.length);
         this.fContext = contextarray;
      }

      this.fContext[++this.fCurrentContext] = this.fAllPrefixes.size();
      if (this.fPrefixes != null) {
         this.fAllPrefixes.addAll(this.fPrefixes);
      }

   }

   public void popContext() {
      this.fAllPrefixes.setSize(this.fContext[this.fCurrentContext--]);
   }

   public boolean declarePrefix(String prefix, String uri) {
      return true;
   }

   public int getDeclaredPrefixCount() {
      return this.fPrefixes != null ? this.fPrefixes.size() : 0;
   }

   public String getDeclaredPrefixAt(int index) {
      return (String)this.fPrefixes.get(index);
   }

   public void reset() {
      this.fCurrentContext = 0;
      this.fContext[this.fCurrentContext] = 0;
      this.fAllPrefixes.clear();
   }
}
