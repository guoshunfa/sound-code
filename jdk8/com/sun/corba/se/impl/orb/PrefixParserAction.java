package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public class PrefixParserAction extends ParserActionBase {
   private Class componentType;
   private ORBUtilSystemException wrapper;

   public PrefixParserAction(String var1, Operation var2, String var3, Class var4) {
      super(var1, true, var2, var3);
      this.componentType = var4;
      this.wrapper = ORBUtilSystemException.get("orb.lifecycle");
   }

   public Object apply(Properties var1) {
      String var2 = this.getPropertyName();
      int var3 = var2.length();
      if (var2.charAt(var3 - 1) != '.') {
         var2 = var2 + '.';
         ++var3;
      }

      LinkedList var4 = new LinkedList();
      Iterator var5 = var1.keySet().iterator();

      String var7;
      Object var10;
      while(var5.hasNext()) {
         String var6 = (String)((String)var5.next());
         if (var6.startsWith(var2)) {
            var7 = var6.substring(var3);
            String var8 = var1.getProperty(var6);
            StringPair var9 = new StringPair(var7, var8);
            var10 = this.getOperation().operate(var9);
            var4.add(var10);
         }
      }

      int var14 = var4.size();
      if (var14 <= 0) {
         return null;
      } else {
         var7 = null;

         Object var15;
         try {
            var15 = Array.newInstance(this.componentType, var14);
         } catch (Throwable var13) {
            throw this.wrapper.couldNotCreateArray((Throwable)var13, this.getPropertyName(), this.componentType, new Integer(var14));
         }

         Iterator var16 = var4.iterator();

         for(int var17 = 0; var16.hasNext(); ++var17) {
            var10 = var16.next();

            try {
               Array.set(var15, var17, var10);
            } catch (Throwable var12) {
               throw this.wrapper.couldNotSetArray((Throwable)var12, this.getPropertyName(), new Integer(var17), this.componentType, new Integer(var14), var10.toString());
            }
         }

         return var15;
      }
   }
}
