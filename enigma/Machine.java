package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author David Oh
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotorSlots = new ArrayList<Rotor>();
        for (String rotorToUse : rotors) {
            boolean rotorContained = false;
            for (Rotor availableRotor : _allRotors) {
                if (availableRotor.name().equals(rotorToUse)) {
                    _rotorSlots.add(availableRotor);
                    rotorContained = true;
                }
            }
            if (!rotorContained) {
                throw new EnigmaException("Rotor from input not "
                        + "contained in available rotors from machine.");
            }
        }
        if (!_rotorSlots.get(0).reflecting()
                || _rotorSlots.get(0).rotates()) {
            throw new EnigmaException("Leftmost rotor must be "
                    + "a reflector and fixed.");
        }
        if (!_rotorSlots.get(_rotorSlots.size() - 1).rotates()) {
            throw new EnigmaException("Rightmost rotor must rotate.");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Settings length does not "
                    + "match the number of available rotors.");
        }
        for (int i = 0; i < setting.length(); i += 1) {
            _rotorSlots.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int last = numRotors() - 1;
        _rotorSlots.get(last).setToWillRotate();
        for (int i = 1; i < last; i += 1) {
            if (_rotorSlots.get(i).rotates()
                    && _rotorSlots.get(i + 1).atNotch()) {
                _rotorSlots.get(i).setToWillRotate();
            } else if (_rotorSlots.get(i).atNotch()
                    && _rotorSlots.get(i - 1).rotates()) {
                _rotorSlots.get(i).setToWillRotate();
            }
        }
        for (Rotor rotor : _rotorSlots) {
            if (rotor.getWillRotate()) {
                rotor.advance();
                rotor.resetWillRotate();
            }
        }
        int result = _plugboard.permute(c);
        int i = numRotors() - 1;
        while (i > 0 || !_rotorSlots.get(i).reflecting()) {
            result = _rotorSlots.get(i).convertForward(result);
            i -= 1;
        }
        while (i < numRotors()) {
            result = _rotorSlots.get(i).convertBackward(result);
            i += 1;
        }
        return _plugboard.permute(result);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int c = 0; c < msg.length(); c += 1) {
            char letter = msg.charAt(c);
            int indexOfLetter = _alphabet.toInt(letter);
            int newIndex = convert(indexOfLetter);
            char newLetter = _alphabet.toChar(newIndex);
            result += newLetter;
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots in machine.*/
    private int _numRotors;

    /** Number of pawls in machine. */
    private int _numPawls;

    /** Collection of all possible rotors. */
    private Collection<Rotor> _allRotors;

    /** Array of rotors in machine. */
    private ArrayList<Rotor> _rotorSlots;

    /** Instance variable for plugboard. */
    private Permutation _plugboard;
}
