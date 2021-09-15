package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public abstract class ParserImplBase {
   private ORBUtilSystemException wrapper = ORBUtilSystemException.get("orb.lifecycle");

   protected abstract PropertyParser makeParser();

   protected void complete() {
   }

   public void init(DataCollector var1) {
      PropertyParser var2 = this.makeParser();
      var1.setParser(var2);
      Properties var3 = var1.getProperties();
      Map var4 = var2.parse(var3);
      this.setFields(var4);
   }

   private Field getAnyField(String var1) {
      Field var2 = null;

      try {
         Class var3 = this.getClass();

         for(var2 = var3.getDeclaredField(var1); var2 == null; var2 = var3.getDeclaredField(var1)) {
            var3 = var3.getSuperclass();
            if (var3 == null) {
               break;
            }
         }
      } catch (Exception var4) {
         throw this.wrapper.fieldNotFound((Throwable)var4, var1);
      }

      if (var2 == null) {
         throw this.wrapper.fieldNotFound(var1);
      } else {
         return var2;
      }
   }

   protected void setFields(Map var1) {
      Set var2 = var1.entrySet();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)((Map.Entry)var3.next());
         final String var5 = (String)((String)var4.getKey());
         final Object var6 = var4.getValue();

         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public Object run() throws IllegalAccessException, IllegalArgumentException {
                  Field var1 = ParserImplBase.this.getAnyField(var5);
                  var1.setAccessible(true);
                  var1.set(ParserImplBase.this, var6);
                  return null;
               }
            });
         } catch (PrivilegedActionException var8) {
            throw this.wrapper.errorSettingField((Throwable)var8.getCause(), var5, var6.toString());
         }
      }

      this.complete();
   }
}
