package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Enumeration;

public class MultipleScopeNamespaceSupport extends NamespaceSupport {
   protected int[] fScope = new int[8];
   protected int fCurrentScope = 0;

   public MultipleScopeNamespaceSupport() {
      this.fScope[0] = 0;
   }

   public MultipleScopeNamespaceSupport(NamespaceContext context) {
      super(context);
      this.fScope[0] = 0;
   }

   public Enumeration getAllPrefixes() {
      int count = 0;
      String[] prefix;
      if (this.fPrefixes.length < this.fNamespace.length / 2) {
         prefix = new String[this.fNamespaceSize];
         this.fPrefixes = prefix;
      }

      prefix = null;
      boolean unique = true;

      for(int i = this.fContext[this.fScope[this.fCurrentScope]]; i <= this.fNamespaceSize - 2; i += 2) {
         String prefix = this.fNamespace[i];

         for(int k = 0; k < count; ++k) {
            if (this.fPrefixes[k] == prefix) {
               unique = false;
               break;
            }
         }

         if (unique) {
            this.fPrefixes[count++] = prefix;
         }

         unique = true;
      }

      return new NamespaceSupport.Prefixes(this.fPrefixes, count);
   }

   public int getScopeForContext(int context) {
      int scope;
      for(scope = this.fCurrentScope; context < this.fScope[scope]; --scope) {
      }

      return scope;
   }

   public String getPrefix(String uri) {
      return this.getPrefix(uri, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
   }

   public String getURI(String prefix) {
      return this.getURI(prefix, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
   }

   public String getPrefix(String uri, int context) {
      return this.getPrefix(uri, this.fContext[context + 1], this.fContext[this.fScope[this.getScopeForContext(context)]]);
   }

   public String getURI(String prefix, int context) {
      return this.getURI(prefix, this.fContext[context + 1], this.fContext[this.fScope[this.getScopeForContext(context)]]);
   }

   public String getPrefix(String uri, int start, int end) {
      if (uri == NamespaceContext.XML_URI) {
         return XMLSymbols.PREFIX_XML;
      } else if (uri == NamespaceContext.XMLNS_URI) {
         return XMLSymbols.PREFIX_XMLNS;
      } else {
         for(int i = start; i > end; i -= 2) {
            if (this.fNamespace[i - 1] == uri && this.getURI(this.fNamespace[i - 2]) == uri) {
               return this.fNamespace[i - 2];
            }
         }

         return null;
      }
   }

   public String getURI(String prefix, int start, int end) {
      if (prefix == XMLSymbols.PREFIX_XML) {
         return NamespaceContext.XML_URI;
      } else if (prefix == XMLSymbols.PREFIX_XMLNS) {
         return NamespaceContext.XMLNS_URI;
      } else {
         for(int i = start; i > end; i -= 2) {
            if (this.fNamespace[i - 2] == prefix) {
               return this.fNamespace[i - 1];
            }
         }

         return null;
      }
   }

   public void reset() {
      this.fCurrentContext = this.fScope[this.fCurrentScope];
      this.fNamespaceSize = this.fContext[this.fCurrentContext];
   }

   public void pushScope() {
      if (this.fCurrentScope + 1 == this.fScope.length) {
         int[] contextarray = new int[this.fScope.length * 2];
         System.arraycopy(this.fScope, 0, contextarray, 0, this.fScope.length);
         this.fScope = contextarray;
      }

      this.pushContext();
      this.fScope[++this.fCurrentScope] = this.fCurrentContext;
   }

   public void popScope() {
      this.fCurrentContext = this.fScope[this.fCurrentScope--];
      this.popContext();
   }
}
