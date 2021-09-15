package com.sun.org.apache.xml.internal.utils;

public class Trie {
   public static final int ALPHA_SIZE = 128;
   Trie.Node m_Root = new Trie.Node();
   private char[] m_charBuffer = new char[0];

   public Object put(String key, Object value) {
      int len = key.length();
      if (len > this.m_charBuffer.length) {
         this.m_charBuffer = new char[len];
      }

      Trie.Node node = this.m_Root;

      label24:
      for(int i = 0; i < len; ++i) {
         Trie.Node nextNode = node.m_nextChar[Character.toUpperCase(key.charAt(i))];
         if (nextNode == null) {
            while(true) {
               if (i >= len) {
                  break label24;
               }

               Trie.Node newNode = new Trie.Node();
               node.m_nextChar[Character.toUpperCase(key.charAt(i))] = newNode;
               node.m_nextChar[Character.toLowerCase(key.charAt(i))] = newNode;
               node = newNode;
               ++i;
            }
         }

         node = nextNode;
      }

      Object ret = node.m_Value;
      node.m_Value = value;
      return ret;
   }

   public Object get(String key) {
      int len = key.length();
      if (this.m_charBuffer.length < len) {
         return null;
      } else {
         Trie.Node node = this.m_Root;
         switch(len) {
         case 0:
            return null;
         case 1:
            char ch = key.charAt(0);
            if (ch < 128) {
               node = node.m_nextChar[ch];
               if (node != null) {
                  return node.m_Value;
               }
            }

            return null;
         default:
            key.getChars(0, len, this.m_charBuffer, 0);

            for(int i = 0; i < len; ++i) {
               char ch = this.m_charBuffer[i];
               if (128 <= ch) {
                  return null;
               }

               node = node.m_nextChar[ch];
               if (node == null) {
                  return null;
               }
            }

            return node.m_Value;
         }
      }
   }

   class Node {
      Trie.Node[] m_nextChar = new Trie.Node[128];
      Object m_Value = null;
   }
}
