package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.generic.AllocationInstruction;
import com.sun.org.apache.bcel.internal.generic.ArrayInstruction;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CPInstruction;
import com.sun.org.apache.bcel.internal.generic.CodeExceptionGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPushInstruction;
import com.sun.org.apache.bcel.internal.generic.EmptyVisitor;
import com.sun.org.apache.bcel.internal.generic.FieldInstruction;
import com.sun.org.apache.bcel.internal.generic.IINC;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InvokeInstruction;
import com.sun.org.apache.bcel.internal.generic.LDC;
import com.sun.org.apache.bcel.internal.generic.LDC2_W;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.ReturnInstruction;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class BCELFactory extends EmptyVisitor {
   private MethodGen _mg;
   private PrintWriter _out;
   private ConstantPoolGen _cp;
   private HashMap branch_map = new HashMap();
   private ArrayList branches = new ArrayList();

   BCELFactory(MethodGen mg, PrintWriter out) {
      this._mg = mg;
      this._cp = mg.getConstantPool();
      this._out = out;
   }

   public void start() {
      if (!this._mg.isAbstract() && !this._mg.isNative()) {
         for(InstructionHandle ih = this._mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            if (i instanceof BranchInstruction) {
               this.branch_map.put(i, ih);
            }

            if (ih.hasTargeters()) {
               if (i instanceof BranchInstruction) {
                  this._out.println("    InstructionHandle ih_" + ih.getPosition() + ";");
               } else {
                  this._out.print("    InstructionHandle ih_" + ih.getPosition() + " = ");
               }
            } else {
               this._out.print("    ");
            }

            if (!this.visitInstruction(i)) {
               i.accept(this);
            }
         }

         this.updateBranchTargets();
         this.updateExceptionHandlers();
      }

   }

   private boolean visitInstruction(Instruction i) {
      short opcode = i.getOpcode();
      if (InstructionConstants.INSTRUCTIONS[opcode] != null && !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction)) {
         this._out.println("il.append(InstructionConstants." + i.getName().toUpperCase() + ");");
         return true;
      } else {
         return false;
      }
   }

   public void visitLocalVariableInstruction(LocalVariableInstruction i) {
      short opcode = i.getOpcode();
      Type type = i.getType(this._cp);
      if (opcode == 132) {
         this._out.println("il.append(new IINC(" + i.getIndex() + ", " + ((IINC)i).getIncrement() + "));");
      } else {
         String kind = opcode < 54 ? "Load" : "Store";
         this._out.println("il.append(_factory.create" + kind + "(" + BCELifier.printType(type) + ", " + i.getIndex() + "));");
      }

   }

   public void visitArrayInstruction(ArrayInstruction i) {
      short opcode = i.getOpcode();
      Type type = i.getType(this._cp);
      String kind = opcode < 79 ? "Load" : "Store";
      this._out.println("il.append(_factory.createArray" + kind + "(" + BCELifier.printType(type) + "));");
   }

   public void visitFieldInstruction(FieldInstruction i) {
      short opcode = i.getOpcode();
      String class_name = i.getClassName(this._cp);
      String field_name = i.getFieldName(this._cp);
      Type type = i.getFieldType(this._cp);
      this._out.println("il.append(_factory.createFieldAccess(\"" + class_name + "\", \"" + field_name + "\", " + BCELifier.printType(type) + ", Constants." + Constants.OPCODE_NAMES[opcode].toUpperCase() + "));");
   }

   public void visitInvokeInstruction(InvokeInstruction i) {
      short opcode = i.getOpcode();
      String class_name = i.getClassName(this._cp);
      String method_name = i.getMethodName(this._cp);
      Type type = i.getReturnType(this._cp);
      Type[] arg_types = i.getArgumentTypes(this._cp);
      this._out.println("il.append(_factory.createInvoke(\"" + class_name + "\", \"" + method_name + "\", " + BCELifier.printType(type) + ", " + BCELifier.printArgumentTypes(arg_types) + ", Constants." + Constants.OPCODE_NAMES[opcode].toUpperCase() + "));");
   }

   public void visitAllocationInstruction(AllocationInstruction i) {
      Type type;
      if (i instanceof CPInstruction) {
         type = ((CPInstruction)i).getType(this._cp);
      } else {
         type = ((NEWARRAY)i).getType();
      }

      short opcode = ((Instruction)i).getOpcode();
      int dim = 1;
      switch(opcode) {
      case 187:
         this._out.println("il.append(_factory.createNew(\"" + ((ObjectType)type).getClassName() + "\"));");
         break;
      case 197:
         dim = ((MULTIANEWARRAY)i).getDimensions();
      case 188:
      case 189:
         this._out.println("il.append(_factory.createNewArray(" + BCELifier.printType(type) + ", (short) " + dim + "));");
         break;
      default:
         throw new RuntimeException("Oops: " + opcode);
      }

   }

   private void createConstant(Object value) {
      String embed = value.toString();
      if (value instanceof String) {
         embed = '"' + Utility.convertString(value.toString()) + '"';
      } else if (value instanceof Character) {
         embed = "(char)0x" + Integer.toHexString((Character)value);
      }

      this._out.println("il.append(new PUSH(_cp, " + embed + "));");
   }

   public void visitLDC(LDC i) {
      this.createConstant(i.getValue(this._cp));
   }

   public void visitLDC2_W(LDC2_W i) {
      this.createConstant(i.getValue(this._cp));
   }

   public void visitConstantPushInstruction(ConstantPushInstruction i) {
      this.createConstant(i.getValue());
   }

   public void visitINSTANCEOF(INSTANCEOF i) {
      Type type = i.getType(this._cp);
      this._out.println("il.append(new INSTANCEOF(_cp.addClass(" + BCELifier.printType(type) + ")));");
   }

   public void visitCHECKCAST(CHECKCAST i) {
      Type type = i.getType(this._cp);
      this._out.println("il.append(_factory.createCheckCast(" + BCELifier.printType(type) + "));");
   }

   public void visitReturnInstruction(ReturnInstruction i) {
      Type type = i.getType(this._cp);
      this._out.println("il.append(_factory.createReturn(" + BCELifier.printType(type) + "));");
   }

   public void visitBranchInstruction(BranchInstruction bi) {
      BranchHandle bh = (BranchHandle)this.branch_map.get(bi);
      int pos = bh.getPosition();
      String name = bi.getName() + "_" + pos;
      if (bi instanceof Select) {
         Select s = (Select)bi;
         this.branches.add(bi);
         StringBuffer args = new StringBuffer("new int[] { ");
         int[] matchs = s.getMatchs();

         int i;
         for(i = 0; i < matchs.length; ++i) {
            args.append(matchs[i]);
            if (i < matchs.length - 1) {
               args.append(", ");
            }
         }

         args.append(" }");
         this._out.print("    Select " + name + " = new " + bi.getName().toUpperCase() + "(" + args + ", new InstructionHandle[] { ");

         for(i = 0; i < matchs.length; ++i) {
            this._out.print("null");
            if (i < matchs.length - 1) {
               this._out.print(", ");
            }
         }

         this._out.println(");");
      } else {
         int t_pos = bh.getTarget().getPosition();
         String target;
         if (pos > t_pos) {
            target = "ih_" + t_pos;
         } else {
            this.branches.add(bi);
            target = "null";
         }

         this._out.println("    BranchInstruction " + name + " = _factory.createBranchInstruction(Constants." + bi.getName().toUpperCase() + ", " + target + ");");
      }

      if (bh.hasTargeters()) {
         this._out.println("    ih_" + pos + " = il.append(" + name + ");");
      } else {
         this._out.println("    il.append(" + name + ");");
      }

   }

   public void visitRET(RET i) {
      this._out.println("il.append(new RET(" + i.getIndex() + ")));");
   }

   private void updateBranchTargets() {
      Iterator i = this.branches.iterator();

      while(true) {
         BranchInstruction bi;
         String name;
         int t_pos;
         do {
            if (!i.hasNext()) {
               return;
            }

            bi = (BranchInstruction)i.next();
            BranchHandle bh = (BranchHandle)this.branch_map.get(bi);
            int pos = bh.getPosition();
            name = bi.getName() + "_" + pos;
            t_pos = bh.getTarget().getPosition();
            this._out.println("    " + name + ".setTarget(ih_" + t_pos + ");");
         } while(!(bi instanceof Select));

         InstructionHandle[] ihs = ((Select)bi).getTargets();

         for(int j = 0; j < ihs.length; ++j) {
            t_pos = ihs[j].getPosition();
            this._out.println("    " + name + ".setTarget(" + j + ", ih_" + t_pos + ");");
         }
      }
   }

   private void updateExceptionHandlers() {
      CodeExceptionGen[] handlers = this._mg.getExceptionHandlers();

      for(int i = 0; i < handlers.length; ++i) {
         CodeExceptionGen h = handlers[i];
         String type = h.getCatchType() == null ? "null" : BCELifier.printType((Type)h.getCatchType());
         this._out.println("    method.addExceptionHandler(ih_" + h.getStartPC().getPosition() + ", ih_" + h.getEndPC().getPosition() + ", ih_" + h.getHandlerPC().getPosition() + ", " + type + ");");
      }

   }
}
