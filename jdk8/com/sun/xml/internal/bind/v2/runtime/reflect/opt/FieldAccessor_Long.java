package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Long extends Accessor {
   public FieldAccessor_Long() {
      super(Long.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).f_long;
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).f_long = value == null ? Const.default_value_long : (Long)value;
   }
}
