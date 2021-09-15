package com.sun.org.apache.xerces.internal.impl.validation;

import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.ArrayList;
import java.util.Locale;

public class ValidationState implements ValidationContext {
   private boolean fExtraChecking = true;
   private boolean fFacetChecking = true;
   private boolean fNormalize = true;
   private boolean fNamespaces = true;
   private EntityState fEntityState = null;
   private NamespaceContext fNamespaceContext = null;
   private SymbolTable fSymbolTable = null;
   private Locale fLocale = null;
   private ArrayList<String> fIdList;
   private ArrayList<String> fIdRefList;

   public void setExtraChecking(boolean newValue) {
      this.fExtraChecking = newValue;
   }

   public void setFacetChecking(boolean newValue) {
      this.fFacetChecking = newValue;
   }

   public void setNormalizationRequired(boolean newValue) {
      this.fNormalize = newValue;
   }

   public void setUsingNamespaces(boolean newValue) {
      this.fNamespaces = newValue;
   }

   public void setEntityState(EntityState state) {
      this.fEntityState = state;
   }

   public void setNamespaceSupport(NamespaceContext namespace) {
      this.fNamespaceContext = namespace;
   }

   public void setSymbolTable(SymbolTable sTable) {
      this.fSymbolTable = sTable;
   }

   public String checkIDRefID() {
      if (this.fIdList == null && this.fIdRefList != null) {
         return (String)this.fIdRefList.get(0);
      } else {
         if (this.fIdRefList != null) {
            for(int i = 0; i < this.fIdRefList.size(); ++i) {
               String key = (String)this.fIdRefList.get(i);
               if (!this.fIdList.contains(key)) {
                  return key;
               }
            }
         }

         return null;
      }
   }

   public void reset() {
      this.fExtraChecking = true;
      this.fFacetChecking = true;
      this.fNamespaces = true;
      this.fIdList = null;
      this.fIdRefList = null;
      this.fEntityState = null;
      this.fNamespaceContext = null;
      this.fSymbolTable = null;
   }

   public void resetIDTables() {
      this.fIdList = null;
      this.fIdRefList = null;
   }

   public boolean needExtraChecking() {
      return this.fExtraChecking;
   }

   public boolean needFacetChecking() {
      return this.fFacetChecking;
   }

   public boolean needToNormalize() {
      return this.fNormalize;
   }

   public boolean useNamespaces() {
      return this.fNamespaces;
   }

   public boolean isEntityDeclared(String name) {
      return this.fEntityState != null ? this.fEntityState.isEntityDeclared(this.getSymbol(name)) : false;
   }

   public boolean isEntityUnparsed(String name) {
      return this.fEntityState != null ? this.fEntityState.isEntityUnparsed(this.getSymbol(name)) : false;
   }

   public boolean isIdDeclared(String name) {
      return this.fIdList == null ? false : this.fIdList.contains(name);
   }

   public void addId(String name) {
      if (this.fIdList == null) {
         this.fIdList = new ArrayList();
      }

      this.fIdList.add(name);
   }

   public void addIdRef(String name) {
      if (this.fIdRefList == null) {
         this.fIdRefList = new ArrayList();
      }

      this.fIdRefList.add(name);
   }

   public String getSymbol(String symbol) {
      return this.fSymbolTable != null ? this.fSymbolTable.addSymbol(symbol) : symbol.intern();
   }

   public String getURI(String prefix) {
      return this.fNamespaceContext != null ? this.fNamespaceContext.getURI(prefix) : null;
   }

   public void setLocale(Locale locale) {
      this.fLocale = locale;
   }

   public Locale getLocale() {
      return this.fLocale;
   }
}
