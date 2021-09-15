package com.sun.corba.se.impl.presentation.rmi;

public class IDLType {
   private Class cl_;
   private String[] modules_;
   private String memberName_;

   public IDLType(Class var1, String[] var2, String var3) {
      this.cl_ = var1;
      this.modules_ = var2;
      this.memberName_ = var3;
   }

   public IDLType(Class var1, String var2) {
      this(var1, new String[0], var2);
   }

   public Class getJavaClass() {
      return this.cl_;
   }

   public String[] getModules() {
      return this.modules_;
   }

   public String makeConcatenatedName(char var1, boolean var2) {
      StringBuffer var3 = new StringBuffer();

      for(int var4 = 0; var4 < this.modules_.length; ++var4) {
         String var5 = this.modules_[var4];
         if (var4 > 0) {
            var3.append(var1);
         }

         if (var2 && IDLNameTranslatorImpl.isIDLKeyword(var5)) {
            var5 = IDLNameTranslatorImpl.mangleIDLKeywordClash(var5);
         }

         var3.append(var5);
      }

      return var3.toString();
   }

   public String getModuleName() {
      return this.makeConcatenatedName('_', false);
   }

   public String getExceptionName() {
      String var1 = this.makeConcatenatedName('/', true);
      String var2 = "Exception";
      String var3 = this.memberName_;
      if (var3.endsWith(var2)) {
         int var4 = var3.length() - var2.length();
         var3 = var3.substring(0, var4);
      }

      var3 = var3 + "Ex";
      return var1.length() == 0 ? "IDL:" + var3 + ":1.0" : "IDL:" + var1 + '/' + var3 + ":1.0";
   }

   public String getMemberName() {
      return this.memberName_;
   }

   public boolean hasModule() {
      return this.modules_.length > 0;
   }
}
