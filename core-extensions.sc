# TODO: I _really_ need to organize and document this!
spice plain? (T)
    T as:= type
    `[('plain? T)]

run-stage;

inline semantically-bind-types (T1 T2 implyf rimplyf)
    typedef+ T1
        inline gen-op (op)
            inline (lhsT rhsT)
                static-if (lhsT == this-type)
                    inline (a b)
                        op (imply a T2) b
                elseif (rhsT == this-type)
                    inline (a b)
                        op a (imply b T2)

        inline __imply (selfT otherT)
            static-if (otherT == T2)
                implyf

        inline __rimply (otherT selfT)
            inline (other)
                rimplyf (imply other T2)

        let __+ = (gen-op +)
        let __- = (gen-op -)
        let __* = (gen-op *)
        let __/ = (gen-op /)
        let __// = (gen-op //)

        inline __repr (self)
            .. (repr (imply self T2))
                default-styler style-operator ":"
                default-styler style-type (tostring this-type)

        unlet gen-op

inline make-handle-type (name storageT dropf)
    typedef (tostring name) <:: storageT
        inline... __typecall (cls init)
            bitcast init this-type
        case (cls)
            bitcast (undef storageT) this-type
        let __drop = dropf

sugar define-scope (name body...)
    qq
        [let] [name] =
            [do]
                unquote-splice body...
                (locals)

inline... &local (T : type, ...)
    &
        local T
            ...
case (source)
    &
        local dummy-name = source

inline some? (x)
    (not (none? x))

inline raw-typecall (T ...)
    ((superof T) . __typecall) T
        ...

inline copy* (original ...)
    """"Copy composite type while overriding fields as desired.
    local result = (copy original)
    va-lfold result
        inline (key value result)
            (getattr result key) = value
            result
        ...
    deref result

sugar sugar-string (str)
    sc_expand  str '() sugar-scope

inline mutate (ref)
    (value) -> (= ref value)

inline Array-sizeof (v)
    using import Array

    let T = (typeof v)
    static-if (not (T < Array))
        static-error "Array-sizeof can only be used on Array values"
    else
        (countof v) * (sizeof T.ElementType)

inline va-tail (...)
    let __ args... = ...
    args...

inline struct-equality-by-field (T)
    inline (lhsT rhsT)
        static-if (lhsT == rhsT)
            inline (l r)
                va-lfold true
                    inline (__ f result)
                        let k = (keyof f.Type)
                        result and ((getattr l k) == (getattr r k))
                    T.__fields__

locals;
