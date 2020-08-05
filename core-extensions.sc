inline make-handle-type (name storageT)
    typedef (tostring name) :: storageT
        inline __typecall (cls init)
            bitcast (deref init) this-type
        inline __imply (cls other-cls)
            inline (self)
                bitcast (view self) other-cls
        inline __rimply (cls other-cls)
            this-type.__typecall
        inline __as (cls other-cls)
            inline (self)
                let value = (storagecast (view self))
                value as other-cls

sugar define-scope (name body...)
    qq
        [let] [name] =
            [do]
                unquote-splice body...
                (locals)

inline &local (T ...)
    &
        local T
            ...

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

# inline va-reduce (f init ...)
#     va-lfold init
#         inline (__ )


locals;
