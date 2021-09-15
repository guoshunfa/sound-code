package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCONST;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Variable extends VariableBase {
   public int getIndex() {
      return this._local != null ? this._local.getIndex() : -1;
   }

   public void parseContents(Parser parser) {
      super.parseContents(parser);
      SyntaxTreeNode parent = this.getParent();
      if (parent instanceof Stylesheet) {
         this._isLocal = false;
         Variable var = parser.getSymbolTable().lookupVariable(this._name);
         if (var != null) {
            int us = this.getImportPrecedence();
            int them = var.getImportPrecedence();
            if (us == them) {
               String name = this._name.toString();
               this.reportError(this, parser, "VARIABLE_REDEF_ERR", name);
            } else {
               if (them > us) {
                  this._ignore = true;
                  this.copyReferences(var);
                  return;
               }

               var.copyReferences(this);
               var.disable();
            }
         }

         ((Stylesheet)parent).addVariable(this);
         parser.getSymbolTable().addVariable(this);
      } else {
         this._isLocal = true;
      }

   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._select != null) {
         this._type = this._select.typeCheck(stable);
      } else if (this.hasContents()) {
         this.typeCheckContents(stable);
         this._type = Type.ResultTree;
      } else {
         this._type = Type.Reference;
      }

      return Type.Void;
   }

   public void initialize(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this.isLocal() && !this._refs.isEmpty()) {
         if (this._local == null) {
            this._local = methodGen.addLocalVariable2(this.getEscapedName(), this._type.toJCType(), (InstructionHandle)null);
         }

         if (!(this._type instanceof IntType) && !(this._type instanceof NodeType) && !(this._type instanceof BooleanType)) {
            if (this._type instanceof RealType) {
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new DCONST(0.0D)));
            } else {
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ACONST_NULL()));
            }
         } else {
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ICONST(0)));
         }

         this._local.setStart(il.append(this._type.STORE(this._local.getIndex())));
      }

   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this._refs.isEmpty()) {
         this._ignore = true;
      }

      if (!this._ignore) {
         this._ignore = true;
         String name = this.getEscapedName();
         if (this.isLocal()) {
            this.translateValue(classGen, methodGen);
            boolean createLocal = this._local == null;
            if (createLocal) {
               this.mapRegister(methodGen);
            }

            InstructionHandle storeInst = il.append(this._type.STORE(this._local.getIndex()));
            if (createLocal) {
               this._local.setStart(storeInst);
            }
         } else {
            String signature = this._type.toSignature();
            if (classGen.containsField(name) == null) {
               classGen.addField(new Field(1, cpg.addUtf8(name), cpg.addUtf8(signature), (com.sun.org.apache.bcel.internal.classfile.Attribute[])null, cpg.getConstantPool()));
               il.append(classGen.loadTranslet());
               this.translateValue(classGen, methodGen);
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new PUTFIELD(cpg.addFieldref(classGen.getClassName(), name, signature))));
            }
         }

      }
   }
}
