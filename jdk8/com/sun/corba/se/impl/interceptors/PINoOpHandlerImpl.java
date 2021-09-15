package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.PIHandler;
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

public class PINoOpHandlerImpl implements PIHandler {
   public void close() {
   }

   public void initialize() {
   }

   public void destroyInterceptors() {
   }

   public void objectAdapterCreated(ObjectAdapter var1) {
   }

   public void adapterManagerStateChanged(int var1, short var2) {
   }

   public void adapterStateChanged(ObjectReferenceTemplate[] var1, short var2) {
   }

   public void disableInterceptorsThisThread() {
   }

   public void enableInterceptorsThisThread() {
   }

   public void invokeClientPIStartingPoint() throws RemarshalException {
   }

   public Exception invokeClientPIEndingPoint(int var1, Exception var2) {
      return null;
   }

   public Exception makeCompletedClientRequest(int var1, Exception var2) {
      return null;
   }

   public void initiateClientPIRequest(boolean var1) {
   }

   public void cleanupClientPIRequest() {
   }

   public void setClientPIInfo(CorbaMessageMediator var1) {
   }

   public void setClientPIInfo(RequestImpl var1) {
   }

   public final void sendCancelRequestIfFinalFragmentNotSent() {
   }

   public void invokeServerPIStartingPoint() {
   }

   public void invokeServerPIIntermediatePoint() {
   }

   public void invokeServerPIEndingPoint(ReplyMessage var1) {
   }

   public void setServerPIInfo(Exception var1) {
   }

   public void setServerPIInfo(NVList var1) {
   }

   public void setServerPIExceptionInfo(Any var1) {
   }

   public void setServerPIInfo(Any var1) {
   }

   public void initializeServerPIInfo(CorbaMessageMediator var1, ObjectAdapter var2, byte[] var3, ObjectKeyTemplate var4) {
   }

   public void setServerPIInfo(Object var1, String var2) {
   }

   public void cleanupServerPIRequest() {
   }

   public void register_interceptor(Interceptor var1, int var2) throws DuplicateName {
   }

   public Current getPICurrent() {
      return null;
   }

   public Policy create_policy(int var1, Any var2) throws PolicyError {
      return null;
   }

   public void registerPolicyFactory(int var1, PolicyFactory var2) {
   }

   public int allocateServerRequestId() {
      return 0;
   }
}
