package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Float extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printFloat(((Bean)o).get_float());
   }

   public void parse(Object o, CharSequence lexical) {
      ((Bean)o).set_float(DatatypeConverterImpl._parseFloat(lexical));
   }

   public boolean hasValue(Object o) {
      return true;
   }
}
