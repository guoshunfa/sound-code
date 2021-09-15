package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_field_Short extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printShort(((Bean)o).f_short);
   }

   public void parse(Object o, CharSequence lexical) {
      ((Bean)o).f_short = DatatypeConverterImpl._parseShort(lexical);
   }

   public boolean hasValue(Object o) {
      return true;
   }
}
