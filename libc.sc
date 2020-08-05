using import .core-extensions
using import .foreign

define-scope errno
    let header =
        foreign "errno.h"
            with-constants
                errno

    using header.extern
    using header.define

    let errno = scopes_constant_wrapper__errno
    unlet header

define-scope unistd
    let header =
        include "unistd.h"

    using header.extern
    using header.define
    using header.enum

    unlet header

# as to not shadow scopes' error function
define-scope _error
    let header =
        include "error.h"

    using header.extern
    using header.define

    unlet header

# as to not shadow scopes' string type
define-scope _string
    let header =
        include "string.h"

    using header.extern
    using header.define

    unlet header

locals;
