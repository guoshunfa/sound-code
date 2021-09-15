package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;

public final class RtMethodGenerator extends MethodGenerator {
   private static final int HANDLER_INDEX = 2;
   private final Instruction _astoreHandler = new ASTORE(2);
   private final Instruction _aloadHandler = new ALOAD(2);

   public RtMethodGenerator(int access_flags, com.sun.org.apache.bcel.internal.generic.Type return_type, com.sun.org.apache.bcel.internal.generic.Type[] arg_types, String[] arg_names, String method_name, String class_name, InstructionList il, ConstantPoolGen cp) {
      super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
   }

   public int getIteratorIndex() {
      return -1;
   }

   public final Instruction storeHandler() {
      return this._astoreHandler;
   }

   public final Instruction loadHandler() {
      return this._aloadHandler;
   }

   public int getLocalIndex(String name) {
      return -1;
   }
}
