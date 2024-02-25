package chess;

import java.util.*;

import chess.Chess.Player;

public abstract class Piece extends ReturnPiece{

    public Piece(Player player){
        super();
    }

    public abstract boolean isValidMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate);

    public boolean move(String move){
        try {
            String newRank = move.replaceAll("[^0-9]", "");
            pieceRank = Integer.parseInt(newRank);
        } 
        catch (NumberFormatException e) {
            System.out.println("Error: New Rank is not valid");
            return false;
        }
        
        switch (move.charAt(0)) {
            case 'a':
                pieceFile = PieceFile.a;
                break;
            case 'b':
                pieceFile = PieceFile.b;
                break;
            case 'c':
                pieceFile = PieceFile.c;
                break;
            case 'd':
                pieceFile = PieceFile.d;
                break;
            case 'e':
                pieceFile = PieceFile.e;
                break;
            case 'f':
                pieceFile = PieceFile.f;
                break;
            case 'g':
                pieceFile = PieceFile.g;
                break;
            case 'h':
                pieceFile = PieceFile.h;
                break;
        }

        return true;
    }

    // Checks if either opponent king is in check
    public static boolean isCheck(ArrayList<ReturnPiece> pieces, Player player){
        Piece curPiece;
        ReturnPiece king = null;
        ArrayList<ReturnPiece> playerPieces = new ArrayList<ReturnPiece>();

        //Iterate through all pieces, stores player's pieces and sets king to opponent's
        if(player == Player.white){
            for(ReturnPiece it : Chess.pieces){
                if(it.pieceType.ordinal()<=5) playerPieces.add(it);
                if(it.pieceType == PieceType.BK) king = it;
            }
        }
        else{
            for(ReturnPiece it : Chess.pieces){
                if(it.pieceType.ordinal()>5) playerPieces.add(it);
                if(it.pieceType == PieceType.WK) king = it;
            }
        }

        //For all player's pieces, check if any move results in opponents king being a valid move
        for(ReturnPiece it : playerPieces){
            curPiece = (Piece)it;
            if(curPiece.isValidMove(pieces, "" + king.pieceFile + king.pieceRank, player, false)){
                return true;
            }
        }

        return false;
    }

    // Used to check whether or not a move will result in your own king being in check
    public boolean isSelfCheck(ArrayList<ReturnPiece> pieces, Player player, String move){
        Piece curPiece;
        ReturnPiece king = null;
        ArrayList<ReturnPiece> piecesCopy = new ArrayList<ReturnPiece>(pieces);
        ArrayList<ReturnPiece> opponentPieces = new ArrayList<ReturnPiece>();

        int storeRank = pieceRank;
        PieceFile storeFile = pieceFile;
        ReturnPiece rem = null;

        // This fills the opponent pieces Array with all opponent pieces and stores the king of the moving color
        // Removes targeted piece if move results in capture
        if(player == Player.white){
            for(ReturnPiece it : pieces){
                if(it.pieceType.ordinal()>5) {
                    if(move.equals("" + it.pieceFile + it.pieceRank)) {
                        piecesCopy.remove(it);
                        rem = it;
                    } else opponentPieces.add(it);

                    if(this instanceof Pawn && it instanceof Pawn){
                        if(((Pawn)it).wasEnPassantBool && (this.pieceRank == it.pieceRank) && (Math.abs(this.pieceFile.ordinal() -
                                it.pieceFile.ordinal()) == 1) && (("" +move.charAt(0)).equals("" + it.pieceFile))){
                            rem = it;
                            piecesCopy.remove(it);
                        }
                    }else opponentPieces.add(it);     
                } else if(it.pieceType == PieceType.WK) king = it;
            }
        }else{
            for(ReturnPiece it : pieces){
                if(it.pieceType.ordinal()<=5) {
                    if(move.equals("" + it.pieceFile + it.pieceRank)) {
                        piecesCopy.remove(it);
                        rem = it;
                    } else opponentPieces.add(it);

                    if(this instanceof Pawn && it instanceof Pawn){
                        if(((Pawn)it).wasEnPassantBool && (this.pieceRank == it.pieceRank) && (Math.abs(this.pieceFile.ordinal() -
                                it.pieceFile.ordinal()) == 1) && (("" +move.charAt(0)).equals("" + it.pieceFile))){
                            rem = it;
                            piecesCopy.remove(it);
                        }
                    }else opponentPieces.add(it); 
                } else if(it.pieceType == PieceType.BK) king = it;
            }
        }
        
        // Move the piece, then check all opponent pieces to see if they can attack your king
        // If this move results in a check on yourself, reset the board to how it was before this call
        this.move(move);
        if(player == Player.white){player = Player.black;}
        else player = Player.white;
        for(ReturnPiece it : opponentPieces){
            curPiece = (Piece)it;
            if(curPiece.isValidMove(piecesCopy, "" + king.pieceFile + king.pieceRank, player, false)){
                pieceRank = storeRank;
                pieceFile = storeFile;
                if(rem != null) piecesCopy.add(rem);
                Chess.pieces = piecesCopy;
                return true;
            }
        }

        Chess.pieces = piecesCopy;
        return false;
    }

    // Checks whether or not the board is in checkmate
    public static boolean isMate(ArrayList<ReturnPiece> pieces, Chess.Player player){
        Piece curPiece;
        ArrayList<ReturnPiece> opponentPieces = new ArrayList<ReturnPiece>();

        // Iterate through all pieces, store opponent's
        if(player == Player.white){
            for(ReturnPiece it : Chess.pieces){
                if(it.pieceType.ordinal()>5) opponentPieces.add(it);
            }
        }
        else{
            for(ReturnPiece it : Chess.pieces){
                if(it.pieceType.ordinal()<=5) opponentPieces.add(it);
            }
        }

        if(player == Player.white){player = Player.black;}
            else player = Player.white;
        
        // Iterates through all opponent pieces to see if they have a valid move
        // Passing mate = true lets isValidMove to check all squares available for a piece
        //      whether they lead to their king being checked. If opponent has a valid move,
        //      there is not checkmate.
        for(ReturnPiece it : opponentPieces){
            curPiece = (Piece)it;
            
            if(curPiece.isValidMove(Chess.pieces, "", player, true)){
                return false;
            }
        }
        
        return true;
    }

    // Returns a 2D array filled with all pieces
    protected ReturnPiece[][] fillBoard(ArrayList<ReturnPiece> pieces){
        ReturnPiece[][] board = new ReturnPiece[8][8];
        for(ReturnPiece it : pieces){
            board[it.pieceFile.ordinal()][it.pieceRank -1] = it;
        }

        return board;
    }

    protected boolean isValidStraightMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        ReturnPiece[][] board = fillBoard(pieces);
        
        // fill validMoves with all clear spots above piece
        int i = pieceRank;
        while(i <= 7 && board[pieceFile.ordinal()][i] == null){
            if(mate){
                if(isSelfCheck(pieces, player, "" + pieceFile + (i+1)))
                    return false;
            } 
            else if(move.equals("" + pieceFile + (i+1)))
                return true;
            i ++;
        }

        // if path is blocked and player and target piece are different colors
        if(i<=7){
            if((player == Chess.Player.black && board[pieceFile.ordinal()][i].pieceType.ordinal() <= 5 ) ||  
                    (player == Chess.Player.white && board[pieceFile.ordinal()][i].pieceType.ordinal() > 5 ) ){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + pieceFile + (i+1)))
                        return false;
                }
                else if(move.equals("" + pieceFile + (i + 1))) {
                    return true;
                }
            }
        }

        // fill validMoves with all clear spots below piece
        i = pieceRank - 2;
        while(i >= 0 && board[pieceFile.ordinal()][i] == null){
            if(mate){
                if(isSelfCheck(pieces, player, "" + pieceFile + (i+1)))
                    return false;
            }
            else if(move.equals("" + pieceFile + (i + 1))) 
                return true;
            i --;
        }

        // if path is blocked and player and target piece are different colors
        if(i >= 0) {
            if((player == Chess.Player.black && board[pieceFile.ordinal()][i].pieceType.ordinal() <= 5 ) ||  
                    (player == Chess.Player.white && board[pieceFile.ordinal()][i].pieceType.ordinal() > 5 ) ){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + pieceFile + (i+1)))
                        return false;
                }
                else if(move.equals("" + pieceFile + (i + 1))) {
                    return true;
                }
            }
        }

        // fill validMoves with all clear spots left of piece
        i = pieceFile.ordinal() - 1;
        while(i >= 0 && board[i][pieceRank - 1] == null){
            if(mate){
                if(isSelfCheck(pieces, player, "" + (ReturnPiece.PieceFile.values()[i]) + pieceRank))
                    return false;
            }
            else if(move.equals("" + (ReturnPiece.PieceFile.values()[i]) + pieceRank)) 
                return true;
            i--;
        }

        // if path is blocked and player and target piece are different colors
        if(i>=0){
            if((player == Chess.Player.black) && (board[i][pieceRank - 1].pieceType.ordinal() <= 5) || 
                    (player == Chess.Player.white) && (board[i][pieceRank - 1].pieceType.ordinal() > 5)){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + (ReturnPiece.PieceFile.values()[i]) + pieceRank))
                        return false;
                }
                else if(move.equals("" + (ReturnPiece.PieceFile.values()[i]) + pieceRank)) {
                    return true;
                }
            }
        }

        // fill validMoves with all clear spots right of piece
        i = pieceFile.ordinal() + 1;
        while(i <= 7 && board[i][pieceRank - 1] == null){
            if(mate){
                if(isSelfCheck(pieces, player, "" + (ReturnPiece.PieceFile.values()[i])  + pieceRank))
                    return false;
            }
            else if(move.equals("" + (ReturnPiece.PieceFile.values()[i])  + pieceRank)) return true;
            i++;
        }

        // if path is blocked and player and target piece are different colors
        if(i<=7){
            if((player == Chess.Player.black) && (board[i][pieceRank - 1].pieceType.ordinal() <= 5) || 
                    (player == Chess.Player.white) && (board[i][pieceRank - 1].pieceType.ordinal() > 5)){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + (ReturnPiece.PieceFile.values()[i])  + pieceRank))
                        return false;
                }
                else if(move.equals("" + (ReturnPiece.PieceFile.values()[i])  + pieceRank)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isValidDiagonalMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        ReturnPiece[][] board = fillBoard(pieces);

        // fill valid moves with moves up and left
        int r = pieceRank;
        int f = pieceFile.ordinal() -1;
        while((r <= 7 && f >= 0) && board[f][r] == null){
            if(mate ){
                if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                    return false;
            }
            else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                return true;
            r++;
            f--;
        }

        // if path is blocked and player and target are different colors
        if(r <= 7 && f >=0){
            if((player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1))){
                    return true;
                } 
                    
            }
        }

        r = pieceRank;
        f = pieceFile.ordinal() + 1;
        // fill validMoves with moves up and right
        while((r <= 7 && f <= 7) && (board[f][r] == null)){
            if(mate ){
                if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                    return false;
            }
            else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1))) 
                return true;
            r++;
            f++;
        }

        // if path is blocked and player and target are different colors
        if(r <= 7 && f <= 7){
            if((player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                if(mate ){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1))) {
                    return true;
                }
            }
        }

        r = pieceRank - 2;
        f = pieceFile.ordinal() - 1;
        // fill validMoves with moves down and left
        while((r >= 0 && f >= 0) && board[f][r] == null){
            if(mate){
                if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                    return false;
            }
            else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1))) return true;
            r--;
            f--;
        }

        // if path is blocked and player and target are different colors
        if(r >= 0 && f >=0){
            if((player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1))) {
                    return true;
                }
            }
        }

        r = pieceRank - 2;
        f = pieceFile.ordinal() + 1;
        // fill validMoves with moves right and down
        while((r >= 0 && f <= 7) && board[f][r] == null){
            if(mate){
                if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                    return false;
            }
            else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1))) return true;
            r--;
            f++;
        }

        // if path is blocked and player and target are different colors
        if(r >= 0 && f <=7){
            if((player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f]+ (r+1))){
                    return true;
                } 
            }
        }

        return false;
    }

}

class King extends Piece{

    public King(Chess.Player color){
        super(color);
        pieceFile = PieceFile.e;
        switch (color) {
            case white:
                this.pieceRank = 1;
                this.pieceType = PieceType.WK;
                break;
            case black:
                this.pieceRank = 8;  
                this.pieceType = PieceType.BK;

                break;
        }
    }

    public boolean isValidMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        ReturnPiece[][] board = fillBoard(pieces);

        int r = pieceRank -1;
        int f = pieceFile.ordinal();

        // fill validMoves with king moves one rank above and below, including diagonal
        if(r+1 <= 7){
            for(int i = f -1; i<=f+1; i++){
                if(board[i][r+1] == null || (board[i][r+1].pieceType.ordinal() >5 && player == Player.white) 
                        || (board[i][r+1].pieceType.ordinal() <= 5 && player == Player.black)){
                    if(mate){
                        if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[i]+ (r+2)))
                            return false;
                    }
                    else if(move.equals("" + ReturnPiece.PieceFile.values()[i] + (r+2))){
                        return true;
                    }
                }
            }
        }
        if(r-1 >= 0){
            for(int i = f -1; i<=f+1; i++){
                if(board[i][r-1] == null || (board[i][r-1].pieceType.ordinal() >5 && player == Player.white) 
                        || (board[i][r-1].pieceType.ordinal() <= 5 && player == Player.black)){
                    if(mate){
                        if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[i]+ (r)))
                            return false;
                    }
                    else if(move.equals("" + ReturnPiece.PieceFile.values()[i] + (r))){
                        return true;
                    }
                }
            }
        }

        if(f-1 >= 0){
            if(board[f-1][r] == null || (board[f-1][r].pieceType.ordinal() > 5 && player == Player.white)
                    || (board[f-1][r].pieceType.ordinal() <= 5 && player == Player.black)){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f-1]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f-1] + (r+1))) {
                    return true;
                }
            }
        }

        if(f+1 <= 7){
            if(board[f+1][r] == null || (board[f+1][r].pieceType.ordinal() > 5 && player == Player.white)
                    || (board[f+1][r].pieceType.ordinal() <= 5 && player == Player.black)){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f+1]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f+1] + (r+1))) {
                    return true;
                }
            }
        }

        return false;
    }
}

class Queen extends Piece{

    public Queen(Chess.Player color){
        super(color);
        pieceFile = PieceFile.d;
        switch (color) {
            case white:
                this.pieceRank = 1;
                this.pieceType = PieceType.WQ;
                break;
        
            case black:
                this.pieceRank = 8;
                this.pieceType = PieceType.BQ;
                break;
        }
    }
    
    public boolean isValidMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        if(isValidDiagonalMove(pieces, move, player, mate) || isValidStraightMove(pieces, move, player, mate)) return true;

        return false;
    }
}

class Rook extends Piece{

    public Rook(Chess.Player color, int typeIteration){
        super(color);
        switch (color) {
            case white:
                this.pieceType = PieceType.WR;
                pieceRank = 1; 
                break;
            case black:
                this.pieceType = PieceType.BR;
                pieceRank = 8; 
                break;
        }

        if(typeIteration == 0)pieceFile = PieceFile.a;
        else pieceFile = PieceFile.h;
    }

    public boolean isValidMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        if(isValidStraightMove(pieces, move, player, mate)) return true;



        return false;
    }
}

class Bishop extends Piece{

    public Bishop(Chess.Player color, int typeIteration){
        super(color);
   
        switch (color) {
            case white:
                this.pieceType = PieceType.WB;
                pieceRank = 1; 
                break;
            case black:
                this.pieceType = PieceType.BB;
                pieceRank = 8; 
                break;
        }
        if(typeIteration == 0)pieceFile = PieceFile.c;
        else pieceFile = PieceFile.f;
    }



    public boolean isValidMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        if(isValidDiagonalMove(pieces, move, player, mate)) return true;

        return false;
    }
}

class Knight extends Piece{

    public Knight(Chess.Player color, int typeIteration){
        super(color);
        switch (color) {
            case white:
                this.pieceType = PieceType.WN;
                pieceRank = 1; 
                break;
            case black:
                this.pieceType = PieceType.BN;
                pieceRank = 8; 
                break;
        }
        if(typeIteration == 0)pieceFile = PieceFile.b;
        else pieceFile = PieceFile.g;
    }

    public boolean isValidMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        ReturnPiece[][] board = fillBoard(pieces);

        int r = pieceRank -1;
        int f = pieceFile.ordinal();
        if(f - 2 >= 0){
            if(r+1 <= 7 && (board[f-2][r+1] == null || (board[f-2][r+1].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f-2][r+1].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f-2]+ (r+2)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f-2] + (r+2))) {
                    return true;
                }
            }
            if(r-1 >= 0 && (board[f-2][r-1] == null || (board[f-2][r-1].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f-2][r-1].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f-2]+ (r)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f-2] + (r))) {
                    return true;
                }
            }

        }
        if(f + 2 <= 7){
            if(r+1 <= 7 && (board[f+2][r+1] == null || (board[f+2][r+1].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f+2][r+1].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f+2]+ (r+2) ))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f+2] + (r+2))) {
                    return true;
                }
            }
            if(r-1 >= 0 && (board[f+2][r-1] == null || (board[f+2][r-1].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f+2][r-1].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f+2]+ (r)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f+2] + (r))) {
                    return true;
                }
            }

        }
        if(r - 2 >= 0){
            if(f+1 <= 7 && (board[f+1][r-2] == null || (board[f+1][r-2].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f+1][r-2].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f+1]+ (r-1) ))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f+1] + (r-1))) {
                    return true;
                }
            }
            if(f-1 >= 0 && (board[f-1][r-2] == null || (board[f-1][r-2].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f-1][r-2].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f-1]+ (r-1) ))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f-1] + (r-1))) {
                    return true;
                }
            }

        }
        if(r + 2 <= 7){
            if(f+1 <= 7 && (board[f+1][r+2] == null || (board[f+1][r+2].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f+1][r+2].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f+1]+ (r+3) ))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f+1] + (r+3))) {
                    return true;
                }
            }
            if(f-1 >= 0 && (board[f-1][r+2] == null || (board[f-1][r+2].pieceType.ordinal() <=5 && player == Player.black)
                    || (board[f-1][r+2].pieceType.ordinal() > 5 && player == Player.white))) {
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f-1]+ (r+3) ))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f-1] + (r+3))) {
                    return true;
                }
            }

        }

        return false;
    }

}

class Pawn extends Piece{

    public boolean hasMoved = false;
    public boolean wasEnPassantBool = false;

    public Pawn(Chess.Player color, int typeIteration){
        super(color);
        switch (color) {
            case white:
                this.pieceType = PieceType.WP;
                pieceRank = 2; 
                break;
            case black:
                this.pieceType = PieceType.BP;
                pieceRank = 7; 
                break;
        }
        switch (typeIteration) {
            case 0:
                this.pieceFile = PieceFile.a;
                break;
            case 1:
                this.pieceFile = PieceFile.b;
                break;
            case 2:
                this.pieceFile = PieceFile.c;
                break;
            case 3:
                this.pieceFile = PieceFile.d;
                break;
            case 4:
                this.pieceFile = PieceFile.e;
                break;
            case 5:
                this.pieceFile = PieceFile.f;
                break;  
            case 6:
                this.pieceFile = PieceFile.g;
                break;
            case 7:
                this.pieceFile = PieceFile.h;
                break;              
        }
    }

    public boolean isValidMove(ArrayList<ReturnPiece> pieces, String move, Chess.Player player, boolean mate){
        ReturnPiece[][] board = fillBoard(pieces);

        int r = pieceRank-1;
        int f = pieceFile.ordinal();

        //For white pawns
        if(player == Chess.Player.white){
            if(r+1 <=7 && board[f][r+1] == null){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1) ))
                        return false;
                }
                else if(move.equals("" + pieceFile + (r + 2))) {
                    return true;
                }
                if(!hasMoved && r+2 < 7 && board[pieceFile.ordinal()][r+2] == null) {
                    if(mate){
                        if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1) ))
                            return false;
                    }
                    if(move.equals("" + pieceFile + (r+3))) {
                        return true;
                    }
                }
            }if((f-1 >= 0) && (r+1 <= 7) && board[f-1][r+1] != null && board[f-1][r+1].pieceType.ordinal() >5){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if( move.equals("" + ReturnPiece.PieceFile.values()[f-1] + (r+2))) {
                    return true;
                }
            }if((f+1 <= 7) && (r+1 <= 7) && board[f+1][r+1] != null && board[f+1][r+1].pieceType.ordinal() >5){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f+1] + (r+2))) {
                    return true;
                }
            }if(r == 4){
                Pawn it = null;
                if((f-1 >= 0) && board[f-1][r] != null && board[f-1][r].pieceType.ordinal() > 5){
                    if(board[f-1][r] instanceof Pawn){
                        it = (Pawn)board[f-1][r];}
                    if(Chess.wasEnPassantPiece.wasEnPassantBool && it.wasEnPassantBool){
                        if(mate){
                            if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                                return false;
                        }
                        else if(move.equals("" + ReturnPiece.PieceFile.values()[f-1] + (r+2))){
                            return true;
                        }
                    }
                }if((f+1 <=7) && board[f+1][r] != null && board[f+1][r].pieceType.ordinal() > 5){
                    if(board[f+1][r] instanceof Pawn){
                        it = (Pawn)board[f+1][r];
                    }
                    if(Chess.wasEnPassantPiece.wasEnPassantBool && it.wasEnPassantBool){
                        if(mate){
                            if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+2)))
                                return false;
                        }
                        else if(move.equals("" + ReturnPiece.PieceFile.values()[f+1] + (r+2))){
                            return true;
                        }
                    }
                }
            }
        } 
        //For black pawns
        else{
            if((r-1 >= 0) && board[pieceFile.ordinal()][r-1] == null){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + pieceFile + (r))) {
                    return true;
                }
                if(!hasMoved && r-2 > 0 && board[pieceFile.ordinal()][r-2] == null) {
                    if(mate){
                        if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                            return false;
                    }else if(move.equals("" + pieceFile + (r-1))) {
                        return true;
                    }
                }
            }if((f-1 >= 0) && (r-1 >= 0) && board[f-1][r-1] != null && board[f-1][r-1].pieceType.ordinal() <=5){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f-1] + (r))) {
                    return true;
                }
            }if((f+1 <= 7) && (r-1 >= 0) && board[f+1][r-1] != null && board[f+1][r-1].pieceType.ordinal() <=5){
                if(mate){
                    if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                        return false;
                }
                else if(move.equals("" + ReturnPiece.PieceFile.values()[f+1] + (r))) {
                    return true;
                }
            }if(r == 3){
                Pawn it = null;
                if((f-1 >= 0) && board[f-1][r] != null && board[f-1][r].pieceType.ordinal() <= 5){
                    if(board[f-1][r] instanceof Pawn){
                        it = (Pawn)board[f-1][r];}
                    if(Chess.wasEnPassantPiece.wasEnPassantBool && it.wasEnPassantBool){
                        if(mate){
                            if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                                return false;
                        }
                        else if(move.equals("" + ReturnPiece.PieceFile.values()[f-1] + (r))){
                            return true;
                        }
                    }
                }if((f+1 <+7) && board[f+1][r] != null && board[f+1][r].pieceType.ordinal() <= 5){
                    if(board[f+1][r] instanceof Pawn){
                        it = (Pawn)board[f+1][r];
                    }
                    if(Chess.wasEnPassantPiece.wasEnPassantBool && it.wasEnPassantBool){
                        if(mate){
                            if(isSelfCheck(pieces, player, "" + ReturnPiece.PieceFile.values()[f]+ (r+1)))
                                return false;
                        }
                        else if(move.equals("" + ReturnPiece.PieceFile.values()[f+1] + (r))){
                            return true;
                        }
                    }
                }
            }
        } 

        return false;
    }

    public ReturnPiece promote(String promoteTo){
        ReturnPiece newPiece;
        switch (promoteTo) {
            case "R":
                newPiece = new Rook(Chess.player, 0);
                break;
            case "B":
                newPiece = new Bishop(Chess.player, 0);
                break;
            case "N":
                newPiece = new Knight(Chess.player, 0);
                break;
            default:
                newPiece = new Queen(Chess.player);
                break;
        }

        newPiece.pieceFile = this.pieceFile;
        newPiece.pieceRank = this.pieceRank;
        return newPiece;
    }

    public boolean isEnPassant(ReturnPiece[][] board){
        
        return true;
    }
}
