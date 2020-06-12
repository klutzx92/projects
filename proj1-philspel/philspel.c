/*
 * Include the provided hashtable library.
 */
#include "hashtable.h"

/*
 * Include the header file.
 */
#include "philspel.h"

/*
 * Standard IO and file routines.
 */
#include <stdio.h>

/*
 * General utility routines (including malloc()).
 */
#include <stdlib.h>

/*
 * Character utility routines.
 */
#include <ctype.h>

/*
 * String utility routines.
 */
#include <string.h>

/*
 * this hashtable stores the dictionary.
 */
HashTable *dictionary;

/*
 * the MAIN routine.  You can safely print debugging information
 * to standard error (stderr) and it will be ignored in the grading
 * process, in the same way which this does.
 */
int main(int argc, char **argv) {
  if (argc != 2) {
    fprintf(stderr, "Specify a dictionary\n");
    return 0;
  }
  /*
   * Allocate a hash table to store the dictionary
   */
  fprintf(stderr, "Creating hashtable\n");
  dictionary = createHashTable(2255, &stringHash, &stringEquals);

  fprintf(stderr, "Loading dictionary %s\n", argv[1]);
  readDictionary(argv[1]);
  fprintf(stderr, "Dictionary loaded\n");

  fprintf(stderr, "Processing stdin\n");
  processInput();

  /* main in C should always return 0 as a way of telling
     whatever program invoked this that everything went OK
     */
  return 0;
}

/*
 * You need to define this function. void *s can be safely casted
 * to a char * (null terminated string) which is done for you here for
 * convenience.
 */
unsigned int stringHash(void *s) {
  char *string = (char *)s;
  unsigned int hash = 0;
  int i;
  for (i = 0; string[i] != '\0'; i++ ) {
    hash = 31 * hash + string[i];
  }
  return hash;
}

/*
 * You need to define this function.  It should return a nonzero
 * value if the two strings are identical (case sensitive comparison)
 * and 0 otherwise.
 */
int stringEquals(void *s1, void *s2) {
  char *string1 = (char *)s1;
  char *string2 = (char *)s2;
  return !strcmp(string1, string2);
}

/*
 * this function should read in every word in the dictionary and
 * store it in the dictionary.  You should first open the file specified,
 * then read the words one at a time and insert them into the dictionary.
 * Once the file is read in completely, exit.  You will need to allocate
 * (using malloc()) space for each word.  As described in the specs, you
 * can initially assume that no word is longer than 60 characters.  However,
 * for the final 20% of your grade, you cannot assumed that words have a bounded
 * length.  You can NOT assume that the specified file exists.  If the file does
 * NOT exist, you should print some message to standard error and call exit(0)
 * to cleanly exit the program.
 *
 * Since the format is one word at a time, with returns in between,
 * you can safely use fscanf() to read in the strings until you want to handle
 * arbitrarily long dictionary chacaters.
 */
void readDictionary(char *filename) {
  FILE *fp;
  char *mode = "r";
  char *word;
  int letterCount;
  fp = fopen(filename, mode);
  if (fp == NULL) {
    printf("File with name %s does not exist", (char *) filename);
    exit(0);
  }
  int maxLine = 61;
  int c;
  int newMax;
  while ((c = getc(fp)) != EOF) {
    word = (char *) malloc(maxLine);
    char *letter = word;
    letterCount = 0;
    while (c != '\n') {
      *letter = (char) c;
      letterCount += 1;
      if (letterCount == maxLine) {
        newMax = maxLine + 1;
        char *temp = (char *) realloc(word, newMax);
        word = temp;
        letter = word + (maxLine - 1);
        maxLine = newMax;
      }
      letter += 1;
      c = getc(fp);
    }
    *letter = '\0';
    insertData(dictionary, (void *) word, (void *) word);
  }
  fclose(fp);
}

/*
 * This should process standard input and copy it to standard output
 * as specified in specs.  EG, if a standard dictionary was used
 * and the string "this is a taest of  this-proGram" was given to
 * standard input, the output to standard output (stdout) should be
 * "this is a teast [sic] of  this-proGram".  All words should be checked
 * against the dictionary as they are input, again with all but the first
 * letter converted to lowercase, and finally with all letters converted
 * to lowercase.  Only if all 3 cases are not in the dictionary should it
 * be reported as not being found, by appending " [sic]" after the
 * error.
 *
 * Since we care about preserving whitespace, and pass on all non alphabet
 * characters untouched, and with all non alphabet characters acting as
 * word breaks, scanf() is probably insufficent (since it only considers
 * whitespace as breaking strings), so you will probably have
 * to get characters from standard input one at a time.
 *
 * As stated in the specs, you can initially assume that no word is longer than
 * 60 characters, but you may have strings of non-alphabetic characters (eg,
 * numbers, punctuation) which are longer than 60 characters. For the final 20%
 * of your grade, you can no longer assume words have a bounded length.
 */
void processInput() {
  int c;
  int maxLine = 61;
  int newMax;
  int letterCount;
  char *word1;
  while ((c = getc(stdin)) != EOF) {
    if (!isalpha(c)) {
      fprintf(stdout, "%c", (char) c);
    } else {
      word1 = (char *) malloc(maxLine);
      char *letter = word1;
      letterCount = 0;
      while (isalpha(c) ) {
        *letter = (char) c;
        letterCount += 1;
        if (letterCount == maxLine) {
          newMax = maxLine + 1;
          char *temp = (char *) realloc(word1, newMax);
          word1 = temp;
          letter = word1 + (maxLine - 1);
          maxLine = newMax;
        }
        letter += 1;
        c = getc(stdin);
      }
      *letter = '\0';
      char *word2 = malloc(maxLine);
      strncpy(word2, word1, strlen(word1) + 1);
      letter = word2;
      while (*letter != '\0') {
        *letter = tolower(*letter);
        letter++;
      }
      char *word3 = malloc(maxLine);
      strncpy(word3, word1, strlen(word1) + 1);
      letter = word3 + 1;
      while (*letter != '\0') {
        *letter = tolower(*letter);
        letter++;
      }
      fprintf(stdout, "%s", word1);
      if (findData(dictionary, (void *)word1) == NULL) {
        if (findData(dictionary, (void *)word2) == NULL) {
          if (findData(dictionary, (void *)word3) == NULL) {
            fprintf(stdout, " [sic]");
          }
        }
      }
      free(word1);
      free(word2);
      free(word3);
      if (c != EOF) {
        fprintf(stdout, "%c", (char) c);}
    }
  }
}
