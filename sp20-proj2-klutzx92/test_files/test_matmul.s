.import ../matmul.s
.import ../utils.s
.import ../dot.s

# static values for testing
.data
m0: .word 2 1 4 0 1 1
m1: .word 6 3 -1 0 1 1 0 4 -2 5 0 2
d: .word 0 0 0 0 0 0 0 0 # allocate static space for output

# (3x3)(3x3)
# 1 2 3 4 5 6 7 8 9
# 1 2 3 4 5 6 7 8 9
# 0 0 0 0 0 0 0 0 0

# (2x3)(3x2)
# 2 1 6 1 3 5
# -1 -2 2 0 -1 -1
# 0 0 0 0

# (2x2)(4x1)  //should error with exit code 2
# 1 2 3 4
# 5 6 7 8
# 0 0

# (2x3)(3x4)
# 2 1 4 0 1 1
# 6 3 -1 0 1 1 0 4 -2 5 0 2
# 0 0 0 0 0 0 0 0

.text
main:
    # Load addresses of input matrices (which are in static memory), and set their dimensions

    # 1. Load matrix addresses into registers
    la s0 m0 # Left matrix
    la s3 m1 # Right matrix
    la s6 d # Output matrix

    # 2. Set matrix dimensions
    addi s1 x0 2 # r0, number of rows in m0
    addi s2 x0 3 # c0, number of columns in m0
    addi s4 x0 3 # r1, number of rows in m1
    addi s5 x0 4 # c1, number of columns in m1

    # Call matrix multiply, m0 * m1
    mv a0 s0
    mv a1 s1
    mv a2 s2
    mv a3 s3
    mv a4 s4
    mv a5 s5
    mv a6 s6
    jal ra matmul

    # Print the output (use print_int_array in utils.s)
    mv a0 s6
    mv a1 s1
    mv a2 s5
    jal ra print_int_array

    # Print newline
    li a1 '\n'
    jal ra print_char

    # Exit the program
    jal exit
