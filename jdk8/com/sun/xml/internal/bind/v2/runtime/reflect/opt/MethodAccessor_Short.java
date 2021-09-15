package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Short extends Accessor {
   public MethodAccessor_Short() {
      super(Short.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).get_short();
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).set_short(value == null ? Const.default_value_short : (Short)value);
   }
}
