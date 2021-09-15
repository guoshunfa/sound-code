package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class LiteralElement extends Instruction {
   private String _name;
   private LiteralElement _literalElemParent = null;
   private List<SyntaxTreeNode> _attributeElements = null;
   private Map<String, String> _accessedPrefixes = null;
   private boolean _allAttributesUnique = false;

   public QName getName() {
      return this._qname;
   }

   public void display(int indent) {
      this.indent(indent);
      Util.println("LiteralElement name = " + this._name);
      this.displayContents(indent + 4);
   }

   private String accessedNamespace(String prefix) {
      if (this._literalElemParent != null) {
         String result = this._literalElemParent.accessedNamespace(prefix);
         if (result != null) {
            return result;
         }
      }

      return this._accessedPrefixes != null ? (String)this._accessedPrefixes.get(prefix) : null;
   }

   public void registerNamespace(String prefix, String uri, SymbolTable stable, boolean declared) {
      String old;
      if (this._literalElemParent != null) {
         old = this._literalElemParent.accessedNamespace(prefix);
         if (old != null && old.equals(uri)) {
            return;
         }
      }

      if (this._accessedPrefixes == null) {
         this._accessedPrefixes = new Hashtable();
      } else if (!declared) {
         old = (String)this._accessedPrefixes.get(prefix);
         if (old != null) {
            if (old.equals(uri)) {
               return;
            }

            prefix = stable.generateNamespacePrefix();
         }
      }

      if (!prefix.equals("xml")) {
         this._accessedPrefixes.put(prefix, uri);
      }

   }

   private String translateQName(QName qname, SymbolTable stable) {
      String localname = qname.getLocalPart();
      String prefix = qname.getPrefix();
      if (prefix == null) {
         prefix = "";
      } else if (prefix.equals("xmlns")) {
         return "xmlns";
      }

      String alternative = stable.lookupPrefixAlias(prefix);
      if (alternative != null) {
         stable.excludeNamespaces(prefix);
         prefix = alternative;
      }

      String uri = this.lookupNamespace(prefix);
      if (uri == null) {
         return localname;
      } else {
         this.registerNamespace(prefix, uri, stable, false);
         return prefix != "" ? prefix + ":" + localname : localname;
      }
   }

   public void addAttribute(SyntaxTreeNode attribute) {
      if (this._attributeElements == null) {
         this._attributeElements = new ArrayList(2);
      }

      this._attributeElements.add(attribute);
   }

   public void setFirstAttribute(SyntaxTreeNode attribute) {
      if (this._attributeElements == null) {
         this._attributeElements = new ArrayList(2);
      }

      this._attributeElements.add(0, attribute);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._attributeElements != null) {
         Iterator var2 = this._attributeElements.iterator();

         while(var2.hasNext()) {
            SyntaxTreeNode node = (SyntaxTreeNode)var2.next();
            node.typeCheck(stable);
         }
      }

      this.typeCheckContents(stable);
      return Type.Void;
   }

   public Set<Map.Entry<String, String>> getNamespaceScope(SyntaxTreeNode node) {
      HashMap all;
      for(all = new HashMap(); node != null; node = node.getParent()) {
         Map<String, String> mapping = node.getPrefixMapping();
         if (mapping != null) {
            Iterator var4 = mapping.keySet().iterator();

            while(var4.hasNext()) {
               String prefix = (String)var4.next();
               if (!all.containsKey(prefix)) {
                  all.put(prefix, mapping.get(prefix));
               }
            }
         }
      }

      return all.entrySet();
   }

   public void parseContents(Parser parser) {
      SymbolTable stable = parser.getSymbolTable();
      stable.setCurrentNode(this);
      SyntaxTreeNode parent = this.getParent();
      if (parent != null && parent instanceof LiteralElement) {
         this._literalElemParent = (LiteralElement)parent;
      }

      this._name = this.translateQName(this._qname, stable);
      int count = this._attributes.getLength();

      String val;
      String prefix;
      for(int i = 0; i < count; ++i) {
         QName qname = parser.getQName(this._attributes.getQName(i));
         String uri = qname.getNamespace();
         val = this._attributes.getValue(i);
         if (qname.equals(parser.getUseAttributeSets())) {
            if (!Util.isValidQNames(val)) {
               ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", val, this);
               parser.reportError(3, err);
            }

            this.setFirstAttribute(new UseAttributeSets(val, parser));
         } else if (qname.equals(parser.getExtensionElementPrefixes())) {
            stable.excludeNamespaces(val);
         } else if (qname.equals(parser.getExcludeResultPrefixes())) {
            stable.excludeNamespaces(val);
         } else {
            prefix = qname.getPrefix();
            if ((prefix == null || !prefix.equals("xmlns")) && (prefix != null || !qname.getLocalPart().equals("xmlns")) && (uri == null || !uri.equals("http://www.w3.org/1999/XSL/Transform"))) {
               String name = this.translateQName(qname, stable);
               LiteralAttribute attr = new LiteralAttribute(name, val, parser, this);
               this.addAttribute(attr);
               attr.setParent(this);
               attr.parseContents(parser);
            }
         }
      }

      Set<Map.Entry<String, String>> include = this.getNamespaceScope(this);
      Iterator var13 = include.iterator();

      while(var13.hasNext()) {
         Map.Entry<String, String> entry = (Map.Entry)var13.next();
         val = (String)entry.getKey();
         if (!val.equals("xml")) {
            prefix = this.lookupNamespace(val);
            if (prefix != null && !stable.isExcludedNamespace(prefix)) {
               this.registerNamespace(val, prefix, stable, true);
            }
         }
      }

      this.parseChildren(parser);

      for(int i = 0; i < count; ++i) {
         QName qname = parser.getQName(this._attributes.getQName(i));
         val = this._attributes.getValue(i);
         if (qname.equals(parser.getExtensionElementPrefixes())) {
            stable.unExcludeNamespaces(val);
         } else if (qname.equals(parser.getExcludeResultPrefixes())) {
            stable.unExcludeNamespaces(val);
         }
      }

   }

   protected boolean contextDependent() {
      return this.dependentContents();
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      this._allAttributesUnique = this.checkAttributesUnique();
      il.append(methodGen.loadHandler());
      il.append((CompoundInstruction)(new PUSH(cpg, this._name)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP2);
      il.append(methodGen.startElement());

      for(int j = 0; j < this.elementCount(); ++j) {
         SyntaxTreeNode item = this.elementAt(j);
         if (item instanceof Variable) {
            item.translate(classGen, methodGen);
         }
      }

      Iterator var10;
      if (this._accessedPrefixes != null) {
         var10 = this._accessedPrefixes.entrySet().iterator();

         while(var10.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var10.next();
            String prefix = (String)entry.getKey();
            String uri = (String)entry.getValue();
            il.append(methodGen.loadHandler());
            il.append((CompoundInstruction)(new PUSH(cpg, prefix)));
            il.append((CompoundInstruction)(new PUSH(cpg, uri)));
            il.append(methodGen.namespace());
         }
      }

      if (this._attributeElements != null) {
         var10 = this._attributeElements.iterator();

         while(var10.hasNext()) {
            SyntaxTreeNode node = (SyntaxTreeNode)var10.next();
            if (!(node instanceof XslAttribute)) {
               node.translate(classGen, methodGen);
            }
         }
      }

      this.translateContents(classGen, methodGen);
      il.append(methodGen.endElement());
   }

   private boolean isHTMLOutput() {
      return this.getStylesheet().getOutputMethod() == 2;
   }

   public ElemDesc getElemDesc() {
      return this.isHTMLOutput() ? ToHTMLStream.getElemDesc(this._name) : null;
   }

   public boolean allAttributesUnique() {
      return this._allAttributesUnique;
   }

   private boolean checkAttributesUnique() {
      boolean hasHiddenXslAttribute = this.canProduceAttributeNodes(this, true);
      if (hasHiddenXslAttribute) {
         return false;
      } else {
         if (this._attributeElements != null) {
            int numAttrs = this._attributeElements.size();
            Map<String, SyntaxTreeNode> attrsTable = null;

            for(int i = 0; i < numAttrs; ++i) {
               SyntaxTreeNode node = (SyntaxTreeNode)this._attributeElements.get(i);
               if (node instanceof UseAttributeSets) {
                  return false;
               }

               if (node instanceof XslAttribute) {
                  if (attrsTable == null) {
                     attrsTable = new HashMap();

                     for(int k = 0; k < i; ++k) {
                        SyntaxTreeNode n = (SyntaxTreeNode)this._attributeElements.get(k);
                        if (n instanceof LiteralAttribute) {
                           LiteralAttribute literalAttr = (LiteralAttribute)n;
                           attrsTable.put(literalAttr.getName(), literalAttr);
                        }
                     }
                  }

                  XslAttribute xslAttr = (XslAttribute)node;
                  AttributeValue attrName = xslAttr.getName();
                  if (attrName instanceof AttributeValueTemplate) {
                     return false;
                  }

                  if (attrName instanceof SimpleAttributeValue) {
                     SimpleAttributeValue simpleAttr = (SimpleAttributeValue)attrName;
                     String name = simpleAttr.toString();
                     if (name != null && attrsTable.get(name) != null) {
                        return false;
                     }

                     if (name != null) {
                        attrsTable.put(name, xslAttr);
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean canProduceAttributeNodes(SyntaxTreeNode node, boolean ignoreXslAttribute) {
      List<SyntaxTreeNode> contents = node.getContents();
      Iterator var4 = contents.iterator();

      while(true) {
         label49:
         while(var4.hasNext()) {
            SyntaxTreeNode child = (SyntaxTreeNode)var4.next();
            if (!(child instanceof Text)) {
               if (!(child instanceof LiteralElement) && !(child instanceof ValueOf) && !(child instanceof XslElement) && !(child instanceof Comment) && !(child instanceof Number) && !(child instanceof ProcessingInstruction)) {
                  if (child instanceof XslAttribute) {
                     if (!ignoreXslAttribute) {
                        return true;
                     }
                     continue;
                  }

                  if (!(child instanceof CallTemplate) && !(child instanceof ApplyTemplates) && !(child instanceof Copy) && !(child instanceof CopyOf)) {
                     if ((child instanceof If || child instanceof ForEach) && this.canProduceAttributeNodes(child, false)) {
                        return true;
                     }

                     if (!(child instanceof Choose)) {
                        continue;
                     }

                     List<SyntaxTreeNode> chooseContents = child.getContents();
                     Iterator var7 = chooseContents.iterator();

                     SyntaxTreeNode chooseChild;
                     do {
                        do {
                           if (!var7.hasNext()) {
                              continue label49;
                           }

                           chooseChild = (SyntaxTreeNode)var7.next();
                        } while(!(chooseChild instanceof When) && !(chooseChild instanceof Otherwise));
                     } while(!this.canProduceAttributeNodes(chooseChild, false));

                     return true;
                  }

                  return true;
               }

               return false;
            } else {
               Text text = (Text)child;
               if (!text.isIgnore()) {
                  return false;
               }
            }
         }

         return false;
      }
   }
}
