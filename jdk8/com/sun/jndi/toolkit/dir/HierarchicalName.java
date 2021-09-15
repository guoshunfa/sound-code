package com.sun.jndi.toolkit.dir;

import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

final class HierarchicalName extends CompoundName {
   private int hashValue = -1;
   private static final long serialVersionUID = -6717336834584573168L;

   HierarchicalName() {
      super(new Enumeration<String>() {
         public boolean hasMoreElements() {
            return false;
         }

         public String nextElement() {
            throw new NoSuchElementException();
         }
      }, HierarchicalNameParser.mySyntax);
   }

   HierarchicalName(Enumeration<String> var1, Properties var2) {
      super(var1, var2);
   }

   HierarchicalName(String var1, Properties var2) throws InvalidNameException {
      super(var1, var2);
   }

   public int hashCode() {
      if (this.hashValue == -1) {
         String var1 = this.toString().toUpperCase(Locale.ENGLISH);
         int var2 = var1.length();
         int var3 = 0;
         char[] var4 = new char[var2];
         var1.getChars(0, var2, var4, 0);

         for(int var5 = var2; var5 > 0; --var5) {
            this.hashValue = this.hashValue * 37 + var4[var3++];
         }
      }

      return this.hashValue;
   }

   public Name getPrefix(int var1) {
      Enumeration var2 = super.getPrefix(var1).getAll();
      return new HierarchicalName(var2, this.mySyntax);
   }

   public Name getSuffix(int var1) {
      Enumeration var2 = super.getSuffix(var1).getAll();
      return new HierarchicalName(var2, this.mySyntax);
   }

   public Object clone() {
      return new HierarchicalName(this.getAll(), this.mySyntax);
   }
}
