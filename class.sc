""""class.sc
    Class decorator implementation that augments an Enum type so every tag acts as a subclass.

using import enum
fn find-enum-tag-by-type (cls T)
    cls as:= type
    T as:= type
    let fields = ('@ cls '__fields__)
    for ft in ('args fields)
        ft as:= type
        let Type = (('@ ft 'Type) as type)
        if (('element@ Type 0) == T)
            return ft
    hide-traceback;
    error (.. "no subtype " (tostring T) " in type " (tostring cls))

spice _enum-class-constructor (cls v)
    let ft = (find-enum-tag-by-type cls ('typeof v))
    return `([('@ ft '__typecall)] ft v)

spice type->enum-tag (enumT tagT)
    let ft = (find-enum-tag-by-type enumT tagT)
    `ft

spice has-subtype? (enumT subT)
    try
        find-enum-tag-by-type enumT subT
        true
    else
        false

run-stage;

inline enum-class-constructor (cls v)
    _enum-class-constructor cls v

let decorate-enum = decorate-struct

inline class (enum_)
    typedef+ enum_
        let __typecall = enum-class-constructor
        inline __as (selfT otherT)
            static-if (imply? selfT otherT)
                inline (self)
                    imply self otherT
            elseif (has-subtype? selfT otherT)
                inline (self)
                    if (not (('literal self) == ((type->enum-tag selfT otherT) . Literal)))
                        assert false (.. "tried to cast " (repr selfT) " to incorrect subtype " (repr otherT))

                    'unsafe-extract-payload self otherT

do
    let class
    locals;
