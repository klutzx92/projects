package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author David Oh
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _currentSetting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _currentSetting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _currentSetting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _currentSetting = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int contactEntered = _permutation.wrap(p + _currentSetting);
        int translatedAccordingToPerm = _permutation.permute(contactEntered);
        int positionExited = _permutation.wrap(
                translatedAccordingToPerm - _currentSetting);
        return positionExited;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int contactEntered = _permutation.wrap(e + _currentSetting);
        int translatedAccordingToPerm = _permutation.invert(contactEntered);
        int positionExited = _permutation.wrap(
                translatedAccordingToPerm - _currentSetting);
        return positionExited;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** My current setting. */
    private int _currentSetting;

    /** Boolean check to see if rotor will rotate
     *  after each key input and before advancing. */
    private boolean _willRotate = false;

    /** Return true if rotor set to rotate. */
    boolean getWillRotate() {
        return _willRotate;
    }

    /** Prepare rotor to rotate. */
    void setToWillRotate() {
        _willRotate = true;
    }

    /** Reset rotor to not rotate. */
    void resetWillRotate() {
        _willRotate = false;
    }
}
