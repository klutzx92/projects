.import ../dot.s
.import ../utils.s

# Set vector values for testing
.data
vector0: .word 2 1 6 1 3 5 #make changes here
vector1: .word -1 -2 2 0 -1 -1 #make changes here

# 1 2 3 4 5 6 7 8 9
# 1 2 3 4 5 6 7 8 9

# 3 4 6
# 5 2 3

# 3 0 4 0 6 0 0 0 0
# 5 0 0 2 0 0 3 0 0

# 1 0 -2 0 -3 0 4 0 0
# 1 0 2 0 3 0 4 0 0

.text
# main function for testing
main:
    # Load vector addresses into registers
    la s0 vector0
    la s1 vector1

    # Set vector attributes
    addi s2 x0 3 #length
    addi s3 x0 1 #v0's stride
    addi s4 x0 2 #v1's stride


    # Call dot function
    mv a0 s0
    mv a1 s1
    mv a2 s2
    mv a3 s3
    mv a4 s4
    jal ra dot

    # Print integer result
    mv a1 a0
    jal ra print_int



    # Print newline
    li a1 '\n'
    jal ra print_char



    # Exit
    jal exit
