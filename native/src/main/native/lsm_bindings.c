#include <jni.h>

// static_assert
#include <assert.h>
// SQLite 4 header
#include "lsm.h"
// JNI generated header
#include "lsm_bindings.h"

#ifndef static_assert
#error Macro static_assert cannot be found, consider compiling the file as a C11 code
#endif

static_assert(org_sqlite_sqlite4_lsm_raw_Cursor_SEEK_LEFAST == LSM_SEEK_LEFAST, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Cursor_SEEK_LE == LSM_SEEK_LE, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Cursor_SEEK_EQ == LSM_SEEK_EQ, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Cursor_SEEK_GE == LSM_SEEK_GE, "");

static_assert(org_sqlite_sqlite4_lsm_raw_Database_SAFETY_OFF == LSM_SAFETY_OFF, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_SAFETY_NORMAL == LSM_SAFETY_NORMAL, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_SAFETY_FULL == LSM_SAFETY_FULL, "");

static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_AUTOFLUSH == LSM_CONFIG_AUTOFLUSH, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_PAGE_SIZE == LSM_CONFIG_PAGE_SIZE, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_SAFETY == LSM_CONFIG_SAFETY, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_BLOCK_SIZE == LSM_CONFIG_BLOCK_SIZE, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_AUTOWORK == LSM_CONFIG_AUTOWORK, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_MMAP == LSM_CONFIG_MMAP, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_USE_LOG == LSM_CONFIG_USE_LOG, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_AUTOMERGE == LSM_CONFIG_AUTOMERGE, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_MAX_FREELIST == LSM_CONFIG_MAX_FREELIST, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_MULTIPLE_PROCESSES == LSM_CONFIG_MULTIPLE_PROCESSES, "");
static_assert(org_sqlite_sqlite4_lsm_raw_Database_CONFIG_AUTOCHECKPOINT == LSM_CONFIG_AUTOCHECKPOINT, "");

#define JLONG_TO_DB(value) ((lsm_db *)((void *)value))
#define JLONG_TO_CURSOR(value) ((lsm_cursor *)((void *)value))
#define JLONG_TO_ENV(value) ((lsm_env *)((void *)value))

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    open
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_open
  (JNIEnv *env, jclass c, jlong database, jlong cursorPointer)
{
    lsm_cursor **outCursor = (lsm_cursor **)((void *)cursorPointer);
    return (jint)lsm_csr_open(JLONG_TO_DB(database), outCursor);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    close
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_close
  (JNIEnv *env, jclass c, jlong cursor)
{
    return (jint)lsm_csr_close(JLONG_TO_CURSOR(cursor));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    seek
 * Signature: (JJII)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_seek
  (JNIEnv *env, jclass c, jlong cursor, jlong keyAddress, jint keySize, jint seekMode)
{
    return (jint)lsm_csr_seek(JLONG_TO_CURSOR(cursor), (void *)keyAddress, keySize, seekMode);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    first
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_first
  (JNIEnv *env, jclass c, jlong cursor)
{
    return (jint)lsm_csr_first(JLONG_TO_CURSOR(cursor));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    last
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_last
  (JNIEnv *env, jclass c, jlong cursor)
{
    return (jint)lsm_csr_last(JLONG_TO_CURSOR(cursor));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    next
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_next
  (JNIEnv *env, jclass c, jlong cursor)
{
    return (jint)lsm_csr_next(JLONG_TO_CURSOR(cursor));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    prev
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_prev
  (JNIEnv *env, jclass c, jlong cursor)
{
    return (jint)lsm_csr_prev(JLONG_TO_CURSOR(cursor));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    valid
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_valid
  (JNIEnv *env, jclass c, jlong cursor)
{
    return (jint)lsm_csr_valid(JLONG_TO_CURSOR(cursor));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    key
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_key
  (JNIEnv *env, jclass c, jlong cursor, jlong keyAddressPointer, jlong keySizePointer)
{
    /*
     * We can safely coerce a native pointer to any type to a jlong value
     * on both 32-bit and 64-bit platforms, and vice versa.
     * In worst case one half of a jlong value will be unused, even though in contains a random garbage value.
     * This also means that we can safely coerce a pointer to pointer to a jlong value.
     * This does not apply to a native int value: it might be 64-bit and jint is always 32-bit.
     * We have to use a local variable of exact native size if we want to modify it by a pointer in a native SQLite call.
     */
    const void **outKeyAddress = (const void **)keyAddressPointer;
    jint *outKeySize = (jint *)keySizePointer;
    int keySize;
    int result = lsm_csr_key(JLONG_TO_CURSOR(cursor), outKeyAddress, &keySize);
    *outKeySize = (jint)keySize;
    return result;
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    value
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_value
  (JNIEnv *env, jclass c, jlong cursor, jlong valueAddressPointer, jlong valueSizePointer)
{
    const void **outValueAddress = (const void **)valueAddressPointer;
    jint *outValueSize = (jint *)valueSizePointer;
    int valueSize;
    int result = lsm_csr_value(JLONG_TO_CURSOR(cursor), outValueAddress, &valueSize);
    *outValueSize = (jint)valueSize;
    return result;
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Cursor
 * Method:    cmp
 * Signature: (JJIJ)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Cursor_cmp
  (JNIEnv *env, jclass c, jlong cursor, jlong keyAddress, jint keySize, jlong resultPointer)
{
    jint *outResult = (jint *)resultPointer;
    int cmpResult;
    int callResult = lsm_csr_cmp(JLONG_TO_CURSOR(cursor), (void *)keyAddress, keySize, &cmpResult);
    *outResult = (jint)cmpResult;
    return callResult;
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    _new
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database__1new
  (JNIEnv *env, jclass c, jlong environment, jlong databaseReference)
{
    lsm_db **outDatabase = (lsm_db **)(void *)databaseReference;
    return (jint)lsm_new(JLONG_TO_ENV(environment), outDatabase);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    open
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_open
  (JNIEnv *env, jclass c, jlong database, jlong filenameAddress)
{
    const char *filename = (const char *)((void *)filenameAddress);
    return (jint)lsm_open(JLONG_TO_DB(database), filename);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    close
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_close
  (JNIEnv *env, jclass c, jlong database)
{
    return (jint)lsm_close(JLONG_TO_DB(database));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    config
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_config
  (JNIEnv *env, jclass c, jlong database, jint paramId, jlong paramReference)
{
    void *outParam = (void *)paramReference;
    return (int)lsm_config(JLONG_TO_DB(database), (int)paramId, outParam);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Environment
 * Method:    getEnvironment
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_sqlite_sqlite4_lsm_raw_Environment_getEnvironment
  (JNIEnv *env, jclass c, jlong database)
{
    return (jlong)lsm_get_env(JLONG_TO_DB(database));
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Environment
 * Method:    getDefaultEnvironment
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_sqlite_sqlite4_lsm_raw_Environment_getDefaultEnvironment
  (JNIEnv *env, jclass c)
{
    return (jlong)lsm_default_env();
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Lsm
 * Method:    malloc
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_sqlite_sqlite4_lsm_raw_Lsm_malloc
  (JNIEnv *env, jclass c, jlong environment, jlong size)
{
    return (jlong)lsm_malloc(JLONG_TO_ENV(environment), (size_t)size);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Lsm
 * Method:    realloc
 * Signature: (JJJ)J
 */
JNIEXPORT jlong JNICALL Java_org_sqlite_sqlite4_lsm_raw_Lsm_realloc
  (JNIEnv *env, jclass c, jlong environment, jlong memoryAddress, jlong size)
{
    return (jlong)lsm_realloc(JLONG_TO_ENV(environment), (void *)memoryAddress, (size_t)size);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Lsm
 * Method:    free
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_org_sqlite_sqlite4_lsm_raw_Lsm_free
  (JNIEnv *env, jclass c, jlong environment, jlong memoryAddress)
{
    return lsm_free(JLONG_TO_ENV(environment), (void *)memoryAddress);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    begin
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_begin
  (JNIEnv *env, jclass c, jlong database, jint level)
{
    return (jint)lsm_begin(JLONG_TO_DB(database), level);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    commit
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_commit
  (JNIEnv *env, jclass c, jlong database, jint level)
{
    return (jint)lsm_commit(JLONG_TO_DB(database), level);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    rollback
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_rollback
  (JNIEnv *env, jclass c, jlong database, jint level)
{
    return (jint)lsm_rollback(JLONG_TO_DB(database), level);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    insert
 * Signature: (JJIJI)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_insert
  (JNIEnv *env, jclass c, jlong database, jlong keyAddress, jint keySize, jlong valueAddress, jint valueSize)
{
    const void *pKey = (const void *)keyAddress;
    const void *pValue = (const void *)valueAddress;
    return (jint)lsm_insert(JLONG_TO_DB(database), pKey, keySize, pValue, valueSize);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    delete
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_delete
  (JNIEnv *env, jclass c, jlong database, jlong keyAddress, jint keySize)
{
    return (jint)lsm_delete(JLONG_TO_DB(database), (const void *)keyAddress, keySize);
}

/*
 * Class:     org_sqlite_sqlite4_lsm_raw_Database
 * Method:    deleteRange
 * Signature: (JJIJI)I
 */
JNIEXPORT jint JNICALL Java_org_sqlite_sqlite4_lsm_raw_Database_deleteRange
  (JNIEnv *env, jclass c, jlong database, jlong key1Address, jint key1Size, jlong key2Address, jint key2Size)
{
    const void *pKey1 = (const void *)key1Address;
    const void *pKey2 = (const void *)key2Address;
    return (jint)lsm_delete_range(JLONG_TO_DB(database), pKey1, key1Size, pKey2, key2Size);
}