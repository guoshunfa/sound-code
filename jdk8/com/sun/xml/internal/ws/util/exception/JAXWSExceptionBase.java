package com.sun.xml.internal.ws.util.exception;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessage;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.NullLocalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;

public abstract class JAXWSExceptionBase extends WebServiceException implements Localizable {
   private static final long serialVersionUID = 1L;
   private transient Localizable msg;

   /** @deprecated */
   protected JAXWSExceptionBase(String key, Object... args) {
      super(findNestedException(args));
      this.msg = new LocalizableMessage(this.getDefaultResourceBundleName(), key, args);
   }

   protected JAXWSExceptionBase(String message) {
      this((Localizable)(new NullLocalizable(message)));
   }

   protected JAXWSExceptionBase(Throwable throwable) {
      this((Localizable)(new NullLocalizable(throwable.toString())), (Throwable)throwable);
   }

   protected JAXWSExceptionBase(Localizable msg) {
      this.msg = msg;
   }

   protected JAXWSExceptionBase(Localizable msg, Throwable cause) {
      super(cause);
      this.msg = msg;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.msg.getResourceBundleName());
      out.writeObject(this.msg.getKey());
      Object[] args = this.msg.getArguments();
      if (args == null) {
         out.writeInt(-1);
      } else {
         out.writeInt(args.length);

         for(int i = 0; i < args.length; ++i) {
            if (args[i] != null && !(args[i] instanceof Serializable)) {
               out.writeObject(args[i].toString());
            } else {
               out.writeObject(args[i]);
            }
         }

      }
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      String resourceBundleName = (String)in.readObject();
      String key = (String)in.readObject();
      int len = in.readInt();
      if (len < -1) {
         throw new NegativeArraySizeException();
      } else {
         Object[] args;
         if (len == -1) {
            args = null;
         } else if (len < 255) {
            args = new Object[len];

            for(int i = 0; i < args.length; ++i) {
               args[i] = in.readObject();
            }
         } else {
            List<Object> argList = new ArrayList(Math.min(len, 1024));

            for(int i = 0; i < len; ++i) {
               argList.add(in.readObject());
            }

            args = argList.toArray(new Object[argList.size()]);
         }

         this.msg = (new LocalizableMessageFactory(resourceBundleName)).getMessage(key, args);
      }
   }

   private static Throwable findNestedException(Object[] args) {
      if (args == null) {
         return null;
      } else {
         Object[] var1 = args;
         int var2 = args.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Object o = var1[var3];
            if (o instanceof Throwable) {
               return (Throwable)o;
            }
         }

         return null;
      }
   }

   public String getMessage() {
      Localizer localizer = new Localizer();
      return localizer.localize(this);
   }

   protected abstract String getDefaultResourceBundleName();

   public final String getKey() {
      return this.msg.getKey();
   }

   public final Object[] getArguments() {
      return this.msg.getArguments();
   }

   public final String getResourceBundleName() {
      return this.msg.getResourceBundleName();
   }
}
