package com.sun.org.apache.bcel.internal.generic;

import java.util.Objects;

public class ReturnaddressType extends Type {
   public static final ReturnaddressType NO_TARGET = new ReturnaddressType();
   private InstructionHandle returnTarget;

   private ReturnaddressType() {
      super((byte)16, "<return address>");
   }

   public ReturnaddressType(InstructionHandle returnTarget) {
      super((byte)16, "<return address targeting " + returnTarget + ">");
      this.returnTarget = returnTarget;
   }

   public int hashCode() {
      return Objects.hashCode(this.returnTarget);
   }

   public boolean equals(Object rat) {
      return !(rat instanceof ReturnaddressType) ? false : ((ReturnaddressType)rat).returnTarget.equals(this.returnTarget);
   }

   public InstructionHandle getTarget() {
      return this.returnTarget;
   }
}
