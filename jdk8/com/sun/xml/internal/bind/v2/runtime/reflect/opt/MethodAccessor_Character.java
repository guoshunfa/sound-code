package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Character extends Accessor {
   public MethodAccessor_Character() {
      super(Character.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).get_char();
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).set_char(value == null ? Const.default_value_char : (Character)value);
   }
}
