package com.sun.org.apache.xerces.internal.util;

public final class SynchronizedSymbolTable extends SymbolTable {
   protected SymbolTable fSymbolTable;

   public SynchronizedSymbolTable(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
   }

   public SynchronizedSymbolTable() {
      this.fSymbolTable = new SymbolTable();
   }

   public SynchronizedSymbolTable(int size) {
      this.fSymbolTable = new SymbolTable(size);
   }

   public String addSymbol(String symbol) {
      synchronized(this.fSymbolTable) {
         return this.fSymbolTable.addSymbol(symbol);
      }
   }

   public String addSymbol(char[] buffer, int offset, int length) {
      synchronized(this.fSymbolTable) {
         return this.fSymbolTable.addSymbol(buffer, offset, length);
      }
   }

   public boolean containsSymbol(String symbol) {
      synchronized(this.fSymbolTable) {
         return this.fSymbolTable.containsSymbol(symbol);
      }
   }

   public boolean containsSymbol(char[] buffer, int offset, int length) {
      synchronized(this.fSymbolTable) {
         return this.fSymbolTable.containsSymbol(buffer, offset, length);
      }
   }
}
