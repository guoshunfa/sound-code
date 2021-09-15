package org.omg.CORBA;

import com.sun.corba.se.impl.orb.ORBImpl;
import com.sun.corba.se.impl.orb.ORBSingleton;
import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.omg.CORBA.ORBPackage.InconsistentTypeCode;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;
import sun.reflect.misc.ReflectUtil;

public abstract class ORB {
   private static final String ORBClassKey = "org.omg.CORBA.ORBClass";
   private static final String ORBSingletonClassKey = "org.omg.CORBA.ORBSingletonClass";
   private static ORB singleton;

   private static String getSystemProperty(final String var0) {
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction() {
         public java.lang.Object run() {
            return System.getProperty(var0);
         }
      });
      return var1;
   }

   private static String getPropertyFromFile(final String var0) {
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction() {
         private Properties getFileProperties(String var1) {
            try {
               File var2 = new File(var1);
               if (!var2.exists()) {
                  return null;
               } else {
                  Properties var3 = new Properties();
                  FileInputStream var4 = new FileInputStream(var2);

                  try {
                     var3.load((InputStream)var4);
                  } finally {
                     var4.close();
                  }

                  return var3;
               }
            } catch (Exception var9) {
               return null;
            }
         }

         public java.lang.Object run() {
            String var1 = System.getProperty("user.home");
            String var2 = var1 + File.separator + "orb.properties";
            Properties var3 = this.getFileProperties(var2);
            String var4;
            if (var3 != null) {
               var4 = var3.getProperty(var0);
               if (var4 != null) {
                  return var4;
               }
            }

            var4 = System.getProperty("java.home");
            var2 = var4 + File.separator + "lib" + File.separator + "orb.properties";
            var3 = this.getFileProperties(var2);
            return var3 == null ? null : var3.getProperty(var0);
         }
      });
      return var1;
   }

   public static synchronized ORB init() {
      if (singleton == null) {
         String var0 = getSystemProperty("org.omg.CORBA.ORBSingletonClass");
         if (var0 == null) {
            var0 = getPropertyFromFile("org.omg.CORBA.ORBSingletonClass");
         }

         if (var0 != null && !var0.equals("com.sun.corba.se.impl.orb.ORBSingleton")) {
            singleton = create_impl(var0);
         } else {
            singleton = new ORBSingleton();
         }
      }

      return singleton;
   }

   private static ORB create_impl(String var0) {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();
      if (var1 == null) {
         var1 = ClassLoader.getSystemClassLoader();
      }

      try {
         ReflectUtil.checkPackageAccess(var0);
         Class var2 = ORB.class;
         Class var5 = Class.forName(var0, true, var1).asSubclass(var2);
         return (ORB)var5.newInstance();
      } catch (Throwable var4) {
         INITIALIZE var3 = new INITIALIZE("can't instantiate default ORB implementation " + var0);
         var3.initCause(var4);
         throw var3;
      }
   }

   public static ORB init(String[] var0, Properties var1) {
      String var2 = null;
      if (var1 != null) {
         var2 = var1.getProperty("org.omg.CORBA.ORBClass");
      }

      if (var2 == null) {
         var2 = getSystemProperty("org.omg.CORBA.ORBClass");
      }

      if (var2 == null) {
         var2 = getPropertyFromFile("org.omg.CORBA.ORBClass");
      }

      java.lang.Object var3;
      if (var2 != null && !var2.equals("com.sun.corba.se.impl.orb.ORBImpl")) {
         var3 = create_impl(var2);
      } else {
         var3 = new ORBImpl();
      }

      ((ORB)var3).set_parameters(var0, var1);
      return (ORB)var3;
   }

   public static ORB init(Applet var0, Properties var1) {
      String var2 = var0.getParameter("org.omg.CORBA.ORBClass");
      if (var2 == null && var1 != null) {
         var2 = var1.getProperty("org.omg.CORBA.ORBClass");
      }

      if (var2 == null) {
         var2 = getSystemProperty("org.omg.CORBA.ORBClass");
      }

      if (var2 == null) {
         var2 = getPropertyFromFile("org.omg.CORBA.ORBClass");
      }

      java.lang.Object var3;
      if (var2 != null && !var2.equals("com.sun.corba.se.impl.orb.ORBImpl")) {
         var3 = create_impl(var2);
      } else {
         var3 = new ORBImpl();
      }

      ((ORB)var3).set_parameters(var0, var1);
      return (ORB)var3;
   }

   protected abstract void set_parameters(String[] var1, Properties var2);

   protected abstract void set_parameters(Applet var1, Properties var2);

   public void connect(Object var1) {
      throw new NO_IMPLEMENT();
   }

   public void destroy() {
      throw new NO_IMPLEMENT();
   }

   public void disconnect(Object var1) {
      throw new NO_IMPLEMENT();
   }

   public abstract String[] list_initial_services();

   public abstract Object resolve_initial_references(String var1) throws InvalidName;

   public abstract String object_to_string(Object var1);

   public abstract Object string_to_object(String var1);

   public abstract NVList create_list(int var1);

   public NVList create_operation_list(Object var1) {
      Throwable var3;
      try {
         String var2 = "org.omg.CORBA.OperationDef";
         var3 = null;
         ClassLoader var4 = Thread.currentThread().getContextClassLoader();
         if (var4 == null) {
            var4 = ClassLoader.getSystemClassLoader();
         }

         Class var11 = Class.forName(var2, true, var4);
         Class[] var5 = new Class[]{var11};
         Method var6 = this.getClass().getMethod("create_operation_list", var5);
         java.lang.Object[] var7 = new java.lang.Object[]{var1};
         return (NVList)var6.invoke(this, var7);
      } catch (InvocationTargetException var8) {
         var3 = var8.getTargetException();
         if (var3 instanceof Error) {
            throw (Error)var3;
         } else if (var3 instanceof RuntimeException) {
            throw (RuntimeException)var3;
         } else {
            throw new NO_IMPLEMENT();
         }
      } catch (RuntimeException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new NO_IMPLEMENT();
      }
   }

   public abstract NamedValue create_named_value(String var1, Any var2, int var3);

   public abstract ExceptionList create_exception_list();

   public abstract ContextList create_context_list();

   public abstract Context get_default_context();

   public abstract Environment create_environment();

   public abstract OutputStream create_output_stream();

   public abstract void send_multiple_requests_oneway(Request[] var1);

   public abstract void send_multiple_requests_deferred(Request[] var1);

   public abstract boolean poll_next_response();

   public abstract Request get_next_response() throws WrongTransaction;

   public abstract TypeCode get_primitive_tc(TCKind var1);

   public abstract TypeCode create_struct_tc(String var1, String var2, StructMember[] var3);

   public abstract TypeCode create_union_tc(String var1, String var2, TypeCode var3, UnionMember[] var4);

   public abstract TypeCode create_enum_tc(String var1, String var2, String[] var3);

   public abstract TypeCode create_alias_tc(String var1, String var2, TypeCode var3);

   public abstract TypeCode create_exception_tc(String var1, String var2, StructMember[] var3);

   public abstract TypeCode create_interface_tc(String var1, String var2);

   public abstract TypeCode create_string_tc(int var1);

   public abstract TypeCode create_wstring_tc(int var1);

   public abstract TypeCode create_sequence_tc(int var1, TypeCode var2);

   /** @deprecated */
   @Deprecated
   public abstract TypeCode create_recursive_sequence_tc(int var1, int var2);

   public abstract TypeCode create_array_tc(int var1, TypeCode var2);

   public TypeCode create_native_tc(String var1, String var2) {
      throw new NO_IMPLEMENT();
   }

   public TypeCode create_abstract_interface_tc(String var1, String var2) {
      throw new NO_IMPLEMENT();
   }

   public TypeCode create_fixed_tc(short var1, short var2) {
      throw new NO_IMPLEMENT();
   }

   public TypeCode create_value_tc(String var1, String var2, short var3, TypeCode var4, ValueMember[] var5) {
      throw new NO_IMPLEMENT();
   }

   public TypeCode create_recursive_tc(String var1) {
      throw new NO_IMPLEMENT();
   }

   public TypeCode create_value_box_tc(String var1, String var2, TypeCode var3) {
      throw new NO_IMPLEMENT();
   }

   public abstract Any create_any();

   /** @deprecated */
   @Deprecated
   public Current get_current() {
      throw new NO_IMPLEMENT();
   }

   public void run() {
      throw new NO_IMPLEMENT();
   }

   public void shutdown(boolean var1) {
      throw new NO_IMPLEMENT();
   }

   public boolean work_pending() {
      throw new NO_IMPLEMENT();
   }

   public void perform_work() {
      throw new NO_IMPLEMENT();
   }

   public boolean get_service_information(short var1, ServiceInformationHolder var2) {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public DynAny create_dyn_any(Any var1) {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public DynAny create_basic_dyn_any(TypeCode var1) throws InconsistentTypeCode {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public DynStruct create_dyn_struct(TypeCode var1) throws InconsistentTypeCode {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public DynSequence create_dyn_sequence(TypeCode var1) throws InconsistentTypeCode {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public DynArray create_dyn_array(TypeCode var1) throws InconsistentTypeCode {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public DynUnion create_dyn_union(TypeCode var1) throws InconsistentTypeCode {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public DynEnum create_dyn_enum(TypeCode var1) throws InconsistentTypeCode {
      throw new NO_IMPLEMENT();
   }

   public Policy create_policy(int var1, Any var2) throws PolicyError {
      throw new NO_IMPLEMENT();
   }
}
