package com.sun.security.jgss;

import jdk.Exported;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

@Exported
public interface ExtendedGSSCredential extends GSSCredential {
   GSSCredential impersonate(GSSName var1) throws GSSException;
}
