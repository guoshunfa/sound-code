package com.sun.java_cup.internal.runtime;

import java.util.Stack;

public abstract class lr_parser {
   protected static final int _error_sync_size = 3;
   protected boolean _done_parsing;
   protected int tos;
   protected Symbol cur_token;
   protected Stack stack;
   protected short[][] production_tab;
   protected short[][] action_tab;
   protected short[][] reduce_tab;
   private Scanner _scanner;
   protected Symbol[] lookahead;
   protected int lookahead_pos;

   public lr_parser() {
      this._done_parsing = false;
      this.stack = new Stack();
   }

   public lr_parser(Scanner s) {
      this();
      this.setScanner(s);
   }

   protected int error_sync_size() {
      return 3;
   }

   public abstract short[][] production_table();

   public abstract short[][] action_table();

   public abstract short[][] reduce_table();

   public abstract int start_state();

   public abstract int start_production();

   public abstract int EOF_sym();

   public abstract int error_sym();

   public void done_parsing() {
      this._done_parsing = true;
   }

   public void setScanner(Scanner s) {
      this._scanner = s;
   }

   public Scanner getScanner() {
      return this._scanner;
   }

   public abstract Symbol do_action(int var1, lr_parser var2, Stack var3, int var4) throws Exception;

   public void user_init() throws Exception {
   }

   protected abstract void init_actions() throws Exception;

   public Symbol scan() throws Exception {
      return this.getScanner().next_token();
   }

   public void report_fatal_error(String message, Object info) throws Exception {
      this.done_parsing();
      this.report_error(message, info);
      throw new Exception("Can't recover from previous error(s)");
   }

   public void report_error(String message, Object info) {
      System.err.print(message);
      if (info instanceof Symbol) {
         if (((Symbol)info).left != -1) {
            System.err.println(" at character " + ((Symbol)info).left + " of input");
         } else {
            System.err.println("");
         }
      } else {
         System.err.println("");
      }

   }

   public void syntax_error(Symbol cur_token) {
      this.report_error("Syntax error", cur_token);
   }

   public void unrecovered_syntax_error(Symbol cur_token) throws Exception {
      this.report_fatal_error("Couldn't repair and continue parse", cur_token);
   }

   protected final short get_action(int state, int sym) {
      short[] row = this.action_tab[state];
      int probe;
      if (row.length >= 20) {
         int first = 0;
         int last = (row.length - 1) / 2 - 1;

         while(first <= last) {
            probe = (first + last) / 2;
            if (sym == row[probe * 2]) {
               return row[probe * 2 + 1];
            }

            if (sym > row[probe * 2]) {
               first = probe + 1;
            } else {
               last = probe - 1;
            }
         }

         return row[row.length - 1];
      } else {
         for(probe = 0; probe < row.length; ++probe) {
            short tag = row[probe++];
            if (tag == sym || tag == -1) {
               return row[probe];
            }
         }

         return 0;
      }
   }

   protected final short get_reduce(int state, int sym) {
      short[] row = this.reduce_tab[state];
      if (row == null) {
         return -1;
      } else {
         for(int probe = 0; probe < row.length; ++probe) {
            short tag = row[probe++];
            if (tag == sym || tag == -1) {
               return row[probe];
            }
         }

         return -1;
      }
   }

   public Symbol parse() throws Exception {
      Symbol lhs_sym = null;
      this.production_tab = this.production_table();
      this.action_tab = this.action_table();
      this.reduce_tab = this.reduce_table();
      this.init_actions();
      this.user_init();
      this.cur_token = this.scan();
      this.stack.removeAllElements();
      this.stack.push(new Symbol(0, this.start_state()));
      this.tos = 0;
      this._done_parsing = false;

      while(true) {
         while(!this._done_parsing) {
            if (this.cur_token.used_by_parser) {
               throw new Error("Symbol recycling detected (fix your scanner).");
            }

            int act = this.get_action(((Symbol)this.stack.peek()).parse_state, this.cur_token.sym);
            if (act > 0) {
               this.cur_token.parse_state = act - 1;
               this.cur_token.used_by_parser = true;
               this.stack.push(this.cur_token);
               ++this.tos;
               this.cur_token = this.scan();
            } else if (act >= 0) {
               if (act == 0) {
                  this.syntax_error(this.cur_token);
                  if (!this.error_recovery(false)) {
                     this.unrecovered_syntax_error(this.cur_token);
                     this.done_parsing();
                  } else {
                     lhs_sym = (Symbol)this.stack.peek();
                  }
               }
            } else {
               lhs_sym = this.do_action(-act - 1, this, this.stack, this.tos);
               short lhs_sym_num = this.production_tab[-act - 1][0];
               short handle_size = this.production_tab[-act - 1][1];

               for(int i = 0; i < handle_size; ++i) {
                  this.stack.pop();
                  --this.tos;
               }

               act = this.get_reduce(((Symbol)this.stack.peek()).parse_state, lhs_sym_num);
               lhs_sym.parse_state = act;
               lhs_sym.used_by_parser = true;
               this.stack.push(lhs_sym);
               ++this.tos;
            }
         }

         return lhs_sym;
      }
   }

   public void debug_message(String mess) {
      System.err.println(mess);
   }

   public void dump_stack() {
      if (this.stack == null) {
         this.debug_message("# Stack dump requested, but stack is null");
      } else {
         this.debug_message("============ Parse Stack Dump ============");

         for(int i = 0; i < this.stack.size(); ++i) {
            this.debug_message("Symbol: " + ((Symbol)this.stack.elementAt(i)).sym + " State: " + ((Symbol)this.stack.elementAt(i)).parse_state);
         }

         this.debug_message("==========================================");
      }
   }

   public void debug_reduce(int prod_num, int nt_num, int rhs_size) {
      this.debug_message("# Reduce with prod #" + prod_num + " [NT=" + nt_num + ", SZ=" + rhs_size + "]");
   }

   public void debug_shift(Symbol shift_tkn) {
      this.debug_message("# Shift under term #" + shift_tkn.sym + " to state #" + shift_tkn.parse_state);
   }

   public void debug_stack() {
      StringBuffer sb = new StringBuffer("## STACK:");

      for(int i = 0; i < this.stack.size(); ++i) {
         Symbol s = (Symbol)this.stack.elementAt(i);
         sb.append(" <state " + s.parse_state + ", sym " + s.sym + ">");
         if (i % 3 == 2 || i == this.stack.size() - 1) {
            this.debug_message(sb.toString());
            sb = new StringBuffer("         ");
         }
      }

   }

   public Symbol debug_parse() throws Exception {
      Symbol lhs_sym = null;
      this.production_tab = this.production_table();
      this.action_tab = this.action_table();
      this.reduce_tab = this.reduce_table();
      this.debug_message("# Initializing parser");
      this.init_actions();
      this.user_init();
      this.cur_token = this.scan();
      this.debug_message("# Current Symbol is #" + this.cur_token.sym);
      this.stack.removeAllElements();
      this.stack.push(new Symbol(0, this.start_state()));
      this.tos = 0;
      this._done_parsing = false;

      while(true) {
         while(!this._done_parsing) {
            if (this.cur_token.used_by_parser) {
               throw new Error("Symbol recycling detected (fix your scanner).");
            }

            int act = this.get_action(((Symbol)this.stack.peek()).parse_state, this.cur_token.sym);
            if (act > 0) {
               this.cur_token.parse_state = act - 1;
               this.cur_token.used_by_parser = true;
               this.debug_shift(this.cur_token);
               this.stack.push(this.cur_token);
               ++this.tos;
               this.cur_token = this.scan();
               this.debug_message("# Current token is " + this.cur_token);
            } else if (act >= 0) {
               if (act == 0) {
                  this.syntax_error(this.cur_token);
                  if (!this.error_recovery(true)) {
                     this.unrecovered_syntax_error(this.cur_token);
                     this.done_parsing();
                  } else {
                     lhs_sym = (Symbol)this.stack.peek();
                  }
               }
            } else {
               lhs_sym = this.do_action(-act - 1, this, this.stack, this.tos);
               short lhs_sym_num = this.production_tab[-act - 1][0];
               short handle_size = this.production_tab[-act - 1][1];
               this.debug_reduce(-act - 1, lhs_sym_num, handle_size);

               for(int i = 0; i < handle_size; ++i) {
                  this.stack.pop();
                  --this.tos;
               }

               act = this.get_reduce(((Symbol)this.stack.peek()).parse_state, lhs_sym_num);
               this.debug_message("# Reduce rule: top state " + ((Symbol)this.stack.peek()).parse_state + ", lhs sym " + lhs_sym_num + " -> state " + act);
               lhs_sym.parse_state = act;
               lhs_sym.used_by_parser = true;
               this.stack.push(lhs_sym);
               ++this.tos;
               this.debug_message("# Goto state #" + act);
            }
         }

         return lhs_sym;
      }
   }

   protected boolean error_recovery(boolean debug) throws Exception {
      if (debug) {
         this.debug_message("# Attempting error recovery");
      }

      if (!this.find_recovery_config(debug)) {
         if (debug) {
            this.debug_message("# Error recovery fails");
         }

         return false;
      } else {
         this.read_lookahead();

         while(true) {
            if (debug) {
               this.debug_message("# Trying to parse ahead");
            }

            if (this.try_parse_ahead(debug)) {
               if (debug) {
                  this.debug_message("# Parse-ahead ok, going back to normal parse");
               }

               this.parse_lookahead(debug);
               return true;
            }

            if (this.lookahead[0].sym == this.EOF_sym()) {
               if (debug) {
                  this.debug_message("# Error recovery fails at EOF");
               }

               return false;
            }

            if (debug) {
               this.debug_message("# Consuming Symbol #" + this.cur_err_token().sym);
            }

            this.restart_lookahead();
         }
      }
   }

   protected boolean shift_under_error() {
      return this.get_action(((Symbol)this.stack.peek()).parse_state, this.error_sym()) > 0;
   }

   protected boolean find_recovery_config(boolean debug) {
      if (debug) {
         this.debug_message("# Finding recovery state on stack");
      }

      int right_pos = ((Symbol)this.stack.peek()).right;
      int left_pos = ((Symbol)this.stack.peek()).left;

      do {
         if (this.shift_under_error()) {
            int act = this.get_action(((Symbol)this.stack.peek()).parse_state, this.error_sym());
            if (debug) {
               this.debug_message("# Recover state found (#" + ((Symbol)this.stack.peek()).parse_state + ")");
               this.debug_message("# Shifting on error to state #" + (act - 1));
            }

            Symbol error_token = new Symbol(this.error_sym(), left_pos, right_pos);
            error_token.parse_state = act - 1;
            error_token.used_by_parser = true;
            this.stack.push(error_token);
            ++this.tos;
            return true;
         }

         if (debug) {
            this.debug_message("# Pop stack by one, state was # " + ((Symbol)this.stack.peek()).parse_state);
         }

         left_pos = ((Symbol)this.stack.pop()).left;
         --this.tos;
      } while(!this.stack.empty());

      if (debug) {
         this.debug_message("# No recovery state found on stack");
      }

      return false;
   }

   protected void read_lookahead() throws Exception {
      this.lookahead = new Symbol[this.error_sync_size()];

      for(int i = 0; i < this.error_sync_size(); ++i) {
         this.lookahead[i] = this.cur_token;
         this.cur_token = this.scan();
      }

      this.lookahead_pos = 0;
   }

   protected Symbol cur_err_token() {
      return this.lookahead[this.lookahead_pos];
   }

   protected boolean advance_lookahead() {
      ++this.lookahead_pos;
      return this.lookahead_pos < this.error_sync_size();
   }

   protected void restart_lookahead() throws Exception {
      for(int i = 1; i < this.error_sync_size(); ++i) {
         this.lookahead[i - 1] = this.lookahead[i];
      }

      this.cur_token = this.scan();
      this.lookahead[this.error_sync_size() - 1] = this.cur_token;
      this.lookahead_pos = 0;
   }

   protected boolean try_parse_ahead(boolean debug) throws Exception {
      virtual_parse_stack vstack = new virtual_parse_stack(this.stack);

      do {
         while(true) {
            int act = this.get_action(vstack.top(), this.cur_err_token().sym);
            if (act == 0) {
               return false;
            }

            if (act > 0) {
               vstack.push(act - 1);
               if (debug) {
                  this.debug_message("# Parse-ahead shifts Symbol #" + this.cur_err_token().sym + " into state #" + (act - 1));
               }
               break;
            }

            if (-act - 1 == this.start_production()) {
               if (debug) {
                  this.debug_message("# Parse-ahead accepts");
               }

               return true;
            }

            short lhs = this.production_tab[-act - 1][0];
            short rhs_size = this.production_tab[-act - 1][1];

            for(int i = 0; i < rhs_size; ++i) {
               vstack.pop();
            }

            if (debug) {
               this.debug_message("# Parse-ahead reduces: handle size = " + rhs_size + " lhs = #" + lhs + " from state #" + vstack.top());
            }

            vstack.push(this.get_reduce(vstack.top(), lhs));
            if (debug) {
               this.debug_message("# Goto state #" + vstack.top());
            }
         }
      } while(this.advance_lookahead());

      return true;
   }

   protected void parse_lookahead(boolean debug) throws Exception {
      Symbol lhs_sym = null;
      this.lookahead_pos = 0;
      if (debug) {
         this.debug_message("# Reparsing saved input with actions");
         this.debug_message("# Current Symbol is #" + this.cur_err_token().sym);
         this.debug_message("# Current state is #" + ((Symbol)this.stack.peek()).parse_state);
      }

      while(true) {
         while(!this._done_parsing) {
            int act = this.get_action(((Symbol)this.stack.peek()).parse_state, this.cur_err_token().sym);
            if (act > 0) {
               this.cur_err_token().parse_state = act - 1;
               this.cur_err_token().used_by_parser = true;
               if (debug) {
                  this.debug_shift(this.cur_err_token());
               }

               this.stack.push(this.cur_err_token());
               ++this.tos;
               if (!this.advance_lookahead()) {
                  if (debug) {
                     this.debug_message("# Completed reparse");
                  }

                  return;
               }

               if (debug) {
                  this.debug_message("# Current Symbol is #" + this.cur_err_token().sym);
               }
            } else if (act >= 0) {
               if (act == 0) {
                  this.report_fatal_error("Syntax error", lhs_sym);
                  return;
               }
            } else {
               lhs_sym = this.do_action(-act - 1, this, this.stack, this.tos);
               short lhs_sym_num = this.production_tab[-act - 1][0];
               short handle_size = this.production_tab[-act - 1][1];
               if (debug) {
                  this.debug_reduce(-act - 1, lhs_sym_num, handle_size);
               }

               for(int i = 0; i < handle_size; ++i) {
                  this.stack.pop();
                  --this.tos;
               }

               act = this.get_reduce(((Symbol)this.stack.peek()).parse_state, lhs_sym_num);
               lhs_sym.parse_state = act;
               lhs_sym.used_by_parser = true;
               this.stack.push(lhs_sym);
               ++this.tos;
               if (debug) {
                  this.debug_message("# Goto state #" + act);
               }
            }
         }

         return;
      }
   }

   protected static short[][] unpackFromStrings(String[] sa) {
      StringBuffer sb = new StringBuffer(sa[0]);

      int n;
      for(n = 1; n < sa.length; ++n) {
         sb.append(sa[n]);
      }

      int n = 0;
      int size1 = sb.charAt(n) << 16 | sb.charAt(n + 1);
      n = n + 2;
      short[][] result = new short[size1][];

      for(int i = 0; i < size1; ++i) {
         int size2 = sb.charAt(n) << 16 | sb.charAt(n + 1);
         n += 2;
         result[i] = new short[size2];

         for(int j = 0; j < size2; ++j) {
            result[i][j] = (short)(sb.charAt(n++) - 2);
         }
      }

      return result;
   }
}
