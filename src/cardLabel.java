package src;

/**
 * Created by Spencer on 2/27/15.
 */

import java.awt.Component;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class cardLabel extends JLabel
{
    public static final int CARD_WIDTH = 80;
    public static final int CARD_HEIGHT = 100;

    private Point starting_location;

    private int suit;
    private int rank;

    /**
     * Running the initializer, setting the image, the suit
     * & rank, and the location.
     *
     * @param image the card image
     * @param y_loc the spot on the grid and the card's value
     * @param x_loc the spot on the grid and the card's suit
     */
    public cardLabel(ImageIcon image, int y_loc, int x_loc)
    {
        super(image);
        this.rank = y_loc - 1;
        this.suit = x_loc;

        setBounds(0, 0, image.getIconWidth(), image.getIconHeight());
        setLocation(y_loc*CARD_WIDTH+5, x_loc*CARD_HEIGHT+5);
    }


    /**
     * Sets the starting position of the card
     */
    public void setStarting_location()
    {
        starting_location = this.getLocation();
    }

    /**
     * Gets the rank of the card.
     *
     * @return rank
     */
    public Integer getRank()
    {
        return rank;
    }

    /**
     * Gets the suit of the card
     *
     * @return suit
     */
    public Integer getSuit()
    {
        return suit;
    }

    /**
     * Gets the starting location from the grid
     *
     * @return grid_loc
     */
    public int getGridLoc()
    {
        return ((((int) starting_location.y) / CARD_HEIGHT) * 14) + (((int) starting_location.x) / CARD_WIDTH);
    }

    /**
     * Checks to see if the card can be placed in this location
     * based on the card to the left.
     *
     * @param comp component to the left
     * @return whether or not it can be placed here
     */
    public boolean canBePlaced(Component comp)
    {
        if(!(comp instanceof cardLabel))
        {
          return false;
        }

        cardLabel card_to_left = (cardLabel) comp;
        if (suit == card_to_left.getSuit() && rank == 1 + card_to_left.getRank())
        {
          return true;
        }
        return false;
    }

    /**
     * Snaps the card to the specified location in the grid.
     *
     * @param grid_loc location in the grid to snap to
     */
    public void snapTo(int grid_loc)
    {
        this.setLocation((grid_loc%14)*CARD_WIDTH+5, (grid_loc/14)*CARD_HEIGHT+5);
    }
}
