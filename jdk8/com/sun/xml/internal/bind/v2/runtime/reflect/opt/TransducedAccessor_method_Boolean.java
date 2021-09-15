package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Boolean extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printBoolean(((Bean)o).get_boolean());
   }

   public void parse(Object o, CharSequence lexical) {
      ((Bean)o).set_boolean(DatatypeConverterImpl._parseBoolean(lexical));
   }

   public boolean hasValue(Object o) {
      return true;
   }
}
