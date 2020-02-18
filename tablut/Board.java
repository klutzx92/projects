package tablut;

import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/** The state of a Tablut Game.
 *  @author David Oh
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        _board = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int y = 0; y < model._board.length; y += 1) {
            for (int x = 0; x < model._board[y].length; x += 1) {
                _board[x][y] = model._board[x][y];
            }
        }
        _turn = model._turn;
        _winner = model._winner;
        _repeated = model._repeated;
        _moveLimit = model._moveLimit;
        _moveCount = model._moveCount;
        _movedPieces = new Stack<Piece>();
        _movedPieces.addAll(model._movedPieces);
        _movedSquares = new Stack<Square>();
        _movedSquares.addAll(model._movedSquares);
        _previousPositions = new Stack<String>();
        _previousPositions.addAll(model._previousPositions);
    }

    /** Clears the board to the initial position. */
    void init() {
        _board = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int y = 0; y < _board.length; y += 1) {
            for (int x = 0; x < _board[y].length; x += 1) {
                _board[x][y] = EMPTY;
            }
        }
        for (Square sq : INITIAL_ATTACKERS) {
            int x = sq.col();
            int y = sq.row();
            _board[x][y] = BLACK;
        }
        for (Square sq : INITIAL_DEFENDERS) {
            int x = sq.col();
            int y = sq.row();
            _board[x][y] = WHITE;
        }
        _winner = null;
        _moveLimit = 1000;
        _moveCount = 0;
        _board[THRONE.col()][THRONE.row()] = KING;
        _turn = BLACK;
        _movedPieces = new Stack<Piece>();
        _movedSquares = new Stack<Square>();
        _previousPositions = new Stack<String>();
        _previousPositions.push(encodedBoard());
    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount(). */
    void setMoveLimit(int lim) {
        _moveLimit = lim;
        if (2 * _moveLimit <= moveCount()) {
            throw new IllegalArgumentException("Move limit is too low");
        }
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        String currentPosition = encodedBoard();
        if (_previousPositions.contains(currentPosition)) {
            _winner = _turn;
            _repeated = true;
        }
        _previousPositions.push(currentPosition);
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        Square locationOfKing = null;
        for (int y = 0; y < _board.length; y += 1) {
            for (int x = 0; x < _board[y].length; x += 1) {
                if (get(x, y) == KING) {
                    locationOfKing = sq(x, y);
                }
            }
        }
        return locationOfKing;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        int x = s.col();
        int y = s.row();
        _board[x][y] = p;
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        put(p, s);
        _movedPieces.push(p);
        _movedSquares.push(s);
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        boolean blocked = false;
        int dir = from.direction(to);
        if (get(to) != EMPTY) {
            return false;
        } else if (to == THRONE && get(from) != KING) {
            return false;
        }
        if (from.isRookMove(to)) {
            int i = 1;
            while (from.rookMove(dir, i) != to) {
                int x = from.rookMove(dir, i).col();
                int y = from.rookMove(dir, i).row();
                if (get(x, y) != EMPTY) {
                    blocked = true;
                }
                i += 1;
            }
        }
        return !blocked;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        boolean b1 = get(to) == EMPTY;
        boolean b2 = isUnblockedMove(from, to);
        boolean b3 = winner() == null;
        return b1 && b2 && b3;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        Piece pieceToMove = get(from);
        revPut(EMPTY, from);
        revPut(pieceToMove, to);
        int d = 0;
        while (d < 4) {
            Square twoSquaresOver = to.rookMove(d, 2);
            if (twoSquaresOver != null) {
                capture(to, twoSquaresOver);
            }
            d += 1;
        }
        if (!hasMove(other(turn()))) {
            _winner = turn();
        }
        if (kingPosition() == null) {
            _winner = BLACK;
        } else if (kingPosition().isEdge()) {
            _winner = WHITE;
        }
        _moveCount += 1;

        _turn = other(_turn);
        checkRepeated();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        Piece pieceMoved = get(sq0);
        Piece pieceTwo = get(sq2);
        Square squareBetween = sq0.between(sq2);
        Piece pieceBetween = get(squareBetween);
        if (pieceMoved.side() == turn()) {
            if (pieceBetween == KING && (kingPosition() == THRONE
                    || kingPosition() == NTHRONE
                    || kingPosition() == WTHRONE
                    || kingPosition() == ETHRONE
                    || kingPosition() == STHRONE)) {
                int surroundedBy = 0;
                for (int d = 0; d < 4; d += 1) {
                    Square squareAround = squareBetween.rookMove(d, 1);
                    if (get(squareAround) == KING.opponent()
                            || (get(squareAround) == EMPTY
                            && squareAround == THRONE)) {
                        surroundedBy += 1;
                    }
                }
                if (surroundedBy == 4) {
                    revPut(EMPTY, squareBetween);
                    _winner = BLACK;
                }
            } else if ((pieceMoved.side() == pieceTwo.side()
                    && pieceBetween == pieceMoved.opponent())
                    || (pieceTwo == EMPTY && sq2 == THRONE)) {
                revPut(EMPTY, squareBetween);
            } else if ((pieceMoved.side() == BLACK)
                    && pieceTwo.side() == BLACK && pieceBetween == KING) {
                revPut(EMPTY, squareBetween);
                _winner = BLACK;
            }
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            if (!_movedPieces.empty() && !_movedSquares.empty()) {
                Piece nextOffPieceStack = _movedPieces.pop();
                Square nextOffSquareStack = _movedSquares.pop();
                if (nextOffPieceStack == EMPTY) {
                    while (nextOffPieceStack == EMPTY) {
                        put(turn(), nextOffSquareStack);
                        nextOffPieceStack = _movedPieces.pop();
                        nextOffSquareStack = _movedSquares.pop();
                    }
                }
                Piece movedPiece = nextOffPieceStack;
                Square movedTo = nextOffSquareStack;
                Piece shouldBeEmpty = _movedPieces.pop();
                Square movedFrom = _movedSquares.pop();
                put(movedPiece, movedFrom);
                put(shouldBeEmpty, movedTo);
                _turn = turn().opponent();
                _moveCount -= 1;
            }
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        if (!_repeated || moveCount() > 0) {
            _previousPositions.pop();
        }
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _previousPositions.clear();
        _previousPositions.push(encodedBoard());
        _movedPieces.clear();
        _movedSquares.clear();
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        HashSet<Square> pieceLocations = pieceLocations(side);
        List<Move> listOfLegalMoves = new java.util.ArrayList<Move>();

        for (Square pieceLocation : pieceLocations) {
            int d = 0;
            while (d < 4) {
                int i = 1;
                Square possibleToSquare = pieceLocation.rookMove(d, i);
                while (possibleToSquare != null
                        && get(possibleToSquare) == EMPTY) {
                    if (isLegal(pieceLocation, possibleToSquare)) {
                        listOfLegalMoves.add(mv(pieceLocation,
                                possibleToSquare));
                    }
                    i += 1;
                    possibleToSquare = pieceLocation.rookMove(d, i);
                }
                d += 1;
            }
        }
        return listOfLegalMoves;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return legalMoves(side).size() != 0;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> sidePositions = new HashSet<>();
        for (int y = 0; y < _board.length; y += 1) {
            for (int x = 0; x < _board[y].length; x += 1) {
                if (get(x, y).side() == side.side()) {
                    sidePositions.add(sq(x, y));
                }
            }
        }
        return sidePositions;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** Move limit. */
    private int _moveLimit;

    /** Returns the other side.
     * @param side : Piece color.*/
    private Piece other(Piece side) {
        return side.opponent();
    }

    /** The state of the board represented as a two-dimensional array of
     * Pieces. */
    private Piece[][] _board;

    /** Record of all previous positions of the board.*/
    private Stack<String> _previousPositions;

    /** Stack of moved pieces used for undoing.*/
    private Stack<Piece> _movedPieces;

    /** Stack of moved to/from squares used for undoing.*/
    private Stack<Square> _movedSquares;
}
