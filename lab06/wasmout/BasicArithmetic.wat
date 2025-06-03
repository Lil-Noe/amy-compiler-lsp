(module 
  (import "wasi_snapshot_preview1" "fd_write" (func $fd_write (param i32 i32 i32 i32) (result i32)))
  (import "wasi_snapshot_preview1" "fd_read" (func $fd_read (param i32 i32 i32 i32) (result i32)))
  (memory $0 100) (export "memory" (memory $0))
  (global (mut i32) i32.const 0) (global (mut i32) i32.const 0) 

  (func $String_len (param i32) (result i32) (local i32)
    loop $label_0
      local.get 0
      i32.load8_u
      if
        local.get 0
        i32.const 1
        i32.add
        local.set 0
        local.get 1
        i32.const 1
        i32.add
        local.set 1
        br $label_0
      else
      end
    end
    local.get 1
  )

  (func $String_concat (param i32 i32) (result i32) (local i32 i32)
    global.get 0
    local.set 3
    local.get 0
    local.set 2
    loop $label_2
      local.get 2
      i32.load8_u
      if
        local.get 3
        local.get 2
        i32.load8_u
        i32.store8
        local.get 3
        i32.const 1
        i32.add
        local.set 3
        local.get 2
        i32.const 1
        i32.add
        local.set 2
        br $label_2
      else
      end
    end
    local.get 1
    local.set 2
    loop $label_3
      local.get 2
      i32.load8_u
      if
        local.get 3
        local.get 2
        i32.load8_u
        i32.store8
        local.get 3
        i32.const 1
        i32.add
        local.set 3
        local.get 2
        i32.const 1
        i32.add
        local.set 2
        br $label_3
      else
      end
    end
    loop $label_1
      local.get 3
      i32.const 0
      i32.store8
      local.get 3
      i32.const 4
      i32.rem_s
      i32.const 3
      i32.eq
      if
      else
        local.get 3
        i32.const 1
        i32.add
        local.set 3
        br $label_1
      end
    end
    global.get 0
    local.get 3
    i32.const 1
    i32.add
    global.set 0
  )

  (func $Std_digitToString (param i32) (result i32) 
    global.get 0
    local.get 0
    i32.const 48
    i32.add
    i32.store
    global.get 0
    global.get 0
    i32.const 4
    i32.add
    global.set 0
  )

  (func $Std_readString (result i32) (local i32 i32 i32)
    global.get 1
    if (result i32)
      global.get 1
      i32.load8_u
      i32.const 0
      i32.eq
    else
      i32.const 1
    end
    if
      global.get 0
      i32.const 4096
      i32.add
      global.get 0
      i32.store
      global.get 0
      i32.const 4100
      i32.add
      i32.const 4095
      i32.store
      i32.const 0
      global.get 0
      i32.const 4096
      i32.add
      i32.const 1
      global.get 0
      i32.const 4104
      i32.add
      call $fd_read
      if
        unreachable
      else
      end
      global.get 0
      i32.const 4104
      i32.add
      i32.load
      local.set 0
      global.get 0
      local.get 0
      global.get 0
      i32.add
      global.set 0
      loop $label_7
        global.get 0
        i32.const 0
        i32.store8
        global.get 0
        i32.const 1
        i32.add
        global.set 0
        global.get 0
        i32.const 4
        i32.rem_s
        if
          br $label_7
        else
        end
      end
      global.set 1
    else
    end
    global.get 1
    global.get 1
    local.set 1
    i32.const 0
    local.set 0
    loop $label_4
      local.get 1
      i32.load8_u
      if
        local.get 1
        i32.load8_u
        i32.const 10
        i32.eq
        if
          local.get 1
          i32.const 1
          i32.add
          global.set 1
        else
          local.get 0
          i32.const 1
          i32.add
          local.set 0
          local.get 1
          i32.const 1
          i32.add
          local.set 1
          br $label_4
        end
      else
        local.get 1
        global.set 1
      end
    end
    local.set 1
    global.get 0
    local.set 2
    loop $label_5
      local.get 0
      if
        local.get 2
        local.get 1
        i32.load8_u
        i32.store8
        local.get 1
        i32.const 1
        i32.add
        local.set 1
        local.get 2
        i32.const 1
        i32.add
        local.set 2
        local.get 0
        i32.const 1
        i32.sub
        local.set 0
        br $label_5
      else
      end
    end
    loop $label_6
      local.get 2
      i32.const 0
      i32.store8
      local.get 2
      i32.const 4
      i32.rem_s
      i32.const 3
      i32.eq
      if
      else
        local.get 2
        i32.const 1
        i32.add
        local.set 2
        br $label_6
      end
    end
    global.get 0
    local.get 2
    i32.const 1
    i32.add
    global.set 0
  )

  (func $Std_readInt (result i32) (local i32 i32 i32 i32)
    call $Std_readString
    local.set 1
    local.get 1
    i32.load8_u
    i32.const 45
    i32.eq
    if
      local.get 1
      i32.const 1
      i32.add
      local.set 1
      i32.const 1
      local.set 0
    else
    end
    loop $label_8
      local.get 1
      i32.load8_u
      i32.const 0
      i32.eq
      if
      else
        local.get 3
        i32.const 10
        i32.mul
        local.get 1
        i32.load8_u
        i32.const 48
        i32.sub
        local.set 2
        local.get 2
        i32.const 10
        i32.lt_u
        if (result i32)
          local.get 2
        else
          unreachable
        end
        i32.add
        local.set 3
        local.get 1
        i32.const 1
        i32.add
        local.set 1
        br $label_8
      end
    end
    local.get 0
    if (result i32)
      i32.const 0
      local.get 3
      i32.sub
    else
      local.get 3
    end
  )

  (func $Std_printString (param i32) (result i32) 
    global.get 0
    local.get 0
    i32.store
    global.get 0
    i32.const 4
    i32.add
    local.get 0
    call $String_len
    i32.store
    i32.const 1
    global.get 0
    i32.const 1
    global.get 0
    i32.const 8
    i32.add
    call $fd_write
    if
      unreachable
    else
    end
    global.get 0
    i32.const 8
    i32.add
    i32.load
    global.get 0
    i32.const 12
    i32.add
    i32.const 10
    i32.store
    global.get 0
    global.get 0
    i32.const 12
    i32.add
    i32.store
    global.get 0
    i32.const 4
    i32.add
    i32.const 1
    i32.store
    i32.const 1
    global.get 0
    i32.const 1
    global.get 0
    i32.const 8
    i32.add
    call $fd_write
    if
      unreachable
    else
    end
  )

  (func $Std_printInt (param i32) (result i32) 
    ;;> def printInt(i: Int(32)): Unit = {
    ;;|   printString(intToString(i))
    ;;| }
    ;;> printString(intToString(i))
    ;;> intToString(i)
    ;;> i
    local.get 0
    call $Std_intToString
    call $Std_printString
  )

  (func $Std_printBoolean (param i32) (result i32) 
    ;;> def printBoolean(b: Boolean): Unit = {
    ;;|   printString(booleanToString(b))
    ;;| }
    ;;> printString(booleanToString(b))
    ;;> booleanToString(b)
    ;;> b
    local.get 0
    call $Std_booleanToString
    call $Std_printString
  )

  (func $Std_intToString (param i32) (result i32) (local i32 i32)
    ;;> def intToString(i: Int(32)): String = {
    ;;|   (if((i < 0)) {
    ;;|     ("-" ++ intToString(-(i)))
    ;;|   } else {
    ;;|     (
    ;;|       val rem: Int(32) =
    ;;|         (i % 10);
    ;;|       val div: Int(32) =
    ;;|         (i / 10);
    ;;|       (if((div == 0)) {
    ;;|         digitToString(rem)
    ;;|       } else {
    ;;|         (intToString(div) ++ digitToString(rem))
    ;;|       })
    ;;|     )
    ;;|   })
    ;;| }
    ;;> (if((i < 0)) {
    ;;|   ("-" ++ intToString(-(i)))
    ;;| } else {
    ;;|   (
    ;;|     val rem: Int(32) =
    ;;|       (i % 10);
    ;;|     val div: Int(32) =
    ;;|       (i / 10);
    ;;|     (if((div == 0)) {
    ;;|       digitToString(rem)
    ;;|     } else {
    ;;|       (intToString(div) ++ digitToString(rem))
    ;;|     })
    ;;|   )
    ;;| })
    ;;> (i < 0)
    ;;> i
    local.get 0
    ;;> 0
    i32.const 0
    i32.lt_s
    if (result i32)
      ;;> ("-" ++ intToString(-(i)))
      ;;> mkString: -
      global.get 0
      i32.const 0
      i32.add
      i32.const 45
      i32.store8
      global.get 0
      i32.const 1
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 2
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 3
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      global.get 0
      i32.const 4
      i32.add
      global.set 0
      ;;> intToString(-(i))
      ;;> i
      i32.const 0
      ;;> i
      local.get 0
      i32.sub
      call $Std_intToString
      call $String_concat
    else
      ;;> (
      ;;|   val rem: Int(32) =
      ;;|     (i % 10);
      ;;|   val div: Int(32) =
      ;;|     (i / 10);
      ;;|   (if((div == 0)) {
      ;;|     digitToString(rem)
      ;;|   } else {
      ;;|     (intToString(div) ++ digitToString(rem))
      ;;|   })
      ;;| )
      ;;> (i % 10)
      ;;> i
      local.get 0
      ;;> 10
      i32.const 10
      i32.rem_s
      local.set 1
      ;;> (
      ;;|   val div: Int(32) =
      ;;|     (i / 10);
      ;;|   (if((div == 0)) {
      ;;|     digitToString(rem)
      ;;|   } else {
      ;;|     (intToString(div) ++ digitToString(rem))
      ;;|   })
      ;;| )
      ;;> (i / 10)
      ;;> i
      local.get 0
      ;;> 10
      i32.const 10
      i32.div_s
      local.set 2
      ;;> (if((div == 0)) {
      ;;|   digitToString(rem)
      ;;| } else {
      ;;|   (intToString(div) ++ digitToString(rem))
      ;;| })
      ;;> (div == 0)
      ;;> div
      local.get 2
      ;;> 0
      i32.const 0
      i32.eq
      if (result i32)
        ;;> digitToString(rem)
        ;;> rem
        local.get 1
        call $Std_digitToString
      else
        ;;> (intToString(div) ++ digitToString(rem))
        ;;> intToString(div)
        ;;> div
        local.get 2
        call $Std_intToString
        ;;> digitToString(rem)
        ;;> rem
        local.get 1
        call $Std_digitToString
        call $String_concat
      end
    end
  )

  (func $Std_booleanToString (param i32) (result i32) 
    ;;> def booleanToString(b: Boolean): String = {
    ;;|   (if(b) {
    ;;|     "true"
    ;;|   } else {
    ;;|     "false"
    ;;|   })
    ;;| }
    ;;> (if(b) {
    ;;|   "true"
    ;;| } else {
    ;;|   "false"
    ;;| })
    ;;> b
    local.get 0
    if (result i32)
      ;;> mkString: true
      global.get 0
      i32.const 0
      i32.add
      i32.const 116
      i32.store8
      global.get 0
      i32.const 1
      i32.add
      i32.const 114
      i32.store8
      global.get 0
      i32.const 2
      i32.add
      i32.const 117
      i32.store8
      global.get 0
      i32.const 3
      i32.add
      i32.const 101
      i32.store8
      global.get 0
      i32.const 4
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 5
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 6
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 7
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      global.get 0
      i32.const 8
      i32.add
      global.set 0
    else
      ;;> mkString: false
      global.get 0
      i32.const 0
      i32.add
      i32.const 102
      i32.store8
      global.get 0
      i32.const 1
      i32.add
      i32.const 97
      i32.store8
      global.get 0
      i32.const 2
      i32.add
      i32.const 108
      i32.store8
      global.get 0
      i32.const 3
      i32.add
      i32.const 115
      i32.store8
      global.get 0
      i32.const 4
      i32.add
      i32.const 101
      i32.store8
      global.get 0
      i32.const 5
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 6
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 7
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      global.get 0
      i32.const 8
      i32.add
      global.set 0
    end
  )

  (func $BasicArithmetic_plus (param i32 i32) (result i32) 
    ;;> def plus(a: Int(32), b: Int(32)): Int(32) = {
    ;;|   (a + b)
    ;;| }
    ;;> (a + b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    i32.add
  )

  (func $BasicArithmetic_minus (param i32 i32) (result i32) 
    ;;> def minus(a: Int(32), b: Int(32)): Int(32) = {
    ;;|   (a - b)
    ;;| }
    ;;> (a - b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    i32.sub
  )

  (func $BasicArithmetic_mul (param i32 i32) (result i32) 
    ;;> def mul(a: Int(32), b: Int(32)): Int(32) = {
    ;;|   (a * b)
    ;;| }
    ;;> (a * b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    i32.mul
  )

  (func $BasicArithmetic_mod (param i32 i32) (result i32) 
    ;;> def mod(a: Int(32), b: Int(32)): Int(32) = {
    ;;|   (a % b)
    ;;| }
    ;;> (a % b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    i32.rem_s
  )

  (func $BasicArithmetic_div (param i32 i32) (result i32) 
    ;;> def div(a: Int(32), b: Int(32)): Int(32) = {
    ;;|   (a / b)
    ;;| }
    ;;> (a / b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    i32.div_s
  )
  (export "_start" (func $BasicArithmetic_main))
  (func $BasicArithmetic_main (local i32 i32)
    ;;> (
    ;;|   val a: Int(32) =
    ;;|     1;
    ;;|   val b: Int(32) =
    ;;|     2;
    ;;|   printString(intToString(plus(a, b)));
    ;;|   printString(intToString(mul(plus(a, b), b)));
    ;;|   printString(intToString(minus(mul(plus(a, b), b), b)));
    ;;|   printString(intToString(mul(minus(mul(plus(a, b), b), b), b)));
    ;;|   printString(intToString(div(4, 2)));
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> 1
    i32.const 1
    local.set 0
    ;;> (
    ;;|   val b: Int(32) =
    ;;|     2;
    ;;|   printString(intToString(plus(a, b)));
    ;;|   printString(intToString(mul(plus(a, b), b)));
    ;;|   printString(intToString(minus(mul(plus(a, b), b), b)));
    ;;|   printString(intToString(mul(minus(mul(plus(a, b), b), b), b)));
    ;;|   printString(intToString(div(4, 2)));
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> 2
    i32.const 2
    local.set 1
    ;;> (
    ;;|   printString(intToString(plus(a, b)));
    ;;|   printString(intToString(mul(plus(a, b), b)));
    ;;|   printString(intToString(minus(mul(plus(a, b), b), b)));
    ;;|   printString(intToString(mul(minus(mul(plus(a, b), b), b), b)));
    ;;|   printString(intToString(div(4, 2)));
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(plus(a, b)))
    ;;> intToString(plus(a, b))
    ;;> plus(a, b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    call $BasicArithmetic_plus
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(mul(plus(a, b), b)));
    ;;|   printString(intToString(minus(mul(plus(a, b), b), b)));
    ;;|   printString(intToString(mul(minus(mul(plus(a, b), b), b), b)));
    ;;|   printString(intToString(div(4, 2)));
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(mul(plus(a, b), b)))
    ;;> intToString(mul(plus(a, b), b))
    ;;> mul(plus(a, b), b)
    ;;> plus(a, b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    call $BasicArithmetic_plus
    ;;> b
    local.get 1
    call $BasicArithmetic_mul
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(minus(mul(plus(a, b), b), b)));
    ;;|   printString(intToString(mul(minus(mul(plus(a, b), b), b), b)));
    ;;|   printString(intToString(div(4, 2)));
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(minus(mul(plus(a, b), b), b)))
    ;;> intToString(minus(mul(plus(a, b), b), b))
    ;;> minus(mul(plus(a, b), b), b)
    ;;> mul(plus(a, b), b)
    ;;> plus(a, b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    call $BasicArithmetic_plus
    ;;> b
    local.get 1
    call $BasicArithmetic_mul
    ;;> b
    local.get 1
    call $BasicArithmetic_minus
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(mul(minus(mul(plus(a, b), b), b), b)));
    ;;|   printString(intToString(div(4, 2)));
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(mul(minus(mul(plus(a, b), b), b), b)))
    ;;> intToString(mul(minus(mul(plus(a, b), b), b), b))
    ;;> mul(minus(mul(plus(a, b), b), b), b)
    ;;> minus(mul(plus(a, b), b), b)
    ;;> mul(plus(a, b), b)
    ;;> plus(a, b)
    ;;> a
    local.get 0
    ;;> b
    local.get 1
    call $BasicArithmetic_plus
    ;;> b
    local.get 1
    call $BasicArithmetic_mul
    ;;> b
    local.get 1
    call $BasicArithmetic_minus
    ;;> b
    local.get 1
    call $BasicArithmetic_mul
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(div(4, 2)));
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(div(4, 2)))
    ;;> intToString(div(4, 2))
    ;;> div(4, 2)
    ;;> 4
    i32.const 4
    ;;> 2
    i32.const 2
    call $BasicArithmetic_div
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(div(3, 2)));
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(div(3, 2)))
    ;;> intToString(div(3, 2))
    ;;> div(3, 2)
    ;;> 3
    i32.const 3
    ;;> 2
    i32.const 2
    call $BasicArithmetic_div
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(div(1, 2)));
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(div(1, 2)))
    ;;> intToString(div(1, 2))
    ;;> div(1, 2)
    ;;> 1
    i32.const 1
    ;;> 2
    i32.const 2
    call $BasicArithmetic_div
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(div(-(1), 2)));
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(div(-(1), 2)))
    ;;> intToString(div(-(1), 2))
    ;;> div(-(1), 2)
    ;;> 1
    i32.const 0
    ;;> 1
    i32.const 1
    i32.sub
    ;;> 2
    i32.const 2
    call $BasicArithmetic_div
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(div(-(2), 2)));
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(div(-(2), 2)))
    ;;> intToString(div(-(2), 2))
    ;;> div(-(2), 2)
    ;;> 2
    i32.const 0
    ;;> 2
    i32.const 2
    i32.sub
    ;;> 2
    i32.const 2
    call $BasicArithmetic_div
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(div(-(3), 2)));
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(div(-(3), 2)))
    ;;> intToString(div(-(3), 2))
    ;;> div(-(3), 2)
    ;;> 3
    i32.const 0
    ;;> 3
    i32.const 3
    i32.sub
    ;;> 2
    i32.const 2
    call $BasicArithmetic_div
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(mod(5, 2)));
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(mod(5, 2)))
    ;;> intToString(mod(5, 2))
    ;;> mod(5, 2)
    ;;> 5
    i32.const 5
    ;;> 2
    i32.const 2
    call $BasicArithmetic_mod
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(mod(-(5), 2)));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(mod(-(5), 2)))
    ;;> intToString(mod(-(5), 2))
    ;;> mod(-(5), 2)
    ;;> 5
    i32.const 0
    ;;> 5
    i32.const 5
    i32.sub
    ;;> 2
    i32.const 2
    call $BasicArithmetic_mod
    call $Std_intToString
    call $Std_printString
    drop
    ;;> printString("test finished")
    ;;> mkString: test finished
    global.get 0
    i32.const 0
    i32.add
    i32.const 116
    i32.store8
    global.get 0
    i32.const 1
    i32.add
    i32.const 101
    i32.store8
    global.get 0
    i32.const 2
    i32.add
    i32.const 115
    i32.store8
    global.get 0
    i32.const 3
    i32.add
    i32.const 116
    i32.store8
    global.get 0
    i32.const 4
    i32.add
    i32.const 32
    i32.store8
    global.get 0
    i32.const 5
    i32.add
    i32.const 102
    i32.store8
    global.get 0
    i32.const 6
    i32.add
    i32.const 105
    i32.store8
    global.get 0
    i32.const 7
    i32.add
    i32.const 110
    i32.store8
    global.get 0
    i32.const 8
    i32.add
    i32.const 105
    i32.store8
    global.get 0
    i32.const 9
    i32.add
    i32.const 115
    i32.store8
    global.get 0
    i32.const 10
    i32.add
    i32.const 104
    i32.store8
    global.get 0
    i32.const 11
    i32.add
    i32.const 101
    i32.store8
    global.get 0
    i32.const 12
    i32.add
    i32.const 100
    i32.store8
    global.get 0
    i32.const 13
    i32.add
    i32.const 0
    i32.store8
    global.get 0
    i32.const 14
    i32.add
    i32.const 0
    i32.store8
    global.get 0
    i32.const 15
    i32.add
    i32.const 0
    i32.store8
    global.get 0
    global.get 0
    i32.const 16
    i32.add
    global.set 0
    call $Std_printString
    drop
  )
)