""""class.sc
    Offers options to augment an Enum type so that it works as a class with different sub types.
    A class sugar, a decorator and an inline to augment a previously defined enum are available.

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

inline... enum-class-constructor (cls v)
    _enum-class-constructor cls v

let decorate-enum = decorate-struct

inline make-class (enum_)
    typedef+ enum_
        let __typecall = enum-class-constructor
        inline __as (selfT otherT)
            static-if (imply? selfT otherT)
                inline (self)
                    imply self otherT
            elseif (has-subtype? selfT otherT)
                inline (self)
                    if (not (('literal self) == ((type->enum-tag selfT otherT) . Literal)))
                        assert false (.. "tried to cast " (tostring selfT) " to incorrect subtype " (tostring otherT))

                    'unsafe-extract-payload self otherT

sugar class (name body...)
    let use-scope body =
        sugar-match body...
        case (('use scope) rest...)
            let scope = ((sc_prove (sc_expand scope '() sugar-scope) ()) as Scope)
            _ scope rest...
        default
            _ (Scope) (body... as list)

    let tags =
        loop (index tags = -1 '())
            let key value index =
                sc_scope_next use-scope index
            if (index < 0)
                break ('reverse tags)
            _ index
                cons
                    qq
                        [key] : [value]
                    tags
    qq
        [embed]
            [enum] [name]
                unquote-splice tags
                unquote-splice body
            [make-class] [name]

do
    let class decorate-enum make-class
    locals;
