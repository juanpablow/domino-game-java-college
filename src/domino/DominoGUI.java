package domino;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DominoGUI extends Dominoes {
    private JFrame window = new JFrame();
    private List<Tile> tilesOnTheTable = new ArrayList<>();
    private List<Tile> tiles = new ArrayList<>();
    private Player[] players;
    private List<Player> playerShuffled;
    private List<String> existingTeamNames = new ArrayList<>();
    private Player currentPlayer;
    private List<JLabel> playerHandLabels;
    private JPanel mainPanel, tablePanel, btnsPanel, handPanel;
    private JButton btnStart, btnShowHand;
    private JLabel currentPlayerLabel;
    private JLabel selectedTileLabel = null;
    private JScrollPane scrollPane;

    public DominoGUI() {
        window.setTitle("Dominó");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        window.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() > 0) {
                    window.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            }
        });

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        currentPlayerLabel = new JLabel("Jogo Dominó");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 36));
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(currentPlayerLabel, gbc);

        tablePanel = new JPanel(null);
        tablePanel.setLayout(null);
        tablePanel.setBackground(new Color(128, 0, 0));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(tablePanel, gbc);
        scrollPane = new JScrollPane(tablePanel); //
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(handPanel, gbc);

        btnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnsPanel.setPreferredSize(new Dimension(800, 100));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnsPanel, gbc);

        btnStart = new JButton("Iniciar Jogo");
        btnStart.setBackground(Color.GREEN); // Altera a cor para verde
        btnStart.setFont(new Font("Arial", Font.BOLD, 24));
        btnStart.setPreferredSize(new Dimension(400, 60)); // Define as dimensões do botão
        btnsPanel.add(btnStart);

        window.add(mainPanel);
        window.setVisible(true);

        playerHandLabels = new ArrayList<>();

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeGame();
            }
        });
    }

    private void updateUiAfterPlay() {
        SwingUtilities.invokeLater(() -> {
            selectedTileLabel.setBorder(null);
            selectedTileLabel = null;
            if (currentPlayer.closeOut()) {
                int result = JOptionPane.showConfirmDialog(null,
                    "O Jogador " + currentPlayer.getNamePlayer() + " BATEU! \nVitória do Time: " + currentPlayer.getNameTeam() + "\nVocê quer recomeçar o jogo?",
                    "Domino Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
    
                if (result == JOptionPane.YES_OPTION) {
                    resetGame();
                } else {
                    System.exit(0);
                }
            } else {
                showPlayerHand();
                updateTableLabels();
                nextPlayer();
                btnShowHand.setVisible(true);
            }
        });
    }

    private void initializeGame() {
        SwingUtilities.invokeLater(() -> {
            this.tiles.addAll(initializeTiles());
            this.players = initializePlayers();

            distributeTiles();

            this.playerShuffled = new ArrayList<>(List.of(players));
            Collections.shuffle(this.playerShuffled);

            List<Player> teamPlayers1 = this.playerShuffled.subList(0, 2);
            List<Player> teamPlayers2 = this.playerShuffled.subList(2, 4);

            createTeam(teamPlayers1, "primeiro");
            createTeam(teamPlayers2, "segundo");

            this.playerShuffled = playerOrder();

            this.currentPlayer = this.playerShuffled.get(0);

            this.currentPlayerLabel.setText("Vez de " + currentPlayer.getNamePlayer() + " do Time " + currentPlayer.getNameTeam());

            btnsPanel.remove(btnStart);

            btnShowHand = new JButton("Mostrar Mão");
            btnShowHand.setBackground(Color.WHITE);
            btnShowHand.setFont(new Font("Arial", Font.BOLD, 24));
            btnShowHand.setPreferredSize(new Dimension(800, 100));
            btnsPanel.add(btnShowHand);

            btnShowHand.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnsPanel.remove(btnShowHand);
                    showPlayerHand();
                    if (!showTiles().isEmpty()) {
                        if (!currentPlayer.canPlayTile(showTiles())) {
                            boolean isClosed = true;
                            for (Player p : playerShuffled) {
                                if(p.canPlayTile(showTiles())) {
                                    isClosed = false;
                                    break;
                                }
                            }
                            if (isClosed) {
                                int result = JOptionPane.showConfirmDialog(null,
                                "NENHUM JOGADOR PEÇAS QUE SE ENCAIXEM EM UMA DAS PONTAS\nO JOGO FECHOU!",
                                "Domino Game",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE);
                
                                if (result == JOptionPane.YES_OPTION) {
                                    resetGame();
                                } else {
                                    System.exit(0);
                                }
                            }
                            JOptionPane.showMessageDialog(null, "O jogador " + currentPlayer.getNamePlayer() + " do time " + currentPlayer.getNameTeam() + "\nNÂO tem peça para jogar KKKKKKKKKKKK. Passou a vez!", "Domino Game", JOptionPane.INFORMATION_MESSAGE);
                            btnsPanel.add(btnShowHand);
                            btnsPanel.revalidate();
                            btnsPanel.repaint();
                            nextPlayer();
                            return;

                        }
                    }

                    btnsPanel.add(btnShowHand);
                    btnsPanel.revalidate();
                    btnsPanel.repaint();
                    btnShowHand.setVisible(false);
                }
            });

            mainPanel.revalidate();
            mainPanel.repaint();

            startGame(); 
        }); 
    }

    private void resetGame() {
        // Remove todas as peças da mesa
        tilesOnTheTable.clear();
        tablePanel.removeAll();
        tablePanel.revalidate();
        tablePanel.repaint();

        handPanel.removeAll();
        handPanel.revalidate();
        handPanel.repaint();
    
        // Reinicia a posição de rolagem para o centro
        scrollPane.getVerticalScrollBar().setValue(0);

        Dimension panelSize = mainPanel.getSize();
        Dimension scrollSize = mainPanel.getSize();
        int x = (panelSize.width - scrollSize.width) / 2;
        int y = (panelSize.height - scrollSize.height) / 2;
        scrollPane.getViewport().setViewPosition(new Point(x, y));

        existingTeamNames.clear();
        playerShuffled.clear();
        playerHandLabels.clear();
        currentPlayerLabel.setText("Jogo Dominó");
        btnsPanel.removeAll();
        btnsPanel.add(btnStart);
        btnsPanel.revalidate();
        btnsPanel.repaint();
        handPanel.removeAll();
        handPanel.revalidate();
        handPanel.repaint();
        selectedTileLabel = null;

        initializeGame();
    }

    @Override
    public List<Tile> showTiles() {
        return this.tilesOnTheTable;
    }

    private void showPlayerHand() {
        handPanel.removeAll();
        playerHandLabels.clear();

        List<Tile> hand = this.currentPlayer.showTiles();
        for (int i = 0; i < hand.size(); i++) {
            Tile tile = hand.get(i);
            JLabel tileLabel = createTileLabel(tile, 0);
            playerHandLabels.add(tileLabel);
            handPanel.add(tileLabel);

            tileLabel.addMouseListener(new TileClickListener(tileLabel));
        }

        handPanel.revalidate();
        handPanel.repaint();
    }

    private class TileClickListener extends MouseAdapter {
        private JLabel tileLabel;

        public TileClickListener(JLabel tileLabel) {
            this.tileLabel = tileLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedTileLabel != null) {
                selectedTileLabel.setBorder(null);
            }

            selectedTileLabel = this.tileLabel;
            selectedTileLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            if (showTiles().size() >= 0 && showTiles().size() < 2) {
                currentPlayer.play(selectedTileLabel, showTiles(), true);
                if(!currentPlayer.getPlayed()) {
                    JOptionPane.showMessageDialog(null, "POR FAVOR JOGUE A BUCHA DE 6!", "Domino Game", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Atualiza a interface após jogada
                updateUiAfterPlay();
            }
        }
    }

    private class LeftEndClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedTileLabel == null) {
                JOptionPane.showMessageDialog(null, "Selecione uma peça da sua mão primeiro!", "Domino Game", JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentPlayer.play(selectedTileLabel, showTiles(), false);
            if(!currentPlayer.getPlayed()) {
                JOptionPane.showMessageDialog(null, "QUER COLAR GATO?\nEssa peça NÃO se encaixa nessa PONTA!", "Domino Game", JOptionPane.ERROR_MESSAGE);
                selectedTileLabel.setBorder(null);
                selectedTileLabel = null;
                return;
            }

            // Atualiza a Interface após jogar
            updateUiAfterPlay();
        }
    }


    private class RightEndClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedTileLabel == null) {
                JOptionPane.showMessageDialog(null, "Selecione uma peça da sua mão primeiro!", "Domino Game", JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentPlayer.play(selectedTileLabel, showTiles(), true);
            if(!currentPlayer.getPlayed()) {
                JOptionPane.showMessageDialog(null, "QUER COLAR GATO?\nEssa peça NÃO se encaixa nessa PONTA!", "Domino Game", JOptionPane.ERROR_MESSAGE);
                selectedTileLabel.setBorder(null);
                selectedTileLabel = null;
                return;
            }
            
            // Atualiza a interface após jogada
            updateUiAfterPlay();
        }
    }


    public JLabel createTileLabel(Tile tile, int angle) {
        ImageIcon tileIcon = tile.getImage();

        scrollPane.setPreferredSize(new Dimension(tileIcon.getIconWidth() + tileIcon.getIconWidth(), scrollPane.getHeight()));

        JLabel tileLabel = new JLabel(RotatedImageLabel.rotateImageIcon(tileIcon, angle));
        tileLabel.setPreferredSize(new Dimension(tileIcon.getIconWidth(), tileIcon.getIconHeight()));
        tileLabel.setName(tile.getFaceUp() + "-" + tile.getFaceDown());

        return tileLabel;
    }

    // @Override
    // public List<Tile> showTiles() {

    // }

    private void updateTableLabels() {
        tablePanel.removeAll();
        int tableX = tablePanel.getWidth();
        int tableY = tablePanel.getHeight();

        int totalWidth = 0;
        int centeredY = 0;
        for (Tile tile : showTiles()) {
            JLabel tileLabel = createTileLabel(tile, tile.getAngle());
            totalWidth += tileLabel.getIcon().
            getIconWidth();
            centeredY = tileLabel.getIcon().getIconHeight();
        }
        int centeredX = (tableX - totalWidth) / 2;
        centeredY = (tableY - centeredY) / 2;

        int x = centeredX;
        JLabel tileLabel = null;
        for(int i = 0; i < showTiles().size(); i++) {
            Tile tile = showTiles().get(i);
            tileLabel = createTileLabel(tile, tile.getAngle());

            int iconWidth = tileLabel.getIcon().getIconWidth();
            int iconHeight = tileLabel.getIcon().getIconHeight();

            centeredY = (tableY - iconHeight) / 2;

            tileLabel.setBounds(x, centeredY, iconWidth, iconHeight);
            if (i == 0) {
                tileLabel.addMouseListener(new LeftEndClickListener());
                tileLabel.setBounds(centeredX, centeredY, iconWidth, iconHeight);
            }
            tablePanel.add(tileLabel);
            if (i == showTiles().size()-1) {
                tileLabel.addMouseListener(new RightEndClickListener());
            }

            x += tileLabel.getIcon().getIconWidth();
            tablePanel.setPreferredSize(new Dimension(totalWidth + 200, centeredY));
            tablePanel.revalidate();
            tablePanel.repaint();
        }
        tileLabel.requestFocusInWindow();
    }

    // private Tile getSelectedTileFromLabel(JLabel tileLabel) {
    //     String[] parts = tileLabel.getName().split("-");
    //     return ;
    // }


    private void nextPlayer() {
        int currentPlayerIndex = this.playerShuffled.indexOf(currentPlayer);
        currentPlayerIndex = (currentPlayerIndex + 1) % this.playerShuffled.size(); 
        currentPlayer = this.playerShuffled.get(currentPlayerIndex);

        currentPlayerLabel.setText("Vez de " + currentPlayer.getNamePlayer() + " do Time " + currentPlayer.getNameTeam());

        handPanel.removeAll();
        handPanel.revalidate();
        handPanel.repaint();
    }

    private void distributeTiles() {
        Collections.shuffle(this.tiles);
        for (int i = 0; i < this.players.length; i++) {
            this.players[i].addTiles(new ArrayList<>(this.tiles.subList(i * 7, (i + 1) * 7)));
        }
    }

    private Player[] initializePlayers() {
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            String playerName = "";
            while (playerName == null || playerName.trim().isEmpty()) {
                playerName = JOptionPane.showInputDialog(null, "Nome do Jogador " + (i + 1) + ":", "Domino Game", JOptionPane.DEFAULT_OPTION);
                if (playerName == null) {
                    int option = JOptionPane.showConfirmDialog(null, "Deseja sair do jogo?", "Confirmação", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        System.exit(0); // Fecha o jogo
                    }
                    continue;
                }
                playerName = playerName.trim();
                if (playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "O nome do jogador não pode estar vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
            players[i] = new Player(playerName);
        }
        return players;
    }

    private int findPlayerWithBuchaSix() {
        for (Player p : this.playerShuffled) {
            if (p.isBuchaSix()) {
                return this.playerShuffled.indexOf(p);
            }
        }
        return -1;
    }

    private List<Player> playerOrder() {
        int currentPlayerIndex = findPlayerWithBuchaSix();

        Player startingPlayer = this.playerShuffled.get(currentPlayerIndex);
        List<Player> orderedPlayers = new ArrayList<>();

        this.playerShuffled.remove(startingPlayer);
        orderedPlayers.add(startingPlayer);

        Player lastPlayer = startingPlayer;
        boolean added = true;

        for (Player p : playerShuffled) {
            if (!orderedPlayers.contains(p) && !p.getNameTeam().equals(lastPlayer.getNameTeam())) {
                orderedPlayers.add(p);
                if (!added && !orderedPlayers.contains(lastPlayer)) {
                    orderedPlayers.add(lastPlayer);
                    added = true;
                    continue;
                }
                lastPlayer = p;
            } else {
                lastPlayer = p;
                added = false;
            }
        }
        return orderedPlayers;
    }

    private Team createTeam(List<Player> teamPlayers, String teamOrder) {
        while (true) {
            StringBuilder playersList = new StringBuilder("No " + teamOrder + " time ficaram os jogadores:\n");
            teamPlayers.forEach(p -> playersList.append(p.getNamePlayer()).append("\n"));
    
            JOptionPane.showConfirmDialog(null, playersList.toString(), "Domino Game", JOptionPane.DEFAULT_OPTION);
    
            String teamName = JOptionPane.showInputDialog(null, "Qual o nome do " + teamOrder + " time?", "Domino Game", JOptionPane.DEFAULT_OPTION);
            if (teamName == null) {
                int option = JOptionPane.showConfirmDialog(null, "Deseja sair do jogo?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0); // Fecha o jogo
                }
                continue;
            } else {
                teamName = teamName.trim(); // Remove espaços em branco extras
                if (!teamName.isEmpty()) {
                    // Verifica se o nome do time já existe
                    if (existingTeamNames.contains(teamName)) {
                        JOptionPane.showMessageDialog(null, "O nome do time já está em uso. Escolha outro nome.", "Erro", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Cria a equipe se o nome do time for único
                        Team team = new Team();
                        teamPlayers.forEach(p -> p.setPlayerOnTheTeam(team));
                        team.setNameTeam(teamName);
                        // Adiciona o nome da equipe à lista de nomes existentes
                        existingTeamNames.add(teamName);
                        return team;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "O nome do time não pode estar vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void startGame() {
        this.currentPlayer = this.playerShuffled.get(0);
        currentPlayerLabel.setText("Vez de " + currentPlayer.getNamePlayer() + " do Time " + currentPlayer.getNameTeam());
    }

}
