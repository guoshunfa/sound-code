package com.sun.beans.decoder;

import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;

final class MethodElementHandler extends NewElementHandler {
   private String name;

   public void addAttribute(String var1, String var2) {
      if (var1.equals("name")) {
         this.name = var2;
      } else {
         super.addAttribute(var1, var2);
      }

   }

   protected ValueObject getValueObject(Class<?> var1, Object[] var2) throws Exception {
      Object var3 = this.getContextBean();
      Class[] var4 = getArgumentTypes(var2);
      Method var5 = var1 != null ? MethodFinder.findStaticMethod(var1, this.name, var4) : MethodFinder.findMethod(var3.getClass(), this.name, var4);
      if (var5.isVarArgs()) {
         var2 = getArguments(var2, var5.getParameterTypes());
      }

      Object var6 = MethodUtil.invoke(var5, var3, var2);
      return var5.getReturnType().equals(Void.TYPE) ? ValueObjectImpl.VOID : ValueObjectImpl.create(var6);
   }
}
