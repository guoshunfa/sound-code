package com.sun.security.jgss;

import jdk.Exported;

@Exported
public enum InquireType {
   KRB5_GET_SESSION_KEY,
   KRB5_GET_TKT_FLAGS,
   KRB5_GET_AUTHZ_DATA,
   KRB5_GET_AUTHTIME;
}
