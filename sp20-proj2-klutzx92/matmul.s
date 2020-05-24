.globl matmul

.text
# =======================================================
# FUNCTION: Matrix Multiplication of 2 integer matrices
# 	d = matmul(m0, m1)
#   If the dimensions don't match, exit with exit code 2
# Arguments:
# 	a0 is the pointer to the start of m0
#	a1 is the # of rows (height) of m0
#	a2 is the # of columns (width) of m0
#	a3 is the pointer to the start of m1
# 	a4 is the # of rows (height) of m1
#	a5 is the # of columns (width) of m1
#	a6 is the pointer to the the start of d
# Returns:
#	None, sets d = matmul(m0, m1)
# =======================================================
matmul:

    # Error if mismatched dimensions
    bne a2 a4 mismatched_dimensions

    # Prologue
    addi sp sp -52
    sw s0 0(sp)
    sw s1 4(sp)
    sw s2 8(sp)
    sw s3 12(sp)
    sw s4 16(sp)
    sw s5 20(sp)
    sw s6 24(sp)
    sw s7 28(sp)
    sw s8 32(sp)
    sw s9 36(sp)
    sw s10 40(sp)
    sw s11 44(sp)
    sw ra 48(sp)

    mv s0 a0  # m0
    mv s1 a1  # r0
    mv s2 a2  # c0
    mv s3 a3  # m1
    mv s4 a4  # r1
    mv s5 a5  # c1
    mv s6 a6  # d

    mul s7 s1 s2 # length = r0 * c0
    add s8 x0 x0 # i = 0
    add s9 x0 x0 # k = 0
    addi s11 x0 1 #constant 1
    j outer_loop_start

outer_loop_start:

    bge s8 s1 outer_loop_end  # i >= r0
    add s10 x0 x0 # j = 0


inner_loop_start:

    bge s10 s5 inner_loop_end # j >= c1
    addi t1 x0 4  # t1 = 4
    mul t1 t1 s8  # t1 = 4i
    mul t1 t1 s2  # t1 = 4i*c0
    add t1 s0 t1  # t1 = m0 + 4i*c0

    addi t2 x0 4  # t2 = 4
    mul t2 t2 s10 # t2 = 4j
    add t2 s3 t2  # t2 = m1 + 4j

    mv a0 t1  # a0 = t1 = m0 + 4i*c0
    mv a1 t2  # a1 = t2 = m1 + 4j
    mv a2 s2  # a2 = s2 = c0
    mv a3 s11 # a3 = s11 = 1
    mv a4 s5  # a4 = s5 = c1
    jal ra dot

    mv t0 a0  # t0 = a0 = dotproduct element = 6
    addi t3 x0 4  # t3 = 4
    mul t3 t3 s9  # t3 = 4k
    add t3 s6 t3  # t3 = d + 4k
    sw t0 0(t3)   #
    addi s9 s9 1   # k += 1
    addi s10 s10 1 # j += 1
    j inner_loop_start


inner_loop_end:

    addi s8 s8 1  # i += 1
    j outer_loop_start


outer_loop_end:

    # Epilogue
    lw s0 0(sp)
    lw s1 4(sp)
    lw s2 8(sp)
    lw s3 12(sp)
    lw s4 16(sp)
    lw s5 20(sp)
    lw s6 24(sp)
    lw s7 28(sp)
    lw s8 32(sp)
    lw s9 36(sp)
    lw s10 40(sp)
    lw s11 44(sp)
    lw ra 48(sp)
    addi sp sp 52
    jr ra

    ret


mismatched_dimensions:
    li a1 2
    jal exit2
