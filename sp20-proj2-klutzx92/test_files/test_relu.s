.import ../relu.s
.import ../utils.s

# Set vector values for testing
.data
m0: .word 1 2 3 4 -5 -6 -7 -8 9 10 11 12 -13 -14 -15 -16 # MAKE CHANGES HERE

# dimensions 3x3
# 1 -2 3 -4 5 -6 7 -8 9
# -5 -4 -10 -20 -1000 -3132 -6 -9 -10
# 0 0 0 0 0 0 0 0 0
# 0 -10 200 -3000 40000 -500000 6000000 -700000000 800000000 -9000000000

# dimensions 4x4
# 1 -2 3 -4 5 -6 7 -8 9 -10 11 -12 13 -14 15 -16
# 1 2 3 4 -5 -6 -7 -8 9 10 11 12 -13 -14 -15 -16


.text
# main function for testing
main:
    # Load address of m0
    la s0 m0

    # Set dimensions of m0
    li s1 4 # MAKE CHANGES HERE
    li s2 4 # MAKE CHANGES HERE

    # Print m0 before running relu
    mv a0 s0
    mv a1 s1
    mv a2 s2
    jal print_int_array

    # Print newline
    li a1 '\n'
    jal print_char

    # Call relu function
    mv a0 s0
    mul a1 s1 s2 # Convert dimensions to total number of elements
    jal ra relu

    # Print m0 after running relu
    mv a0 s0
    mv a1 s1
    mv a2 s2
    jal print_int_array

    # Exit
    jal exit
