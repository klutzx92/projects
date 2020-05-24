.globl dot

.text
# =======================================================
# FUNCTION: Dot product of 2 int vectors
# Arguments:
#   a0 is the pointer to the start of v0
#   a1 is the pointer to the start of v1
#   a2 is the length of the vectors
#   a3 is the stride of v0
#   a4 is the stride of v1
# Returns:
#   a0 is the dot product of v0 and v1
# =======================================================
dot:

    # Prologue
    addi sp sp -32
    sw s0 0(sp)
    sw s1 4(sp)
    sw s2 8(sp)
    sw s3 12(sp)
    sw s4 16(sp)
    sw s5 20(sp)
    sw s6 24(sp)
    sw ra 28(sp)

    mv s0 a0
    mv s1 a1
    mv s2 a2
    mv s3 a3
    mv s4 a4
    add s5 x0 x0
    add s6 x0 x0

loop_start:

    bge s6 s2 loop_end
    addi t6 x0 4
    mul t3 s6 s3
    mul t3 t3 t6
    add t4 t3 s0
    lw t0 0(t4)
    mul t3 s6 s4
    mul t3 t3 t6
    add t4 t3 s1
    lw t1 0(t4)
    mul t2 t0 t1
    add s5 s5 t2
    addi s6 s6 1
    j loop_start

loop_end:

    mv a0 s5

    # Epilogue
    lw s0 0(sp)
    lw s1 4(sp)
    lw s2 8(sp)
    lw s3 12(sp)
    lw s4 16(sp)
    lw s5 20(sp)
    lw s6 24(sp)
    lw ra 28(sp)
    addi sp sp 32
    jr ra


    ret
