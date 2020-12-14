spice has-symbol? (T sym)
    """"Checks for the existence of a symbol in a type at compile time.
    T as:= type
    sym as:= Symbol
    try
        let sym = ('@ T sym)
        `true
    else
        `false
run-stage;

inline member-typeof (T member)
    typeof (getattr (nullof T) member)

inline expand-inline (f args...)
    static-typify
        fn ()
            f args...

do
    let
        member-typeof
        expand-inline
    locals;
