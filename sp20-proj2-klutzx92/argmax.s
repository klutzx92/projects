.globl argmax

.text
# =================================================================
# FUNCTION: Given a int vector, return the index of the largest
#	element. If there are multiple, return the one
#	with the smallest index.
# Arguments:
# 	a0 is the pointer to the start of the vector
#	a1 is the # of elements in the vector
# Returns:
#	a0 is the first index of the largest element
# =================================================================
argmax:

    # Prologue
    addi sp sp -24
    sw s0 0(sp)
    sw s1 4(sp)
    sw s2 8(sp)
    sw s3 12(sp)
    sw s4 16(sp)
    sw ra 20(sp)

    mv s0 a0
    mv s1 a1
    addi s2 x0 1
    lw s3 0(s0)
    add s4 x0 x0
    addi s0 s0 4

loop_start:

    bge s2 s1 loop_end
    lw t0 0(s0)
    blt s3 t0 loop_continue
    addi s2 s2 1
    addi s0 s0 4
    j loop_start

loop_continue:

  lw s3 0(s0)
  mv s4 s2
  addi s2 s2 1
  addi s0 s0 4
  j loop_start

loop_end:

    mv a0 s4

    # Epilogue
    lw s0 0(sp)
    lw s1 4(sp)
    lw s2 8(sp)
    lw s3 12(sp)
    lw s4 16(sp)
    lw ra 20(sp)
    addi sp sp 24
    jr ra

    ret
