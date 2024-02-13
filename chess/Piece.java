package chess;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Piece extends ReturnPiece{

    public abstract boolean isValidMove(String move);
    public abstract boolean move(String move);

    public ArrayList<String> straightMoves(ArrayList<ReturnPiece> pieces){
        ArrayList<ReturnPiece> rankBlock = new ArrayList<ReturnPiece>();
        ArrayList<ReturnPiece> fileBlock = new ArrayList<ReturnPiece>();
        int minRank = 1;
        int maxRank = 8;
        String minFile = "a";
        String maxFile = "h";

        // Fill ArrayLists with pieces that could block movement
        for(ReturnPiece it : pieces){
            if(it.pieceFile == this.pieceFile) rankBlock.add(it);
            if(it.pieceRank == this.pieceRank) fileBlock.add(it);
        } 

        // Check vertical spaces that are free to move to
        int[] rankBlockInts = rankBlock.stream().mapToInt(ReturnPiece -> ReturnPiece.pieceRank).toArray();
        Arrays.sort(rankBlockInts);
        for(int i=0; i <= rankBlockInts.length-1 ; i++){
            //if the rankBlockInts[i] is the moving piece
            if(rankBlockInts[i] == this.pieceRank){
                //Check ranks below moving piece's
                if(i == 0) minRank = 1;
                else {
                    for(ReturnPiece it : rankBlock){
                        if(it.pieceRank == rankBlockInts[i-1]){
                            String sub = ""+it.pieceType;
                            if(sub.charAt(0) == 'w'){
                                if(Chess.player == Chess.Player.white) minRank = rankBlockInts[i-1] + 1;
                                else{
                                    minRank = rankBlockInts[i-1];
                                }
                            }
                            else{
                                if(Chess.player == Chess.Player.black) minRank = rankBlockInts[i-1] + 1;
                                else{
                                     minRank = rankBlockInts[i-1];
                                }
                            }
                        }
                    }
                }
                
                //Check ranks above moving piece's
                if(i == rankBlockInts.length -1) maxRank = 8;
                else {
                    for(ReturnPiece it : rankBlock){
                        if(it.pieceRank == rankBlockInts[i+1]){
                            String sub = ""+it.pieceType;
                            if(sub.charAt(0) == 'w'){
                                if(Chess.player == Chess.Player.white) maxRank = rankBlockInts[i+1] - 1;
                                else{
                                    maxRank = rankBlockInts[i+1];
                                }
                            }
                            else{
                                if(Chess.player == Chess.Player.black) maxRank = rankBlockInts[i+1] - 1;
                                else{
                                    maxRank = rankBlockInts[i+1];
                                }
                            }
                        }
                    }
                }

                i = rankBlockInts.length-1;
            }
        }

        String curFile = ""+this.pieceFile;
        String[] fileBlockStrings = new String[fileBlock.size()];
        for(int i = 0; i < fileBlock.size(); i++){
            ReturnPiece.PieceFile file = fileBlock.get(i).pieceFile;
            fileBlockStrings[i] = ""+file;
        }
        Arrays.sort(fileBlockStrings);
        for(int i=0; i <= fileBlockStrings.length-1 ; i++){
            //if the fileBlockInts[i] is the moving piece
            if(fileBlockStrings[i].equals(curFile)){
                //Check files below moving piece's
                if(i == 0) minFile = "a";
                else {
                    String itPieceFile;
                    for(ReturnPiece it : fileBlock){
                        itPieceFile = ""+it.pieceFile;
                        if(itPieceFile.equals(fileBlockStrings[i-1])){
                            String sub = ""+it.pieceType;
                            if(sub.charAt(0) == 'w'){
                                if(Chess.player == Chess.Player.white) minFile = "" + (fileBlockStrings[i-1].charAt(0) + 1);
                                else{
                                    minFile = fileBlockStrings[i-1];
                                }
                            }
                            else{
                                if(Chess.player == Chess.Player.black) minFile = "" + (fileBlockStrings[i-1].charAt(0) + 1);
                                else{
                                     minFile = fileBlockStrings[i-1];
                                }
                            }
                        }
                    }
                }
                
                //Check files above moving piece's
                if(i == fileBlockStrings.length -1) maxFile = "h";
                else {
                    for(ReturnPiece it : fileBlock){
                        String itPieceFile = "" + it.pieceFile;
                        if(itPieceFile.equals(fileBlockStrings[i+1])){
                            String sub = ""+it.pieceType;
                            if(sub.charAt(0) == 'w'){
                                if(Chess.player == Chess.Player.white) maxFile = "" + (fileBlockStrings[i+1].charAt(0) - 1);
                                else{
                                    maxFile = fileBlockStrings[i+1];
                                }
                            }
                            else{
                                if(Chess.player == Chess.Player.black) maxFile = "" + (fileBlockStrings[i+1].charAt(0) - 1);
                                else{
                                    maxFile = fileBlockStrings[i+1];
                                }
                            }
                        }
                    }
                }

                i = rankBlockInts.length-1;
            }
        }

        ArrayList<String> ret = new ArrayList<String>();
        for(int i = minRank; i <= maxRank; i++){
            ret.add(""+this.pieceFile+i);
        }
        char c = minFile.charAt(0);
        while (c <= maxFile.charAt(0)) {
            ret.add("" + c + this.pieceRank);
            c++;
        }

        return ret;
        
        
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

    public boolean isValidMove(String move){
        return true;
    }

    //Takes in a position to move to, sets fields to ending position
    public boolean move(String move){

        return false;
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
    public boolean isValidMove(String move){
        ArrayList<String> validMoves = straightMoves(Chess.pieces);
        for(String it : validMoves){
            if(it.equals(move)) return true;
        }
        return false;
    }

    public boolean move(String move){
        if(isValidMove(move)){
            return true;
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

    public boolean isValidMove(String move){
        return true;
    }

    public boolean move(String move){
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

    public boolean isValidMove(String move){
        return true;
    }

    public boolean move(String move){
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

    public boolean isValidMove(String move){
        return true;
    }

    public boolean move(String move){
        return false;
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

    public boolean isValidMove(String move){
        return true;
    }

    public boolean move(String move){
        return false;
    }
}