using import ..stringtools
using import testing

test
    (remove-prefix "sg_query_buffer_overflow" "sg_") == "query_buffer_overflow"

do
    inline test-join (expected-output inputs...)
        test
            (join-strings inputs...) == expected-output
        test
            (join-strings-array (local arr = (arrayof string inputs...))) == expected-output

    test-join "abcdefghi" "abc" "def" "ghi"
    test-join "abc" "a" "" "b" "" "c" ""
    test-join "abc" "abc"
    test-join "" ""

do
    let ABC = 123
    let CDE = 345
    let str = "banana"

    test
        ==
            interpolate
                "ABC is ${ABC}, CDE is ${CDE}, and the sum is ${(+ ABC CDE)}. This other string is ${str}"
            "ABC is 123, CDE is 345, and the sum is 468. This other string is banana"

    let varargs... = 1 2 3 4
    test
        ==
            interpolate
                "some ... ${varargs...} for you!"
            "some ... 1234 for you!"

do
    test
        ==
            replace "abc def ghi jkl " " " "_"
            "abc_def_ghi_jkl_"
    test
        ==
            replace "abc def ghi jkl" "def " "9"
            "abc 9ghi jkl"
    test
        ==
            replace "abcdef" "not in string" "cdef"
            "abcdef"
    test
        ==
            replace "abcdef" "abcdef" ""
            ""
    test
        ==
            replace "0abcdef" "abcdef" ""
            "0"
    test
        ==
            replace "abc000def" "000" ""
            "abcdef"
;
