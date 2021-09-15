package com.sun.xml.internal.bind.v2.model.annotation;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   DUPLICATE_ANNOTATIONS,
   CLASS_NOT_FOUND;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
