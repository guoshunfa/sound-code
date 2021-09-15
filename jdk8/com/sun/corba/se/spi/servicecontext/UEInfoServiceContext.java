package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class UEInfoServiceContext extends ServiceContext {
   public static final int SERVICE_CONTEXT_ID = 9;
   private Throwable unknown = null;

   public UEInfoServiceContext(Throwable var1) {
      this.unknown = var1;
   }

   public UEInfoServiceContext(InputStream var1, GIOPVersion var2) {
      super(var1, var2);

      try {
         this.unknown = (Throwable)this.in.read_value();
      } catch (ThreadDeath var4) {
         throw var4;
      } catch (Throwable var5) {
         this.unknown = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
      }

   }

   public int getId() {
      return 9;
   }

   public void writeData(OutputStream var1) throws SystemException {
      var1.write_value(this.unknown);
   }

   public Throwable getUE() {
      return this.unknown;
   }

   public String toString() {
      return "UEInfoServiceContext[ unknown=" + this.unknown.toString() + " ]";
   }
}
