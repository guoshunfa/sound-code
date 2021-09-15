package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Character extends Accessor {
   public FieldAccessor_Character() {
      super(Character.class);
   }

   public Object get(Object bean) {
      return ((Bean)bean).f_char;
   }

   public void set(Object bean, Object value) {
      ((Bean)bean).f_char = value == null ? Const.default_value_char : (Character)value;
   }
}
