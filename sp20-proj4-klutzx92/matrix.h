#include <Python.h>

typedef struct matrix {
    int rows; // number of rows
    int cols; // number of columns
    int free_data; // 1 if we want to free this data during deallocation, 0 otherwise
    double* data; // pointer to rows * columns doubles
} matrix;

int allocate_matrix(matrix **mat, int rows, int cols);
int allocate_matrix_ref(matrix **mat, matrix *from, int offset, int rows, int cols);
void deallocate_matrix(matrix *mat);
double get(matrix *mat, int row, int col);
void set(matrix *mat, int row, int col, double val);
void fill_matrix(matrix *mat, double val);
int add_matrix(matrix *result, matrix *mat1, matrix *mat2);
int sub_matrix(matrix *result, matrix *mat1, matrix *mat2);
int mul_matrix(matrix *result, matrix *mat1, matrix *mat2);
int pow_matrix(matrix *result, matrix *mat, int pow);
int neg_matrix(matrix *result, matrix *mat);
int abs_matrix(matrix *result, matrix *mat);
