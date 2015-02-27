package src;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * A frame which holds cardLabels and helps out with
 * mouse events.
 */
public class cardFrame extends JFrame implements MouseListener, MouseMotionListener
{
    final private String[] suits = {"Spades", "Hearts", "Diamonds", "Clubs"};
    final private String[] rank = {"ace", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten", "jack", "queen", "king"};

    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 100;

    final private static Dimension size = new Dimension(1250, 500);

    private JLayeredPane layeredPane;
    private JPanel backGround;
    private cardLabel[] cardLabels = new cardLabel[52];

    // Determines whether or not the specific grid spot is empty
    private boolean[] isEmpty = new boolean[56];

    // Differences between the mouse and card location
    private int deltaX;
    private int deltaY;

    //Current card being dragged
    private cardLabel dragging_card;

    /**
     * Creates the frame and throws its parts together.
     */
    public cardFrame()
    {
        //Creates the layered pane, setting the size and adding mouse operation utilities.
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(size);
        layeredPane.setLayout(null);
        layeredPane.setBackground(Color.decode("#64C866"));
        layeredPane.setOpaque(true);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);

        //Adding the layered pane to the frame
        getContentPane().add(layeredPane);

        //Creating the background panel with an overridden method to display gray images
        //behind the card labels.
        backGround = new JPanel()
        {
            final ImageIcon gray = new ImageIcon("cardImages/gray.gif");

            /**
             * Adds additional gray images at each of the card locations
             *
             * @Override
             */
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                for (int i = 0; i < 4; i++)
                {
                    for (int j = 0; j < 14; j++)
                    {
                        gray.paintIcon(this, g, j * CARD_WIDTH + 5, i * CARD_HEIGHT + 5);
                    }
                }
            }
        };

        //Setting layout of the background panel
        backGround.setPreferredSize(size);
        backGround.setBounds(0, 0, size.width, size.height);
        backGround.setOpaque(false);
        backGround.setLayout(null);

        //Creates cardLabels
        for (int i = 0; i < 4; i++)
        {
            isEmpty[i*14] = true;
            for (int j = 0; j < 13; j++)
            {
                String cardFileName = "cardImages/" + rank[j] + suits[i] + ".gif";
                cardLabels[i*13+j] = new cardLabel(new ImageIcon(cardFileName), j+1, i);
                isEmpty[i*14+j+1] = false;

                layeredPane.add(cardLabels[i*13+j], 1);
            }
        }

        scrambleCards();

        //Finally adding the panel to the layered pane
        layeredPane.add(backGround, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * Creates a cardFrame, setting closing ops and size then makes the frame
     * visible
     *
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame cardFrame = new cardFrame();
        cardFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cardFrame.setPreferredSize(size);
        cardFrame.pack();
        cardFrame.setIconImage(new ImageIcon("cardImages/cat.png").getImage());
        cardFrame.setVisible(true);
    }

    /**
     * Swaps locations of each card with a random card
     */
    public void scrambleCards()
    {
        for (int i = 0; i < 52; i++)
        {
            int randomIndex = (int) (Math.random() * 52);
            Point tempPoint = cardLabels[i].getLocation();

            cardLabels[i].setLocation(cardLabels[randomIndex].getLocation());
            cardLabels[randomIndex].setLocation(tempPoint);
        }
    }

    /**
     * @return if they won
     */
    public boolean hasWon()
    {
        int rankVal = 0;
        int suitVal = 0;
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 13; j++)
            {
                Component comp = layeredPane.findComponentAt(j*CARD_WIDTH+5, i*CARD_HEIGHT+5);
                if (comp instanceof cardLabel)
                {
                    cardLabel tempCard = (cardLabel) comp;
                    if (j == 0)
                    {
                        suitVal = tempCard.getSuit();
                        rankVal = tempCard.getRank();
                    }
                    else
                    {
                        if (!(tempCard.getSuit() == suitVal || tempCard.getRank() == rankVal + 1))
                        {
                            return false;
                        }
                        rankVal++;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Updates the dragging cards location when mouse is being dragged
     *
     * @Override
     */
    public void mouseDragged(MouseEvent me)
    {
        if(dragging_card != null)
        {
            Point card_location = dragging_card.getLocation();
            int new_y = card_location.x + (me.getX() - dragging_card.getX()) - deltaY;
            int new_x = card_location.y + (me.getY() - dragging_card.getY()) - deltaX;
            dragging_card.setLocation(new_y, new_x);
        }
    }

    /**
     * When the mouse is clicked, it first checks to see if it clicked on a cardLabel.
     * Then if it is, is sets that card to the dragging layer, sets its starting location
     * and calculates the difference between mouse location and card location.
     *
     * @Override
     */
    public void mousePressed(MouseEvent me)
    {
        Component comp = layeredPane.findComponentAt(me.getX(), me.getY());

        if(comp instanceof cardLabel)
        {
            dragging_card = (cardLabel) comp;
            layeredPane.setLayer(dragging_card, JLayeredPane.DRAG_LAYER);
            dragging_card.setStarting_location();

            Point clickPoint = me.getPoint();
            deltaX = (int) clickPoint.getY() - dragging_card.getY();
            deltaY = (int) clickPoint.getX() - dragging_card.getX();
        }
    }

    /**
     * When the mouse is released and there is a dragging_card this will preform checks to see
     * if it is left in a valid location other wise it will snap back to its original location
     * and also checks if the player has won.
     *
     * @Override
     */
    public void mouseReleased(MouseEvent me)
    {
        if(dragging_card != null)
        {
            layeredPane.setLayer(dragging_card, 1);

            //Getting component one grid location to the left
            Component component_to_left = layeredPane.findComponentAt(me.getX() - CARD_WIDTH, me.getY());

            //The grid location of the mouse
            int gridIndex = (((((int) me.getY()) / CARD_HEIGHT) *14) + (((int) me.getX()) / CARD_WIDTH));
            //If that location does not exist, set it to the original dragging_card grid location
            int grid_location = (gridIndex > 55) ? dragging_card.getGridLoc() : gridIndex;

            //First case checks if the location is valid for any card
            //Second case checks if the card is an ace and is being put into an empty first row
            if(dragging_card.canBePlaced(component_to_left) && isEmpty[grid_location] || grid_location % 14 == 0 && dragging_card.getRank() == 0 && isEmpty[grid_location])
            {
                isEmpty[grid_location] = false;
                isEmpty[dragging_card.getGridLoc()] = true;
                dragging_card.snapTo(grid_location);
                dragging_card.setStarting_location();
            }
            else
            {
                dragging_card.snapTo(dragging_card.getGridLoc());
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }
        if(hasWon())
        {
            JOptionPane.showMessageDialog(layeredPane, "Winner winner chicken dinner! You beat the game of Carpet Solitaire!");
        }
        dragging_card = null;
    }

    //Overridden methods
    @Override
    public void mouseMoved(MouseEvent arg0) {}

    @Override
    public void mouseClicked(MouseEvent arg0) {}

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}
}