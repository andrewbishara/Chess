package chess;

public abstract class Piece extends ReturnPiece{

    public abstract boolean validMove(String move);
    public abstract ReturnPiece move(String move);

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

    public boolean validMove(String move){
        return true;
    }

    //Takes in a position to move to, sets fields to ending position
    public ReturnPiece move(String move){

        return new ReturnPiece();
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
    public boolean validMove(String move){
        return true;
    }

    public ReturnPiece move(String move){
        return new ReturnPiece();
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

    public boolean validMove(String move){
        return true;
    }

    public ReturnPiece move(String move){
        return new ReturnPiece();
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

    public boolean validMove(String move){
        return true;
    }

    public ReturnPiece move(String move){
        return new ReturnPiece();
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

    public boolean validMove(String move){
        return true;
    }

    public ReturnPiece move(String move){
        return new ReturnPiece();
    }
}

class Pawn extends Piece{

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

    public boolean validMove(String move){
        return true;
    }

    public ReturnPiece move(String move){
        return new ReturnPiece();
    }
}