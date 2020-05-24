.import ../argmax.s
.import ../utils.s

.data
v0: .word 0 0 0 3 0 0 -6 0 3 # MAKE CHANGES HERE

# 3 -42 432 7 -5 6 5 -114 2
# 0 1 -1000 6 8 20 500 500 500
# 8 7 6 5 4 3 2 1 0
# 1 2 3 4 5 6 7 8 9
# -1 -2 -3 -4 -5 -6 -7 -8 -9
# 0 -1 -2 -3 -4 -3 -2 -1 0
# 0 5 10 15 10 5 0 -5 -10
# -10 -9 -8 -7 -6 -5 -4 -10 -4
# 2342 -3543 99999999 25425 10002 2324524 -134 0 99999999
# -4

.text
main:
    # Load address of v0
    la s0 v0

    # Set length of v0
    addi s1 x0 9 # MAKE CHANGES HERE

    # Call argmax
    mv a0 s0
    mv a1 s1
    jal ra argmax

    # Print the output of argmax
    mv a1 a0
    jal ra print_int

    # Print newline
    li a1 '\n'
    jal ra print_char

    # Exit program
    jal exit
