package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.TypeMismatchException;
import com.sun.corba.se.impl.util.RepositoryId;
import java.io.Serializable;
import java.net.MalformedURLException;

public final class RepIdDelegator implements RepositoryIdStrings, RepositoryIdUtility, RepositoryIdInterface {
   private final RepositoryId delegate;

   public String createForAnyType(Class var1) {
      return RepositoryId.createForAnyType(var1);
   }

   public String createForJavaType(Serializable var1) throws TypeMismatchException {
      return RepositoryId.createForJavaType(var1);
   }

   public String createForJavaType(Class var1) throws TypeMismatchException {
      return RepositoryId.createForJavaType(var1);
   }

   public String createSequenceRepID(Object var1) {
      return RepositoryId.createSequenceRepID(var1);
   }

   public String createSequenceRepID(Class var1) {
      return RepositoryId.createSequenceRepID(var1);
   }

   public RepositoryIdInterface getFromString(String var1) {
      return new RepIdDelegator(RepositoryId.cache.getId(var1));
   }

   public boolean isChunkedEncoding(int var1) {
      return RepositoryId.isChunkedEncoding(var1);
   }

   public boolean isCodeBasePresent(int var1) {
      return RepositoryId.isCodeBasePresent(var1);
   }

   public String getClassDescValueRepId() {
      return RepositoryId.kClassDescValueRepID;
   }

   public String getWStringValueRepId() {
      return "IDL:omg.org/CORBA/WStringValue:1.0";
   }

   public int getTypeInfo(int var1) {
      return RepositoryId.getTypeInfo(var1);
   }

   public int getStandardRMIChunkedNoRepStrId() {
      return RepositoryId.kPreComputed_StandardRMIChunked_NoRep;
   }

   public int getCodeBaseRMIChunkedNoRepStrId() {
      return RepositoryId.kPreComputed_CodeBaseRMIChunked_NoRep;
   }

   public int getStandardRMIChunkedId() {
      return RepositoryId.kPreComputed_StandardRMIChunked;
   }

   public int getCodeBaseRMIChunkedId() {
      return RepositoryId.kPreComputed_CodeBaseRMIChunked;
   }

   public int getStandardRMIUnchunkedId() {
      return RepositoryId.kPreComputed_StandardRMIUnchunked;
   }

   public int getCodeBaseRMIUnchunkedId() {
      return RepositoryId.kPreComputed_CodeBaseRMIUnchunked;
   }

   public int getStandardRMIUnchunkedNoRepStrId() {
      return RepositoryId.kPreComputed_StandardRMIUnchunked_NoRep;
   }

   public int getCodeBaseRMIUnchunkedNoRepStrId() {
      return RepositoryId.kPreComputed_CodeBaseRMIUnchunked_NoRep;
   }

   public Class getClassFromType() throws ClassNotFoundException {
      return this.delegate.getClassFromType();
   }

   public Class getClassFromType(String var1) throws ClassNotFoundException, MalformedURLException {
      return this.delegate.getClassFromType(var1);
   }

   public Class getClassFromType(Class var1, String var2) throws ClassNotFoundException, MalformedURLException {
      return this.delegate.getClassFromType(var1, var2);
   }

   public String getClassName() {
      return this.delegate.getClassName();
   }

   public RepIdDelegator() {
      this((RepositoryId)null);
   }

   private RepIdDelegator(RepositoryId var1) {
      this.delegate = var1;
   }

   public String toString() {
      return this.delegate != null ? this.delegate.toString() : this.getClass().getName();
   }

   public boolean equals(Object var1) {
      return this.delegate != null ? this.delegate.equals(var1) : super.equals(var1);
   }

   public int hashCode() {
      return this.delegate != null ? this.delegate.hashCode() : super.hashCode();
   }
}
