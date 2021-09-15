package com.sun.security.jgss;

import jdk.Exported;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

@Exported
public interface ExtendedGSSContext extends GSSContext {
   Object inquireSecContext(InquireType var1) throws GSSException;

   void requestDelegPolicy(boolean var1) throws GSSException;

   boolean getDelegPolicyState();
}
