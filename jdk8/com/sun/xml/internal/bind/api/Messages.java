package com.sun.xml.internal.bind.api;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   ARGUMENT_CANT_BE_NULL;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
