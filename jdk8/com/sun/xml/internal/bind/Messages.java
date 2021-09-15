package com.sun.xml.internal.bind;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   FAILED_TO_INITIALE_DATATYPE_FACTORY;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
