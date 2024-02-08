package chess;

public interface Piece {

    public boolean validMove();
    public ReturnPiece move();

}

class King implements Piece{

    enum PieceFile {a, b, c, d, e, f, g, h};
    int pieceRank; //1-8

    public boolean validMove(){
        return true;
    }

    public ReturnPiece move(){
        return new ReturnPiece();
    }
}

class Queen implements Piece{

    enum PieceFile {a, b, c, d, e, f, g, h};
    int pieceRank; //1-8

    public boolean validMove(){
        return true;
    }

    public ReturnPiece move(){
        return new ReturnPiece();
    }
}

class Rook implements Piece{

    enum PieceFile {a, b, c, d, e, f, g, h};
    int pieceRank; //1-8

    public boolean validMove(){
        return true;
    }

    public ReturnPiece move(){
        return new ReturnPiece();
    }
}

class Bishop implements Piece{

    enum PieceFile {a, b, c, d, e, f, g, h};
    int pieceRank; //1-8

    public boolean validMove(){
        return true;
    }

    public ReturnPiece move(){
        return new ReturnPiece();
    }
}

class Knight implements Piece{

    enum PieceFile {a, b, c, d, e, f, g, h};
    int pieceRank; //1-8

    public boolean validMove(){
        return true;
    }

    public ReturnPiece move(){
        return new ReturnPiece();
    }
}

class Pawn implements Piece{

    enum PieceFile {a, b, c, d, e, f, g, h};
    int pieceRank; //1-8

    public boolean validMove(){
        return true;
    }

    public ReturnPiece move(){
        return new ReturnPiece();
    }
}