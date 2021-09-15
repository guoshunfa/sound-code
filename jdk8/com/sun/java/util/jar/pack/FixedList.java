package com.sun.java.util.jar.pack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class FixedList<E> implements List<E> {
   private final ArrayList<E> flist;

   protected FixedList(int var1) {
      this.flist = new ArrayList(var1);

      for(int var2 = 0; var2 < var1; ++var2) {
         this.flist.add((Object)null);
      }

   }

   public int size() {
      return this.flist.size();
   }

   public boolean isEmpty() {
      return this.flist.isEmpty();
   }

   public boolean contains(Object var1) {
      return this.flist.contains(var1);
   }

   public Iterator<E> iterator() {
      return this.flist.iterator();
   }

   public Object[] toArray() {
      return this.flist.toArray();
   }

   public <T> T[] toArray(T[] var1) {
      return this.flist.toArray(var1);
   }

   public boolean add(E var1) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public boolean remove(Object var1) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public boolean containsAll(Collection<?> var1) {
      return this.flist.containsAll(var1);
   }

   public boolean addAll(Collection<? extends E> var1) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public boolean addAll(int var1, Collection<? extends E> var2) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public boolean removeAll(Collection<?> var1) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public boolean retainAll(Collection<?> var1) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public void clear() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public E get(int var1) {
      return this.flist.get(var1);
   }

   public E set(int var1, E var2) {
      return this.flist.set(var1, var2);
   }

   public void add(int var1, E var2) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public E remove(int var1) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("operation not permitted");
   }

   public int indexOf(Object var1) {
      return this.flist.indexOf(var1);
   }

   public int lastIndexOf(Object var1) {
      return this.flist.lastIndexOf(var1);
   }

   public ListIterator<E> listIterator() {
      return this.flist.listIterator();
   }

   public ListIterator<E> listIterator(int var1) {
      return this.flist.listIterator(var1);
   }

   public List<E> subList(int var1, int var2) {
      return this.flist.subList(var1, var2);
   }

   public String toString() {
      return "FixedList{plist=" + this.flist + '}';
   }
}
