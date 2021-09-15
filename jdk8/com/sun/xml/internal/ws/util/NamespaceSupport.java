package com.sun.xml.internal.ws.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class NamespaceSupport {
   public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
   private static final Iterable<String> EMPTY_ENUMERATION = new ArrayList();
   private NamespaceSupport.Context[] contexts;
   private NamespaceSupport.Context currentContext;
   private int contextPos;

   public NamespaceSupport() {
      this.reset();
   }

   public NamespaceSupport(NamespaceSupport that) {
      this.contexts = new NamespaceSupport.Context[that.contexts.length];
      this.currentContext = null;
      this.contextPos = that.contextPos;
      NamespaceSupport.Context currentParent = null;

      for(int i = 0; i < that.contexts.length; ++i) {
         NamespaceSupport.Context thatContext = that.contexts[i];
         if (thatContext == null) {
            this.contexts[i] = null;
         } else {
            NamespaceSupport.Context thisContext = new NamespaceSupport.Context(thatContext, currentParent);
            this.contexts[i] = thisContext;
            if (that.currentContext == thatContext) {
               this.currentContext = thisContext;
            }

            currentParent = thisContext;
         }
      }

   }

   public void reset() {
      this.contexts = new NamespaceSupport.Context[32];
      this.contextPos = 0;
      this.contexts[this.contextPos] = this.currentContext = new NamespaceSupport.Context();
      this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
   }

   public void pushContext() {
      int max = this.contexts.length;
      ++this.contextPos;
      if (this.contextPos >= max) {
         NamespaceSupport.Context[] newContexts = new NamespaceSupport.Context[max * 2];
         System.arraycopy(this.contexts, 0, newContexts, 0, max);
         this.contexts = newContexts;
      }

      this.currentContext = this.contexts[this.contextPos];
      if (this.currentContext == null) {
         this.contexts[this.contextPos] = this.currentContext = new NamespaceSupport.Context();
      }

      if (this.contextPos > 0) {
         this.currentContext.setParent(this.contexts[this.contextPos - 1]);
      }

   }

   public void popContext() {
      --this.contextPos;
      if (this.contextPos < 0) {
         throw new EmptyStackException();
      } else {
         this.currentContext = this.contexts[this.contextPos];
      }
   }

   public void slideContextUp() {
      --this.contextPos;
      this.currentContext = this.contexts[this.contextPos];
   }

   public void slideContextDown() {
      ++this.contextPos;
      if (this.contexts[this.contextPos] == null) {
         this.contexts[this.contextPos] = this.contexts[this.contextPos - 1];
      }

      this.currentContext = this.contexts[this.contextPos];
   }

   public boolean declarePrefix(String prefix, String uri) {
      if ((!prefix.equals("xml") || uri.equals("http://www.w3.org/XML/1998/namespace")) && !prefix.equals("xmlns")) {
         this.currentContext.declarePrefix(prefix, uri);
         return true;
      } else {
         return false;
      }
   }

   public String[] processName(String qName, String[] parts, boolean isAttribute) {
      String[] myParts = this.currentContext.processName(qName, isAttribute);
      if (myParts == null) {
         return null;
      } else {
         parts[0] = myParts[0];
         parts[1] = myParts[1];
         parts[2] = myParts[2];
         return parts;
      }
   }

   public String getURI(String prefix) {
      return this.currentContext.getURI(prefix);
   }

   public Iterable<String> getPrefixes() {
      return this.currentContext.getPrefixes();
   }

   public String getPrefix(String uri) {
      return this.currentContext.getPrefix(uri);
   }

   public Iterator getPrefixes(String uri) {
      List prefixes = new ArrayList();
      Iterator var3 = this.getPrefixes().iterator();

      while(var3.hasNext()) {
         String prefix = (String)var3.next();
         if (uri.equals(this.getURI(prefix))) {
            prefixes.add(prefix);
         }
      }

      return prefixes.iterator();
   }

   public Iterable<String> getDeclaredPrefixes() {
      return this.currentContext.getDeclaredPrefixes();
   }

   static final class Context {
      HashMap prefixTable;
      HashMap uriTable;
      HashMap elementNameTable;
      HashMap attributeNameTable;
      String defaultNS = null;
      private ArrayList declarations = null;
      private boolean tablesDirty = false;
      private NamespaceSupport.Context parent = null;

      Context() {
         this.copyTables();
      }

      Context(NamespaceSupport.Context that, NamespaceSupport.Context newParent) {
         if (that == null) {
            this.copyTables();
         } else {
            if (newParent != null && !that.tablesDirty) {
               this.prefixTable = that.prefixTable == that.parent.prefixTable ? newParent.prefixTable : (HashMap)that.prefixTable.clone();
               this.uriTable = that.uriTable == that.parent.uriTable ? newParent.uriTable : (HashMap)that.uriTable.clone();
               this.elementNameTable = that.elementNameTable == that.parent.elementNameTable ? newParent.elementNameTable : (HashMap)that.elementNameTable.clone();
               this.attributeNameTable = that.attributeNameTable == that.parent.attributeNameTable ? newParent.attributeNameTable : (HashMap)that.attributeNameTable.clone();
               this.defaultNS = that.defaultNS == that.parent.defaultNS ? newParent.defaultNS : that.defaultNS;
            } else {
               this.prefixTable = (HashMap)that.prefixTable.clone();
               this.uriTable = (HashMap)that.uriTable.clone();
               this.elementNameTable = (HashMap)that.elementNameTable.clone();
               this.attributeNameTable = (HashMap)that.attributeNameTable.clone();
               this.defaultNS = that.defaultNS;
            }

            this.tablesDirty = that.tablesDirty;
            this.parent = newParent;
            this.declarations = that.declarations == null ? null : (ArrayList)that.declarations.clone();
         }
      }

      void setParent(NamespaceSupport.Context parent) {
         this.parent = parent;
         this.declarations = null;
         this.prefixTable = parent.prefixTable;
         this.uriTable = parent.uriTable;
         this.elementNameTable = parent.elementNameTable;
         this.attributeNameTable = parent.attributeNameTable;
         this.defaultNS = parent.defaultNS;
         this.tablesDirty = false;
      }

      void declarePrefix(String prefix, String uri) {
         if (!this.tablesDirty) {
            this.copyTables();
         }

         if (this.declarations == null) {
            this.declarations = new ArrayList();
         }

         prefix = prefix.intern();
         uri = uri.intern();
         if ("".equals(prefix)) {
            if ("".equals(uri)) {
               this.defaultNS = null;
            } else {
               this.defaultNS = uri;
            }
         } else {
            this.prefixTable.put(prefix, uri);
            this.uriTable.put(uri, prefix);
         }

         this.declarations.add(prefix);
      }

      String[] processName(String qName, boolean isAttribute) {
         HashMap table;
         if (isAttribute) {
            table = this.elementNameTable;
         } else {
            table = this.attributeNameTable;
         }

         String[] name = (String[])((String[])table.get(qName));
         if (name != null) {
            return name;
         } else {
            name = new String[3];
            int index = qName.indexOf(58);
            if (index == -1) {
               if (!isAttribute && this.defaultNS != null) {
                  name[0] = this.defaultNS;
               } else {
                  name[0] = "";
               }

               name[1] = qName.intern();
               name[2] = name[1];
            } else {
               String prefix = qName.substring(0, index);
               String local = qName.substring(index + 1);
               String uri;
               if ("".equals(prefix)) {
                  uri = this.defaultNS;
               } else {
                  uri = (String)this.prefixTable.get(prefix);
               }

               if (uri == null) {
                  return null;
               }

               name[0] = uri;
               name[1] = local.intern();
               name[2] = qName.intern();
            }

            table.put(name[2], name);
            this.tablesDirty = true;
            return name;
         }
      }

      String getURI(String prefix) {
         if ("".equals(prefix)) {
            return this.defaultNS;
         } else {
            return this.prefixTable == null ? null : (String)this.prefixTable.get(prefix);
         }
      }

      String getPrefix(String uri) {
         return this.uriTable == null ? null : (String)this.uriTable.get(uri);
      }

      Iterable<String> getDeclaredPrefixes() {
         return (Iterable)(this.declarations == null ? NamespaceSupport.EMPTY_ENUMERATION : this.declarations);
      }

      Iterable<String> getPrefixes() {
         return (Iterable)(this.prefixTable == null ? NamespaceSupport.EMPTY_ENUMERATION : this.prefixTable.keySet());
      }

      private void copyTables() {
         if (this.prefixTable != null) {
            this.prefixTable = (HashMap)this.prefixTable.clone();
         } else {
            this.prefixTable = new HashMap();
         }

         if (this.uriTable != null) {
            this.uriTable = (HashMap)this.uriTable.clone();
         } else {
            this.uriTable = new HashMap();
         }

         this.elementNameTable = new HashMap();
         this.attributeNameTable = new HashMap();
         this.tablesDirty = true;
      }
   }
}
