package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author David Oh
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        int leftParenthesis = 0;
        int rightParenthesis = 0;
        for (int c = 0; c < cycles.length(); c += 1) {
            if (cycles.charAt(c) == '(') {
                leftParenthesis += 1;
            } else if (cycles.charAt(c) == ')') {
                rightParenthesis += 1;
            }
        }
        if (leftParenthesis != rightParenthesis) {
            throw new EnigmaException("Malformed cycles.");
        }
        String[] splitCycles = cycles.replaceAll(
                "\\s*", "").split("\\(|\\)");
        _allCycles = new String[splitCycles.length / 2];
        int j = 0;
        for (int k = 1; k < splitCycles.length; k += 2) {
            _allCycles[j] = splitCycles[k];
            j += 1;
        }
        for (String cycle : _allCycles) {
            for (int i = 0; i < cycle.length(); i += 1) {
                if (!_alphabet.contains(cycle.charAt(i))) {
                    throw new EnigmaException(
                            "Letter in cycle not in alphabet.");
                }
            }
        }
        _arrayOfLinkedCycles = new java.util.ArrayList<CharList>();
        for (String c : _allCycles) {
            if (c.length() > 1) {
                _arrayOfLinkedCycles.add(linkedCycleMaker(c));
            }
        }
        _permutation = constructPermutation();
        _invertedPermutation = constructInvertedPermutation();
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _arrayOfLinkedCycles.add(linkedCycleMaker(cycle));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int intMod = wrap(p);
        char letterOfAlphabet = _alphabet.toChar(intMod);
        return _alphabet.toInt(permute(letterOfAlphabet));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int intMod = wrap(c);
        char letterOfAlphabet = _alphabet.toChar(intMod);
        return _alphabet.toInt(invert(letterOfAlphabet));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        for (int i = 0; i < _permutation.length; i += 1) {
            if (p == _permutation[i]) {
                int index = _alphabet.toInt(p);
                return _permutation[index];
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (int i = 0; i < _invertedPermutation.length; i += 1) {
            if (c == _invertedPermutation[i]) {
                int index = _alphabet.toInt(c);
                return _invertedPermutation[index];
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int numberOfLettersInCycles = 0;
        for (String cycle : _allCycles) {
            numberOfLettersInCycles += cycle.length();
        }

        return numberOfLettersInCycles == size();
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** This method takes each string in the String array of cycles
     *  and constructs a cyclical linked list where each HEAD is is the
     *  character.
     *
     * @param s : a String representing a cycle
     * @return a char linked list*/
    public CharList linkedCycleMaker(String s) {
        CharList last = new CharList(s.charAt(0), null);
        CharList first = last;
        int k = 1;
        int i = 1;
        while (k < s.length()) {
            first = new CharList(s.charAt(k), first);
            k += 1;
        }
        last.tail = first;
        return first;
    }

    /** This method uses all of the linked cycles to create
     *  a charArray of the new permutation, where each permuted
     *  character is in the index of the original letter in the
     *  alphabet. Returns the CharList.*/
    char[] constructPermutation() {
        char[] permutation = new char[size()];
        for (CharList c : _arrayOfLinkedCycles) {
            CharList last = c;
            char currentLetter = last.tail.head;
            char mapTo = last.head;
            int index = _alphabet.toInt(currentLetter);
            permutation[index] = mapTo;
            CharList pointer = c.tail;
            while (pointer != last) {
                currentLetter = pointer.tail.head;
                mapTo = pointer.head;
                index = _alphabet.toInt(currentLetter);
                permutation[index] = mapTo;
                pointer = pointer.tail;
            }
        }
        return permutation;
    }

    /** This method uses all of the linked cycles to create
     *  a charArray of the new inverted permutation, where each
     *  permuted character is in the index of the original letter in the
     *  alphabet. Returns the CharList.*/
    char[] constructInvertedPermutation() {
        char[] invertedPerm = new char[size()];
        for (CharList c : _arrayOfLinkedCycles) {
            CharList last = c;
            char currentLetter = last.head;
            char mapTo = last.tail.head;
            int index = _alphabet.toInt(currentLetter);
            invertedPerm[index] = mapTo;
            CharList pointer = c.tail;
            while (pointer != last) {
                currentLetter = pointer.head;
                mapTo = pointer.tail.head;
                index = _alphabet.toInt(currentLetter);
                invertedPerm[index] = mapTo;
                pointer = pointer.tail;
            }
        }
        return invertedPerm;
    }

    /** Instance variable String array of all the cycles. */
    private String[] _allCycles;

    /** Instance variable _permutation is a char array where each permuted
     *  letter is indexed by the original letter's index in alphabet. */
    private char[] _permutation;

    /** Instance variable _invertedPermutation is a char array where each
     *  inverted letter is indexed by the original letter's index in
     *  alphabet. */
    private char[] _invertedPermutation;

    /** ArrayList of type char contains linked list representation of each
     *  cycle. */
    private java.util.ArrayList<CharList> _arrayOfLinkedCycles;

    /** Nested class CharList for the linked list implementation of cycles. */
    public class CharList {

        /** CharList constructor.
         * @param c : character
         * @param l : CharList */
        CharList(char c, CharList l) {
            head = c;
            tail = l;
        }
        /** Default CharList constructor. */
        CharList() { }

        /** Head of linked list holds a character value. */
        private char head;

        /** Tail of linked list is another char linked list. */
        private CharList tail;
    }

}


