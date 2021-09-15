package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Integer extends Accessor {
   public FieldAccessor_Integer() {
      super(Integer.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).f_int;
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).f_int = value == null ? Const.default_value_int : (Integer)value;
   }
}
