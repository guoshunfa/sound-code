package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.corba.ContextListImpl;
import com.sun.corba.se.impl.corba.EnvironmentImpl;
import com.sun.corba.se.impl.corba.ExceptionListImpl;
import com.sun.corba.se.impl.corba.NVListImpl;
import com.sun.corba.se.impl.corba.NamedValueImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.applet.Applet;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Current;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Request;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueFactory;
import sun.corba.OutputStreamFactory;

public class ORBSingleton extends ORB {
   private ORB fullORB;
   private static PresentationManager.StubFactoryFactory staticStubFactoryFactory = PresentationDefaults.getStaticStubFactoryFactory();

   public void set_parameters(Properties var1) {
   }

   protected void set_parameters(Applet var1, Properties var2) {
   }

   protected void set_parameters(String[] var1, Properties var2) {
   }

   public OutputStream create_output_stream() {
      return OutputStreamFactory.newEncapsOutputStream(this);
   }

   public TypeCode create_struct_tc(String var1, String var2, StructMember[] var3) {
      return new TypeCodeImpl(this, 15, var1, var2, var3);
   }

   public TypeCode create_union_tc(String var1, String var2, TypeCode var3, UnionMember[] var4) {
      return new TypeCodeImpl(this, 16, var1, var2, var3, var4);
   }

   public TypeCode create_enum_tc(String var1, String var2, String[] var3) {
      return new TypeCodeImpl(this, 17, var1, var2, var3);
   }

   public TypeCode create_alias_tc(String var1, String var2, TypeCode var3) {
      return new TypeCodeImpl(this, 21, var1, var2, var3);
   }

   public TypeCode create_exception_tc(String var1, String var2, StructMember[] var3) {
      return new TypeCodeImpl(this, 22, var1, var2, var3);
   }

   public TypeCode create_interface_tc(String var1, String var2) {
      return new TypeCodeImpl(this, 14, var1, var2);
   }

   public TypeCode create_string_tc(int var1) {
      return new TypeCodeImpl(this, 18, var1);
   }

   public TypeCode create_wstring_tc(int var1) {
      return new TypeCodeImpl(this, 27, var1);
   }

   public TypeCode create_sequence_tc(int var1, TypeCode var2) {
      return new TypeCodeImpl(this, 19, var1, var2);
   }

   public TypeCode create_recursive_sequence_tc(int var1, int var2) {
      return new TypeCodeImpl(this, 19, var1, var2);
   }

   public TypeCode create_array_tc(int var1, TypeCode var2) {
      return new TypeCodeImpl(this, 20, var1, var2);
   }

   public TypeCode create_native_tc(String var1, String var2) {
      return new TypeCodeImpl(this, 31, var1, var2);
   }

   public TypeCode create_abstract_interface_tc(String var1, String var2) {
      return new TypeCodeImpl(this, 32, var1, var2);
   }

   public TypeCode create_fixed_tc(short var1, short var2) {
      return new TypeCodeImpl(this, 28, var1, var2);
   }

   public TypeCode create_value_tc(String var1, String var2, short var3, TypeCode var4, ValueMember[] var5) {
      return new TypeCodeImpl(this, 29, var1, var2, var3, var4, var5);
   }

   public TypeCode create_recursive_tc(String var1) {
      return new TypeCodeImpl(this, var1);
   }

   public TypeCode create_value_box_tc(String var1, String var2, TypeCode var3) {
      return new TypeCodeImpl(this, 30, var1, var2, var3);
   }

   public TypeCode get_primitive_tc(TCKind var1) {
      return this.get_primitive_tc(var1.value());
   }

   public Any create_any() {
      return new AnyImpl(this);
   }

   public NVList create_list(int var1) {
      return new NVListImpl(this, var1);
   }

   public NVList create_operation_list(Object var1) {
      throw this.wrapper.genericNoImpl();
   }

   public NamedValue create_named_value(String var1, Any var2, int var3) {
      return new NamedValueImpl(this, var1, var2, var3);
   }

   public ExceptionList create_exception_list() {
      return new ExceptionListImpl();
   }

   public ContextList create_context_list() {
      return new ContextListImpl(this);
   }

   public Context get_default_context() {
      throw this.wrapper.genericNoImpl();
   }

   public Environment create_environment() {
      return new EnvironmentImpl();
   }

   public Current get_current() {
      throw this.wrapper.genericNoImpl();
   }

   public String[] list_initial_services() {
      throw this.wrapper.genericNoImpl();
   }

   public Object resolve_initial_references(String var1) throws InvalidName {
      throw this.wrapper.genericNoImpl();
   }

   public void register_initial_reference(String var1, Object var2) throws InvalidName {
      throw this.wrapper.genericNoImpl();
   }

   public void send_multiple_requests_oneway(Request[] var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void send_multiple_requests_deferred(Request[] var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public boolean poll_next_response() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public Request get_next_response() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public String object_to_string(Object var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public Object string_to_object(String var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public Remote string_to_remote(String var1) throws RemoteException {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void connect(Object var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void disconnect(Object var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void run() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void shutdown(boolean var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   protected void shutdownServants(boolean var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   protected void destroyConnections() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void destroy() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public boolean work_pending() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void perform_work() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public ValueFactory register_value_factory(String var1, ValueFactory var2) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void unregister_value_factory(String var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public ValueFactory lookup_value_factory(String var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public TransportManager getTransportManager() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public CorbaTransportManager getCorbaTransportManager() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public LegacyServerSocketManager getLegacyServerSocketManager() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   private synchronized ORB getFullORB() {
      if (this.fullORB == null) {
         Properties var1 = new Properties();
         this.fullORB = new ORBImpl();
         this.fullORB.set_parameters(var1);
      }

      return this.fullORB;
   }

   public RequestDispatcherRegistry getRequestDispatcherRegistry() {
      return this.getFullORB().getRequestDispatcherRegistry();
   }

   public ServiceContextRegistry getServiceContextRegistry() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public int getTransientServerId() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public int getORBInitialPort() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public String getORBInitialHost() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public String getORBServerHost() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public int getORBServerPort() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public CodeSetComponentInfo getCodeSetComponentInfo() {
      return new CodeSetComponentInfo();
   }

   public boolean isLocalHost(String var1) {
      return false;
   }

   public boolean isLocalServerId(int var1, int var2) {
      return false;
   }

   public ORBVersion getORBVersion() {
      return ORBVersionFactory.getORBVersion();
   }

   public void setORBVersion(ORBVersion var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public String getAppletHost() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public URL getAppletCodeBase() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public int getHighWaterMark() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public int getLowWaterMark() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public int getNumberToReclaim() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public int getGIOPFragmentSize() {
      return 1024;
   }

   public int getGIOPBuffMgrStrategy(GIOPVersion var1) {
      return 0;
   }

   public IOR getFVDCodeBaseIOR() {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public Policy create_policy(int var1, Any var2) throws PolicyError {
      throw new NO_IMPLEMENT();
   }

   public LegacyServerSocketEndPointInfo getServerEndpoint() {
      return null;
   }

   public void setPersistentServerId(int var1) {
   }

   public TypeCodeImpl getTypeCodeForClass(Class var1) {
      return null;
   }

   public void setTypeCodeForClass(Class var1, TypeCodeImpl var2) {
   }

   public boolean alwaysSendCodeSetServiceContext() {
      return true;
   }

   public boolean isDuringDispatch() {
      return false;
   }

   public void notifyORB() {
   }

   public PIHandler getPIHandler() {
      return null;
   }

   public void checkShutdownState() {
   }

   public void startingDispatch() {
   }

   public void finishedDispatch() {
   }

   public void registerInitialReference(String var1, Closure var2) {
   }

   public ORBData getORBData() {
      return this.getFullORB().getORBData();
   }

   public void setClientDelegateFactory(ClientDelegateFactory var1) {
   }

   public ClientDelegateFactory getClientDelegateFactory() {
      return this.getFullORB().getClientDelegateFactory();
   }

   public void setCorbaContactInfoListFactory(CorbaContactInfoListFactory var1) {
   }

   public CorbaContactInfoListFactory getCorbaContactInfoListFactory() {
      return this.getFullORB().getCorbaContactInfoListFactory();
   }

   public Operation getURLOperation() {
      return null;
   }

   public void setINSDelegate(CorbaServerRequestDispatcher var1) {
   }

   public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() {
      return this.getFullORB().getTaggedComponentFactoryFinder();
   }

   public IdentifiableFactoryFinder getTaggedProfileFactoryFinder() {
      return this.getFullORB().getTaggedProfileFactoryFinder();
   }

   public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder() {
      return this.getFullORB().getTaggedProfileTemplateFactoryFinder();
   }

   public ObjectKeyFactory getObjectKeyFactory() {
      return this.getFullORB().getObjectKeyFactory();
   }

   public void setObjectKeyFactory(ObjectKeyFactory var1) {
      throw new SecurityException("ORBSingleton: access denied");
   }

   public void handleBadServerId(ObjectKey var1) {
   }

   public OAInvocationInfo peekInvocationInfo() {
      return null;
   }

   public void pushInvocationInfo(OAInvocationInfo var1) {
   }

   public OAInvocationInfo popInvocationInfo() {
      return null;
   }

   public ClientInvocationInfo createOrIncrementInvocationInfo() {
      return null;
   }

   public void releaseOrDecrementInvocationInfo() {
   }

   public ClientInvocationInfo getInvocationInfo() {
      return null;
   }

   public ConnectionCache getConnectionCache(ContactInfo var1) {
      return null;
   }

   public void setResolver(Resolver var1) {
   }

   public Resolver getResolver() {
      return null;
   }

   public void setLocalResolver(LocalResolver var1) {
   }

   public LocalResolver getLocalResolver() {
      return null;
   }

   public void setURLOperation(Operation var1) {
   }

   public void setBadServerIdHandler(BadServerIdHandler var1) {
   }

   public void initBadServerIdHandler() {
   }

   public Selector getSelector(int var1) {
      return null;
   }

   public void setThreadPoolManager(ThreadPoolManager var1) {
   }

   public ThreadPoolManager getThreadPoolManager() {
      return null;
   }

   public CopierManager getCopierManager() {
      return null;
   }

   public void validateIORClass(String var1) {
      this.getFullORB().validateIORClass(var1);
   }
}
