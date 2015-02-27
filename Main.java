/**
 * First Last
 */

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class Main
{
    public static final int PERIMETER_BEVEL = 10;               //space between panel border and perimeter cards
    public static final int INTERIOR_BEVEL = 5;                 //space between cards
    public static final String CARD_FOLDER="cardImages/";
    public static final int CARD_HEIGHT = 97;
    public static final int CARD_WIDTH = 73;
    public static final String[] SUITS = {"Hearts", "Diamonds", "Spades", "Clubs"};
    public static final String[] RANKS = { "ace","two","three","four","five","six","seven",
            "eight","nine","ten","jack","queen","king"};
    public static final int PANEL_HEIGHT = (PERIMETER_BEVEL) + (4*CARD_HEIGHT) + (3*INTERIOR_BEVEL);
    public static final int PANEL_WIDTH = (PERIMETER_BEVEL) + (14*CARD_WIDTH) + (13*INTERIOR_BEVEL);
    public static final String   BACKGROUND_COLOR = "#64C866";  //window background color [hex] - Changed from #76ee00 (lime green) to a more game table-y green
    public static List<JLabel> deck = new ArrayList<JLabel>();
    public static MouseInputAdapter mouseHandler = new MouseInputAdapter()
    {
        public int labelX;
        public int labelY;
        public void mousePressed(MouseEvent me)
        {
            // Grabbing the top card and moving it on the top.
            me.getComponent().getParent().setComponentZOrder(me.getComponent(), 0);
            labelX = me.getX();
            labelY = me.getY();
            me.getComponent().getParent().repaint();
        }
        public void mouseDragged (MouseEvent me)
        {
            JPanel panel = (JPanel) me.getComponent().getParent();
            //get new coordinates
            int newX = me.getComponent().getX() + me.getX() - labelX;
            int newY = me.getComponent().getY() + me.getY() - labelY;

            //Making sure that the card is bound within the panel.
            if(newX > panel.getWidth() - CARD_WIDTH)
            {
                newX = panel.getWidth() - CARD_WIDTH;
            }
            if(newY > panel.getHeight() - CARD_HEIGHT)
            {
                newY = panel.getHeight() - CARD_HEIGHT;
            }
            if(newX < 0)
            {
                newX = 0;
            }
            if(newY < 0)
            {
                newY = 0;
            }
            me.getComponent().setLocation(newX, newY);
        }
    };

    /**
     * main method which prints out the deck of cards, now in random order
     * with a leading gray image for each row.
     * @param args, the arguments for the main method [ignored]
     */
    public static void main(String[] args)
    {
        JFrame window = new JFrame("Deck");

        JPanel panel = new JPanel(
                new GridLayout(SUITS.length, RANKS.length, INTERIOR_BEVEL, INTERIOR_BEVEL)) {
            //create the gray ImageIcon
            ImageIcon gray = new ImageIcon(CARD_FOLDER+"gray.gif");
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //print out 56 gray images on the panel
                //in a 4 x 14 grid pattern [extra credit]
                //i/4 is the column number to draw in
                //i%4 is the row number to draw in
                for (int i = 0; i < ((SUITS.length)*(RANKS.length+1)); i++) {
                    gray.paintIcon(this, g,
                            INTERIOR_BEVEL + (i / 4) * (CARD_WIDTH + INTERIOR_BEVEL),
                            INTERIOR_BEVEL + (i % 4) * (CARD_HEIGHT + INTERIOR_BEVEL));
                }
            }
        };


        // Create the deck and add the cards into it
        for(String suit: SUITS)
        {
            for(String rank: RANKS)
            {
                deck.add(new JLabel(
                        new ImageIcon(CARD_FOLDER+rank+suit+".gif")));
            }
        }
        // Shuffle the deck
        Collections.shuffle(deck);

        // Add Mouse listeners and add it to the panel
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5+CARD_WIDTH, 5, 5));

        for(JLabel card: deck)
        {
            card.addMouseMotionListener(mouseHandler);
            card.addMouseListener(mouseHandler);
            panel.add(card);
        }


        panel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        panel.setBackground(Color.decode(BACKGROUND_COLOR));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(panel);
        window.setVisible(true);
        window.pack();
    }
}
