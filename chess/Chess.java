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
	static Pawn wasEnPassantPiece = null;
	
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
			if(player == Player.black) ret.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
			else ret.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
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


		// Check for targeted piece being null
		// Capturing en passant requires an empty square to be targeted, so check to see whether 
		//		en passant is valid for this piece
		// If en passant is not valid for this move, return with error
		
		if(curPiece == null && wasEnPassantPiece != null && wasEnPassantPiece.wasEnPassantBool ){
			if(!(curPiece instanceof Pawn && curPiece.pieceRank == wasEnPassantPiece.pieceRank && (Math.abs(curPiece.pieceFile.ordinal() + 
					wasEnPassantPiece.pieceFile.ordinal()) == 1) && "" + subMove[1].charAt(0) != "" + wasEnPassantPiece.pieceFile)){
				ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
				ret.piecesOnBoard = pieces;
				System.out.println("Error: Empty square was attempted to move");
				return ret;
			}
		}else if(curPiece == null){
			ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
			ret.piecesOnBoard = pieces;
			System.out.println("Error: Empty square was attempted to move");
			return ret;
		} 

		// Checks whether the move is valid, then whether it results in own piece being put in check
		// Check whether the move made the piece elligible to be taken en passant, and store it in
		// 		the wasEnPassantPiece object. If this piece didn't become en passant elligible, We must 
		//		later change the wasEnPassantPiece object to reflect that
		int pawnRank =0;
		Pawn temp = null;
		boolean switchEnPassant = false;
		if(curPiece instanceof Pawn) pawnRank = curPiece.pieceRank; 
		if(curPiece.isValidMove(pieces, subMove[1], player, false)){
			if(curPiece.isSelfCheck(pieces, player, subMove[1])){
				ret.message = Message.ILLEGAL_MOVE;
				ret.piecesOnBoard = pieces;
				return ret;
			} 
			if(curPiece instanceof Pawn){
				for(ReturnPiece it : pieces){
					if(it instanceof Pawn){
						if(("" + it.pieceFile + it.pieceRank).equals(subMove[1])){
							temp = (Pawn)it;
						}
					}
				}
				if(Math.abs(pawnRank - temp.pieceRank) == 2) {
					temp.wasEnPassantBool = true;
					wasEnPassantPiece = temp;
				}else switchEnPassant = true;
				if(temp.pieceRank == 8 || temp.pieceRank == 1) {
					pieces.remove(temp);
					if(subMove.length == 3) pieces.add(temp.promote(subMove[2]));
					else pieces.add(temp.promote(""));
				}
			}else switchEnPassant = true;
			if(curPiece instanceof King && !((King)curPiece).hasMoved) {
                
                ReturnPiece curRook = null;
				((King)curPiece).hasMoved = true; 

				if (subMove[1].equals("g1")) {
					if (curPiece.pieceFile == PieceFile.e && curPiece.pieceRank == 1) {
						for (ReturnPiece it : pieces) {
							if (it.pieceType == PieceType.WR && it.pieceFile == PieceFile.h && it.pieceRank == 1 && player == Player.white) {
								curRook = it;
							}
						}
                    	if (curRook != null && curRook.pieceFile == PieceFile.h){
							curPiece.pieceFile = PieceFile.g;
                			curRook.pieceFile = PieceFile.f;
                			((Rook)curRook).hasMoved = true;
                   	 	} 

                	}
           	 	}
				if (subMove[1].equals("c1")) {
					if (curPiece.pieceFile == PieceFile.e && curPiece.pieceRank == 1) {
						for (ReturnPiece it : pieces) {
							if (it.pieceType == PieceType.WR && it.pieceFile == PieceFile.a && it.pieceRank == 1 && player == Player.white) {
								curRook = it;
							}
						}
						if (curRook != null && curRook.pieceFile == PieceFile.a){
							curPiece.pieceFile = PieceFile.c;
							curRook.pieceFile = PieceFile.d;
							((Rook)curRook).hasMoved = true;
						} 
	
						}
					}
				if (subMove[1].equals("g8")) {
					if (curPiece.pieceFile == PieceFile.e && curPiece.pieceRank == 8) {
						for (ReturnPiece it : pieces) {
							if (it.pieceType == PieceType.BR && it.pieceFile == PieceFile.h && it.pieceRank == 8 && player == Player.black) {
								curRook = it;
							}
						}
						if (curRook != null && curRook.pieceFile == PieceFile.h){
							curPiece.pieceFile = PieceFile.g;
							curRook.pieceFile = PieceFile.f;
							((Rook)curRook).hasMoved = true;
						} 
		
						}
					}
				if (subMove[1].equals("c8")) {
					if (curPiece.pieceFile == PieceFile.e && curPiece.pieceRank == 8) {
						for (ReturnPiece it : pieces) {
							if (it.pieceType == PieceType.BR && it.pieceFile == PieceFile.a && it.pieceRank == 8 && player == Player.black) {
								curRook = it;								
							}
						}
						if (curRook != null && curRook.pieceFile == PieceFile.a){
							curPiece.pieceFile = PieceFile.c;
							curRook.pieceFile = PieceFile.d;
							((Rook)curRook).hasMoved = true;
						} 
						
						}
					}
        	}
		}else{
			ret.message = Message.ILLEGAL_MOVE;
			ret.piecesOnBoard = pieces;
			return ret;
		}

		for(ReturnPiece it : pieces){
			if(subMove[1].equals("" + it.pieceFile + it.pieceRank)) curPiece = (Piece)it;
		}
		if(curPiece instanceof Rook) ((Rook)curPiece).hasMoved = true;
		if(curPiece instanceof King) ((King)curPiece).hasMoved = true;
		if(curPiece instanceof Pawn) ((Pawn)curPiece).hasMoved = true;

		
		// Check for check, then checkmate
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

		// If there is an additional message in the move (draw or promotion), then change the wasEnPassantPiece if
		// 		last move was not en passant, then change the player
		if(subMove.length == 3 && subMove[2].equals("draw?")) ret.message = ReturnPlay.Message.DRAW;
		if(switchEnPassant && wasEnPassantPiece != null) wasEnPassantPiece.wasEnPassantBool = false;
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
		PlayChess.printBoard(pieces);
	}
}