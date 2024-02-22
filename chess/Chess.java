package chess;

import java.util.ArrayList;
import java.lang.NumberFormatException;

import chess.ReturnPiece.PieceFile;
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
			return ret;
		}

		// Split move into starting position and ending position
		// Set curPiece to the piece at played position and plays the move
		String subMove[] = move.split(" ");
		Piece curPiece = null;
		for(ReturnPiece it : pieces){
			if(subMove[0].equals(""+it.pieceFile+it.pieceRank)){
				curPiece = (Piece)it;
				if((it.pieceType.ordinal() <= 5 && player != Player.white) ||
						(it.pieceType.ordinal() >5 && player != Player.black)){
					System.out.println("\nError: Wrong color piece was attempted to move");
					ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
					ret.piecesOnBoard = pieces;
					return ret;
				}
			}

			
		}
		if(curPiece == null){
			ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
			ret.piecesOnBoard = pieces;
			System.out.println("Error: Empty square was attempted to move");
			return ret;
		}
		if(curPiece.move(subMove[1])){
			for(ReturnPiece it : pieces){
				if ((""+it.pieceFile+it.pieceRank).equals(subMove[1])) {
					pieces.remove(it);
					break;
				}
				
			}
			
			try {
				String newRank = subMove[1].replaceAll("[^0-9]", "");
				curPiece.pieceRank = Integer.parseInt(newRank);
			} 
			catch (NumberFormatException e) {
				System.out.println("Error: New Rank is not valid");
			}
			
			switch (subMove[1].charAt(0)) {
				case 'a':
					curPiece.pieceFile = PieceFile.a;
					break;
				case 'b':
					curPiece.pieceFile = PieceFile.b;
					break;
				case 'c':
					curPiece.pieceFile = PieceFile.c;
					break;
				case 'd':
					curPiece.pieceFile = PieceFile.d;
					break;
				case 'e':
					curPiece.pieceFile = PieceFile.e;
					break;
				case 'f':
					curPiece.pieceFile = PieceFile.f;
					break;
				case 'g':
					curPiece.pieceFile = PieceFile.g;
					break;
				case 'h':
					curPiece.pieceFile = PieceFile.h;
					break;
			}
		}
		

		// If there is an additional message in the move (draw or promotion)
		if(subMove.length == 3 && subMove[2].equals("draw?")) ret.message = ReturnPlay.Message.DRAW;
		
		if(player == Player.white) player = Player.black;
		else player = Player.white;
		ret.piecesOnBoard = pieces;
		return ret;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		String board[][] = PlayChess.makeBlankBoard();
		pieces = new ArrayList<ReturnPiece>();
		

		//Testing Code

		pieces.add(new Queen(Player.white));
		pieces.add(new Queen(Player.black));
		pieces.add(new Pawn(Player.black, 2));


		//Fill board with starting positions for pieces 
		/* Commented out for testing purposes 
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
		*/
		// 1st turn is always white
		
		player = Player.white;
		PlayChess.printBoard(pieces);
	}
}
