package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Short extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printShort(((Bean)o).get_short());
   }

   public void parse(Object o, CharSequence lexical) {
      ((Bean)o).set_short(DatatypeConverterImpl._parseShort(lexical));
   }

   public boolean hasValue(Object o) {
      return true;
   }
}
