package Project3_6680091;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame {

    private String theme;
    private String path = MyConstants.PATH;
    private String base = MyConstants.BASEPATH;
    private int frameWidth = MyConstants.FRAME_WIDTH;
    private int frameHeight = MyConstants.FRAME_HEIGHT;
    private String bg = MyConstants.FILE_BG;
    private String sound = MyConstants.SOUND;
    private MyImageIcon backgroundImg;
    private JLabel contentpane;

    private String[] shotLevels = {
        MyConstants.SHOT0, MyConstants.SHOT1, MyConstants.SHOT2,
        MyConstants.SHOT3, MyConstants.SHOT4, MyConstants.SHOT5,
        MyConstants.SHOT6, MyConstants.SHOT7
    };
    private JLabel shotLabel;

    private int buttonwidth = MyConstants.BUTTONWIDTH;
    private int buttonheight = MyConstants.BUTTONHEIGHT;

    private MySoundEffect themeSound, pourSound, shakeSound;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private Character bartender;
    private JList<String> soundLevel;
    private JLabel soundIcon;
    private JScrollPane soundScrollPane;
    private JToggleButton[] tb;
    private ButtonGroup bgroup;
    private String name;
    private JTextField gameText1, gameText2, gameText3, gameText4, nameField, Bar, Enter;
    private Ingredient selectedIngredient = null;
    private ArrayList<Ingredient> pouredIngredients = new ArrayList<>();
    private int shot = 0;
    private boolean Shaken = false;
    private boolean readytoserve = false;
    private boolean served = false;
    private String selectedGlass;

    public GameFrame(String n) {
        this.theme = n;
        setTitle(n);
        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setContentPane(contentpane = new JLabel());
        contentpane.setLayout(null);
        backgroundImg = new MyImageIcon(path + n + bg).resize(frameWidth, frameHeight);
        contentpane.setIcon(backgroundImg);
        addKeyListeners();
        AddBasicComponents();
        AddSpecialDrink();
        AddSpecialTopping();
        AddBartender();
        AddShotLabel();

        for (Ingredient i : ingredients) {
            i.setParentFrame(this);
            i.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    i.intersect(bartender); // Pass the bartender object to check for intersection
                }
            });
            i.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {

                    updateText("", "", i.getName(), "");
                    //System.out.println("Mouse entered ingredient: " + i.getName());
                    if (served && Shaken) {
                        updateText("", "", "Press 'R' to see score", "");
                    }
                    if (Shaken && !served) {
                        updateText("Cannot add,", "Let's shake and serve!", "", "");
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (i.isHold() && !served && !Shaken) {
                        updateText("Press 'Spacebar' to pour!", "Hold 'Enter' to shake!", "", "Press 'S' to serve!");
                    } else if (!i.isHold()) {
                        updateText("Drag any ingredient", "to make your drink!", "", "");
                    } else if (served && Shaken) {
                        updateText("", "", "Press 'R' to see score", "");
                    } else if (shot >= 7) {
                        updateText("", "", "Press 'S' to serve!", "");
                    }
                }
            });
        }

        bartender.setParentFrame(this);
        themeSound = new MySoundEffect(path + n + sound);
        themeSound.setVolume(0.4f);
        initTextBox();
        initAudioControls();
        initSoundLevelControls();

    }

    public void AddBasicComponents() {
        int num = 1; //num1-6 : upper shelf, 7-10 : lower shelf
        ingredients.add(new Mixer("Soda", MyConstants.SODA, theme));
        ingredients.add(new Alcohol("Gin", MyConstants.GIN, theme));
        ingredients.add(new Alcohol("Vodka", MyConstants.VODKA, theme));
        ingredients.add(new Alcohol("Martini", MyConstants.MARTINI, theme));
        ingredients.add(new Alcohol("Rum", MyConstants.RUM, theme));
        ingredients.add(new Alcohol("Tequila", MyConstants.TEQUILA, theme));
        ingredients.add(new Mixer("Milk", MyConstants.MILK, theme));
        ingredients.add(new Sweetener("Passion Fruit", MyConstants.PASSION, theme));
        ingredients.add(new Sweetener("Orange", MyConstants.ORANGE, theme));
        ingredients.add(new Sweetener("Pomegranate", MyConstants.POMEGRANATE, theme));
        int x = 30, y = 90;
        for (Ingredient i : ingredients) {
            i.setDefaultPosition(x, y);
            contentpane.add(i);
            x += 74;
            num++;
            if (num == 7) {
                x = 33;
                y = 187;
            }
        }
        ingredients.add(new Topping("Ice", MyConstants.ICE, 83, 118, theme));
        AddtoFrame(10, 20, 273);
        ingredients.add(new Topping("Lemon", MyConstants.LEMON, 72, 65, theme));
        AddtoFrame(11, 134, 278);
        contentpane.revalidate();
        contentpane.repaint();
    }

    public void AddSpecialDrink() {
        int x = 326, y = 185, distance = 74;
        switch (theme) {
            case "Rooftop":
                ingredients.add(new Alcohol("Whiskey", path + theme + MyConstants.WHISKEY, theme));
                ingredients.add(new Alcohol("Champagne", path + theme + MyConstants.CHAMPAGNE, theme));
                break;
            case "Beach":
                ingredients.add(new Sweetener("Blue Hawaii", path + theme + MyConstants.BLUE, theme));
                ingredients.add(new Sweetener("Coconut Juice", path + theme + MyConstants.COCONUT, theme));
                break;
            case "MaidCafe":
                x = 315;
                distance = 85;
                ingredients.add(new Sweetener("Love Potion", path + theme + MyConstants.LOVE, 50, 70, theme));
                ingredients.add(new Alcohol("Sake", path + theme + MyConstants.SAKE, theme));
                break;
            case "ThaiTempleFestival":
                x = 315;
                distance = 85;
                ingredients.add(new Alcohol("Yadong", path + theme + MyConstants.YADONG, 55, 75, theme));
                ingredients.add(new Alcohol("Rice Whiskey", path + theme + MyConstants.RICE, theme));
                break;

        }
        for (int i = 12; i < ingredients.size(); i++) {
            ingredients.get(i).setDefaultPosition(x, y);
            contentpane.add(ingredients.get(i));
            x += distance;
        }
        contentpane.revalidate();
        contentpane.repaint();
    }

    public void AddSpecialTopping() {
        switch (theme) {
            case "Rooftop":
                ingredients.add(new Topping("Cherry", path + theme + MyConstants.CHERRY, 36, 47, theme));
                AddtoFrame(14, 220, 275);
                ingredients.add(new Topping("Olive", path + theme + MyConstants.OLIVE, 80, 25, theme));
                AddtoFrame(15, 206, 325);
                break;
            case "Beach":
                ingredients.add(new Topping("Seaweed", path + theme + MyConstants.SEAWEED, 65, 32, theme));
                AddtoFrame(14, 220, 311);
                ingredients.add(new Topping("Fruit Salad", path + theme + MyConstants.FRUIT, 57, 48, theme));
                AddtoFrame(15, 303, 295);
                break;
            case "MaidCafe":
                ingredients.add(new Topping("Ice cream", path + theme + MyConstants.ICECREAM, 52, 81, theme));
                AddtoFrame(14, 220, 262);
                ingredients.add(new Topping("Wasabi", path + theme + MyConstants.WASABI, 32, 85, theme));
                AddtoFrame(15, 307, 272);
                break;
            case "ThaiTempleFestival":
                ingredients.add(new Topping("Durian", path + theme + MyConstants.DURIAN, 73, 71, theme));
                AddtoFrame(14, 218, 272);
                ingredients.add(new Topping("ChilliSalt", path + theme + MyConstants.CHILLI, 30, 50, theme));
                AddtoFrame(15, 313, 293);
                break;
        }
        contentpane.revalidate();
        contentpane.repaint();
    }

    public void AddBartender() {
        bartender = new Character(name, path + theme + MyConstants.HOLD, 270, 200, theme);
        bartender.setDefaultPosition(500, 113);
        contentpane.add(bartender);
        contentpane.revalidate();
        contentpane.repaint();
    }

    private void AddShotLabel() {
        shotLabel = new JLabel();
        shotLabel.setSize(50, 70);
        shotLabel.setLocation(533, 380);
        shotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentpane.add(shotLabel);
        UpdateShotLevel(0); // Initialize with level 0
    }

    private void UpdateShotLevel(int level) {
        if (level >= 0 && level < shotLevels.length) {
            MyImageIcon icon = new MyImageIcon(shotLevels[level]).resize(40, 50);
            shotLabel.setIcon(icon);
            contentpane.revalidate();
            contentpane.repaint();
        }

        if (level >= 7) {
            updateText("", "", "Full! Let's shake!", "");
        }
    }

    private void addKeyListeners() {
        setFocusable(true); // Ensure the frame can receive key events
        requestFocus(); // Request focus for the frame

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 32 -> { // Space bar key code
                        if (!Shaken) {
                            Ingredient selected = getSelectedIngredient();
                            if (selected == null) {
                                return;
                            } else {
                                Pour(selected);
                            }
                        } /*if (selected != null && shot < 7 && !Shaken) {
                           
                            updateText("", "", selected.getName() + " poured!", "");
                        } else */ else if (served) {
                            updateText("", "", "Press 'R' to see score", "");
                        } else {
                            updateText("", "", "Cannot add drink anymore", "");
                        }
                    }

                    case 83 -> // 'S' key code
                    {
                        if (!served && Shaken) {
                            selectGlass();
                            selectedIngredient.getDefault();
                        } else if (!Shaken) {
                            updateText("", "", "Shake first!", "");
                        }

                    }
                    case KeyEvent.VK_ENTER -> {
                        if (shot > 0 && !served) {
                            Shake();

                        } else if (served) {
                            updateText("", "", "Press 'R' to see score", "");
                        } else {
                            updateText("", "", "   Empty! Add at least a Shot.", "");
                        }
                    }
                    case 82 -> { // 'R' key
                        if (served) {
                            drinkServed();

                        }
                    }

                }
            }
        });
    }

    public void AddtoFrame(int i, int x, int y) {
        ingredients.get(i).setDefaultPosition(x, y);
        contentpane.add(ingredients.get(i));
    }

    public void PlaySound() {
        themeSound.playLoop();
    }

    public void StopSound() {
        themeSound.stop();
    }

    public void initTextBox() {
        gameText1 = new JTextField("Drag any ingredient");
        gameText1.setEditable(false);
        gameText1.setOpaque(false);
        gameText1.setBorder(null);
        gameText1.setHorizontalAlignment(JTextField.CENTER);
        gameText1.setFont(new Font("Arial", Font.BOLD, 12));
        gameText1.setBounds(575, 372, 200, 50);
        contentpane.add(gameText1);

        gameText2 = new JTextField("to make your drink!");
        gameText2.setEditable(false);
        gameText2.setOpaque(false);
        gameText2.setBorder(null);
        gameText2.setHorizontalAlignment(JTextField.CENTER);
        gameText2.setFont(new Font("Arial", Font.BOLD, 12));
        gameText2.setBounds(578, 392, 200, 50); // adjusted position
        contentpane.add(gameText2);

        //middle box
        gameText3 = new JTextField("");
        gameText3.setEditable(false);
        gameText3.setOpaque(false);
        gameText3.setBorder(null);
        gameText3.setHorizontalAlignment(JTextField.CENTER);
        gameText3.setFont(new Font("Arial", Font.BOLD, 12));
        gameText3.setBounds(555, 390, 200, 50); // adjusted position
        contentpane.add(gameText3);

        gameText4 = new JTextField("");
        gameText4.setEditable(false);
        gameText4.setOpaque(false);
        gameText4.setBorder(null);
        gameText4.setHorizontalAlignment(JTextField.CENTER);
        gameText4.setFont(new Font("Arial", Font.BOLD, 12));
        gameText4.setBounds(575, 412, 200, 50); // adjusted position
        contentpane.add(gameText4);

        contentpane.repaint();
    }

    public void updateText(String text1, String text2, String text3, String text4 ) {
        gameText1.setText(text1);
        gameText2.setText(text2);
        gameText3.setText(text3);
        gameText4.setText(text4);
        contentpane.repaint();
    }

    public void initAudioControls() {
        tb = new JRadioButton[2];
        bgroup = new ButtonGroup();
        tb[0] = new JRadioButton("On");
        tb[0].setName("On");
        tb[0].setOpaque(false);
        tb[0].setContentAreaFilled(false);
        tb[0].setBorderPainted(false);
        tb[0].setForeground(Color.WHITE);

        tb[1] = new JRadioButton("Off");
        tb[1].setName("Off");
        tb[1].setOpaque(false);
        tb[1].setContentAreaFilled(false);
        tb[1].setBorderPainted(false);
        tb[1].setForeground(Color.WHITE);

        tb[0].setSelected(true); // Default to "On"

        bgroup.add(tb[0]);
        bgroup.add(tb[1]);

        // Position the radio buttons in the top-right corner
        tb[0].setBounds(730, 10, 50, 20);
        tb[1].setBounds(730, 28, 60, 30);

        // Add the radio buttons to the content pane
        contentpane.add(tb[0]);
        contentpane.add(tb[1]);

        // Load the images for "On" and "Off" states
        MyImageIcon onIcon = new MyImageIcon(MyConstants.ON).resize(30, 30);
        MyImageIcon offIcon = new MyImageIcon(MyConstants.OFF).resize(30, 30);

        // Add ItemListener to handle state changes
        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JRadioButton button = (JRadioButton) e.getItem();
                if (button.getName().equals("On")) {
                    PlaySound();
                    soundIcon.setIcon(onIcon);
                } else if (button.getName().equals("Off")) {
                    StopSound();
                    soundIcon.setIcon(offIcon);

                }
                GameFrame.this.requestFocusInWindow();
            }
        };

        tb[0].addItemListener(itemListener);
        tb[1].addItemListener(itemListener);

        contentpane.revalidate();
        contentpane.repaint();
    }

    public void initSoundLevelControls() {
        String[] levels = {"20%", "40%", "60%", "80%", "100%"};
        soundLevel = new JList<>(levels);
        soundLevel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        soundLevel.setSelectedIndex(3); // Default to 60%
        soundLevel.setVisible(false);
        soundLevel.setFont(new Font("Arial", Font.BOLD, 12));

        soundScrollPane = new JScrollPane(soundLevel);
        soundScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        soundScrollPane.setBounds(700, 60, 60, 60);
        soundScrollPane.setVisible(false);
        contentpane.add(soundScrollPane, Integer.valueOf(3));

        soundIcon = new JLabel(new MyImageIcon(MyConstants.ON).resize(30, 30));
        soundIcon.setBounds(700, 17, 30, 30);
        soundIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = !soundLevel.isVisible();
                soundLevel.setVisible(isVisible);
                soundScrollPane.setVisible(isVisible);
                revalidate();
                GameFrame.this.requestFocusInWindow();
            }
        });
        contentpane.add(soundIcon, Integer.valueOf(3));

        soundLevel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String selected = soundLevel.getSelectedValue();
                //System.out.println("Volume Changes: " + selected);
                adjustVolume(selected);
                soundLevel.setVisible(false);
                soundScrollPane.setVisible(false);
                GameFrame.this.requestFocusInWindow();
            }
        });
    }

    public void adjustVolume(String selectedVolume) {
        float volume = switch (selectedVolume) {

            case "20%" ->
                0.2f;
            case "40%" ->
                0.4f;
            case "60%" ->
                0.6f;
            case "80%" ->
                0.8f;
            case "100%" ->
                1.0f;
            default ->
                0.5f;
        };

        // Set the volume in the themeSound
        themeSound.setVolume(volume);

        // If the volume is 0%, turn off the radio button
        // If the volume is not 0%, ensure the "On" radio button is selected
        tb[0].setSelected(true);
        PlaySound(); // Ensure the sound is playing
        soundIcon.setIcon(new MyImageIcon(MyConstants.ON).resize(30, 30)); // Update the sound icon to "On"

    }

    public void setSelectedIngredient(Ingredient i) {
        selectedIngredient = i;
        //System.out.printf("selected: %s\n", i.getName());
    }

    public Ingredient getSelectedIngredient() {
        return selectedIngredient;
    }

    public ArrayList<Ingredient> getAllIngredients() {
        return ingredients;
    }

    public void drinkServed() {
        StopSound();
        this.setVisible(false);
        new Result(this.theme, pouredIngredients, this).setVisible(true);
    }

    public void Pour(Ingredient selected) {
        if (!Shaken && shot < 7) {
            selectedIngredient.Pour();
            bartender.Pour();
            shot++;
            pouredIngredients.add(selected);
            UpdateShotLevel(shot);
            if (isSoundOn()) {
                pourSound = new MySoundEffect(base + MyConstants.POURSOUND);
                pourSound.setVolume(2.4f);
                pourSound.playOnce();
            }

        }
        if (shot >= 7) {
            updateText("Max shots!", "Hold 'Enter' to shake", "", "");
        } else {
            updateText("", "", selected.getName() + " added! (" + shot + "/7)", "");
        }
    }

    public void selectGlass() {
        if (readytoserve) {
            JDialog sg = new JDialog(this, "Select Your Glass!", true);
            sg.setSize(440, 248);
            sg.setLayout(null);
            sg.setLocationRelativeTo(this);

            ButtonGroup glassGroup = new ButtonGroup();

            JRadioButton glass1 = createRadioButton(51, 160, "glass1");
            JRadioButton glass2 = createRadioButton(125, 160, "glass2");
            JRadioButton glass3 = createRadioButton(210, 160, "glass3");
            JRadioButton glass4 = createRadioButton(287, 160, "glass4");
            JRadioButton glass5 = createRadioButton(365, 160, "glass5");

            glass1.setFocusable(false);
            glass2.setFocusable(false);
            glass3.setFocusable(false);
            glass4.setFocusable(false);
            glass5.setFocusable(false);

            glassGroup.add(glass1);
            glassGroup.add(glass2);
            glassGroup.add(glass3);
            glassGroup.add(glass4);
            glassGroup.add(glass5);

            sg.add(glass1);
            sg.add(glass2);
            sg.add(glass3);
            sg.add(glass4);
            sg.add(glass5);

            MyImageIcon selectG = new MyImageIcon(MyConstants.SELECTGLASS).resize(440, 248);

            JLabel backgroundLabel = new JLabel(selectG);
            backgroundLabel.setBounds(0, 0, 440, 248);

            Enter = new JTextField("Press 'Enter' to continue!");
            Enter.setEditable(false);
            Enter.setOpaque(false);
            Enter.setBorder(null);
            Enter.setHorizontalAlignment(JTextField.LEFT);
            Enter.setFont(new Font("Arial", Font.BOLD, 12));
            Enter.setForeground(Color.WHITE);
            Enter.setBounds(150, 175, 200, 50);
            sg.add(Enter);
            sg.add(backgroundLabel);
            sg.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10 && glassGroup.getSelection() != null) {
                        selectedGlass = glassGroup.getSelection().getActionCommand();
                        //System.out.println(selectedGlass);
                        sg.dispose();
                        Drink();
                    }
                }
            });

            sg.setFocusable(true);
            sg.requestFocusInWindow();

            sg.setVisible(true);
        }
    }

    private JRadioButton createRadioButton(int x, int y, String glassName) {
        JRadioButton radioButton = new JRadioButton();
        radioButton.setOpaque(false);
        radioButton.setBounds(x, y, 30, 30);
        radioButton.setActionCommand(glassName);
        return radioButton;
    }

    public void SetName(String n) {
        Bar = new JTextField("Bartender");
        Bar.setEditable(false);
        Bar.setOpaque(false);
        Bar.setBorder(null);
        Bar.setHorizontalAlignment(JTextField.LEFT);
        Bar.setFont(new Font("Arial", Font.BOLD, 12));
        Bar.setForeground(Color.WHITE);
        Bar.setBounds(100, 425, 200, 50);
        contentpane.add(Bar);

        nameField = new JTextField(n); // Use the passed name
        name = nameField.getText();
        nameField.setEditable(false);
        nameField.setOpaque(false);
        nameField.setBorder(null);
        nameField.setHorizontalAlignment(JTextField.LEFT);
        nameField.setFont(new Font("Arial", Font.BOLD, 20));
        nameField.setForeground(Color.WHITE);
        nameField.setBounds(85, 402, 200, 50);
        contentpane.add(nameField);

        contentpane.repaint();
    }

    public void Shake() {
        Shaken = true;

        selectedIngredient.Shake();
        updateText("", "", "     Shaken! Press 'S' to serve!", "");
        readytoserve = true;
        if (isSoundOn()) {
            shakeSound = new MySoundEffect(base + MyConstants.SHAKESOUND);
            shakeSound.setVolume(0.2f);
            shakeSound.playOnce();
        }
        //Thread.sleep(200);
        bartender.Shake();

    }

    public void Drink() {

        bartender.Drink();

        JLayeredPane layeredPane = getLayeredPane();

       
        layeredPane.setLayer(bartender, JLayeredPane.DEFAULT_LAYER);

        
        MyImageIcon glassIcon = new MyImageIcon(MyConstants.BASEPATH + selectedGlass + ".png").resize(50, 50);
        JLabel glassLabel = new JLabel(glassIcon);
        glassLabel.setBounds(530, 220, 50, 50);
        layeredPane.add(glassLabel, JLayeredPane.PALETTE_LAYER);

        layeredPane.revalidate();
        layeredPane.repaint();
        served = true;
        updateText("", "", "Press 'R' to see score", "");

    }

    public boolean isShaken() {
        return Shaken;
    }

    public boolean isSoundOn() {
        return tb[0].isSelected();
    }
}
