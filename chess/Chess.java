package chess;

import java.util.ArrayList;
import chess.ReturnPlay.Message;

class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank;  // 1..8
	public String toString() {
		return ""+pieceFile+pieceRank+":"+pieceType;
	}
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece)other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

public class Chess {
	
	enum Player { white, black }
	static Player player;
 	static ArrayList<ReturnPiece> pieces;
	
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
	
		ReturnPlay ret = new ReturnPlay();

		//Checks for resign first
		if(move.equals("resign")){
			if(player == Player.black) ret.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
			else ret.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
		}

		// Split move into starting position and ending position
		// Set curPiece to the piece at played position and plays the move
		String subMove[] = move.split(" ");
		Piece curPiece = null;
		for(ReturnPiece it : pieces){
			if(""+it.pieceFile+it.pieceRank == subMove[0]){
				pieces.remove(it);
				curPiece = (Piece)it;
			}
		}
		if(!curPiece.equals(null)) {
			pieces.add(curPiece.move(subMove[1]));
		} 

		// If there is an additional message in the move (draw or promotion)
		if(subMove.length == 3){
			if(subMove[2].equals("draw?")){
				ret.message = ReturnPlay.Message.DRAW;
			}
		}
		
		if(player == Player.white) player = Player.white;
		else player = Player.white;
		return ret;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		String board[][] = PlayChess.makeBlankBoard();
		pieces = new ArrayList<ReturnPiece>();

		//Fill board with starting positions for pieces 
		for(Player player : Player.values()){
			pieces.add(new King(player));
			pieces.add(new Queen(player));

			for(int i = 0; i < 2; i++){
				pieces.add(new Rook(player, i));
				pieces.add(new Bishop(player, i));
				pieces.add(new Knight(player, i));
			}

			for(int i = 0; i < 8; i++){
				pieces.add(new Pawn(player, i));
			}
		}

		// 1st turn is always white
		player = Player.white;

		System.out.println("Calling printBoard:\n");
		PlayChess.printBoard(pieces);
		System.out.println("Calling printPiecesOnBoard:\n");
		PlayChess.printPiecesOnBoard(pieces, board);
	}
}
