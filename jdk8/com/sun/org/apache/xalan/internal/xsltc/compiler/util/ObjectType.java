package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;

public final class ObjectType extends Type {
   private String _javaClassName = "java.lang.Object";
   private Class _clazz = Object.class;

   protected ObjectType(String javaClassName) {
      this._javaClassName = javaClassName;

      try {
         this._clazz = ObjectFactory.findProviderClass(javaClassName, true);
      } catch (ClassNotFoundException var3) {
         this._clazz = null;
      }

   }

   protected ObjectType(Class clazz) {
      this._clazz = clazz;
      this._javaClassName = clazz.getName();
   }

   public int hashCode() {
      return Object.class.hashCode();
   }

   public boolean equals(Object obj) {
      return obj instanceof ObjectType;
   }

   public String getJavaClassName() {
      return this._javaClassName;
   }

   public Class getJavaClass() {
      return this._clazz;
   }

   public String toString() {
      return this._javaClassName;
   }

   public boolean identicalTo(Type other) {
      return this == other;
   }

   public String toSignature() {
      StringBuffer result = new StringBuffer("L");
      result.append(this._javaClassName.replace('.', '/')).append(';');
      return result.toString();
   }

   public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
      return Util.getJCRefType(this.toSignature());
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
      if (type == Type.String) {
         this.translateTo(classGen, methodGen, (StringType)type);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append((Instruction)DUP);
      BranchHandle ifNull = il.append((BranchInstruction)(new IFNULL((InstructionHandle)null)));
      il.append((Instruction)(new INVOKEVIRTUAL(cpg.addMethodref(this._javaClassName, "toString", "()Ljava/lang/String;"))));
      BranchHandle gotobh = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
      ifNull.setTarget(il.append((Instruction)POP));
      il.append((CompoundInstruction)(new PUSH(cpg, "")));
      gotobh.setTarget(il.append(NOP));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      if (clazz.isAssignableFrom(this._clazz)) {
         methodGen.getInstructionList().append(NOP);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getClass().toString());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      methodGen.getInstructionList().append(NOP);
   }

   public Instruction LOAD(int slot) {
      return new ALOAD(slot);
   }

   public Instruction STORE(int slot) {
      return new ASTORE(slot);
   }
}
