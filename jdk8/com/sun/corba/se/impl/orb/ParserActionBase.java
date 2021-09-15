package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import java.util.Properties;

public abstract class ParserActionBase implements ParserAction {
   private String propertyName;
   private boolean prefix;
   private Operation operation;
   private String fieldName;

   public int hashCode() {
      return this.propertyName.hashCode() ^ this.operation.hashCode() ^ this.fieldName.hashCode() ^ (this.prefix ? 0 : 1);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ParserActionBase)) {
         return false;
      } else {
         ParserActionBase var2 = (ParserActionBase)var1;
         return this.propertyName.equals(var2.propertyName) && this.prefix == var2.prefix && this.operation.equals(var2.operation) && this.fieldName.equals(var2.fieldName);
      }
   }

   public ParserActionBase(String var1, boolean var2, Operation var3, String var4) {
      this.propertyName = var1;
      this.prefix = var2;
      this.operation = var3;
      this.fieldName = var4;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public boolean isPrefix() {
      return this.prefix;
   }

   public String getFieldName() {
      return this.fieldName;
   }

   public abstract Object apply(Properties var1);

   protected Operation getOperation() {
      return this.operation;
   }
}
