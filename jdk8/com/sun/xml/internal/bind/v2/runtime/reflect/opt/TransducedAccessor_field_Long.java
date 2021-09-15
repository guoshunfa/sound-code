package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_field_Long extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printLong(((Bean)o).f_long);
   }

   public void parse(Object o, CharSequence lexical) {
      ((Bean)o).f_long = DatatypeConverterImpl._parseLong(lexical);
   }

   public boolean hasValue(Object o) {
      return true;
   }
}
