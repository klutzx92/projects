package tablut;

import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author David Oh
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move mv = findMove();
        System.out.println("* " + mv);
        _controller.reportMove(mv);
        return mv.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        if (myPiece() == BLACK) {
            findMove(b, maxDepth(board()), true, -1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(board()), true, 1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        int bestValue = -INFTY * sense;
        int moveValue;

        Piece turn;
        if (sense == 1) {
            turn = WHITE;
        } else {
            turn = BLACK;
        }

        java.util.Iterator<Move> moveIt = board.legalMoves(
                turn).iterator();
        while (moveIt.hasNext()) {
            Move possibleMove = moveIt.next();
            board.makeMove(possibleMove);
            moveValue = findMove(board, depth - 1, false,
                    sense * -1, alpha, beta);
            board.undo();
            if (moveValue * sense >= bestValue * sense) {
                bestValue = moveValue;
                if (saveMove) {
                    _lastFoundMove = possibleMove;
                }
                if (sense == 1) {
                    alpha = Integer.max(alpha, moveValue);
                } else {
                    beta = Integer.min(beta, moveValue);
                }
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestValue;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        int x = 10;
        if (board.moveCount() < x) {
            return 1;
        } else if (board.moveCount() < x + x) {
            return 2;
        } else if (board.moveCount() < x + x + x) {
            return 3;
        } else {
            return 4;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        int numberOfWhite = board.pieceLocations(WHITE).size();
        int numberOfBlack = board.pieceLocations(BLACK).size();
        int score = numberOfWhite - numberOfBlack;
        Square kingLocation = board.kingPosition();
        if (board.winner() == BLACK) {
            return -WINNING_VALUE;
        } else if (board.winner() == WHITE) {
            return WINNING_VALUE;
        }
        if (kingLocation == null) {
            return -WILL_WIN_VALUE;
        } else if (kingLocation.isEdge()) {
            return WILL_WIN_VALUE;
        }
        return score;
    }
}
