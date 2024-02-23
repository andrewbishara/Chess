package chess;

import java.util.ArrayList;
import java.lang.NumberFormatException;

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
		
		ReturnPiece rem = null;

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

		// Save a copy of moving piece in case move makes check
		ReturnPiece temp;
		if(curPiece instanceof Pawn){temp = new Pawn(player, 0);}
		else if(curPiece instanceof Rook){  temp = new Rook(player,0);}
		else if(curPiece instanceof Knight){  temp = new Knight(player, 0);}
		else if(curPiece instanceof Bishop){  temp = new Bishop(player, 0);}
		else if(curPiece instanceof Queen){  temp = new Queen(player);}
		else { temp = new King(player);}
		temp.pieceFile = curPiece.pieceFile;
		temp.pieceRank = curPiece.pieceRank;
		temp.pieceType = curPiece.pieceType; 
		Piece curPieceCopy = (Piece)temp;
		

		// Check if there is a capture then move piece to new square
		if(curPiece.move(subMove[1])){
			for(ReturnPiece it : pieces){
				if ((""+it.pieceFile+it.pieceRank).equals(subMove[1])) {
					rem = it;
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

			// Check for check/checkmate, if not, restore pieces
			Player storePlayer = player;
			if((player == Player.white && Piece.isCheck(pieces) == 1) || player == Player.black && Piece.isCheck(pieces) == -1){
				player = storePlayer;
				if(rem != null) pieces.add(rem);
				for(ReturnPiece it : pieces){
					if(it.equals(curPiece)){
						it.pieceFile = curPieceCopy.pieceFile;
						it.pieceRank = curPieceCopy.pieceRank;
					}
				}
				System.out.println("Error: Cannot let own king get checked");
				ret.message = Message.ILLEGAL_MOVE;
				ret.piecesOnBoard = pieces;
				return ret;
			}
			else{
				player = storePlayer;
				if((player == Player.white && Piece.isCheck(pieces) == -1) || (player == Player.black && Piece.isCheck(pieces) == 1))
					ret.message = Message.CHECK;
				player = storePlayer;
				
			}
		}else{
			System.out.println("Error: Invalid move");
			ret.message = Message.ILLEGAL_MOVE;
			ret.piecesOnBoard = pieces;
			return ret;
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
