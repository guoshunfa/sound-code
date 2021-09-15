package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.Stack;

public class ClassStack implements Serializable {
   private Stack stack = new Stack();

   public void push(JavaClass clazz) {
      this.stack.push(clazz);
   }

   public JavaClass pop() {
      return (JavaClass)this.stack.pop();
   }

   public JavaClass top() {
      return (JavaClass)this.stack.peek();
   }

   public boolean empty() {
      return this.stack.empty();
   }
}
