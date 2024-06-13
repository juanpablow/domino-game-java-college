package domino;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class Player extends Dominoes {
    private String namePlayer;
    private List<Tile> hand = new ArrayList<>();
    private Team team;
    private static int leftEndTable;
    private static int rightEndTable;
    private static boolean played = false;

    public Player(String namePlayer) {
        this.namePlayer = namePlayer;
        this.hand = new ArrayList<>();
    }

    public static int getLeftEndTable() {
        return leftEndTable;
    }

    public static void setLeftEndTable(int v) {
        Player.leftEndTable = v;
    }

    public static int getRightEndTable() {
        return rightEndTable;
    }

    public static void setRightEndTable(int v) {
        Player.rightEndTable = v;
    }

    public void addTiles(List<Tile> tiles) {
        this.hand.addAll(tiles);
    }

    // public List<Tile> showHand() {
    //     return this.hand;
    // }

    public String getNamePlayer() {
        return namePlayer.toUpperCase();
    }

    public String getNameTeam() {
        return team.getNameTeam().toUpperCase();
    }

    public void setPlayerOnTheTeam(Team team) {
        this.team = team;
    }

    public boolean getPlayed() {
        return played;
    }

    private void setIsPlayed(boolean played) {
        Player.played = played;
    }


    public boolean isBuchaSix() {
        boolean buchaSix = false;
        for (Tile t : hand) {
            if (t.getFaceUp() == 6 && t.getFaceDown() == 6) {
                buchaSix = true;
            }
        }
        return buchaSix;
    }

    @Override
    public List<Tile> showTiles() {
        return this.hand;
    }

    public void play(JLabel tileLabel, List<Tile> tilesOnTheTable, boolean isRightTableSide) {
        // Buscar peca na mão do usuário
        int tileFaceUp = Integer.valueOf(tileLabel.getName().split("-")[0]);
        int tileFaceDown = Integer.valueOf(tileLabel.getName().split("-")[1]);

        Tile tile = null;
        for (int i = 0; i < this.hand.size(); i++) {
            if (hand.get(i).getFaceUp() == tileFaceUp && hand.get(i).getFaceDown() == tileFaceDown) {
                tile = hand.get(i);
            }
        }
        if (tilesOnTheTable.isEmpty()) {
            if (tile.getFaceUp() == 6 && tile.getFaceDown() == 6) {
                setLeftEndTable(tile.getFaceUp());
                setRightEndTable(tile.getFaceDown());
                tilesOnTheTable.add(tile);
            } else {
                setIsPlayed(false);
                return;
            }
        } else {
            if (isRightTableSide) {
                if(tileFaceUp == getRightEndTable()) {
                    setRightEndTable(tileFaceDown);
                    // Rotacionar -90 
                    tile.setAngle(-90);
                    tilesOnTheTable.add(tile);
                } else if(tileFaceDown == getRightEndTable()){
                    setRightEndTable(tileFaceUp);
                    tile.setAngle(90);
                    tilesOnTheTable.add(tile);
                } else {
                    setIsPlayed(false);
                    return;
                }
            } else {
                if (tileFaceUp == getLeftEndTable()) {
                    setLeftEndTable(tileFaceDown);
                    tile.setAngle(90);
                    tilesOnTheTable.add(0, tile);
                } else if (tileFaceDown == getLeftEndTable()){
                    setLeftEndTable(tileFaceUp);
                    tile.setAngle(-90);
                    tilesOnTheTable.add(0, tile);
                } else {
                    setIsPlayed(false);
                    return;
                }
            }
        }
        hand.remove(tile);
        setIsPlayed(true);
    }

    public boolean canPlayTile(List<Tile> tilesOnTheTable) {
        boolean canPlayTile = false;
        for (Tile t : hand) {
            if (t.getFaceUp() == getLeftEndTable() || t.getFaceDown() == getLeftEndTable() || t.getFaceUp() == getRightEndTable() || t.getFaceDown() == getRightEndTable()) {
                canPlayTile = true;
            }
        }
        return canPlayTile;
    }


    public boolean closeOut() {
        return hand.isEmpty();
    }
}
