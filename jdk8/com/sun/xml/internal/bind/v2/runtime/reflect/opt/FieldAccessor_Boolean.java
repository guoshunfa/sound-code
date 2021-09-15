package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Boolean extends Accessor {
   public FieldAccessor_Boolean() {
      super(Boolean.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).f_boolean;
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).f_boolean = value == null ? Const.default_value_boolean : (Boolean)value;
   }
}
