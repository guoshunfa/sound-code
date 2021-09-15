package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import java.io.CharConversionException;
import java.util.Locale;

public class MalformedByteSequenceException extends CharConversionException {
   static final long serialVersionUID = 8436382245048328739L;
   private MessageFormatter fFormatter;
   private Locale fLocale;
   private String fDomain;
   private String fKey;
   private Object[] fArguments;
   private String fMessage;

   public MalformedByteSequenceException(MessageFormatter formatter, Locale locale, String domain, String key, Object[] arguments) {
      this.fFormatter = formatter;
      this.fLocale = locale;
      this.fDomain = domain;
      this.fKey = key;
      this.fArguments = arguments;
   }

   public String getDomain() {
      return this.fDomain;
   }

   public String getKey() {
      return this.fKey;
   }

   public Object[] getArguments() {
      return this.fArguments;
   }

   public String getMessage() {
      if (this.fMessage == null) {
         this.fMessage = this.fFormatter.formatMessage(this.fLocale, this.fKey, this.fArguments);
         this.fFormatter = null;
         this.fLocale = null;
      }

      return this.fMessage;
   }
}
