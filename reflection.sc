spice member-typeof (T member)
    let anchor = ('anchor `T)
    T as:= type
    member as:= Symbol

    if ((T < Struct) or (T < CStruct))
        for elem in ('elements T)
            let k v = ('keyof elem)
            if (k == member)
                return `v
        hide-traceback;
        error@ anchor "while querying type information"
            .. "field " (repr member) " not found in type " (repr T) "."

    else
        hide-traceback;
        error@ ('anchor `T) "while querying type information" "not a struct type."

run-stage;

inline expand-inline (f args...)
    static-typify
        fn ()
            f args...

do
    let
        member-typeof
        expand-inline
    locals;
