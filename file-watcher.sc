if (operating-system == 'windows)
    error "file watching not yet implemented on windows"

using import struct
using import Array
using import Map
using import Box
using import enum
using import .core-extensions
using import .foreign
import .libc
inotify := (import .linux.inotify)

vvv bind errno
do
    let code =
        include
            """"#include <errno.h>
                typeof(errno) scopes_wrapper_errno () {
                    return errno;
                }
    let errno = code.extern.scopes_wrapper_errno
    using code.define
    locals;

enum EventKind
    ACCESSED = inotify.IN_ACCESS
    MODIFIED = inotify.IN_MODIFY
    METADATA_CHANGED = inotify.IN_ATTRIB
    CLOSED = inotify.IN_CLOSE
    OPENED = inotify.IN_OPEN
    # directory events
    FILE_MOVED = inotify.IN_MOVE
    # FILE_CREATED

    inline __& (T otherT)
        static-if (otherT < integer)
            inline (lhs rhs)
                ((extractvalue lhs 0) as otherT) & rhs

spice i32->EventKind (value)
    let constructor-sw = (sc_switch_new `(value as i32))
    va-map
        inline (f)
            sc_switch_append_case constructor-sw `[(f.Literal as i32)] `(f)
        EventKind.__fields__
    sc_switch_append_default constructor-sw
        spice-quote
            error "mask doesn't match any event"
    constructor-sw
run-stage;

global event-masks =
    arrayof i32
        va-map
            inline (f)
                f.Literal
            EventKind.__fields__

FileEventCallback := (pointer (function void))
struct WatchDescriptor plain
    handle : i32
    event-kind : i32

    inline __hash (self)
        hash self.event-kind self.handle

    inline __== (T otherT)
        inline (self other)
            and
                self.handle == other.handle
                self.event-kind == other.event-kind

struct FileWatcher
    event-callbacks : (Map WatchDescriptor FileEventCallback)
    _fd : i32

    inline __typecall (cls)
        let fd = (inotify.init1 (bitcast inotify.IN_NONBLOCK i32))
        if (fd == -1)
            error "inotify instance initialization failed"
        super-type.__typecall cls
            _fd = fd

    inline __drop (self)
        using libc.unistd
        close self._fd
        ;

    # returns watch descriptor / handle, which is used to unwatch a file
    inline watch-file (self path event callback)
        fn watch-file (self path event callback)
            imply event EventKind
            let event-index = (extractvalue event 0)
            # IN_MASK_ADD makes it so we can call this function once
            # for each event instead of OR'ing them.
            let wd = (inotify.add_watch self._fd path (inotify.IN_MASK_ADD | (event-index as u32)))
            assert (wd != -1) # FIXME: check for specific errors
            'set self.event-callbacks
                WatchDescriptor
                    handle = wd
                    event-kind = event-index
                imply callback FileEventCallback
            # try
            #     local c = ('get self.event-callbacks wd)
            #     'set c event callback
            # else
            #     local c = ((Map EventKind FileEventCallback))
            #     'set c event callback
            #     'set self.event-callbacks wd (dupe c)

            wd
        watch-file self path event (static-typify callback FileEventCallback)

    fn unwatch-file (self watch-handle)
        inotify.rm_watch self._fd watch-handle
        va-map
            inline (evk)
                let wd =
                    WatchDescriptor
                        handle = watch-handle
                        event-kind = evk.Literal
                'discard self.event-callbacks wd
            EventKind.__fields__

    fn watching? (self path)
        # dummy watch request to get a wd
        let wd = (inotify.add_watch self._fd path inotify.IN_MASK_ADD)
        # 'in? self.event-callbacks wd
        # this is so bad lol
        va-lfold false
            inline (__ next computed)
                if (not computed)
                    'in? self.event-callbacks
                        WatchDescriptor
                            handle = wd
                            event-kind = next.Literal
                else
                    computed
            EventKind.__fields__

    fn poll-events (self)
        using libc.unistd

        loop () # repeat until nothing left to read
            local buf : (array i8 4096)
            let len = (read self._fd &buf (sizeof buf))
            if (len == -1) # nothing to read
                assert ((errno.errno) == errno.EAGAIN)
                break;
            loop (position = 0:i64)
                if (position >= len)
                    break;

                let ev = (bitcast (& (buf @ position)) (pointer inotify.event))
                # handle event
                va-map
                    inline (evk)
                        if ((evk.Literal & ev.mask) as bool)
                            try
                                call
                                    'get self.event-callbacks
                                        WatchDescriptor
                                            handle = ev.wd
                                            event-kind = evk.Literal
                            else
                                # because of compound literals, a literal might match
                                # even though it doesn't have a callback associated with it,
                                # eg. there's a callback associated with the compound but none
                                # of te composing parts.
                                ;
                    EventKind.__fields__
                + position (sizeof inotify.event) ev.len

# do
#     local fw = (FileWatcher)
#     let test-wd =
#         'watch-file fw "test_.c" (EventKind.ACCESSED)
#             fn "test access" ()
#                 print "file accessed"
#     'watch-file fw "test_.c" (EventKind.CLOSED)
#         fn "test closement" ()
#             print "file closed"

#     print ('watching? fw "test_.c") # true
#     for i in (range 100)
#         using import .platform
#         sleep 1
#         'poll-events fw
#         if (i == 15)
#             print "unwatching file"
#             'unwatch-file fw test-wd
do
    let FileWatcher EventKind
    locals;
