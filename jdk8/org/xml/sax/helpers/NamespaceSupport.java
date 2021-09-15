package org.xml.sax.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamespaceSupport {
   public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
   public static final String NSDECL = "http://www.w3.org/xmlns/2000/";
   private static final Enumeration EMPTY_ENUMERATION = Collections.enumeration(new ArrayList());
   private NamespaceSupport.Context[] contexts;
   private NamespaceSupport.Context currentContext;
   private int contextPos;
   private boolean namespaceDeclUris;

   public NamespaceSupport() {
      this.reset();
   }

   public void reset() {
      this.contexts = new NamespaceSupport.Context[32];
      this.namespaceDeclUris = false;
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
         max *= 2;
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
      this.contexts[this.contextPos].clear();
      --this.contextPos;
      if (this.contextPos < 0) {
         throw new EmptyStackException();
      } else {
         this.currentContext = this.contexts[this.contextPos];
      }
   }

   public boolean declarePrefix(String prefix, String uri) {
      if (!prefix.equals("xml") && !prefix.equals("xmlns")) {
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

   public Enumeration getPrefixes() {
      return this.currentContext.getPrefixes();
   }

   public String getPrefix(String uri) {
      return this.currentContext.getPrefix(uri);
   }

   public Enumeration getPrefixes(String uri) {
      List<String> prefixes = new ArrayList();
      Enumeration allPrefixes = this.getPrefixes();

      while(allPrefixes.hasMoreElements()) {
         String prefix = (String)allPrefixes.nextElement();
         if (uri.equals(this.getURI(prefix))) {
            prefixes.add(prefix);
         }
      }

      return Collections.enumeration(prefixes);
   }

   public Enumeration getDeclaredPrefixes() {
      return this.currentContext.getDeclaredPrefixes();
   }

   public void setNamespaceDeclUris(boolean value) {
      if (this.contextPos != 0) {
         throw new IllegalStateException();
      } else if (value != this.namespaceDeclUris) {
         this.namespaceDeclUris = value;
         if (value) {
            this.currentContext.declarePrefix("xmlns", "http://www.w3.org/xmlns/2000/");
         } else {
            this.contexts[this.contextPos] = this.currentContext = new NamespaceSupport.Context();
            this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
         }

      }
   }

   public boolean isNamespaceDeclUris() {
      return this.namespaceDeclUris;
   }

   final class Context {
      Map<String, String> prefixTable;
      Map<String, String> uriTable;
      Map<String, String[]> elementNameTable;
      Map<String, String[]> attributeNameTable;
      String defaultNS = null;
      private List<String> declarations = null;
      private boolean declSeen = false;
      private NamespaceSupport.Context parent = null;

      Context() {
         this.copyTables();
      }

      void setParent(NamespaceSupport.Context parent) {
         this.parent = parent;
         this.declarations = null;
         this.prefixTable = parent.prefixTable;
         this.uriTable = parent.uriTable;
         this.elementNameTable = parent.elementNameTable;
         this.attributeNameTable = parent.attributeNameTable;
         this.defaultNS = parent.defaultNS;
         this.declSeen = false;
      }

      void clear() {
         this.parent = null;
         this.prefixTable = null;
         this.uriTable = null;
         this.elementNameTable = null;
         this.attributeNameTable = null;
         this.defaultNS = null;
      }

      void declarePrefix(String prefix, String uri) {
         if (!this.declSeen) {
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
         Map table;
         if (isAttribute) {
            table = this.attributeNameTable;
         } else {
            table = this.elementNameTable;
         }

         String[] name = (String[])((String[])table.get(qName));
         if (name != null) {
            return name;
         } else {
            name = new String[]{null, null, qName.intern()};
            int index = qName.indexOf(58);
            if (index == -1) {
               if (isAttribute) {
                  if (qName == "xmlns" && NamespaceSupport.this.namespaceDeclUris) {
                     name[0] = "http://www.w3.org/xmlns/2000/";
                  } else {
                     name[0] = "";
                  }
               } else if (this.defaultNS == null) {
                  name[0] = "";
               } else {
                  name[0] = this.defaultNS;
               }

               name[1] = name[2];
            } else {
               String prefix = qName.substring(0, index);
               String local = qName.substring(index + 1);
               String uri;
               if ("".equals(prefix)) {
                  uri = this.defaultNS;
               } else {
                  uri = (String)this.prefixTable.get(prefix);
               }

               if (uri == null || !isAttribute && "xmlns".equals(prefix)) {
                  return null;
               }

               name[0] = uri;
               name[1] = local.intern();
            }

            table.put(name[2], name);
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

      Enumeration getDeclaredPrefixes() {
         return this.declarations == null ? NamespaceSupport.EMPTY_ENUMERATION : Collections.enumeration(this.declarations);
      }

      Enumeration getPrefixes() {
         return this.prefixTable == null ? NamespaceSupport.EMPTY_ENUMERATION : Collections.enumeration(this.prefixTable.keySet());
      }

      private void copyTables() {
         if (this.prefixTable != null) {
            this.prefixTable = new HashMap(this.prefixTable);
         } else {
            this.prefixTable = new HashMap();
         }

         if (this.uriTable != null) {
            this.uriTable = new HashMap(this.uriTable);
         } else {
            this.uriTable = new HashMap();
         }

         this.elementNameTable = new HashMap();
         this.attributeNameTable = new HashMap();
         this.declSeen = true;
      }
   }
}
