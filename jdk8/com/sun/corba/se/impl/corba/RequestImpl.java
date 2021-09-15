package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class RequestImpl extends Request {
   protected Object _target;
   protected String _opName;
   protected NVList _arguments;
   protected ExceptionList _exceptions;
   private NamedValue _result;
   protected Environment _env;
   private Context _ctx;
   private ContextList _ctxList;
   protected ORB _orb;
   private ORBUtilSystemException _wrapper;
   protected boolean _isOneWay = false;
   private int[] _paramCodes;
   private long[] _paramLongs;
   private java.lang.Object[] _paramObjects;
   protected boolean gotResponse = false;

   public RequestImpl(ORB var1, Object var2, Context var3, String var4, NVList var5, NamedValue var6, ExceptionList var7, ContextList var8) {
      this._orb = var1;
      this._wrapper = ORBUtilSystemException.get(var1, "oa.invocation");
      this._target = var2;
      this._ctx = var3;
      this._opName = var4;
      if (var5 == null) {
         this._arguments = new NVListImpl(this._orb);
      } else {
         this._arguments = var5;
      }

      this._result = var6;
      if (var7 == null) {
         this._exceptions = new ExceptionListImpl();
      } else {
         this._exceptions = var7;
      }

      if (var8 == null) {
         this._ctxList = new ContextListImpl(this._orb);
      } else {
         this._ctxList = var8;
      }

      this._env = new EnvironmentImpl();
   }

   public Object target() {
      return this._target;
   }

   public String operation() {
      return this._opName;
   }

   public NVList arguments() {
      return this._arguments;
   }

   public NamedValue result() {
      return this._result;
   }

   public Environment env() {
      return this._env;
   }

   public ExceptionList exceptions() {
      return this._exceptions;
   }

   public ContextList contexts() {
      return this._ctxList;
   }

   public synchronized Context ctx() {
      if (this._ctx == null) {
         this._ctx = new ContextImpl(this._orb);
      }

      return this._ctx;
   }

   public synchronized void ctx(Context var1) {
      this._ctx = var1;
   }

   public synchronized Any add_in_arg() {
      return this._arguments.add(1).value();
   }

   public synchronized Any add_named_in_arg(String var1) {
      return this._arguments.add_item(var1, 1).value();
   }

   public synchronized Any add_inout_arg() {
      return this._arguments.add(3).value();
   }

   public synchronized Any add_named_inout_arg(String var1) {
      return this._arguments.add_item(var1, 3).value();
   }

   public synchronized Any add_out_arg() {
      return this._arguments.add(2).value();
   }

   public synchronized Any add_named_out_arg(String var1) {
      return this._arguments.add_item(var1, 2).value();
   }

   public synchronized void set_return_type(TypeCode var1) {
      if (this._result == null) {
         this._result = new NamedValueImpl(this._orb);
      }

      this._result.value().type(var1);
   }

   public synchronized Any return_value() {
      if (this._result == null) {
         this._result = new NamedValueImpl(this._orb);
      }

      return this._result.value();
   }

   public synchronized void add_exception(TypeCode var1) {
      this._exceptions.add(var1);
   }

   public synchronized void invoke() {
      this.doInvocation();
   }

   public synchronized void send_oneway() {
      this._isOneWay = true;
      this.doInvocation();
   }

   public synchronized void send_deferred() {
      AsynchInvoke var1 = new AsynchInvoke(this._orb, this, false);
      (new Thread(var1)).start();
   }

   public synchronized boolean poll_response() {
      return this.gotResponse;
   }

   public synchronized void get_response() throws WrongTransaction {
      while(!this.gotResponse) {
         try {
            this.wait();
         } catch (InterruptedException var2) {
         }
      }

   }

   protected void doInvocation() {
      Delegate var1 = StubAdapter.getDelegate(this._target);
      this._orb.getPIHandler().initiateClientPIRequest(true);
      this._orb.getPIHandler().setClientPIInfo(this);
      InputStream var2 = null;

      try {
         OutputStream var3 = var1.request((Object)null, this._opName, !this._isOneWay);

         try {
            for(int var4 = 0; var4 < this._arguments.count(); ++var4) {
               NamedValue var5 = this._arguments.item(var4);
               switch(var5.flags()) {
               case 1:
                  var5.value().write_value(var3);
               case 2:
               default:
                  break;
               case 3:
                  var5.value().write_value(var3);
               }
            }
         } catch (Bounds var12) {
            throw this._wrapper.boundsErrorInDiiRequest((Throwable)var12);
         }

         var2 = var1.invoke((Object)null, var3);
      } catch (ApplicationException var13) {
      } catch (RemarshalException var14) {
         this.doInvocation();
      } catch (SystemException var15) {
         this._env.exception(var15);
         throw var15;
      } finally {
         var1.releaseReply((Object)null, var2);
      }

   }

   public void unmarshalReply(InputStream var1) {
      if (this._result != null) {
         Any var2 = this._result.value();
         TypeCode var3 = var2.type();
         if (var3.kind().value() != 1) {
            var2.read_value(var1, var3);
         }
      }

      try {
         int var6 = 0;

         while(var6 < this._arguments.count()) {
            NamedValue var7 = this._arguments.item(var6);
            switch(var7.flags()) {
            case 2:
            case 3:
               Any var4 = var7.value();
               var4.read_value(var1, var4.type());
            case 1:
            default:
               ++var6;
            }
         }
      } catch (Bounds var5) {
      }

   }
}
