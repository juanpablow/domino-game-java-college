package domino;

import java.util.ArrayList;
import java.util.List;

public class Dominoes {
    private Tile tile;
    private List<Tile> tiles = new ArrayList<>();

    public List<Tile> initializeTiles() {
       for (int faceUp=0; faceUp<=6; faceUp++) {
           for (int faceDown = faceUp; faceDown <= 6; faceDown++) {
                tile = new Tile();
                this.tile.setFaceUp(faceUp);
                this.tile.setFaceDown(faceDown);
                this.tiles.add(this.tile);
           }
       }
       return this.tiles;
    }

    public List<Tile> showTiles() {
        
        return this.tiles;
    }
        
}
