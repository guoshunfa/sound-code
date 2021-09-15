package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;

public final class BooleanType extends Type {
   protected BooleanType() {
   }

   public String toString() {
      return "boolean";
   }

   public boolean identicalTo(Type other) {
      return this == other;
   }

   public String toSignature() {
      return "Z";
   }

   public boolean isSimple() {
      return true;
   }

   public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
      return com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN;
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
      if (type == Type.String) {
         this.translateTo(classGen, methodGen, (StringType)type);
      } else if (type == Type.Real) {
         this.translateTo(classGen, methodGen, (RealType)type);
      } else if (type == Type.Reference) {
         this.translateTo(classGen, methodGen, (ReferenceType)type);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      BranchHandle falsec = il.append((BranchInstruction)(new IFEQ((InstructionHandle)null)));
      il.append((CompoundInstruction)(new PUSH(cpg, "true")));
      BranchHandle truec = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
      falsec.setTarget(il.append((CompoundInstruction)(new PUSH(cpg, "false"))));
      truec.setTarget(il.append(NOP));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
      methodGen.getInstructionList().append((Instruction)I2D);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ReferenceType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append((Instruction)(new NEW(cpg.addClass("java.lang.Boolean"))));
      il.append((Instruction)DUP_X1);
      il.append((Instruction)SWAP);
      il.append((Instruction)(new INVOKESPECIAL(cpg.addMethodref("java.lang.Boolean", "<init>", "(Z)V"))));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      if (clazz == java.lang.Boolean.TYPE) {
         methodGen.getInstructionList().append(NOP);
      } else if (clazz.isAssignableFrom(Boolean.class)) {
         this.translateTo(classGen, methodGen, Type.Reference);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      this.translateTo(classGen, methodGen, clazz);
   }

   public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
      this.translateTo(classGen, methodGen, Type.Reference);
   }

   public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append((Instruction)(new CHECKCAST(cpg.addClass("java.lang.Boolean"))));
      il.append((Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("java.lang.Boolean", "booleanValue", "()Z"))));
   }

   public Instruction LOAD(int slot) {
      return new ILOAD(slot);
   }

   public Instruction STORE(int slot) {
      return new ISTORE(slot);
   }

   public BranchInstruction GT(boolean tozero) {
      return (BranchInstruction)(tozero ? new IFGT((InstructionHandle)null) : new IF_ICMPGT((InstructionHandle)null));
   }

   public BranchInstruction GE(boolean tozero) {
      return (BranchInstruction)(tozero ? new IFGE((InstructionHandle)null) : new IF_ICMPGE((InstructionHandle)null));
   }

   public BranchInstruction LT(boolean tozero) {
      return (BranchInstruction)(tozero ? new IFLT((InstructionHandle)null) : new IF_ICMPLT((InstructionHandle)null));
   }

   public BranchInstruction LE(boolean tozero) {
      return (BranchInstruction)(tozero ? new IFLE((InstructionHandle)null) : new IF_ICMPLE((InstructionHandle)null));
   }
}
