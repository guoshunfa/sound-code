package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.AccessFlags;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import java.util.ArrayList;

public abstract class FieldGenOrMethodGen extends AccessFlags implements NamedAndTyped, Cloneable {
   protected String name;
   protected Type type;
   protected ConstantPoolGen cp;
   private ArrayList attribute_vec = new ArrayList();

   protected FieldGenOrMethodGen() {
   }

   public void setType(Type type) {
      if (type.getType() == 16) {
         throw new IllegalArgumentException("Type can not be " + type);
      } else {
         this.type = type;
      }
   }

   public Type getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public ConstantPoolGen getConstantPool() {
      return this.cp;
   }

   public void setConstantPool(ConstantPoolGen cp) {
      this.cp = cp;
   }

   public void addAttribute(Attribute a) {
      this.attribute_vec.add(a);
   }

   public void removeAttribute(Attribute a) {
      this.attribute_vec.remove(a);
   }

   public void removeAttributes() {
      this.attribute_vec.clear();
   }

   public Attribute[] getAttributes() {
      Attribute[] attributes = new Attribute[this.attribute_vec.size()];
      this.attribute_vec.toArray(attributes);
      return attributes;
   }

   public abstract String getSignature();

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         System.err.println((Object)var2);
         return null;
      }
   }
}
