
#include "hashtable.h"
#include <stdlib.h>

/*
 * creates a hashtable
 */
HashTable *createHashTable(int size, unsigned int (*hashFunction)(void *),
                           int (*equalFunction)(void *, void *)) {
  int i = 0;
  HashTable *newTable = malloc(sizeof(HashTable));
  newTable->size = size;
  newTable->data = malloc(sizeof(struct HashBucket *) * size);
  for (i = 0; i < size; ++i) {
    newTable->data[i] = NULL;
  }
  newTable->hashFunction = hashFunction;
  newTable->equalFunction = equalFunction;
  return newTable;
}

/*
 * This inserts a bit of data and key into a hashtable.  To use this
 * to store strings, simply cast a char * to a void *.  EG, to store
 * the string refered to by the declaration char *string, you would
 * call insertData(someHashTable, (void *) string, (void *) string);
 * if you wanted to use the string both as data and as the key
 * (such as in the philspel project)
 */
void insertData(HashTable *table, void *key, void *data) {
  // HINT:
  // 1. find the right hash bucket location with table->hashFunction.
  // 2. allocate a new hash bucket struct.
  // 3. add to the head of the hash bucket list.

  unsigned int hash = table->hashFunction(key) % table->size;
  struct HashBucket *newBucket = (struct HashBucket *) malloc(sizeof(struct HashBucket)); //allocate space
  newBucket->key = key;
  newBucket->data = data;
  newBucket->next = table->data[hash];
  table->data[hash] = newBucket;
}

/*
 * This takes a key and returns the corresponding data to that key,
 * or NULL if the key was not found.
 */
void *findData(HashTable *table, void *key) {
  // HINT:
  // 1. find the right hash bucket with table->hashFunction.
  // 2. walk the bucket list and check equality with table->equalFunction.
  unsigned int hash = table->hashFunction(key) % table->size;
  struct HashBucket *ptr = table->data[hash];
  for (ptr = table->data[hash]; ptr != NULL; ptr = ptr->next) {
    if (table->equalFunction(key, ptr->data)) {
      return ptr->data;
    }
  }
  return NULL;
}
