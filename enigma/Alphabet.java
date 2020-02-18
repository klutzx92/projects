package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author David Oh
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        if (chars.contains("(") || chars.contains(")")
                || chars.contains("*")) {
            throw new EnigmaException("Alphabet contains "
                    + "incorrect letters.");
        }
        _alphabetArray = chars.toCharArray();
        for (int i = 0; i < _alphabetArray.length; i += 1) {
            for (int j = i + 1; j < _alphabetArray.length; j += 1) {
                if (_alphabetArray[i] == _alphabetArray[j]) {
                    throw new EnigmaException("Alphabet can't "
                            + "have duplicate letters.");
                }
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabetArray.length;
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        boolean contained = false;
        for (int j = 0; j < _alphabetArray.length; j += 1) {
            if (_alphabetArray[j] == ch) {
                contained = true;
            }
        }
        return contained;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabetArray[index];
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < _alphabetArray.length; i += 1) {
            if (_alphabetArray[i] == ch) {
                return i;
            }
        }
        return -1;
    }

    /** Instance variable char array of letters in alphabet. */
    private char[] _alphabetArray;
}
