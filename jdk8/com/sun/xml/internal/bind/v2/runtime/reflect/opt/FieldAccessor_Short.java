package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Short extends Accessor {
   public FieldAccessor_Short() {
      super(Short.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).f_short;
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).f_short = value == null ? Const.default_value_short : (Short)value;
   }
}
