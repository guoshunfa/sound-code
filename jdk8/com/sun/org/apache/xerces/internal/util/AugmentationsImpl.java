package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class AugmentationsImpl implements Augmentations {
   private AugmentationsImpl.AugmentationsItemsContainer fAugmentationsContainer = new AugmentationsImpl.SmallContainer();

   public Object putItem(String key, Object item) {
      Object oldValue = this.fAugmentationsContainer.putItem(key, item);
      if (oldValue == null && this.fAugmentationsContainer.isFull()) {
         this.fAugmentationsContainer = this.fAugmentationsContainer.expand();
      }

      return oldValue;
   }

   public Object getItem(String key) {
      return this.fAugmentationsContainer.getItem(key);
   }

   public Object removeItem(String key) {
      return this.fAugmentationsContainer.removeItem(key);
   }

   public Enumeration keys() {
      return this.fAugmentationsContainer.keys();
   }

   public void removeAllItems() {
      this.fAugmentationsContainer.clear();
   }

   public String toString() {
      return this.fAugmentationsContainer.toString();
   }

   class LargeContainer extends AugmentationsImpl.AugmentationsItemsContainer {
      final Map<Object, Object> fAugmentations = new HashMap();

      LargeContainer() {
         super();
      }

      public Object getItem(Object key) {
         return this.fAugmentations.get(key);
      }

      public Object putItem(Object key, Object item) {
         return this.fAugmentations.put(key, item);
      }

      public Object removeItem(Object key) {
         return this.fAugmentations.remove(key);
      }

      public Enumeration keys() {
         return Collections.enumeration(this.fAugmentations.keySet());
      }

      public void clear() {
         this.fAugmentations.clear();
      }

      public boolean isFull() {
         return false;
      }

      public AugmentationsImpl.AugmentationsItemsContainer expand() {
         return this;
      }

      public String toString() {
         StringBuilder buff = new StringBuilder();
         buff.append("LargeContainer");
         Iterator var2 = this.fAugmentations.keySet().iterator();

         while(var2.hasNext()) {
            Object key = var2.next();
            buff.append("\nkey == ");
            buff.append(key);
            buff.append("; value == ");
            buff.append(this.fAugmentations.get(key));
         }

         return buff.toString();
      }
   }

   class SmallContainer extends AugmentationsImpl.AugmentationsItemsContainer {
      static final int SIZE_LIMIT = 10;
      final Object[] fAugmentations = new Object[20];
      int fNumEntries = 0;

      SmallContainer() {
         super();
      }

      public Enumeration keys() {
         return new AugmentationsImpl.SmallContainer.SmallContainerKeyEnumeration();
      }

      public Object getItem(Object key) {
         for(int i = 0; i < this.fNumEntries * 2; i += 2) {
            if (this.fAugmentations[i].equals(key)) {
               return this.fAugmentations[i + 1];
            }
         }

         return null;
      }

      public Object putItem(Object key, Object item) {
         for(int i = 0; i < this.fNumEntries * 2; i += 2) {
            if (this.fAugmentations[i].equals(key)) {
               Object oldValue = this.fAugmentations[i + 1];
               this.fAugmentations[i + 1] = item;
               return oldValue;
            }
         }

         this.fAugmentations[this.fNumEntries * 2] = key;
         this.fAugmentations[this.fNumEntries * 2 + 1] = item;
         ++this.fNumEntries;
         return null;
      }

      public Object removeItem(Object key) {
         for(int i = 0; i < this.fNumEntries * 2; i += 2) {
            if (this.fAugmentations[i].equals(key)) {
               Object oldValue = this.fAugmentations[i + 1];

               for(int j = i; j < this.fNumEntries * 2 - 2; j += 2) {
                  this.fAugmentations[j] = this.fAugmentations[j + 2];
                  this.fAugmentations[j + 1] = this.fAugmentations[j + 3];
               }

               this.fAugmentations[this.fNumEntries * 2 - 2] = null;
               this.fAugmentations[this.fNumEntries * 2 - 1] = null;
               --this.fNumEntries;
               return oldValue;
            }
         }

         return null;
      }

      public void clear() {
         for(int i = 0; i < this.fNumEntries * 2; i += 2) {
            this.fAugmentations[i] = null;
            this.fAugmentations[i + 1] = null;
         }

         this.fNumEntries = 0;
      }

      public boolean isFull() {
         return this.fNumEntries == 10;
      }

      public AugmentationsImpl.AugmentationsItemsContainer expand() {
         AugmentationsImpl.LargeContainer expandedContainer = AugmentationsImpl.this.new LargeContainer();

         for(int i = 0; i < this.fNumEntries * 2; i += 2) {
            expandedContainer.putItem(this.fAugmentations[i], this.fAugmentations[i + 1]);
         }

         return expandedContainer;
      }

      public String toString() {
         StringBuilder buff = new StringBuilder();
         buff.append("SmallContainer - fNumEntries == ").append(this.fNumEntries);

         for(int i = 0; i < 20; i += 2) {
            buff.append("\nfAugmentations[").append(i).append("] == ").append(this.fAugmentations[i]).append("; fAugmentations[").append(i + 1).append("] == ").append(this.fAugmentations[i + 1]);
         }

         return buff.toString();
      }

      class SmallContainerKeyEnumeration implements Enumeration {
         Object[] enumArray;
         int next;

         SmallContainerKeyEnumeration() {
            this.enumArray = new Object[SmallContainer.this.fNumEntries];
            this.next = 0;

            for(int i = 0; i < SmallContainer.this.fNumEntries; ++i) {
               this.enumArray[i] = SmallContainer.this.fAugmentations[i * 2];
            }

         }

         public boolean hasMoreElements() {
            return this.next < this.enumArray.length;
         }

         public Object nextElement() {
            if (this.next >= this.enumArray.length) {
               throw new NoSuchElementException();
            } else {
               Object nextVal = this.enumArray[this.next];
               this.enumArray[this.next] = null;
               ++this.next;
               return nextVal;
            }
         }
      }
   }

   abstract class AugmentationsItemsContainer {
      public abstract Object putItem(Object var1, Object var2);

      public abstract Object getItem(Object var1);

      public abstract Object removeItem(Object var1);

      public abstract Enumeration keys();

      public abstract void clear();

      public abstract boolean isFull();

      public abstract AugmentationsImpl.AugmentationsItemsContainer expand();
   }
}
