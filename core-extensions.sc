inline make-handle-type (name storageT dropf)
    typedef (tostring name) :: storageT
        inline __typecall (cls init)
            bitcast (deref init) this-type
        inline __imply (cls other-cls)
            inline (self)
                imply (storagecast (view self)) other-cls
        inline __rimply (cls other-cls)
            this-type.__typecall
        inline __as (cls other-cls)
            inline (self)
                let value = (storagecast (view self))
                value as other-cls
        inline __toptr (self)
            '__toptr (storagecast (view self))

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

locals;
