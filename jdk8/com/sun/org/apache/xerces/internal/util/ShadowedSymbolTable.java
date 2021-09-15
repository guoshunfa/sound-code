package com.sun.org.apache.xerces.internal.util;

public final class ShadowedSymbolTable extends SymbolTable {
   protected SymbolTable fSymbolTable;

   public ShadowedSymbolTable(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
   }

   public String addSymbol(String symbol) {
      return this.fSymbolTable.containsSymbol(symbol) ? this.fSymbolTable.addSymbol(symbol) : super.addSymbol(symbol);
   }

   public String addSymbol(char[] buffer, int offset, int length) {
      return this.fSymbolTable.containsSymbol(buffer, offset, length) ? this.fSymbolTable.addSymbol(buffer, offset, length) : super.addSymbol(buffer, offset, length);
   }

   public int hash(String symbol) {
      return this.fSymbolTable.hash(symbol);
   }

   public int hash(char[] buffer, int offset, int length) {
      return this.fSymbolTable.hash(buffer, offset, length);
   }
}
