package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

final class SymbolTable {
   private final Map<String, Stylesheet> _stylesheets = new HashMap();
   private final Map<String, Vector> _primops = new HashMap();
   private Map<String, VariableBase> _variables = null;
   private Map<String, Template> _templates = null;
   private Map<String, AttributeSet> _attributeSets = null;
   private Map<String, String> _aliases = null;
   private Map<String, Integer> _excludedURI = null;
   private Stack<Map<String, Integer>> _excludedURIStack = null;
   private Map<String, DecimalFormatting> _decimalFormats = null;
   private Map<String, Key> _keys = null;
   private int _nsCounter = 0;
   private SyntaxTreeNode _current = null;

   public DecimalFormatting getDecimalFormatting(QName name) {
      return this._decimalFormats == null ? null : (DecimalFormatting)this._decimalFormats.get(name.getStringRep());
   }

   public void addDecimalFormatting(QName name, DecimalFormatting symbols) {
      if (this._decimalFormats == null) {
         this._decimalFormats = new HashMap();
      }

      this._decimalFormats.put(name.getStringRep(), symbols);
   }

   public Key getKey(QName name) {
      return this._keys == null ? null : (Key)this._keys.get(name.getStringRep());
   }

   public void addKey(QName name, Key key) {
      if (this._keys == null) {
         this._keys = new HashMap();
      }

      this._keys.put(name.getStringRep(), key);
   }

   public Stylesheet addStylesheet(QName name, Stylesheet node) {
      return (Stylesheet)this._stylesheets.put(name.getStringRep(), node);
   }

   public Stylesheet lookupStylesheet(QName name) {
      return (Stylesheet)this._stylesheets.get(name.getStringRep());
   }

   public Template addTemplate(Template template) {
      QName name = template.getName();
      if (this._templates == null) {
         this._templates = new HashMap();
      }

      return (Template)this._templates.put(name.getStringRep(), template);
   }

   public Template lookupTemplate(QName name) {
      return this._templates == null ? null : (Template)this._templates.get(name.getStringRep());
   }

   public Variable addVariable(Variable variable) {
      if (this._variables == null) {
         this._variables = new HashMap();
      }

      String name = variable.getName().getStringRep();
      return (Variable)this._variables.put(name, variable);
   }

   public Param addParam(Param parameter) {
      if (this._variables == null) {
         this._variables = new HashMap();
      }

      String name = parameter.getName().getStringRep();
      return (Param)this._variables.put(name, parameter);
   }

   public Variable lookupVariable(QName qname) {
      if (this._variables == null) {
         return null;
      } else {
         String name = qname.getStringRep();
         VariableBase obj = (VariableBase)this._variables.get(name);
         return obj instanceof Variable ? (Variable)obj : null;
      }
   }

   public Param lookupParam(QName qname) {
      if (this._variables == null) {
         return null;
      } else {
         String name = qname.getStringRep();
         VariableBase obj = (VariableBase)this._variables.get(name);
         return obj instanceof Param ? (Param)obj : null;
      }
   }

   public SyntaxTreeNode lookupName(QName qname) {
      if (this._variables == null) {
         return null;
      } else {
         String name = qname.getStringRep();
         return (SyntaxTreeNode)this._variables.get(name);
      }
   }

   public AttributeSet addAttributeSet(AttributeSet atts) {
      if (this._attributeSets == null) {
         this._attributeSets = new HashMap();
      }

      return (AttributeSet)this._attributeSets.put(atts.getName().getStringRep(), atts);
   }

   public AttributeSet lookupAttributeSet(QName name) {
      return this._attributeSets == null ? null : (AttributeSet)this._attributeSets.get(name.getStringRep());
   }

   public void addPrimop(String name, MethodType mtype) {
      Vector methods = (Vector)this._primops.get(name);
      if (methods == null) {
         this._primops.put(name, methods = new Vector());
      }

      methods.addElement(mtype);
   }

   public Vector lookupPrimop(String name) {
      return (Vector)this._primops.get(name);
   }

   public String generateNamespacePrefix() {
      return "ns" + this._nsCounter++;
   }

   public void setCurrentNode(SyntaxTreeNode node) {
      this._current = node;
   }

   public String lookupNamespace(String prefix) {
      return this._current == null ? "" : this._current.lookupNamespace(prefix);
   }

   public void addPrefixAlias(String prefix, String alias) {
      if (this._aliases == null) {
         this._aliases = new HashMap();
      }

      this._aliases.put(prefix, alias);
   }

   public String lookupPrefixAlias(String prefix) {
      return this._aliases == null ? null : (String)this._aliases.get(prefix);
   }

   public void excludeURI(String uri) {
      if (uri != null) {
         if (this._excludedURI == null) {
            this._excludedURI = new HashMap();
         }

         Integer refcnt = (Integer)this._excludedURI.get(uri);
         if (refcnt == null) {
            refcnt = 1;
         } else {
            refcnt = refcnt + 1;
         }

         this._excludedURI.put(uri, refcnt);
      }
   }

   public void excludeNamespaces(String prefixes) {
      if (prefixes != null) {
         StringTokenizer tokens = new StringTokenizer(prefixes);

         while(tokens.hasMoreTokens()) {
            String prefix = tokens.nextToken();
            String uri;
            if (prefix.equals("#default")) {
               uri = this.lookupNamespace("");
            } else {
               uri = this.lookupNamespace(prefix);
            }

            if (uri != null) {
               this.excludeURI(uri);
            }
         }
      }

   }

   public boolean isExcludedNamespace(String uri) {
      if (uri != null && this._excludedURI != null) {
         Integer refcnt = (Integer)this._excludedURI.get(uri);
         return refcnt != null && refcnt > 0;
      } else {
         return false;
      }
   }

   public void unExcludeNamespaces(String prefixes) {
      if (this._excludedURI != null) {
         if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);

            while(tokens.hasMoreTokens()) {
               String prefix = tokens.nextToken();
               String uri;
               if (prefix.equals("#default")) {
                  uri = this.lookupNamespace("");
               } else {
                  uri = this.lookupNamespace(prefix);
               }

               Integer refcnt = (Integer)this._excludedURI.get(uri);
               if (refcnt != null) {
                  this._excludedURI.put(uri, refcnt - 1);
               }
            }
         }

      }
   }

   public void pushExcludedNamespacesContext() {
      if (this._excludedURIStack == null) {
         this._excludedURIStack = new Stack();
      }

      this._excludedURIStack.push(this._excludedURI);
      this._excludedURI = null;
   }

   public void popExcludedNamespacesContext() {
      this._excludedURI = (Map)this._excludedURIStack.pop();
      if (this._excludedURIStack.isEmpty()) {
         this._excludedURIStack = null;
      }

   }
}
