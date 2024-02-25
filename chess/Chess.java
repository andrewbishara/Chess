package chess;

import java.util.ArrayList;

import chess.ReturnPiece.PieceFile;
import chess.ReturnPiece.PieceType;
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
	static boolean whiteMoves[][] = new boolean[8][8]; 
	static boolean blackMoves[][] = new boolean[8][8];
	static String lastMove = null;
	
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

		// Check to see if piece is on the board
		if(curPiece == null){
			ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
			ret.piecesOnBoard = pieces;
			System.out.println("Error: Empty square was attempted to move");
			return ret;
		}
		
		// Check if there is a capture then move piece to new square
		if(curPiece.isValidMove(pieces, subMove[1], player, false)){
			if(curPiece.isSelfCheck(pieces, player, subMove[1], false)){
				ret.message = Message.ILLEGAL_MOVE;
				ret.piecesOnBoard = pieces;
				return ret;
			} 
			if(curPiece instanceof Pawn){
				Pawn temp = (Pawn) curPiece;
				temp.hasMoved = true;
				if(temp.pieceRank == 8) {
					pieces.remove(temp);
					if(subMove.length == 3) pieces.add(temp.promote(pieces, subMove[2]));
					else pieces.add(temp.promote(pieces, ""));
				}
				if (((Pawn)curPiece).enPassant == true) {

					if (player == Player.white) {
						for(ReturnPiece it : Chess.pieces){
							if ((it.pieceRank + 1) == curPiece.pieceRank && it.pieceFile == curPiece.pieceFile) 
								pieces.remove(it); // BP behind WP is removed
						}
					} else {
						for(ReturnPiece it : Chess.pieces){
							if ((it.pieceRank - 1) == curPiece.pieceRank && it.pieceFile == curPiece.pieceFile)
								pieces.remove(it); // WP behind BP is removed
						}
					}
						
					((Pawn)curPiece).enPassant = false; // so if the pawn moves again if wont remove pieces behind it
				}
			}
		}else{
			ret.message = Message.ILLEGAL_MOVE;
			ret.piecesOnBoard = pieces;
			return ret;
		}
		
		System.out.println("TEST:");
		PlayChess.printBoard(pieces);
		if(Piece.isCheck(pieces, player)){
			if(Piece.isMate(pieces, player)){
				if(player == Player.white) ret.message = Message.CHECKMATE_WHITE_WINS;
				else ret.message = Message.CHECKMATE_BLACK_WINS;
				ret.piecesOnBoard = pieces;
				return ret;
			}
			if(player == Player.white) player = Player.black;
			else player = Player.white;
			ret.message = Message.CHECK;
			ret.piecesOnBoard = pieces;
			return ret;             
		}
		
		// Tracks last move for en passant
		lastMove = move;

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
		//String board[][] = PlayChess.makeBlankBoard();
		pieces = new ArrayList<ReturnPiece>();
		


		//Fill board with starting positions for pieces 
		for(Player player : Player.values()){
			pieces.add(new King(player));
			pieces.add(new Queen(player));

			/* for(int i = 0; i < 2; i++){
				pieces.add(new Rook(player, i));
				pieces.add(new Bishop(player, i));
				pieces.add(new Knight(player, i));
			} */

		 	for(int i = 0; i < 8; i++){
				pieces.add(new Pawn(player, i));
			}
		}
		// 1st turn is always white
		
		player = Player.white;
		PlayChess.printBoard(pieces);
	}
}
