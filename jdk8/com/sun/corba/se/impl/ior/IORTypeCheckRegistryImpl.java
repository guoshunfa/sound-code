package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation._ActivatorStub;
import com.sun.corba.se.spi.activation._InitialNameServiceStub;
import com.sun.corba.se.spi.activation._LocatorStub;
import com.sun.corba.se.spi.activation._RepositoryStub;
import com.sun.corba.se.spi.activation._ServerManagerStub;
import com.sun.corba.se.spi.activation._ServerStub;
import com.sun.corba.se.spi.ior.IORTypeCheckRegistry;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming._BindingIteratorStub;
import org.omg.CosNaming._NamingContextExtStub;
import org.omg.CosNaming._NamingContextStub;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynArray;
import org.omg.DynamicAny.DynEnum;
import org.omg.DynamicAny.DynFixed;
import org.omg.DynamicAny.DynSequence;
import org.omg.DynamicAny.DynStruct;
import org.omg.DynamicAny.DynUnion;
import org.omg.DynamicAny.DynValue;
import org.omg.DynamicAny._DynAnyFactoryStub;
import org.omg.DynamicAny._DynAnyStub;
import org.omg.DynamicAny._DynArrayStub;
import org.omg.DynamicAny._DynEnumStub;
import org.omg.DynamicAny._DynFixedStub;
import org.omg.DynamicAny._DynSequenceStub;
import org.omg.DynamicAny._DynStructStub;
import org.omg.DynamicAny._DynUnionStub;
import org.omg.DynamicAny._DynValueStub;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer._ServantActivatorStub;
import org.omg.PortableServer._ServantLocatorStub;

public class IORTypeCheckRegistryImpl implements IORTypeCheckRegistry {
   private final Set<String> iorTypeNames;
   private static final Set<String> builtinIorTypeNames = initBuiltinIorTypeNames();
   private ORB theOrb;

   public IORTypeCheckRegistryImpl(String var1, ORB var2) {
      this.theOrb = var2;
      this.iorTypeNames = this.parseIorClassNameList(var1);
   }

   public boolean isValidIORType(String var1) {
      this.dprintTransport(".isValidIORType : iorClassName == " + var1);
      return this.validateIorTypeByName(var1);
   }

   private boolean validateIorTypeByName(String var1) {
      this.dprintTransport(".validateIorTypeByName : iorClassName == " + var1);
      boolean var2 = this.checkIorTypeNames(var1);
      if (!var2) {
         var2 = this.checkBuiltinClassNames(var1);
      }

      this.dprintTransport(".validateIorTypeByName : isValidType == " + var2);
      return var2;
   }

   private boolean checkIorTypeNames(String var1) {
      return this.iorTypeNames != null && this.iorTypeNames.contains(var1);
   }

   private boolean checkBuiltinClassNames(String var1) {
      return builtinIorTypeNames.contains(var1);
   }

   private Set<String> parseIorClassNameList(String var1) {
      Set var2 = null;
      if (var1 != null) {
         String[] var3 = var1.split(";");
         var2 = Collections.unmodifiableSet(new HashSet(Arrays.asList(var3)));
         if (this.theOrb.orbInitDebugFlag) {
            this.dprintConfiguredIorTypeNames();
         }
      }

      return var2;
   }

   private static Set<String> initBuiltinIorTypeNames() {
      Set var0 = initBuiltInCorbaStubTypes();
      String[] var1 = new String[var0.size()];
      int var2 = 0;

      Class var4;
      for(Iterator var3 = var0.iterator(); var3.hasNext(); var1[var2++] = var4.getName()) {
         var4 = (Class)var3.next();
      }

      return Collections.unmodifiableSet(new HashSet(Arrays.asList(var1)));
   }

   private static Set<Class<?>> initBuiltInCorbaStubTypes() {
      Class[] var0 = new Class[]{Activator.class, _ActivatorStub.class, _InitialNameServiceStub.class, _LocatorStub.class, _RepositoryStub.class, _ServerManagerStub.class, _ServerStub.class, BindingIterator.class, _BindingIteratorStub.class, NamingContextExt.class, _NamingContextExtStub.class, NamingContext.class, _NamingContextStub.class, DynAnyFactory.class, _DynAnyFactoryStub.class, DynAny.class, _DynAnyStub.class, DynArray.class, _DynArrayStub.class, DynEnum.class, _DynEnumStub.class, DynFixed.class, _DynFixedStub.class, DynSequence.class, _DynSequenceStub.class, DynStruct.class, _DynStructStub.class, DynUnion.class, _DynUnionStub.class, _DynValueStub.class, DynValue.class, ServantActivator.class, _ServantActivatorStub.class, ServantLocator.class, _ServantLocatorStub.class};
      return new HashSet(Arrays.asList(var0));
   }

   private void dprintConfiguredIorTypeNames() {
      if (this.iorTypeNames != null) {
         Iterator var1 = this.iorTypeNames.iterator();

         while(var1.hasNext()) {
            String var2 = (String)var1.next();
            ORBUtility.dprint((Object)this, ".dprintConfiguredIorTypeNames: " + var2);
         }
      }

   }

   private void dprintTransport(String var1) {
      if (this.theOrb.transportDebugFlag) {
         ORBUtility.dprint((Object)this, var1);
      }

   }
}
