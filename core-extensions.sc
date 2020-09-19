spice _enum-class-constructor (cls v)
    cls as:= type
    T := ('typeof v)
    let fields = ('@ cls '__fields__)
    for ft in ('args fields)
        ft as:= type
        let Type = (('@ ft 'Type) as type)
        if (('element@ Type 0) == T)
            return `([('@ ft '__typecall)] ft v)
    hide-traceback;
    error (.. "type " (tostring cls) " doesn't contain subtype " (tostring T))

spice plain? (T)
    T as:= type
    `[('plain? T)]

run-stage;

inline make-handle-type (name storageT dropf)
    typedef (tostring name) :: storageT
        inline __typecall (cls init)
            bitcast (imply init (storageof this-type)) this-type

        inline __imply (cls other-cls)
            static-if (imply? (storageof cls) other-cls)
                inline (self)
                    imply (storagecast (view self)) other-cls

        inline __rimply (cls other-cls)
            inline (val)
                this-type val

        inline __as (cls other-cls)
            inline (self)
                let value = (storagecast (view self))
                value as other-cls

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

inline enum-class-constructor (cls v)
    _enum-class-constructor cls v

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
