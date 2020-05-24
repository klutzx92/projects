.globl relu

.text
# ==============================================================================
# FUNCTION: Performs an inplace element-wise ReLU on an array of ints
# Arguments:
# 	a0 is the pointer to the array
#	a1 is the # of elements in the array
# Returns:
#	None
# ==============================================================================
relu:
    # Prologue
    addi sp sp -16
    sw s0 0(sp)
    sw s1 4(sp)
    sw s2 8(sp)
    sw ra 12(sp)

    mv s0 a0
    mv s1 a1
    add s2 x0 x0

loop_start:

    bge s2 s1 loop_end
    lw t0 0(s0)
    blt t0 x0 loop_continue
    addi s2 s2 1
    addi s0 s0 4
    j loop_start

loop_continue:

    add t0 x0 x0
    sw t0 0(s0)
    addi s2 s2 1
    addi s0 s0 4
    j loop_start

loop_end:

    # Epilogue
    lw s0 0(sp)
    lw s1 4(sp)
    lw s2 8(sp)
    lw ra 12(sp)
    addi sp sp 16
    jr ra


	ret
