package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_field_Boolean extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printBoolean(((Bean)o).f_boolean);
   }

   public void parse(Object o, CharSequence lexical) {
      Boolean b = DatatypeConverterImpl._parseBoolean(lexical);
      if (b != null) {
         ((Bean)o).f_boolean = b;
      }

   }

   public boolean hasValue(Object o) {
      return true;
   }
}
