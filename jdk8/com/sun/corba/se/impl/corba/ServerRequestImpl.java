package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class ServerRequestImpl extends ServerRequest {
   private ORB _orb = null;
   private ORBUtilSystemException _wrapper = null;
   private String _opName = null;
   private NVList _arguments = null;
   private Context _ctx = null;
   private InputStream _ins = null;
   private boolean _paramsCalled = false;
   private boolean _resultSet = false;
   private boolean _exceptionSet = false;
   private Any _resultAny = null;
   private Any _exception = null;

   public ServerRequestImpl(CorbaMessageMediator var1, ORB var2) {
      this._opName = var1.getOperationName();
      this._ins = (InputStream)var1.getInputObject();
      this._ctx = null;
      this._orb = var2;
      this._wrapper = ORBUtilSystemException.get(var2, "oa.invocation");
   }

   public String operation() {
      return this._opName;
   }

   public void arguments(NVList var1) {
      if (this._paramsCalled) {
         throw this._wrapper.argumentsCalledMultiple();
      } else if (this._exceptionSet) {
         throw this._wrapper.argumentsCalledAfterException();
      } else if (var1 == null) {
         throw this._wrapper.argumentsCalledNullArgs();
      } else {
         this._paramsCalled = true;
         NamedValue var2 = null;

         for(int var3 = 0; var3 < var1.count(); ++var3) {
            try {
               var2 = var1.item(var3);
            } catch (Bounds var5) {
               throw this._wrapper.boundsCannotOccur((Throwable)var5);
            }

            try {
               if (var2.flags() == 1 || var2.flags() == 3) {
                  var2.value().read_value(this._ins, var2.value().type());
               }
            } catch (Exception var6) {
               throw this._wrapper.badArgumentsNvlist((Throwable)var6);
            }
         }

         this._arguments = var1;
         this._orb.getPIHandler().setServerPIInfo(this._arguments);
         this._orb.getPIHandler().invokeServerPIIntermediatePoint();
      }
   }

   public void set_result(Any var1) {
      if (!this._paramsCalled) {
         throw this._wrapper.argumentsNotCalled();
      } else if (this._resultSet) {
         throw this._wrapper.setResultCalledMultiple();
      } else if (this._exceptionSet) {
         throw this._wrapper.setResultAfterException();
      } else if (var1 == null) {
         throw this._wrapper.setResultCalledNullArgs();
      } else {
         this._resultAny = var1;
         this._resultSet = true;
         this._orb.getPIHandler().setServerPIInfo(this._resultAny);
      }
   }

   public void set_exception(Any var1) {
      if (var1 == null) {
         throw this._wrapper.setExceptionCalledNullArgs();
      } else {
         TCKind var2 = var1.type().kind();
         if (var2 != TCKind.tk_except) {
            throw this._wrapper.setExceptionCalledBadType();
         } else {
            this._exception = var1;
            this._orb.getPIHandler().setServerPIExceptionInfo(this._exception);
            if (!this._exceptionSet && !this._paramsCalled) {
               this._orb.getPIHandler().invokeServerPIIntermediatePoint();
            }

            this._exceptionSet = true;
         }
      }
   }

   public Any checkResultCalled() {
      if (this._paramsCalled && this._resultSet) {
         return null;
      } else if (this._paramsCalled && !this._resultSet && !this._exceptionSet) {
         try {
            TypeCode var1 = this._orb.get_primitive_tc(TCKind.tk_void);
            this._resultAny = this._orb.create_any();
            this._resultAny.type(var1);
            this._resultSet = true;
            return null;
         } catch (Exception var2) {
            throw this._wrapper.dsiResultException(CompletionStatus.COMPLETED_MAYBE, var2);
         }
      } else if (this._exceptionSet) {
         return this._exception;
      } else {
         throw this._wrapper.dsimethodNotcalled(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public void marshalReplyParams(OutputStream var1) {
      this._resultAny.write_value(var1);
      NamedValue var2 = null;

      for(int var3 = 0; var3 < this._arguments.count(); ++var3) {
         try {
            var2 = this._arguments.item(var3);
         } catch (Bounds var5) {
         }

         if (var2.flags() == 2 || var2.flags() == 3) {
            var2.value().write_value(var1);
         }
      }

   }

   public Context ctx() {
      if (this._paramsCalled && !this._resultSet && !this._exceptionSet) {
         throw this._wrapper.contextNotImplemented();
      } else {
         throw this._wrapper.contextCalledOutOfOrder();
      }
   }
}
