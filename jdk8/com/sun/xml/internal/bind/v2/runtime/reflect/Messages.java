package com.sun.xml.internal.bind.v2.runtime.reflect;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   UNABLE_TO_ACCESS_NON_PUBLIC_FIELD,
   UNASSIGNABLE_TYPE,
   NO_SETTER,
   NO_GETTER;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
