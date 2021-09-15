package com.sun.security.jgss;

import javax.security.auth.Subject;
import jdk.Exported;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

@Exported
public class GSSUtil {
   public static Subject createSubject(GSSName var0, GSSCredential var1) {
      return sun.security.jgss.GSSUtil.getSubject(var0, var1);
   }
}
