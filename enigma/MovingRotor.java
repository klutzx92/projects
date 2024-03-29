package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author David Oh
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        String currentLetterSetting = "";
        currentLetterSetting += alphabet().toChar(setting());
        if (_notches.contains(currentLetterSetting)) {
            return true;
        }
        return false;
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    /** Instance variable is a String representing notch locations. */
    private String _notches;
}
