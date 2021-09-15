package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;

public final class AttributeSetMethodGenerator extends MethodGenerator {
   protected static final int CURRENT_INDEX = 4;
   private static final int PARAM_START_INDEX = 5;
   private static final String[] argNames = new String[4];
   private static final com.sun.org.apache.bcel.internal.generic.Type[] argTypes = new com.sun.org.apache.bcel.internal.generic.Type[4];

   public AttributeSetMethodGenerator(String methodName, ClassGenerator classGen) {
      super(2, com.sun.org.apache.bcel.internal.generic.Type.VOID, argTypes, argNames, methodName, classGen.getClassName(), new InstructionList(), classGen.getConstantPool());
   }

   public int getLocalIndex(String name) {
      return name.equals("current") ? 4 : super.getLocalIndex(name);
   }

   public Instruction loadParameter(int index) {
      return new ALOAD(index + 5);
   }

   public Instruction storeParameter(int index) {
      return new ASTORE(index + 5);
   }

   static {
      argTypes[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
      argTypes[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      argTypes[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
      argTypes[3] = com.sun.org.apache.bcel.internal.generic.Type.INT;
      argNames[0] = "document";
      argNames[1] = "iterator";
      argNames[2] = "handler";
      argNames[3] = "node";
   }
}
