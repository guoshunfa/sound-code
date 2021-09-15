package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Byte extends DefaultTransducedAccessor {
   public String print(Object o) {
      return DatatypeConverterImpl._printByte(((Bean)o).get_byte());
   }

   public void parse(Object o, CharSequence lexical) {
      ((Bean)o).set_byte(DatatypeConverterImpl._parseByte(lexical));
   }

   public boolean hasValue(Object o) {
      return true;
   }
}
