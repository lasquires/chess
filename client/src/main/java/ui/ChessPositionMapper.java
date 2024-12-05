package ui;

import chess.ChessPosition;

import java.util.HashMap;
import java.util.Map;

public class ChessPositionMapper {
    private final Map<String, ChessPosition> positionMap = new HashMap<>();

    public ChessPositionMapper() {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                String alphabetString = "-abcdefgh";
                Character c = alphabetString.charAt(col);
                String key = c + String.valueOf(row);
                positionMap.put(key, new ChessPosition(row, col));
            }
        }
    }
    public Map<String, ChessPosition> getPositionMap(){
        return positionMap;
    }
}
