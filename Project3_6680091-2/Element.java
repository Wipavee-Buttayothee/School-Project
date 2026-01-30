package Project3_6680091;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static java.lang.Thread.sleep;

abstract class BaseLabel extends JLabel {

    protected int framewidth = MyConstants.FRAME_WIDTH;
    protected int frameheight = MyConstants.FRAME_HEIGHT;
    protected String name;
    protected String type;
    protected String category;
    protected String theme;
    protected MyImageIcon icon;
    protected int width, height, dfx, dfy;
    protected GameFrame parentFrame;

    public BaseLabel(String name, String type, String imagePath, int width, int height, String theme) {
        this.name = name;
        this.type = type;
        this.width = width;
        this.height = height;
        this.theme = theme;
        // Load and resize image
        this.icon = new MyImageIcon(imagePath).resize(width, height);
        setIcon(icon);
        setSize(width, height);
        setHorizontalAlignment(CENTER);
    }

    public void setParentFrame(GameFrame pf) {
        parentFrame = pf;
    }

    // Getters
    @Override
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    // Move element within the frame
    public void setPosition(int x, int y) {
        setLocation(x, y);
    }

    public void setDefaultPosition(int x, int y) {
        dfx = x;
        dfy = y;
        setLocation(dfx, dfy);
    }

    abstract public void getDefault();

    abstract public void Shake();

    abstract public void Pour();
}

class Ingredient extends BaseLabel implements MouseMotionListener, MouseListener {

    private int dx, dy;
    private int curX, curY;
    private boolean AlrHold = false;
    private boolean dragging = true;
    private Ingredient Onhold, Previoushold;
    private int PreviousholdX, PreviousholdY;
    private boolean isHold = false;

    public Ingredient(String name, String category, String imagePath, int width, int height, String theme) {
        super(name, "Ingredient", imagePath, width, height, theme);
        this.category = category;
        this.addMouseListener(this); // Add MouseListener to capture mouse press events
        this.addMouseMotionListener(this);
    }

    @Override
    public void getDefault() {
        setLocation(dfx, dfy);
        dragging = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging == false || parentFrame.isShaken()) {
            return;
        }
        dx = e.getX();
        dy = e.getY();
        curX += dx;
        curY += dy;
        if (curX < 0) {
            curX = 0;
        }
        if (curX > framewidth - this.width) {
            curX = (framewidth - this.width);
        }
        if (curY < 0) {
            curY = 0;
        }
        if (curY > frameheight - 1.4 * this.height) {
            curY = (int) (frameheight - 1.4 * this.height);
        }
        setLocation(curX, curY);
    }

    public void intersect(Character c) {
        Ingredient holding = parentFrame.getSelectedIngredient();
        if (this != holding && this.getBounds().intersects(c.getBounds())) {
            if (holding != null) {

                holding.getDefault();

            }
            this.dragging = false;
            this.setLocation(515, 234);
            parentFrame.setSelectedIngredient(this);
        }
    }

    public boolean isHold() {
        if (parentFrame.getSelectedIngredient() != null) {
            return this.isHold = true;
        } else {
            return this.isHold = false;
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!dragging && this == parentFrame.getSelectedIngredient()) {
            getDefault();
            parentFrame.setSelectedIngredient(null);
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        curX = getX();
        curY = getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this != parentFrame.getSelectedIngredient()) {
            getDefault();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void Pour() {

    }

    @Override
    public void Shake() {
        getDefault();
    }
}

class Alcohol extends Ingredient {

    public Alcohol(String name, String imagePath, String theme) {
        super(name, "Alcohol", imagePath, 26, 75, theme);
    }

    public Alcohol(String name, String imagePath, int width, int height, String theme) {
        super(name, "Alcohol", imagePath, width, height, theme);
    }
}

class Sweetener extends Ingredient {

    public Sweetener(String name, String imagePath, String theme) {
        super(name, "Sweetener", imagePath, 27, 70, theme);
    }

    public Sweetener(String name, String imagePath, int width, int height, String theme) {
        super(name, "Sweetener", imagePath, width, height, theme);
    }
}

class Mixer extends Ingredient {

    public Mixer(String name, String imagePath, String theme) {
        super(name, "Mixer", imagePath, 26, 72, theme);
    }
}

class Topping extends Ingredient {

    public Topping(String name, String imagePath, int width, int height, String theme) {
        super(name, "Topping", imagePath, width, height, theme);
    }
}

class Character extends BaseLabel {

    private String path = MyConstants.PATH;
    private boolean isShake1 = true;
    private int shot;

    public Character(String name, String imagePath, int width, int height, String theme) {
        super(name, "Character", imagePath, width, height, theme);
    }

    public void getDefault() {

    }

    public int getShotLevel() {
        return shot;
    }

    public void Shake() {

        setIcon(isShake1 ? new MyImageIcon(path + theme + MyConstants.SHAKE1) : new MyImageIcon(path + theme + MyConstants.SHAKE2));
        isShake1 = !isShake1;
        try {
            sleep(1);
        } catch (InterruptedException e) {
        }
    }

    public void Pour() {

    }

    public void Drink() {
        setIcon(new MyImageIcon(path + theme + MyConstants.HOLD));
    }

}
