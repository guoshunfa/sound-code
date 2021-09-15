package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Field;

class ObjectStreamField implements Comparable {
   private String name;
   private char type;
   private Field field;
   private String typeString;
   private Class clazz;
   private String signature;
   private long fieldID;

   ObjectStreamField(String var1, Class var2) {
      this.fieldID = -1L;
      this.name = var1;
      this.clazz = var2;
      if (var2.isPrimitive()) {
         if (var2 == Integer.TYPE) {
            this.type = 'I';
         } else if (var2 == Byte.TYPE) {
            this.type = 'B';
         } else if (var2 == Long.TYPE) {
            this.type = 'J';
         } else if (var2 == Float.TYPE) {
            this.type = 'F';
         } else if (var2 == Double.TYPE) {
            this.type = 'D';
         } else if (var2 == Short.TYPE) {
            this.type = 'S';
         } else if (var2 == Character.TYPE) {
            this.type = 'C';
         } else if (var2 == Boolean.TYPE) {
            this.type = 'Z';
         }
      } else if (var2.isArray()) {
         this.type = '[';
         this.typeString = ObjectStreamClass_1_3_1.getSignature(var2);
      } else {
         this.type = 'L';
         this.typeString = ObjectStreamClass_1_3_1.getSignature(var2);
      }

      if (this.typeString != null) {
         this.signature = this.typeString;
      } else {
         this.signature = String.valueOf(this.type);
      }

   }

   ObjectStreamField(Field var1) {
      this(var1.getName(), var1.getType());
      this.field = var1;
   }

   ObjectStreamField(String var1, char var2, Field var3, String var4) {
      this.fieldID = -1L;
      this.name = var1;
      this.type = var2;
      this.field = var3;
      this.typeString = var4;
      if (this.typeString != null) {
         this.signature = this.typeString;
      } else {
         this.signature = String.valueOf(this.type);
      }

   }

   public String getName() {
      return this.name;
   }

   public Class getType() {
      if (this.clazz != null) {
         return this.clazz;
      } else {
         switch(this.type) {
         case 'B':
            this.clazz = Byte.TYPE;
            break;
         case 'C':
            this.clazz = Character.TYPE;
            break;
         case 'D':
            this.clazz = Double.TYPE;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         default:
            break;
         case 'F':
            this.clazz = Float.TYPE;
            break;
         case 'I':
            this.clazz = Integer.TYPE;
            break;
         case 'J':
            this.clazz = Long.TYPE;
            break;
         case 'L':
         case '[':
            this.clazz = Object.class;
            break;
         case 'S':
            this.clazz = Short.TYPE;
            break;
         case 'Z':
            this.clazz = Boolean.TYPE;
         }

         return this.clazz;
      }
   }

   public char getTypeCode() {
      return this.type;
   }

   public String getTypeString() {
      return this.typeString;
   }

   Field getField() {
      return this.field;
   }

   void setField(Field var1) {
      this.field = var1;
      this.fieldID = -1L;
   }

   ObjectStreamField() {
      this.fieldID = -1L;
   }

   public boolean isPrimitive() {
      return this.type != '[' && this.type != 'L';
   }

   public int compareTo(Object var1) {
      ObjectStreamField var2 = (ObjectStreamField)var1;
      boolean var3 = this.typeString == null;
      boolean var4 = var2.typeString == null;
      if (var3 != var4) {
         return var3 ? -1 : 1;
      } else {
         return this.name.compareTo(var2.name);
      }
   }

   public boolean typeEquals(ObjectStreamField var1) {
      if (var1 != null && this.type == var1.type) {
         return this.typeString == null && var1.typeString == null ? true : ObjectStreamClass_1_3_1.compareClassNames(this.typeString, var1.typeString, '/');
      } else {
         return false;
      }
   }

   public String getSignature() {
      return this.signature;
   }

   public String toString() {
      return this.typeString != null ? this.typeString + " " + this.name : this.type + " " + this.name;
   }

   public Class getClazz() {
      return this.clazz;
   }
}
