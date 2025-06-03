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

  (func $BasicPatternMatching_isRegularLetter (param i32) (result i32) (local i32 i32 i32 i32 i32)
    ;;> def isRegularLetter(l: Letter): Boolean = {
    ;;|   l match {
    ;;|     case A() =>
    ;;|       true
    ;;|     case B() =>
    ;;|       true
    ;;|     case APrim(_) =>
    ;;|       false
    ;;|     case BPrim(_) =>
    ;;|       false
    ;;|   }
    ;;| }
    ;;> l
    local.get 0
    local.set 1
    local.get 1
    local.set 2
    local.get 2
    i32.load
    i32.const 0
    i32.eq
    if (result i32)
      i32.const 1
    else
      i32.const 0
    end
    if (result i32)
      ;;> true
      i32.const 1
    else
      local.get 1
      local.set 3
      local.get 3
      i32.load
      i32.const 2
      i32.eq
      if (result i32)
        i32.const 1
      else
        i32.const 0
      end
      if (result i32)
        ;;> true
        i32.const 1
      else
        local.get 1
        local.set 4
        local.get 4
        i32.load
        i32.const 1
        i32.eq
        if (result i32)
          local.get 4
          ;;> adtField index: 0
          i32.const 4
          i32.add
          i32.load
          ;;> _
          drop
          i32.const 1
        else
          i32.const 0
        end
        if (result i32)
          ;;> false
          i32.const 0
        else
          local.get 1
          local.set 5
          local.get 5
          i32.load
          i32.const 3
          i32.eq
          if (result i32)
            local.get 5
            ;;> adtField index: 0
            i32.const 4
            i32.add
            i32.load
            ;;> _
            drop
            i32.const 1
          else
            i32.const 0
          end
          if (result i32)
            ;;> false
            i32.const 0
          else
            ;;> mkString: Matching error: 
            global.get 0
            i32.const 0
            i32.add
            i32.const 77
            i32.store8
            global.get 0
            i32.const 1
            i32.add
            i32.const 97
            i32.store8
            global.get 0
            i32.const 2
            i32.add
            i32.const 116
            i32.store8
            global.get 0
            i32.const 3
            i32.add
            i32.const 99
            i32.store8
            global.get 0
            i32.const 4
            i32.add
            i32.const 104
            i32.store8
            global.get 0
            i32.const 5
            i32.add
            i32.const 105
            i32.store8
            global.get 0
            i32.const 6
            i32.add
            i32.const 110
            i32.store8
            global.get 0
            i32.const 7
            i32.add
            i32.const 103
            i32.store8
            global.get 0
            i32.const 8
            i32.add
            i32.const 32
            i32.store8
            global.get 0
            i32.const 9
            i32.add
            i32.const 101
            i32.store8
            global.get 0
            i32.const 10
            i32.add
            i32.const 114
            i32.store8
            global.get 0
            i32.const 11
            i32.add
            i32.const 114
            i32.store8
            global.get 0
            i32.const 12
            i32.add
            i32.const 111
            i32.store8
            global.get 0
            i32.const 13
            i32.add
            i32.const 114
            i32.store8
            global.get 0
            i32.const 14
            i32.add
            i32.const 58
            i32.store8
            global.get 0
            i32.const 15
            i32.add
            i32.const 32
            i32.store8
            global.get 0
            i32.const 16
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            i32.const 17
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            i32.const 18
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            i32.const 19
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            global.get 0
            i32.const 20
            i32.add
            global.set 0
            call $Std_printString
            unreachable
          end
        end
      end
    end
  )

  (func $BasicPatternMatching_getPrimNumber (param i32) (result i32) (local i32 i32 i32 i32 i32 i32)
    ;;> def getPrimNumber(l: Letter): Int(32) = {
    ;;|   !(isRegularLetter(l)) match {
    ;;|     case false =>
    ;;|       42
    ;;|     case true =>
    ;;|       l match {
    ;;|         case APrim(i) =>
    ;;|           i
    ;;|         case BPrim(i) =>
    ;;|           i
    ;;|       }
    ;;|   }
    ;;| }
    ;;> isRegularLetter(l)
    ;;> isRegularLetter(l)
    ;;> l
    local.get 0
    call $BasicPatternMatching_isRegularLetter
    i32.eqz
    local.set 1
    local.get 1
    ;;> false
    ;;> false
    i32.const 0
    i32.eq
    if (result i32)
      ;;> 42
      i32.const 42
    else
      local.get 1
      ;;> true
      ;;> true
      i32.const 1
      i32.eq
      if (result i32)
        ;;> l
        local.get 0
        local.set 2
        local.get 2
        local.set 3
        local.get 3
        i32.load
        i32.const 1
        i32.eq
        if (result i32)
          local.get 3
          ;;> adtField index: 0
          i32.const 4
          i32.add
          i32.load
          ;;> i
          local.set 4
          i32.const 1
        else
          i32.const 0
        end
        if (result i32)
          ;;> i
          local.get 4
        else
          local.get 2
          local.set 5
          local.get 5
          i32.load
          i32.const 3
          i32.eq
          if (result i32)
            local.get 5
            ;;> adtField index: 0
            i32.const 4
            i32.add
            i32.load
            ;;> i
            local.set 6
            i32.const 1
          else
            i32.const 0
          end
          if (result i32)
            ;;> i
            local.get 6
          else
            ;;> mkString: Matching error: 
            global.get 0
            i32.const 0
            i32.add
            i32.const 77
            i32.store8
            global.get 0
            i32.const 1
            i32.add
            i32.const 97
            i32.store8
            global.get 0
            i32.const 2
            i32.add
            i32.const 116
            i32.store8
            global.get 0
            i32.const 3
            i32.add
            i32.const 99
            i32.store8
            global.get 0
            i32.const 4
            i32.add
            i32.const 104
            i32.store8
            global.get 0
            i32.const 5
            i32.add
            i32.const 105
            i32.store8
            global.get 0
            i32.const 6
            i32.add
            i32.const 110
            i32.store8
            global.get 0
            i32.const 7
            i32.add
            i32.const 103
            i32.store8
            global.get 0
            i32.const 8
            i32.add
            i32.const 32
            i32.store8
            global.get 0
            i32.const 9
            i32.add
            i32.const 101
            i32.store8
            global.get 0
            i32.const 10
            i32.add
            i32.const 114
            i32.store8
            global.get 0
            i32.const 11
            i32.add
            i32.const 114
            i32.store8
            global.get 0
            i32.const 12
            i32.add
            i32.const 111
            i32.store8
            global.get 0
            i32.const 13
            i32.add
            i32.const 114
            i32.store8
            global.get 0
            i32.const 14
            i32.add
            i32.const 58
            i32.store8
            global.get 0
            i32.const 15
            i32.add
            i32.const 32
            i32.store8
            global.get 0
            i32.const 16
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            i32.const 17
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            i32.const 18
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            i32.const 19
            i32.add
            i32.const 0
            i32.store8
            global.get 0
            global.get 0
            i32.const 20
            i32.add
            global.set 0
            call $Std_printString
            unreachable
          end
        end
      else
        ;;> mkString: Matching error: 
        global.get 0
        i32.const 0
        i32.add
        i32.const 77
        i32.store8
        global.get 0
        i32.const 1
        i32.add
        i32.const 97
        i32.store8
        global.get 0
        i32.const 2
        i32.add
        i32.const 116
        i32.store8
        global.get 0
        i32.const 3
        i32.add
        i32.const 99
        i32.store8
        global.get 0
        i32.const 4
        i32.add
        i32.const 104
        i32.store8
        global.get 0
        i32.const 5
        i32.add
        i32.const 105
        i32.store8
        global.get 0
        i32.const 6
        i32.add
        i32.const 110
        i32.store8
        global.get 0
        i32.const 7
        i32.add
        i32.const 103
        i32.store8
        global.get 0
        i32.const 8
        i32.add
        i32.const 32
        i32.store8
        global.get 0
        i32.const 9
        i32.add
        i32.const 101
        i32.store8
        global.get 0
        i32.const 10
        i32.add
        i32.const 114
        i32.store8
        global.get 0
        i32.const 11
        i32.add
        i32.const 114
        i32.store8
        global.get 0
        i32.const 12
        i32.add
        i32.const 111
        i32.store8
        global.get 0
        i32.const 13
        i32.add
        i32.const 114
        i32.store8
        global.get 0
        i32.const 14
        i32.add
        i32.const 58
        i32.store8
        global.get 0
        i32.const 15
        i32.add
        i32.const 32
        i32.store8
        global.get 0
        i32.const 16
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        i32.const 17
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        i32.const 18
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        i32.const 19
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        global.get 0
        i32.const 20
        i32.add
        global.set 0
        call $Std_printString
        unreachable
      end
    end
  )

  (func $BasicPatternMatching_isPrimLetter (param i32) (result i32) (local i32)
    ;;> def isPrimLetter(l: Letter): String = {
    ;;|   getPrimNumber(l) match {
    ;;|     case 42 =>
    ;;|       "not prim"
    ;;|     case _ =>
    ;;|       "prim"
    ;;|   }
    ;;| }
    ;;> getPrimNumber(l)
    ;;> l
    local.get 0
    call $BasicPatternMatching_getPrimNumber
    local.set 1
    local.get 1
    ;;> 42
    ;;> 42
    i32.const 42
    i32.eq
    if (result i32)
      ;;> mkString: not prim
      global.get 0
      i32.const 0
      i32.add
      i32.const 110
      i32.store8
      global.get 0
      i32.const 1
      i32.add
      i32.const 111
      i32.store8
      global.get 0
      i32.const 2
      i32.add
      i32.const 116
      i32.store8
      global.get 0
      i32.const 3
      i32.add
      i32.const 32
      i32.store8
      global.get 0
      i32.const 4
      i32.add
      i32.const 112
      i32.store8
      global.get 0
      i32.const 5
      i32.add
      i32.const 114
      i32.store8
      global.get 0
      i32.const 6
      i32.add
      i32.const 105
      i32.store8
      global.get 0
      i32.const 7
      i32.add
      i32.const 109
      i32.store8
      global.get 0
      i32.const 8
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 9
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 10
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      i32.const 11
      i32.add
      i32.const 0
      i32.store8
      global.get 0
      global.get 0
      i32.const 12
      i32.add
      global.set 0
    else
      local.get 1
      ;;> _
      drop
      i32.const 1
      if (result i32)
        ;;> mkString: prim
        global.get 0
        i32.const 0
        i32.add
        i32.const 112
        i32.store8
        global.get 0
        i32.const 1
        i32.add
        i32.const 114
        i32.store8
        global.get 0
        i32.const 2
        i32.add
        i32.const 105
        i32.store8
        global.get 0
        i32.const 3
        i32.add
        i32.const 109
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
        ;;> mkString: Matching error: 
        global.get 0
        i32.const 0
        i32.add
        i32.const 77
        i32.store8
        global.get 0
        i32.const 1
        i32.add
        i32.const 97
        i32.store8
        global.get 0
        i32.const 2
        i32.add
        i32.const 116
        i32.store8
        global.get 0
        i32.const 3
        i32.add
        i32.const 99
        i32.store8
        global.get 0
        i32.const 4
        i32.add
        i32.const 104
        i32.store8
        global.get 0
        i32.const 5
        i32.add
        i32.const 105
        i32.store8
        global.get 0
        i32.const 6
        i32.add
        i32.const 110
        i32.store8
        global.get 0
        i32.const 7
        i32.add
        i32.const 103
        i32.store8
        global.get 0
        i32.const 8
        i32.add
        i32.const 32
        i32.store8
        global.get 0
        i32.const 9
        i32.add
        i32.const 101
        i32.store8
        global.get 0
        i32.const 10
        i32.add
        i32.const 114
        i32.store8
        global.get 0
        i32.const 11
        i32.add
        i32.const 114
        i32.store8
        global.get 0
        i32.const 12
        i32.add
        i32.const 111
        i32.store8
        global.get 0
        i32.const 13
        i32.add
        i32.const 114
        i32.store8
        global.get 0
        i32.const 14
        i32.add
        i32.const 58
        i32.store8
        global.get 0
        i32.const 15
        i32.add
        i32.const 32
        i32.store8
        global.get 0
        i32.const 16
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        i32.const 17
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        i32.const 18
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        i32.const 19
        i32.add
        i32.const 0
        i32.store8
        global.get 0
        global.get 0
        i32.const 20
        i32.add
        global.set 0
        call $Std_printString
        unreachable
      end
    end
  )
  (export "_start" (func $BasicPatternMatching_main))
  (func $BasicPatternMatching_main (local i32 i32 i32 i32 i32 i32 i32 i32)
    ;;> (
    ;;|   val a: Letter =
    ;;|     A();
    ;;|   val ap: Letter =
    ;;|     APrim(2);
    ;;|   val b: Letter =
    ;;|     B();
    ;;|   val bp: Letter =
    ;;|     BPrim(3);
    ;;|   printString(intToString(getPrimNumber(a)));
    ;;|   printString(intToString(getPrimNumber(b)));
    ;;|   printString(intToString(getPrimNumber(ap)));
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> A()
    global.get 0
    local.set 1
    global.get 0
    ;;> adtField index: 0
    i32.const 4
    i32.add
    global.set 0
    local.get 1
    i32.const 0
    i32.store
    local.get 1
    local.set 0
    ;;> (
    ;;|   val ap: Letter =
    ;;|     APrim(2);
    ;;|   val b: Letter =
    ;;|     B();
    ;;|   val bp: Letter =
    ;;|     BPrim(3);
    ;;|   printString(intToString(getPrimNumber(a)));
    ;;|   printString(intToString(getPrimNumber(b)));
    ;;|   printString(intToString(getPrimNumber(ap)));
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> APrim(2)
    global.get 0
    local.set 3
    global.get 0
    ;;> adtField index: 1
    i32.const 8
    i32.add
    global.set 0
    local.get 3
    i32.const 1
    i32.store
    local.get 3
    ;;> adtField index: 0
    i32.const 4
    i32.add
    ;;> 2
    i32.const 2
    i32.store
    local.get 3
    local.set 2
    ;;> (
    ;;|   val b: Letter =
    ;;|     B();
    ;;|   val bp: Letter =
    ;;|     BPrim(3);
    ;;|   printString(intToString(getPrimNumber(a)));
    ;;|   printString(intToString(getPrimNumber(b)));
    ;;|   printString(intToString(getPrimNumber(ap)));
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> B()
    global.get 0
    local.set 5
    global.get 0
    ;;> adtField index: 0
    i32.const 4
    i32.add
    global.set 0
    local.get 5
    i32.const 2
    i32.store
    local.get 5
    local.set 4
    ;;> (
    ;;|   val bp: Letter =
    ;;|     BPrim(3);
    ;;|   printString(intToString(getPrimNumber(a)));
    ;;|   printString(intToString(getPrimNumber(b)));
    ;;|   printString(intToString(getPrimNumber(ap)));
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> BPrim(3)
    global.get 0
    local.set 7
    global.get 0
    ;;> adtField index: 1
    i32.const 8
    i32.add
    global.set 0
    local.get 7
    i32.const 3
    i32.store
    local.get 7
    ;;> adtField index: 0
    i32.const 4
    i32.add
    ;;> 3
    i32.const 3
    i32.store
    local.get 7
    local.set 6
    ;;> (
    ;;|   printString(intToString(getPrimNumber(a)));
    ;;|   printString(intToString(getPrimNumber(b)));
    ;;|   printString(intToString(getPrimNumber(ap)));
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(getPrimNumber(a)))
    ;;> intToString(getPrimNumber(a))
    ;;> getPrimNumber(a)
    ;;> a
    local.get 0
    call $BasicPatternMatching_getPrimNumber
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(getPrimNumber(b)));
    ;;|   printString(intToString(getPrimNumber(ap)));
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(getPrimNumber(b)))
    ;;> intToString(getPrimNumber(b))
    ;;> getPrimNumber(b)
    ;;> b
    local.get 4
    call $BasicPatternMatching_getPrimNumber
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(getPrimNumber(ap)));
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(getPrimNumber(ap)))
    ;;> intToString(getPrimNumber(ap))
    ;;> getPrimNumber(ap)
    ;;> ap
    local.get 2
    call $BasicPatternMatching_getPrimNumber
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(intToString(getPrimNumber(bp)));
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(intToString(getPrimNumber(bp)))
    ;;> intToString(getPrimNumber(bp))
    ;;> getPrimNumber(bp)
    ;;> bp
    local.get 6
    call $BasicPatternMatching_getPrimNumber
    call $Std_intToString
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(isPrimLetter(ap));
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(isPrimLetter(ap))
    ;;> isPrimLetter(ap)
    ;;> ap
    local.get 2
    call $BasicPatternMatching_isPrimLetter
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(isPrimLetter(bp));
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(isPrimLetter(bp))
    ;;> isPrimLetter(bp)
    ;;> bp
    local.get 6
    call $BasicPatternMatching_isPrimLetter
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(isPrimLetter(a));
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(isPrimLetter(a))
    ;;> isPrimLetter(a)
    ;;> a
    local.get 0
    call $BasicPatternMatching_isPrimLetter
    call $Std_printString
    drop
    ;;> (
    ;;|   printString(isPrimLetter(b));
    ;;|   printString("test finished")
    ;;| )
    ;;> printString(isPrimLetter(b))
    ;;> isPrimLetter(b)
    ;;> b
    local.get 4
    call $BasicPatternMatching_isPrimLetter
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