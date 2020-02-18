package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author David Oh
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        while (_input.hasNext()) {
            String settingLine = _input.nextLine();
            while (settingLine.equals("")) {
                _output.println();
                settingLine = _input.nextLine();
            }
            if (settingLine.charAt(0) != '*') {
                throw new EnigmaException("Incorrect settings line.");
            }
            settingLine = settingLine.substring(2);
            String[] settings = settingLine.split("\\s");
            int count = 0;
            for (int i = 0; i < settings.length; i += 1) {
                if (!(settings[i].contains("(") || settings[i].contains(")"))) {
                    count += 1;
                }
            }
            int numberOfInputRotors = count - 1;
            settingPresent(m.numRotors(), settingLine);
            numberOfRotorsTest(numberOfInputRotors, m);
            String [] rotorNames = rotorNames(numberOfInputRotors, m, settings);
            m.insertRotors(rotorNames);
            _setting = settings[numberOfInputRotors];
            checkSettingLength(_setting, m);
            for (int i = 0; i < _setting.length(); i += 1) {
                if (!_alphabet.contains(_setting.charAt(i))) {
                    throw new EnigmaException("Setting characters "
                            + "not found in alphabet.");
                }
            }
            m.setRotors(_setting);
            String plug = "";
            for (int k = numberOfInputRotors + 1; k < settings.length; k += 1) {
                plug += settings[k];
            }
            m.setPlugboard(new Permutation(plug, _alphabet));
            while (_input.hasNext() && !_input.hasNext("\\*")) {
                String eachLine = _input.nextLine();
                if (eachLine.equals("")) {
                    _output.println();
                } else {
                    String removedSpaces = eachLine.replaceAll("\\s", "");
                    for (int i = 0; i < removedSpaces.length(); i += 1) {
                        if (!_alphabet.contains(removedSpaces.charAt(i))) {
                            throw new EnigmaException("Message contains "
                                    + "characters not found in alphabet.");
                        }
                    }
                    String out = m.convert(removedSpaces);
                    printMessageLine(out);
                }
            }
            if (!_input.hasNext() && _input.hasNextLine()) {
                _output.println();
            }
        }
    }

    /** Check to see if initial setting token is present.
     * @param numRotors : Number of rotors read from input.
     * @param s : setting line. */
    void settingPresent(int numRotors, String s) {
        String[] arr = s.split("\\s");
        String settingLine = "";
        for (int i = 0; i < arr.length; i += 1) {
            if (!arr[i].contains("(")) {
                settingLine += arr[i];
                settingLine += " ";
            }
        }
        String[] withoutPerms = settingLine.split("\\s");
        if (numRotors != withoutPerms.length - 1) {
            throw new EnigmaException("Settings line has wrong number of "
                    + "arguments.");
        }
    }

    /** Check to see that length of SETTING matches the number of rotors
     *  not including the reflector.
     *  @param setting : Setting token.
     *  @param m : Machine used for translation. */
    void checkSettingLength(String setting, Machine m) {
        if (setting.length() != m.numRotors() - 1) {
            throw new EnigmaException("Incorrect number of letters "
                    + "in setting.");
        }
    }

    /** Method creates array of rotor names and checks if there are
     * any duplicates.
     * @param numRotors : Number of Rotors from input file.
     * @param m : Machine used for translation.
     * @param settings : String of the settings line.
     * @return : String array of rotor names. */
    String[] rotorNames(int numRotors, Machine m, String[] settings) {
        String[] rotorNames = new String[m.numRotors()];
        for (int i = 0; i < numRotors; i += 1) {
            rotorNames[i] = settings[i];
        }
        for (int i = 0; i < rotorNames.length; i += 1) {
            for (int j = i + 1; j < rotorNames.length; j += 1) {
                if (rotorNames[i].equals(rotorNames[j])) {
                    throw new EnigmaException("Can't have duplicate "
                            + "rotors in slots.");
                }
            }
        }
        return rotorNames;
    }

    /** Check to see if number of Rotors match.
     * @param numRotorInput : number of rotors to be used read from input file.
     * @param m : machine used for translation. */
    void numberOfRotorsTest(int numRotorInput, Machine m) {
        if (numRotorInput != m.numRotors()) {
            throw new EnigmaException("Settings line has wrong number of "
                    + "arguments.");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _allRotors = new ArrayList<Rotor>();
            if (_config.hasNext()) {
                String alpha = _config.next();
                _alphabet = new Alphabet(alpha);
            }
            if (_config.hasNextInt()) {
                try {
                    _numRotors = Integer.parseInt(_config.next());
                    _numPawls = Integer.parseInt(_config.next());
                } catch (NumberFormatException excp) {
                    throw new EnigmaException("Missing alphabet.");
                }
                if (_numRotors <= _numPawls) {
                    throw new EnigmaException("Wrong number of rotors/pawls");
                }
            } else {
                throw new EnigmaException("Incorrect configuration file.");
            }
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, _numRotors, _numPawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String typeAndNotches = _config.next();
            String permutationCycles = "";
            while (_config.hasNext("\\(\\w+.*\\)")) {
                permutationCycles += _config.next();
            }
            Permutation newPerm = new Permutation(permutationCycles, _alphabet);
            if (typeAndNotches.charAt(0) == 'M') {
                String notches = typeAndNotches.substring(1);
                _newRotor = new MovingRotor(name, newPerm, notches);
            } else if (typeAndNotches.charAt(0) == 'N') {
                _newRotor = new FixedRotor(name, newPerm);
            } else {
                _newRotor = new Reflector(name, newPerm);
            }
            return _newRotor;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String outputMessage = msg;
        _output.print(outputMessage.charAt(0));
        for (int i = 1; i < outputMessage.length(); i += 1) {
            if (i % 5 == 0) {
                _output.print(" ");
            }
            _output.print(outputMessage.charAt(i));
        }
        if (_input.hasNextLine()) {
            _output.print("\n");
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** A new rotor that will be returned by the readRotor method. */
    private Rotor _newRotor;

    /** An ArrayList of Rotors that are available to machine. */
    private ArrayList<Rotor> _allRotors;

    /** Number of rotors from config file. */
    private int _numRotors;

    /** Number of pawls from config file. */
    private int _numPawls;

    /** String of letters indicating initial setting of rotors. */
    private String _setting;

    /** String of plugboards from input file. */
    private String _plugboard = "";
}
