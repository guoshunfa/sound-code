package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;

public final class CompareGenerator extends MethodGenerator {
   private static int DOM_INDEX = 1;
   private static int CURRENT_INDEX = 2;
   private static int LEVEL_INDEX = 3;
   private static int TRANSLET_INDEX = 4;
   private static int LAST_INDEX = 5;
   private int ITERATOR_INDEX = 6;
   private final Instruction _iloadCurrent;
   private final Instruction _istoreCurrent;
   private final Instruction _aloadDom;
   private final Instruction _iloadLast;
   private final Instruction _aloadIterator;
   private final Instruction _astoreIterator;

   public CompareGenerator(int access_flags, com.sun.org.apache.bcel.internal.generic.Type return_type, com.sun.org.apache.bcel.internal.generic.Type[] arg_types, String[] arg_names, String method_name, String class_name, InstructionList il, ConstantPoolGen cp) {
      super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
      this._iloadCurrent = new ILOAD(CURRENT_INDEX);
      this._istoreCurrent = new ISTORE(CURRENT_INDEX);
      this._aloadDom = new ALOAD(DOM_INDEX);
      this._iloadLast = new ILOAD(LAST_INDEX);
      LocalVariableGen iterator = this.addLocalVariable("iterator", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
      this.ITERATOR_INDEX = iterator.getIndex();
      this._aloadIterator = new ALOAD(this.ITERATOR_INDEX);
      this._astoreIterator = new ASTORE(this.ITERATOR_INDEX);
      il.append((Instruction)(new ACONST_NULL()));
      il.append(this.storeIterator());
   }

   public Instruction loadLastNode() {
      return this._iloadLast;
   }

   public Instruction loadCurrentNode() {
      return this._iloadCurrent;
   }

   public Instruction storeCurrentNode() {
      return this._istoreCurrent;
   }

   public Instruction loadDOM() {
      return this._aloadDom;
   }

   public int getHandlerIndex() {
      return -1;
   }

   public int getIteratorIndex() {
      return -1;
   }

   public Instruction storeIterator() {
      return this._astoreIterator;
   }

   public Instruction loadIterator() {
      return this._aloadIterator;
   }

   public int getLocalIndex(String name) {
      return name.equals("current") ? CURRENT_INDEX : super.getLocalIndex(name);
   }
}
