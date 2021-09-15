package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DUP_X1;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public abstract class SyntaxTreeNode implements Constants {
   private Parser _parser;
   protected SyntaxTreeNode _parent;
   private Stylesheet _stylesheet;
   private Template _template;
   private final List<SyntaxTreeNode> _contents = new ArrayList(2);
   protected QName _qname;
   private int _line;
   protected AttributesImpl _attributes = null;
   private Map<String, String> _prefixMapping = null;
   protected static final SyntaxTreeNode Dummy = new AbsolutePathPattern((RelativePathPattern)null);
   protected static final int IndentIncrement = 4;
   private static final char[] _spaces = "                                                       ".toCharArray();

   public SyntaxTreeNode() {
      this._line = 0;
      this._qname = null;
   }

   public SyntaxTreeNode(int line) {
      this._line = line;
      this._qname = null;
   }

   public SyntaxTreeNode(String uri, String prefix, String local) {
      this._line = 0;
      this.setQName(uri, prefix, local);
   }

   protected final void setLineNumber(int line) {
      this._line = line;
   }

   public final int getLineNumber() {
      if (this._line > 0) {
         return this._line;
      } else {
         SyntaxTreeNode parent = this.getParent();
         return parent != null ? parent.getLineNumber() : 0;
      }
   }

   protected void setQName(QName qname) {
      this._qname = qname;
   }

   protected void setQName(String uri, String prefix, String localname) {
      this._qname = new QName(uri, prefix, localname);
   }

   protected QName getQName() {
      return this._qname;
   }

   protected void setAttributes(AttributesImpl attributes) {
      this._attributes = attributes;
   }

   protected String getAttribute(String qname) {
      if (this._attributes == null) {
         return "";
      } else {
         String value = this._attributes.getValue(qname);
         return value != null && !value.equals("") ? value : "";
      }
   }

   protected String getAttribute(String prefix, String localName) {
      return this.getAttribute(prefix + ':' + localName);
   }

   protected boolean hasAttribute(String qname) {
      return this._attributes != null && this._attributes.getValue(qname) != null;
   }

   protected void addAttribute(String qname, String value) {
      int index = this._attributes.getIndex(qname);
      if (index != -1) {
         this._attributes.setAttribute(index, "", Util.getLocalName(qname), qname, "CDATA", value);
      } else {
         this._attributes.addAttribute("", Util.getLocalName(qname), qname, "CDATA", value);
      }

   }

   protected Attributes getAttributes() {
      return this._attributes;
   }

   protected void setPrefixMapping(Map<String, String> mapping) {
      this._prefixMapping = mapping;
   }

   protected Map<String, String> getPrefixMapping() {
      return this._prefixMapping;
   }

   protected void addPrefixMapping(String prefix, String uri) {
      if (this._prefixMapping == null) {
         this._prefixMapping = new HashMap();
      }

      this._prefixMapping.put(prefix, uri);
   }

   protected String lookupNamespace(String prefix) {
      String uri = null;
      if (this._prefixMapping != null) {
         uri = (String)this._prefixMapping.get(prefix);
      }

      if (uri == null && this._parent != null) {
         uri = this._parent.lookupNamespace(prefix);
         if (prefix == "" && uri == null) {
            uri = "";
         }
      }

      return uri;
   }

   protected String lookupPrefix(String uri) {
      String prefix = null;
      if (this._prefixMapping != null && this._prefixMapping.containsValue(uri)) {
         Iterator var3 = this._prefixMapping.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var3.next();
            prefix = (String)entry.getKey();
            String mapsTo = (String)entry.getValue();
            if (mapsTo.equals(uri)) {
               return prefix;
            }
         }
      } else if (this._parent != null) {
         prefix = this._parent.lookupPrefix(uri);
         if (uri == "" && prefix == null) {
            prefix = "";
         }
      }

      return prefix;
   }

   protected void setParser(Parser parser) {
      this._parser = parser;
   }

   public final Parser getParser() {
      return this._parser;
   }

   protected void setParent(SyntaxTreeNode parent) {
      if (this._parent == null) {
         this._parent = parent;
      }

   }

   protected final SyntaxTreeNode getParent() {
      return this._parent;
   }

   protected final boolean isDummy() {
      return this == Dummy;
   }

   protected int getImportPrecedence() {
      Stylesheet stylesheet = this.getStylesheet();
      return stylesheet == null ? Integer.MIN_VALUE : stylesheet.getImportPrecedence();
   }

   public Stylesheet getStylesheet() {
      if (this._stylesheet == null) {
         SyntaxTreeNode parent;
         for(parent = this; parent != null; parent = parent.getParent()) {
            if (parent instanceof Stylesheet) {
               return (Stylesheet)parent;
            }
         }

         this._stylesheet = (Stylesheet)parent;
      }

      return this._stylesheet;
   }

   protected Template getTemplate() {
      if (this._template == null) {
         SyntaxTreeNode parent;
         for(parent = this; parent != null && !(parent instanceof Template); parent = parent.getParent()) {
         }

         this._template = (Template)parent;
      }

      return this._template;
   }

   protected final XSLTC getXSLTC() {
      return this._parser.getXSLTC();
   }

   protected final SymbolTable getSymbolTable() {
      return this._parser == null ? null : this._parser.getSymbolTable();
   }

   public void parseContents(Parser parser) {
      this.parseChildren(parser);
   }

   protected final void parseChildren(Parser parser) {
      List<QName> locals = null;
      Iterator var3 = this._contents.iterator();

      while(var3.hasNext()) {
         SyntaxTreeNode child = (SyntaxTreeNode)var3.next();
         parser.getSymbolTable().setCurrentNode(child);
         child.parseContents(parser);
         QName varOrParamName = this.updateScope(parser, child);
         if (varOrParamName != null) {
            if (locals == null) {
               locals = new ArrayList(2);
            }

            locals.add(varOrParamName);
         }
      }

      parser.getSymbolTable().setCurrentNode(this);
      if (locals != null) {
         var3 = locals.iterator();

         while(var3.hasNext()) {
            QName varOrParamName = (QName)var3.next();
            parser.removeVariable(varOrParamName);
         }
      }

   }

   protected QName updateScope(Parser parser, SyntaxTreeNode node) {
      if (node instanceof Variable) {
         Variable var = (Variable)node;
         parser.addVariable(var);
         return var.getName();
      } else if (node instanceof Param) {
         Param param = (Param)node;
         parser.addParameter(param);
         return param.getName();
      } else {
         return null;
      }
   }

   public abstract Type typeCheck(SymbolTable var1) throws TypeCheckError;

   protected Type typeCheckContents(SymbolTable stable) throws TypeCheckError {
      Iterator var2 = this._contents.iterator();

      while(var2.hasNext()) {
         SyntaxTreeNode item = (SyntaxTreeNode)var2.next();
         item.typeCheck(stable);
      }

      return Type.Void;
   }

   public abstract void translate(ClassGenerator var1, MethodGenerator var2);

   protected void translateContents(ClassGenerator classGen, MethodGenerator methodGen) {
      int n = this.elementCount();
      Iterator var4 = this._contents.iterator();

      while(var4.hasNext()) {
         SyntaxTreeNode item = (SyntaxTreeNode)var4.next();
         methodGen.markChunkStart();
         item.translate(classGen, methodGen);
         methodGen.markChunkEnd();
      }

      for(int i = 0; i < n; ++i) {
         if (this._contents.get(i) instanceof VariableBase) {
            VariableBase var = (VariableBase)this._contents.get(i);
            var.unmapRegister(classGen, methodGen);
         }
      }

   }

   private boolean isSimpleRTF(SyntaxTreeNode node) {
      List<SyntaxTreeNode> contents = node.getContents();
      Iterator var3 = contents.iterator();

      SyntaxTreeNode item;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         item = (SyntaxTreeNode)var3.next();
      } while(this.isTextElement(item, false));

      return false;
   }

   private boolean isAdaptiveRTF(SyntaxTreeNode node) {
      List<SyntaxTreeNode> contents = node.getContents();
      Iterator var3 = contents.iterator();

      SyntaxTreeNode item;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         item = (SyntaxTreeNode)var3.next();
      } while(this.isTextElement(item, true));

      return false;
   }

   private boolean isTextElement(SyntaxTreeNode node, boolean doExtendedCheck) {
      if (!(node instanceof ValueOf) && !(node instanceof Number) && !(node instanceof Text)) {
         if (node instanceof If) {
            return doExtendedCheck ? this.isAdaptiveRTF(node) : this.isSimpleRTF(node);
         } else if (node instanceof Choose) {
            List<SyntaxTreeNode> contents = node.getContents();
            Iterator var4 = contents.iterator();

            SyntaxTreeNode item;
            do {
               do {
                  if (!var4.hasNext()) {
                     return true;
                  }

                  item = (SyntaxTreeNode)var4.next();
               } while(item instanceof Text);
            } while((item instanceof When || item instanceof Otherwise) && (doExtendedCheck && this.isAdaptiveRTF(item) || !doExtendedCheck && this.isSimpleRTF(item)));

            return false;
         } else {
            return doExtendedCheck && (node instanceof CallTemplate || node instanceof ApplyTemplates);
         }
      } else {
         return true;
      }
   }

   protected void compileResultTree(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      Stylesheet stylesheet = classGen.getStylesheet();
      boolean isSimple = this.isSimpleRTF(this);
      boolean isAdaptive = false;
      if (!isSimple) {
         isAdaptive = this.isAdaptiveRTF(this);
      }

      int rtfType = isSimple ? 0 : (isAdaptive ? 1 : 2);
      il.append(methodGen.loadHandler());
      String DOM_CLASS = classGen.getDOMClass();
      il.append(methodGen.loadDOM());
      int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IIZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
      il.append((CompoundInstruction)(new PUSH(cpg, 32)));
      il.append((CompoundInstruction)(new PUSH(cpg, rtfType)));
      il.append((CompoundInstruction)(new PUSH(cpg, stylesheet.callsNodeset())));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(index, 4)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(index, 1)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      il.append(methodGen.storeHandler());
      il.append(methodGen.startDocument());
      this.translateContents(classGen, methodGen);
      il.append(methodGen.loadHandler());
      il.append(methodGen.endDocument());
      if (stylesheet.callsNodeset() && !DOM_CLASS.equals("com/sun/org/apache/xalan/internal/xsltc/DOM")) {
         index = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;[Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new DUP_X1()));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
         if (!stylesheet.callsNodeset()) {
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ICONST(0)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ANEWARRAY(cpg.addClass("java.lang.String"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ICONST(0)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEWARRAY(BasicType.INT)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(index)));
         } else {
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)ALOAD_0);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)ALOAD_0);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)ALOAD_0);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)ALOAD_0);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(index)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
            il.append(methodGen.loadDOM());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new CHECKCAST(cpg.addClass(classGen.getDOMClass()))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
            index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "addDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)I");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(index)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)POP);
         }
      }

      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
      il.append(methodGen.storeHandler());
   }

   protected boolean contextDependent() {
      return true;
   }

   protected boolean dependentContents() {
      Iterator var1 = this._contents.iterator();

      SyntaxTreeNode item;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         item = (SyntaxTreeNode)var1.next();
      } while(!item.contextDependent());

      return true;
   }

   protected final void addElement(SyntaxTreeNode element) {
      this._contents.add(element);
      element.setParent(this);
   }

   protected final void setFirstElement(SyntaxTreeNode element) {
      this._contents.add(0, element);
      element.setParent(this);
   }

   protected final void removeElement(SyntaxTreeNode element) {
      this._contents.remove(element);
      element.setParent((SyntaxTreeNode)null);
   }

   protected final List<SyntaxTreeNode> getContents() {
      return this._contents;
   }

   protected final boolean hasContents() {
      return this.elementCount() > 0;
   }

   protected final int elementCount() {
      return this._contents.size();
   }

   protected final Iterator<SyntaxTreeNode> elements() {
      return this._contents.iterator();
   }

   protected final SyntaxTreeNode elementAt(int pos) {
      return (SyntaxTreeNode)this._contents.get(pos);
   }

   protected final SyntaxTreeNode lastChild() {
      return this._contents.isEmpty() ? null : (SyntaxTreeNode)this._contents.get(this._contents.size() - 1);
   }

   public void display(int indent) {
      this.displayContents(indent);
   }

   protected void displayContents(int indent) {
      Iterator var2 = this._contents.iterator();

      while(var2.hasNext()) {
         SyntaxTreeNode item = (SyntaxTreeNode)var2.next();
         item.display(indent);
      }

   }

   protected final void indent(int indent) {
      System.out.print(new String(_spaces, 0, indent));
   }

   protected void reportError(SyntaxTreeNode element, Parser parser, String errorCode, String message) {
      ErrorMsg error = new ErrorMsg(errorCode, message, element);
      parser.reportError(3, error);
   }

   protected void reportWarning(SyntaxTreeNode element, Parser parser, String errorCode, String message) {
      ErrorMsg error = new ErrorMsg(errorCode, message, element);
      parser.reportError(4, error);
   }
}
