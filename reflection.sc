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
