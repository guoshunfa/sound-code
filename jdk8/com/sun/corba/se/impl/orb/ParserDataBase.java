package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.ParserData;

public abstract class ParserDataBase implements ParserData {
   private String propertyName;
   private Operation operation;
   private String fieldName;
   private Object defaultValue;
   private Object testValue;

   protected ParserDataBase(String var1, Operation var2, String var3, Object var4, Object var5) {
      this.propertyName = var1;
      this.operation = var2;
      this.fieldName = var3;
      this.defaultValue = var4;
      this.testValue = var5;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public Operation getOperation() {
      return this.operation;
   }

   public String getFieldName() {
      return this.fieldName;
   }

   public Object getDefaultValue() {
      return this.defaultValue;
   }

   public Object getTestValue() {
      return this.testValue;
   }
}
