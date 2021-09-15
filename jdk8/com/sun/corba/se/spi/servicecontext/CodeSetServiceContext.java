package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class CodeSetServiceContext extends ServiceContext {
   public static final int SERVICE_CONTEXT_ID = 1;
   private CodeSetComponentInfo.CodeSetContext csc;

   public CodeSetServiceContext(CodeSetComponentInfo.CodeSetContext var1) {
      this.csc = var1;
   }

   public CodeSetServiceContext(InputStream var1, GIOPVersion var2) {
      super(var1, var2);
      this.csc = new CodeSetComponentInfo.CodeSetContext();
      this.csc.read((MarshalInputStream)this.in);
   }

   public int getId() {
      return 1;
   }

   public void writeData(OutputStream var1) throws SystemException {
      this.csc.write((MarshalOutputStream)var1);
   }

   public CodeSetComponentInfo.CodeSetContext getCodeSetContext() {
      return this.csc;
   }

   public String toString() {
      return "CodeSetServiceContext[ csc=" + this.csc + " ]";
   }
}
