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

# inline va-reduce (f init ...)
#     va-lfold init
#         inline (__ )


locals;
