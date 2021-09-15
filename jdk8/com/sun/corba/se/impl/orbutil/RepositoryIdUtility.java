package com.sun.corba.se.impl.orbutil;

public interface RepositoryIdUtility {
   int NO_TYPE_INFO = 0;
   int SINGLE_REP_TYPE_INFO = 2;
   int PARTIAL_LIST_TYPE_INFO = 6;

   boolean isChunkedEncoding(int var1);

   boolean isCodeBasePresent(int var1);

   int getTypeInfo(int var1);

   int getStandardRMIChunkedNoRepStrId();

   int getCodeBaseRMIChunkedNoRepStrId();

   int getStandardRMIChunkedId();

   int getCodeBaseRMIChunkedId();

   int getStandardRMIUnchunkedId();

   int getCodeBaseRMIUnchunkedId();

   int getStandardRMIUnchunkedNoRepStrId();

   int getCodeBaseRMIUnchunkedNoRepStrId();
}
