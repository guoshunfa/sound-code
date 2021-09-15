package com.sun.security.auth;

import javax.security.auth.Subject;
import jdk.Exported;

@Exported
public interface PrincipalComparator {
   boolean implies(Subject var1);
}
