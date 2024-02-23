package chess;

import java.util.ArrayList;
import chess.Chess.Player;

public abstract class Piece extends ReturnPiece{

    public abstract boolean move(String move);

    // Checks if either king is in check, returns 0 if neither, -1 if black king is checked, and 1 if white king
    public static int isCheck(ArrayList<ReturnPiece> pieces){
        Piece curPiece;
        ReturnPiece blackKing = null;
        ReturnPiece whiteKing = null;
        ArrayList<ReturnPiece> whites = new ArrayList<ReturnPiece>();
        ArrayList<ReturnPiece> blacks = new ArrayList<ReturnPiece>();

        for(ReturnPiece it : pieces){
            if(it.pieceType == PieceType.BK) blackKing = it;
            else if(it.pieceType == PieceType.WK) whiteKing = it;
            else if(it.pieceType.ordinal() <= 5) whites.add(it);
            else blacks.add(it);
        }

        Chess.player = Player.white;
        for(ReturnPiece it : whites){
            curPiece = (Piece)it;
            if(curPiece.move(""+ blackKing.pieceFile + blackKing.pieceRank)) return -1;

        }

        Chess.player = Player.black;
        for(ReturnPiece it : blacks){
            curPiece = (Piece)it;
            if(curPiece.move("" + whiteKing.pieceFile + whiteKing.pieceRank)) return 1;
        }

        return 0;
    }

    public static boolean isCheckmate(ArrayList<ReturnPiece> pieces) {
        // Check if the opposing king is in check
        if ((Chess.player == Player.white && isCheck(pieces) == -1) || (Chess.player == Player.black && isCheck(pieces) == 1)){

            Piece king = null;

            // Gets opposing king of current player
            for (ReturnPiece it : pieces){
                if (Chess.player == Player.white && it.pieceType == PieceType.BK) king = (Piece) it;
                else if (Chess.player == Player.black && it.pieceType == PieceType.WK) king = (Piece) it;
            }

            // Check if opposing king has any valid moves
            if (((King) king).getValidKingMoves(pieces).size() != 0) { 
                
                // Any valid moves means no checkmate
                return false; 

            } else { 

                ArrayList<ReturnPiece> opposingPieces = new ArrayList<ReturnPiece>();

                // Fills opposingPieces with non-King opposing colored pieces
                for (ReturnPiece it : pieces) {
                    if(it.pieceType.ordinal() <= 5 && Chess.player == Player.black && it.pieceType != PieceType.WK) {
                        opposingPieces.add(it);
                    } else if (it.pieceType.ordinal() > 5 && Chess.player == Player.white && it.pieceType != PieceType.BK) {
                        opposingPieces.add(it);
                    }
                }
            
                // Checks if opposing piece can block mate
                // Cycle every possible move per opposing piece and check if valid (i is file, j is rank)
                for (PieceFile i : PieceFile.values()) {
                    for (int j = 0; j <= 7; j++) {

                        for (ReturnPiece piece : opposingPieces) {
                            if (((Piece) piece).move("" + i + j) == true)
                                return false;

                        }

                    }
                }

                // If not, current player wins
                return true; 
            }

        }
        // If the king is not in check, there is no checkmate
        return false;
    }

    protected boolean isBlockingMove(String move){
        // Temporarily make the move
        PieceFile originalFile = this.pieceFile;
        int originalRank = this.pieceRank;
        this.pieceFile = PieceFile.valueOf(move.substring(0,1));
        this.pieceRank = Integer.parseInt(move.substring(1));
    

        // Check if the checked player's king is still in check after the move
        boolean isBlockingMove = false;
        if (isCheck(Chess.pieces) == 0
            || (Chess.player == Player.white && isCheck(Chess.pieces) == -1) // safety checks to avoid bugs
            || (Chess.player == Player.black && isCheck(Chess.pieces) == 1)) // do not delete
            isBlockingMove = true;
    
        // Undo the move
        this.pieceFile = originalFile;
        this.pieceRank = originalRank;
    
        // If it is a blocking move, returns true
        return isBlockingMove;
    }


    protected ReturnPiece[][] fillBoard(ArrayList<ReturnPiece> pieces){
        ReturnPiece[][] board = new ReturnPiece[8][8];
        for(ReturnPiece it : pieces){
            board[it.pieceFile.ordinal()][it.pieceRank -1] = it;
        }

        return board;
    }

    protected ArrayList<String> getValidStraightMoves(ArrayList<ReturnPiece> pieces){

        ArrayList<String> validMoves = new ArrayList<String>();
        ReturnPiece[][] board = fillBoard(pieces);
        
        // fill validMoves with all clear spots above piece
        int i = pieceRank;
        while(i <= 7 && board[pieceFile.ordinal()][i] == null){
            validMoves.add("" + pieceFile + (i + 1));
            i ++;
        }

        // if path is blocked and player and target piece are different colors
        if(i<=7){
            if((Chess.player == Chess.Player.black && board[pieceFile.ordinal()][i].pieceType.ordinal() <= 5 ) ||  
                (Chess.player == Chess.Player.white && board[pieceFile.ordinal()][i].pieceType.ordinal() > 5 ) ){

            validMoves.add("" + pieceFile + (i + 1));
            }
        }

        // fill validMoves with all clear spots below piece
        i = pieceRank - 2;
        while(i >= 0 && board[pieceFile.ordinal()][i] == null){
            validMoves.add("" + pieceFile + (i + 1));
            i --;
        }

        // if path is blocked and player and target piece are different colors
        if(i >= 0) {
            if((Chess.player == Chess.Player.black && board[pieceFile.ordinal()][i].pieceType.ordinal() <= 5 ) ||  
                (Chess.player == Chess.Player.white && board[pieceFile.ordinal()][i].pieceType.ordinal() > 5 ) ){

            validMoves.add("" + pieceFile + (i + 1));
            }
        }

        // fill validMoves with all clear spots left of piece
        i = pieceFile.ordinal() - 1;
        while(i >= 0 && board[i][pieceRank - 1] == null){
            validMoves.add("" + ReturnPiece.PieceFile.values()[i] + pieceRank);
            i--;
        }

        // if path is blocked and player and target piece are different colors
        if(i>=0){
            if((Chess.player == Chess.Player.black) && (board[i][pieceRank - 1].pieceType.ordinal() <= 5) || 
                (Chess.player == Chess.Player.white) && (board[i][pieceRank - 1].pieceType.ordinal() > 5)){
            
            validMoves.add("" + ReturnPiece.PieceFile.values()[i] + pieceRank);
            }
        }

        // fill validMoves with all clear spots right of piece
        i = pieceFile.ordinal() + 1;
        while(i <= 7 && board[i][pieceRank - 1] == null){
            validMoves.add("" + ReturnPiece.PieceFile.values()[i] + pieceRank);
            i++;
        }

        // if path is blocked and player and target piece are different colors
        if(i<=7){
            if((Chess.player == Chess.Player.black) && (board[i][pieceRank - 1].pieceType.ordinal() <= 5) || 
                (Chess.player == Chess.Player.white) && (board[i][pieceRank - 1].pieceType.ordinal() > 5)){
            
            validMoves.add("" + ReturnPiece.PieceFile.values()[i] + pieceRank);
            }
        }

        return validMoves;
    }

    protected ArrayList<String> getValidDiagonalMoves(ArrayList<ReturnPiece> pieces){
        ArrayList<String> validMoves = new ArrayList<String>();
        ReturnPiece[][] board = fillBoard(pieces);

        // fill valid moves with moves up and left
        int r = pieceRank;
        int f = pieceFile.ordinal() -1;
        while((r <= 7 && f >= 0) && board[f][r] == null){
            validMoves.add("" + ReturnPiece.PieceFile.values()[f]+ (r+1));
            r++;
            f--;
        }

        // if path is blocked and player and target are different colors
        if(r <= 7 && f >=0){
            if((Chess.player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (Chess.player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                validMoves.add("" + ReturnPiece.PieceFile.values()[f] + (r+1));
            }
        }

        r = pieceRank;
        f = pieceFile.ordinal() + 1;
        // fill validMoves with moves up and right
        while((r <= 7 && f <= 7) && board[f][r] == null){
            validMoves.add("" + ReturnPiece.PieceFile.values()[f]+ (r+1));
            r++;
            f++;
        }

        // if path is blocked and player and target are different colors
        if(r <= 7 && f <= 7){
            if((Chess.player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (Chess.player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                validMoves.add("" + ReturnPiece.PieceFile.values()[f] + (r+1));
            }
        }

        r = pieceRank - 2;
        f = pieceFile.ordinal() - 1;
        // fill validMoves with moves down and left
        while((r >= 0 && f >= 0) && board[f][r] == null){
            validMoves.add("" + ReturnPiece.PieceFile.values()[f]+ (r+1));
            r--;
            f--;
        }

        // if path is blocked and player and target are different colors
        if(r >= 0 && f >=0){
            if((Chess.player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (Chess.player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                validMoves.add("" + ReturnPiece.PieceFile.values()[f] + (r+1));
            }
        }

        r = pieceRank - 2;
        f = pieceFile.ordinal() + 1;
        // fill validMoves with moves right and down
        while((r >= 0 && f <= 7) && board[f][r] == null){
            validMoves.add("" + ReturnPiece.PieceFile.values()[f]+ (r+1));
            r--;
            f++;
        }

        // if path is blocked and player and target are different colors
        if(r >= 0 && f <=7){
            if((Chess.player == Chess.Player.black) && (board[f][r].pieceType.ordinal() <= 5) || 
                    (Chess.player == Chess.Player.white) && (board[f][r].pieceType.ordinal() > 5)){
                validMoves.add("" + ReturnPiece.PieceFile.values()[f] + (r+1));
            }
        }

        return validMoves;
    }

}

class King extends Piece{

    public King(Chess.Player color){
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

    //Takes in a position to move to, sets fields to ending position
    public boolean move(String move){

        PieceFile originalFile = this.pieceFile;
        int originalRank = this.pieceRank;
        this.pieceFile = PieceFile.valueOf(move.substring(0,1));
        this.pieceRank = Integer.parseInt(move.substring(1));

        // Check if the player moves king into check
        boolean moveIntoCheck = false;
        if ((Chess.player == Player.white && isCheck(Chess.pieces) == 1)
            || (Chess.player == Player.black && isCheck(Chess.pieces) == -1))
            moveIntoCheck = true;
    
        // Undo the move
        this.pieceFile = originalFile;
        this.pieceRank = originalRank;

        ArrayList<String> validMoves = getValidKingMoves(Chess.pieces);
        for(String it : validMoves){
            if(it.equals(move) && isBlockingMove(move) && moveIntoCheck != true) return true; // if king is moving into check, return false (INVALID MOVE)
        }
        return false;
    }

    protected ArrayList<String> getValidKingMoves(ArrayList<ReturnPiece> pieces){
        ArrayList<String> validMoves = new ArrayList<String>();
        ReturnPiece[][] board = fillBoard(pieces);

        int r = pieceRank -1;
        int f = pieceFile.ordinal();

        // fill validMoves with king moves one rank above and below, including diagonal
        if(r+1 <= 7){
            for(int i = f -1; i<=f+1; i++){
                if(board[i][r+1] == null || (board[i][r+1].pieceType.ordinal() >5 && Chess.player == Player.white) 
                        || (board[i][r+1].pieceType.ordinal() <= 5 && Chess.player == Player.black))
                    validMoves.add("" + ReturnPiece.PieceFile.values()[i] + (r+2));
            }
        }
        if(r-1 >= 0){
            for(int i = f -1; i<=f+1; i++){
                if(board[i][r-1] == null || (board[i][r-1].pieceType.ordinal() >5 && Chess.player == Player.white) 
                        || (board[i][r-1].pieceType.ordinal() <= 5 && Chess.player == Player.black))
                    validMoves.add("" + ReturnPiece.PieceFile.values()[i] + (r));
            }
        }

        if(f-1 >= 0){
            if(board[f-1][r] == null || (board[f-1][r].pieceType.ordinal() > 5 && Chess.player == Player.white)
                    || (board[f-1][r].pieceType.ordinal() <= 5 && Chess.player == Player.black))
                validMoves.add("" + ReturnPiece.PieceFile.values()[f-1] + (r+1));
        }

        if(f+1 <= 7){
            if(board[f+1][r] == null || (board[f+1][r].pieceType.ordinal() > 5 && Chess.player == Player.white)
                    || (board[f+1][r].pieceType.ordinal() <= 5 && Chess.player == Player.black))
                validMoves.add("" + ReturnPiece.PieceFile.values()[f+1] + (r+1));
        }

        return validMoves;
    }
}

class Queen extends Piece{

    public Queen(Chess.Player color){
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
    public boolean move(String move){

        ArrayList<String> validMoves = getValidDiagonalMoves(Chess.pieces);
        for(String it : validMoves){
            if(it.equals(move)) {
                if (isBlockingMove(move)){
                    return true;
                }
            }
        }
        validMoves = getValidStraightMoves(Chess.pieces);
        for(String it : validMoves){
            if(it.equals(move) && isBlockingMove(move)) return true;
        }
        return false;

    }


}

class Rook extends Piece{

    public Rook(Chess.Player color, int typeIteration){
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

    public boolean move(String move){
        ArrayList<String> validMoves = getValidStraightMoves(Chess.pieces);
        for(String it : validMoves){
            if(it.equals(move) && isBlockingMove(move)) return true;
        }
        return false;
    }
}

class Bishop extends Piece{

    public Bishop(Chess.Player color, int typeIteration){
   
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



    public boolean move(String move){
        ArrayList<String> validMoves = getValidDiagonalMoves(Chess.pieces);
        for(String it : validMoves){
            if(it.equals(move) && isBlockingMove(move)) return true;
        }
        return false;
    }
}

class Knight extends Piece{

    public Knight(Chess.Player color, int typeIteration){
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

    public boolean move(String move){
        ArrayList<String> validMoves = getValidKnightMoves(Chess.pieces);
        for(String it : validMoves){
            if(it.equals(move) && isBlockingMove(move)) return true;
        }
        return false;
    }

    private ArrayList<String> getValidKnightMoves(ArrayList<ReturnPiece> pieces){
        ArrayList<String> validMoves = new ArrayList<String>();
        ReturnPiece[][] board = fillBoard(pieces);

        int r = pieceRank -1;
        int f = pieceFile.ordinal();
        if(f - 2 >= 0){
            if(r+1 <= 7 && (board[f-2][r+1] == null || (board[f-2][r+1].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f-2][r+1].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f-2] + (r+2));
            }
            if(r-1 >= 0 && (board[f-2][r-1] == null || (board[f-2][r-1].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f-2][r-1].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f-2] + (r));
            }

        }
        if(f + 2 <= 7){
            if(r+1 <= 7 && (board[f+2][r+1] == null || (board[f+2][r+1].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f+2][r+1].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f+2] + (r+2));
            }
            if(r-1 >= 0 && (board[f+2][r-1] == null || (board[f+2][r-1].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f+2][r-1].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f+2] + (r));
            }

        }
        if(r - 2 >= 0){
            if(f+1 <= 7 && (board[f+1][r-2] == null || (board[f+1][r-2].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f+1][r-2].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f+1] + (r-1));
            }
            if(f-1 >= 0 && (board[f-1][r-2] == null || (board[f-1][r-2].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f-1][r-2].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f-1] + (r-1));
            }

        }
        if(r + 2 <= 7){
            if(f+1 <= 7 && (board[f+1][r+2] == null || (board[f+1][r+2].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f+1][r+2].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f+1] + (r+3));
            }
            if(f-1 >= 0 && (board[f-1][r+2] == null || (board[f-1][r+2].pieceType.ordinal() <=5 && Chess.player == Player.black)
                    || (board[f-1][r+2].pieceType.ordinal() > 5 && Chess.player == Player.white))) {
                validMoves.add("" + ReturnPiece.PieceFile.values()[f-1] + (r+3));
            }

        }

        return validMoves;
    }


}

class Pawn extends Piece{

    public boolean hasMoved = false;

    public Pawn(Chess.Player color, int typeIteration){
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

    public boolean move(String move){
        ArrayList<String> validMoves = getValidPawnMoves(Chess.pieces);
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        
        for(String it : validMoves){
            if(it.equals(move) && isBlockingMove(move)){
                if(!(stack[2].toString().contains("isCheck")))
                    hasMoved = true;
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> getValidPawnMoves(ArrayList<ReturnPiece> pieces){
        ArrayList<String> validMoves = new ArrayList<String>();
        ReturnPiece[][] board = fillBoard(pieces);

        int r = pieceRank-1;
        int f = pieceFile.ordinal();

        //For white pawns
        if(Chess.player == Chess.Player.white){
            if(r+1 <7 && board[f][r+1] == null){
                validMoves.add("" + pieceFile + (r + 2));
                if((f-1 >= 0) && board[f-1][r+1] != null && board[f-1][r+1].pieceType.ordinal() >5) 
                    validMoves.add("" + ReturnPiece.PieceFile.values()[f-1] + (r+2));
                if((f+1 <= 7) &&board[f+1][r+1] != null && board[f+1][r+1].pieceType.ordinal() >5) 
                    validMoves.add("" + ReturnPiece.PieceFile.values()[f+1] + (r+2));
                if(!hasMoved && r+2 < 7 && board[pieceFile.ordinal()][r+2] == null) {
                    validMoves.add("" + pieceFile + (r+3));
                }
            }
        } 
        //For black pawns
        else{
            if(r-1 > 0 && board[pieceFile.ordinal()][r-1] == null){
                validMoves.add("" + pieceFile + (r));
                if((f-1 >= 0) && board[f-1][r-1] != null && board[f-1][r-1].pieceType.ordinal() <=5) 
                    validMoves.add("" + ReturnPiece.PieceFile.values()[f-1] + (r));
                if((f+1 <= 7) && board[f+1][r-1] != null && board[f+1][r-1].pieceType.ordinal() <=5) 
                    validMoves.add("" + ReturnPiece.PieceFile.values()[f+1] + (r));
                if(!hasMoved && r-2 > 0 && board[pieceFile.ordinal()][r-2] == null) {
                    validMoves.add("" + pieceFile + (r-1));
                }
            } 
        }



        return validMoves;
    }

    public boolean getHasMoved(){
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved){
        this.hasMoved = hasMoved;
    }
}