package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import java.io.Closeable;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

public interface PIHandler extends Closeable {
   void initialize();

   void destroyInterceptors();

   void objectAdapterCreated(ObjectAdapter var1);

   void adapterManagerStateChanged(int var1, short var2);

   void adapterStateChanged(ObjectReferenceTemplate[] var1, short var2);

   void disableInterceptorsThisThread();

   void enableInterceptorsThisThread();

   void invokeClientPIStartingPoint() throws RemarshalException;

   Exception invokeClientPIEndingPoint(int var1, Exception var2);

   Exception makeCompletedClientRequest(int var1, Exception var2);

   void initiateClientPIRequest(boolean var1);

   void cleanupClientPIRequest();

   void setClientPIInfo(RequestImpl var1);

   void setClientPIInfo(CorbaMessageMediator var1);

   void invokeServerPIStartingPoint();

   void invokeServerPIIntermediatePoint();

   void invokeServerPIEndingPoint(ReplyMessage var1);

   void initializeServerPIInfo(CorbaMessageMediator var1, ObjectAdapter var2, byte[] var3, ObjectKeyTemplate var4);

   void setServerPIInfo(Object var1, String var2);

   void setServerPIInfo(Exception var1);

   void setServerPIInfo(NVList var1);

   void setServerPIExceptionInfo(Any var1);

   void setServerPIInfo(Any var1);

   void cleanupServerPIRequest();

   Policy create_policy(int var1, Any var2) throws PolicyError;

   void register_interceptor(Interceptor var1, int var2) throws DuplicateName;

   Current getPICurrent();

   void registerPolicyFactory(int var1, PolicyFactory var2);

   int allocateServerRequestId();
}
