import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import javax.sound.sampled.*;

public class Game {
   //frame
    private JFrame f;
    //pixel font
    private Font pixelDialogue;
    //levels for the game
    private int day = 1;
    private int phoneLevel = 0;
    private int compLevel = 0;
    private int clothesLevel = 0;
    //global character and fridge, so that it's data can be saved
    private Character c;
    private Fridge fridge;
    private boolean fridging;
    //global background image, so that it can be chagned when you fold your clothes
    private Image bg;
    //arraylists to hold scripts
    private ArrayList<Dialogue> introText = new ArrayList<Dialogue>();
    private ArrayList<Dialogue> phoneText= new ArrayList<Dialogue>();
    private ArrayList<Dialogue> compText = new ArrayList<Dialogue>();
    private ArrayList<Dialogue> clothesText = new ArrayList<Dialogue>();
    private ArrayList <Dialogue> foodText = new ArrayList<Dialogue>();
    private ArrayList <Dialogue> doorText = new ArrayList<Dialogue>();
    private ArrayList <Dialogue> aerobicsText = new ArrayList<Dialogue>();
    //for pausing
    private JComponent currentComponent;
    
    
    //Constructor: loads the font, creates the frame, starts the first day off
    public Game() {
        //frame
        f = new JFrame("ReLive");
        f.setSize(600, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        //character
        c = new Character(this);
        //Draw the loading screen before we start loading things
        Loading l = new Loading();
        f.add(l);
        f.repaint();
        f.revalidate();
         //load the font
        try {
            File fontFile = new File("fonts/PressStart2P-Regular.ttf");
            pixelDialogue = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(15f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelDialogue);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        // I'm going to be fully honest. The above code came from ChatGPT, with minor edits.
        // I could not figure out how to get the stupid fonts in for the life of me.
        
        //fridge
        fridge = new Fridge(pixelDialogue, this);
        //load the background images
        
      bg = null;
      Image roomBG = null;
      Image phoneBG = null;
      Image compBG = null;
      Image clothesUNFOLDED = null;
      Image clothesFOLDED = null;
      Image fridgeBG = null;
      try {
         roomBG =ImageIO.read(new File("images/backgrounds/wake up.png"));
         phoneBG = ImageIO.read(new File("images/backgrounds/phone background.png"));
         compBG = ImageIO.read(new File("images/backgrounds/computer.png"));
         bg = ImageIO.read(new File("images/backgrounds/room.png"));
         clothesUNFOLDED = ImageIO.read(new File("images/backgrounds/clothes background.png"));
         clothesFOLDED = ImageIO.read(new File ("images/backgrounds/folded background.png"));
         fridgeBG = ImageIO.read(new File("images/backgrounds/fridgeCLOSED.png"));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      //load Dialogues
      introText = loadDialogue( new File("texts/intro.txt"), roomBG);
      doorText = loadDialogue( new File("texts/door.txt"), roomBG);
      aerobicsText = loadDialogue( new File("texts/aerobics.txt"), null);
      foodText = loadDialogue( new File("texts/food.txt"), fridgeBG);
      phoneText = loadDialogue(new File("texts/phone.txt"), phoneBG);
      compText = loadDialogue(new File ("texts/comp.txt"), compBG);
      clothesText = loadDialogue(new File ("texts/clothesU.txt"), clothesUNFOLDED);
      ArrayList<Dialogue> clothesF = loadDialogue(new File ("texts/clothesF.txt"), clothesFOLDED);
      for (Dialogue d: clothesF) {
         clothesText.add(d);
      }
      /*pause so that the loading screen can show you it's true beauty
      try {
         Thread.sleep(2000); 
      } catch (InterruptedException e) {
         e.printStackTrace();
      }*/
      f.remove(l);
      /*move on to the main menu*/
      MainMenu m = new MainMenu(pixelDialogue, this);
      f.add(m);


      f.revalidate();
      f.repaint();

    }


    //create a new game and start the running process
    public static void main(String[] args) {
        Game g = new Game();
    }

   //method so that I don't have to rewrite the loading process everytime
   public ArrayList<Dialogue> loadDialogue (File f, Image bg) {
      //the Sarahs
      Image happy = null;
      Image happyTalk = null;
      Image sad = null;
      Image sadTalk = null;
      //load all the images & variables that throw exceptions
      try {
         happy = ImageIO.read(new File("images/characters/h.png"));
         happyTalk = ImageIO.read(new File("images/characters/ht.png"));
         sad = ImageIO.read(new File("images/characters/s.png"));
         sadTalk = ImageIO.read(new File("images/characters/st.png"));
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      ArrayList<Dialogue> d = new ArrayList<Dialogue>();
      //arraylists to pass to the dialogues when they're created
      ArrayList<String> text = new ArrayList<String>();
      ArrayList<Image> talk = new ArrayList<Image>();
      try {
         //load reader
         BufferedReader br = new BufferedReader(new FileReader(f));
         //first line is a placeholder, and skippable
         br.readLine();
         //second line is the number of dialogues
         int num = Integer.parseInt(br.readLine().trim());
         //repeat for all of the dialogues
         for (int i =0; i < num; i++) {
            //first line is the number of lines
         int numLines = Integer.parseInt(br.readLine().trim());
         for (int l = 0; l <numLines; l++) {
            String lineText = br.readLine();
            text.add(lineText.trim());
            int sarah = Integer.parseInt(br.readLine().trim());
            if (sarah ==0) {
               talk.add(null);
            }
            else if (sarah ==1) {
               talk.add(happy);
            }
            else if (sarah ==2) {
               talk.add(happyTalk);
            }
            else if (sarah ==3) {
               talk.add(sad);
            }
            else {
               talk.add(sadTalk);
            }
         }
         d.add(new Dialogue(bg, text, talk, pixelDialogue, this));
         text.clear();
         talk.clear();
         }
         br.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return d;
   }

    //method that draws the ending screne.
    public void endScreen() {
      f.getContentPane().removeAll();
      EndScreen e = new EndScreen(pixelDialogue, this);
      f.add(e);
      f.revalidate();
      f.repaint();
    }
   
    //method that decides what to do once a choice has been made on the main menu
    public void choiceMade (MainMenu m) {
      f.remove(m);
      //new game
      if (m.getChoice() == 1) {
         //start off with the first day
         NewDay n = new NewDay(day, pixelDialogue, this);
         f.add(n);
         n.run();
         f.revalidate();
         f.repaint();
      }
      //load game
      else if (m.getChoice() ==2) {
         //loading screen so that it doesn't take too long
         Loading l = new Loading();
         f.add(l);
         try {
            //load the data
            BufferedReader br = new BufferedReader(new FileReader(new File("texts/saveFile.txt")));
            //first line is a placeholder
            br.readLine();
            //next is the day
            String line = br.readLine();
            day = Integer.parseInt(line.trim());
            //next couple of lines are the levels
            line = br.readLine();
            phoneLevel = Integer.parseInt(line.trim());
            line = br.readLine();
            compLevel = Integer.parseInt(line.trim());
            line = br.readLine();
            clothesLevel = Integer.parseInt(line.trim());
            //check if we need to change things based off clothes level
            if (clothesLevel >3) c.changeClothes();
            if (clothesLevel > 2) changeBG();
            line = br.readLine();
            int[] temp = fridge.getAvailability();
            for (int i = 0; i < temp.length; i++) {
               line = br.readLine();
               temp[i] = Integer.parseInt(line.trim());
               System.out.println(line);
            }
            fridge.setAvailability(temp);
            //next two lines are the morale and energy
            line = br.readLine();
            int morale = Integer.parseInt(line.trim());
            line = br.readLine();
            int energy = Integer.parseInt(line.trim());
            c.setMax(morale, energy);
            br.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
         //clean up
         f.remove(l);
         //start the game
         NewDay n = new NewDay(day, pixelDialogue, this);
         f.add(n);
         n.run();
         f.revalidate();
         f.repaint();
      }
      //tutorials
      else if (m.getChoice() == 3) {
         Tutorial t = new Tutorial (this);
         f.add(t);
         f.repaint();
         f.revalidate();
      }
      //quit
      else {
         quitter();
      }
    }
    
    //method to change the background: called when a certain clothes level has been passed
    public void changeBG() {
      try {
         bg = ImageIO.read(new File("images/backgrounds/folded clothes.png"));
      } catch (IOException e) {
         e.printStackTrace();
      }
    }
   
    //method that all of the components call to draw the background
    public void drawBG(Graphics g) {
         g.drawImage(bg,0,0, null);
         //draw pause button
         g.setColor(Color.black);
         g.fillRect(520, 10, 50,50);
         g.setColor(Color.white);
         g.fillRect(524, 14, 42, 42);
         g.setColor(Color.black);
         g.fillRect(533, 20, 4, 30);
         g.fillRect(551, 20, 4, 30);
    }
   
    //overloaded method for a specific image
    public void drawBG(Graphics g, Image bg) {
         if (bg != null) {
            g.drawImage(bg,0,0, null);
         //draw pause button
         g.setColor(Color.black);
         g.fillRect(520, 10, 50,50);
         g.setColor(Color.white);
         g.fillRect(524, 14, 42, 42);
         g.setColor(Color.black);
         g.fillRect(533, 20, 4, 30);
         g.fillRect(551, 20, 4, 30);
         }
         else drawBG(g);
    }
   
    //method that Character class calls when it wants to interact with something
    public void charInteract (char item) {
      f.remove(c);
      //if it's the yoga mat
      if (item == 'y') {
         //play text if you don't have enough morale or energy
         if (c.getMorale() <=30 || c.getEnergy() <=30) {
            aerobicsText.get(0).reset();
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
            f.add(aerobicsText.get(0));
         }
         else {
            Aerobics y = new Aerobics (pixelDialogue, this);
            f.add(y);
            y.requestFocusInWindow();
         }
      }
      //if it's the door
      else if (item == 'd') {
         //if we're not at max, play the normal dialogue
         if (c.getEnergy() <100 || c.getMorale() <100) {
            doorText.get(0).reset();
            f.add(doorText.get(0));
         }
         //if we're at max, play the end dialogue
         else {
            f.add(doorText.get(1));
         }
      }
      //if it's the fridge
      if (item == 'f') {
         //already finished
         if (Arrays.equals(fridge.getAvailability(), new int[6])) {
            foodText.get(foodText.size()-1).reset();
            f.add(foodText.get(foodText.size()-1));
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
         }
         //already interacted
         else if (c.getInteract()[3] != 0) {
            foodText.get(foodText.size()-2).reset();
            f.add(foodText.get(foodText.size()-2));
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
         }
         //otherwise, if first time interacting
         else if (fridge.isFirstTime()) {
            foodText.get(0).reset();
            f.add(foodText.get(0));
            fridging = true;
         }
         //otherwise
         else {
            foodText.get(1).reset();
            f.add(foodText.get(1));
            fridging = true;
         }
      }
      //if it's the phone, play the phone level that it's at
      if (item == 'p') {
         //if they're at the end, display the last message
         if (phoneLevel == phoneText.size()-2) {
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
            phoneText.get(phoneText.size()-1).reset();
            f.add(phoneText.get(phoneText.size()-1));
         }
         //otherwise if they've already interacted, print the second last message
         else if (c.getInteract()[1] != 0) {
            f.add(phoneText.get(phoneText.size()-2));
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
         }
         //otherwise, display the level message
         else {
            f.add(phoneText.get(phoneLevel));
            phoneLevel++;
         }
      }
      //same for compputer
      else if (item == 'c') {
      //if they're at the end, display the last message
     if (compLevel == compText.size()-2) {
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
         compText.get(compText.size()-1).reset();
        f.add(compText.get(compText.size()-1));
      }
         //otherwise if they've already interacted, print the second last message
         else if (c.getInteract()[0] != 0) {
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
         compText.get(compText.size()-2).reset();
            f.add(compText.get(compText.size()-2));
         }
         //otherwise, display the level message
         else {
            f.add(compText.get(compLevel));
            compLevel++;
         }
    }
      //same for clothes
      else if (item == 'l') {
      //if they're at the end, display the last message
     if (clothesLevel == clothesText.size()-2) {
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
         clothesText.get(clothesText.size()-1).reset();
        f.add(clothesText.get(clothesText.size()-1));
      }
         //otherwise if they've already interacted, print the second last message
         else if (c.getInteract()[2] != 0) {
            //don't lose morale
            c.changeMorale(10);
            c.changeMaxMorale(-10);
            //background changes dep on level
            if (clothesLevel ==0) {
               clothesText.get(2).reset();
               f.add(clothesText.get(2));
            }
            else {
               clothesText.get(clothesText.size()-2).reset();
               f.add(clothesText.get(clothesText.size()-2));
            }
         }
         //otherwise, display the level message
         else {
            //certain messages increase morale
            if (clothesLevel == 3) c.changeMorale(20);
            f.add(clothesText.get(clothesLevel));
            clothesLevel++;
            //if past a certain level have to add 1, and change the background
            if (clothesLevel == 2) {
               clothesLevel++;
               changeBG();
            }
            //if past a certain level have to change clothes
            if (clothesLevel ==4) {
               c.changeClothes();
            }
         }

    }

      f.repaint();
      f.revalidate();
    }
    
    //method for deciding what to do when the screen is paused 
    public void pause (JComponent component) {
      //create instance of pausescreen
      PauseScreen p = new PauseScreen (pixelDialogue, this);
      currentComponent = component;
      f.getContentPane().removeAll();
      f.add(p);
      f.repaint();
      f.revalidate();
    }
    
    //method to return to game after pause
    public void returnToGame() {
      f.getContentPane().removeAll();
      if (currentComponent instanceof Character) {
            f.add(c);
            c.requestFocusInWindow();
      }
      else {
         f.add(currentComponent);
      }
      f.repaint();
      f.revalidate();
    }
    
    //method to return to main screen
    public void returnToMainMenu() {
      //reset all the levels
      phoneLevel = 0;
      compLevel = 0;
      fridge = new Fridge(pixelDialogue, this);
      day = 1;
      //reset the character data
      c.setMax(10,50);
      //go back to the main menu
      f.getContentPane().removeAll();
      MainMenu m = new MainMenu(pixelDialogue, this);
      f.add(m);
      f.repaint();
      f.revalidate();
    }
    
    //method to save the game
    public void saveGame() {
      try {
         FileWriter w = new FileWriter("texts/saveFile.txt");
         w.write("placeholder\n");
         w.write(day+"\n");
         w.write(phoneLevel+"\n");
         w.write(compLevel+"\n");
         w.write(clothesLevel+"\n");
         int[] temp = fridge.getAvailability();
         for (int i: temp) {
            w.write(i+"\n");
         }
         w.write(c.getMaxMorale()+"\n");
         w.write(c.getMaxEnergy()+"\n");
         w.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
    }
    
    //method to quit
    public void quitter() {
      f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
    }


    //method that is called by the component when it is finished, and ONLY when it is
    //utterly finished. The Character, for example, calls another method as long as it
    //isn't dead. decides where to go from here
    public void moveOn (JComponent current) {
      f.remove(current);
      //if the end dialogue just played, end the game
      if (current.equals(doorText.get(1))) {
         EndScreen e = new EndScreen(pixelDialogue, this);
         f.add(e);
         e.run();
      }
      //otherwise if the aerobics just played, reduce energy and let the character walk
      else if (current instanceof Aerobics) {
         c.changeEnergy(-30);
         int p = ((Aerobics) current).getPoints();
         if (p <10) p*=2;
         else if (p >=50) p/=4;
         else if (p >=20) p/=2;
         c.changeMaxEnergy(p);
         f.add(c);
         c.requestFocusInWindow();
      }
      //otherwise if the fridge dialogue just finished, show the fridge
      else if (fridging && current instanceof Dialogue) {
         fridge.reset();
         f.add(fridge);
         fridging = false;
      }
      //otherwise if the fridge has just happened
      else if (current instanceof Fridge) {
         c.changeEnergy(fridge.getNutrition()[0]/2);
         c.changeMaxEnergy(fridge.getNutrition()[0]);
         c.changeMorale(fridge.getNutrition()[1]);
         f.add(c);
         c.requestFocusInWindow();
      }
      //if the first day, there's a special order to things
      else if (day == 1) {
        //if the day has just started, play the intro dialogue
        if (current instanceof NewDay) {
          f.getContentPane().add(introText.get(0));
        }
        //otherwise, if the intro dialogue has just finished and you haven't interacted with anything, play the tutorial dialogue
        else if (current instanceof Dialogue && Arrays.equals(c.getInteract(), new int[5])) {
            TutText t = new TutText(pixelDialogue, this);
            f.add(t);
        }
        //otherwise, if you've finished an actual interaction, lose morale and move on
        else if (current instanceof Dialogue) {
            f.add(c);
            c.requestFocusInWindow();
            c.changeMaxMorale(10);
            c.changeMorale(-10);
        }
        //if it's tutorial text, let the character move
        else if ( current instanceof TutText) {
         f.add(c);
         c.requestFocusInWindow();
        }
        //if you've finished moving, end the day
        else if (current instanceof Character) {
          f.remove(c);
          c.reset();
          SleepyTime s = new SleepyTime(pixelDialogue,this);
          f.add(s);
          day++;
          s.run();
        }
      }
      //default day runner
      else {
         //otherwise: if you've just finished, start a new day
         if (current instanceof SleepyTime) {
            NewDay n = new NewDay(day, pixelDialogue, this);
            //if we've reached a certain clothesLevel, Alex changes clothes in the morning
            if (clothesLevel >=2) c.changeClothes();
            f.add(n);
            n.run();
         }
         //if you've just woken up, resets the character, let you walk
         if (current instanceof NewDay) {
            f.remove(current);
            c.reset();
            f.add(c);
            c.requestFocusInWindow();
         }
         //if you've interacted, lose morale, let you walk
         if (current instanceof Dialogue) {
            f.remove(current);
            c.changeMaxMorale(10);
            c.changeMorale(-10);
            f.add(c);
            c.requestFocusInWindow();
         }
         //if the character is passed, that's because it's dead
         if (current instanceof Character) {
            f.remove(current);
            c.reset();
            SleepyTime s = new SleepyTime(pixelDialogue, this);
            f.add(s);
            day++;
            s.run();
         }
      }
      f.revalidate();
      f.repaint();
     
    }

}

//class that draws the pause screen
class PauseScreen extends JComponent implements MouseListener {
   private Font f;
   private Game sup;
   
   //constructor: gets font and game
   public PauseScreen (Font f, Game sup) {
      this.f = f;
      this.sup = sup;
      addMouseListener(this);
   }
   
   public void paint(Graphics g) {
      sup.drawBG(g);
      //fade
      g.setColor(new Color(0,0,0,50));
      g.fillRect(0,0,600,600);
      //button
      g.setColor(Color.black);
      g.fillRect(520, 10, 50,50);
      g.setColor(Color.white);
      g.fillRect(524, 14, 42, 42);
      g.setColor(Color.black);
      int[] x = {541, 551, 541};
      int[] y = {23, 34, 47};
      g.fillPolygon(x, y, 3);
      //option boxes
      g.fillRect(157,200,285,31);
      g.fillRect(157,260,285,31);
      g.fillRect(157,320,285,31);
      g.fillRect(157,380,285,31);
      //option boxes text
      g.setColor(Color.white);
      g.setFont(f);
      g.drawString("Return to Game", 194, 224);
      g.drawString("Save Game", 231, 284);
      g.drawString("Quit to Main Menu", 171, 344);
      g.drawString("Quit to Desktop", 186, 404);
   }
   
    public void mousePressed(MouseEvent e) {
      //play button
      if (e.getX() >=520 && e.getX() <=570 && e.getY() >=10 && e.getY() <=60) sup.returnToGame();
      else if (e.getX() >=157 && e.getX() <= 442) {
         if (e.getY() >=200 && e.getY() <=231) sup.returnToGame();
         else if (e.getY() >=260 && e.getY() <=291) sup.saveGame();
         else if (e.getY() >=320 && e.getY() <=351) sup.returnToMainMenu();
         else if (e.getY() >=380 && e.getY() <=411) sup.quitter();
      }
    }

    // mouse listener methods that I'm not using
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}

//class that runs the main menu
class MainMenu extends JComponent implements MouseListener {
   private Font f;
   private Game sup;
   private int choice = 0;
   
   //Constructor: gets font and game
   public MainMenu(Font f, Game sup) {
      this.f = f;
      this.sup = sup;
      addMouseListener(this);
   }
   
   //getter method for choice
   public int getChoice () {
      return choice;
   }
   
   //paint method, draws the background
   public void paint (Graphics g) {
      g.setColor(new Color(247, 184, 193));
      g.fillRect(0,0,600,600);
      //title
      Font bigger = f.deriveFont(80f);
      g.setFont(bigger);
      g.setColor(Color.white);
      g.drawString("RELIVE",60,160);
      //option boxes
      g.fillRect(200,230,200,31);
      g.fillRect(200,290,200,31);
      g.fillRect(200,350,200,31);
      g.fillRect(200,410,200,31);
      //option boxes text
      g.setColor(Color.black);
      g.setFont(f);
      g.drawString("New Game", 240, 254);
      g.drawString("Load Game", 233, 314);
      g.drawString("Tutorial", 248, 374);
      g.drawString("Quit", 270, 434);
   }
   
   //when the mouse is pressed: change choice depending on which button was clicked, then
   //call sup to move on
    public void mousePressed(MouseEvent e) {
      System.out.println(e.getX()+", "+e.getY());
    //if new game
      if (e.getX() >= 200 && e.getX() <= 400 && e.getY() >=230 && e.getY() <=261) choice = 1;
      //if load game
      else if (e.getX() >= 200 && e.getX() <= 400 && e.getY() >=290 && e.getY() <=321) choice = 2;
      //if tutorial
      else if (e.getX() >= 200 && e.getX() <= 400 && e.getY() >=350 && e.getY() <=381) choice = 3;
      //if quit
      else if (e.getX() >= 200 && e.getX() <= 400 && e.getY() >=410 && e.getY() <=441) choice = 4;
      //if something useful has been clicked, move on
      if (choice != 0) sup.choiceMade(this);
    }




    // mouse listener methods that I'm not using
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


}

//class for the player character when it's moving
class Character extends JComponent implements KeyListener, MouseListener {
    //images
    private Image you;
    private Image[] outfits;
    private Image eKey;
    //data
    private int x;
    private int y;
    private int morale;
    private int maxMorale;
    private int energy;
    private int maxEnergy;
    //for interactions
    private boolean interactable;
    private int[] prevInteract;
    //for moving on
    private Game sup;


    public Character (Game g) {
      addKeyListener(this);
      addMouseListener(this);
      setFocusable(true);
      requestFocusInWindow();
      this.sup = g;
      this.maxMorale = 10;
      this.maxEnergy = 40;
      this.x = 250;
      this.y = 155;
      this.morale = maxMorale;
      this.energy = maxEnergy;
      //load the images
      outfits = new Image[5];
      try {
         you = ImageIO.read(new File("images/characters/alexGROSS.png"));
         eKey = ImageIO.read(new File("images/characters/eKey.png"));
         outfits[0] = ImageIO.read(new File("images/characters/alexVINDICATOR.png"));
         outfits[1] = ImageIO.read(new File("images/characters/alexSHAMAN.png"));
         outfits[2] = ImageIO.read(new File("images/characters/alexPUCK.png"));
         outfits[3] = ImageIO.read(new File("images/characters/alexSNOWBIRD.png"));
         outfits[4] = ImageIO.read(new File("images/characters/alexAURORA.png"));
      } catch (Exception e) {
        e.printStackTrace();
      }
      eKey = eKey.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
      prevInteract = new int[5];
    }


    //method to reset data to default at the start of a day
    public void reset() {
      energy = maxEnergy;
      morale = maxMorale;
      x = 250;
      y = 155;
      prevInteract = new int[5];
         interactable = false;
    }
   
    //getter method for interactions
    public int[] getInteract() {
      return prevInteract;
    }
   
    //getter methods for morale and energy, and their respective maximums
    public int getMorale() {
      return morale;
    }
   
    public int getEnergy () {
      return energy;
    }
    
    public int getMaxMorale() {
      return maxMorale;
    }
    
    public int getMaxEnergy() {
      return maxEnergy;
    }
   
    //methods to change morale and energy
    public void changeMorale (int changer) {
       morale += changer;
       if (morale >100) morale = 100;
       if (morale <=0) sup.moveOn(this);
    }
   
    public void changeEnergy (int changer) {
       energy += changer;
       if (energy >100) energy = 100;
       if (energy <=0) sup.moveOn(this);
    }
   
    //methods to change maximum morale and energy
    public void changeMaxMorale (int changer) {
       maxMorale += changer;
       if (maxMorale >100) maxMorale = 100;
    }
   
    public void changeMaxEnergy (int changer) {
       maxEnergy += changer;
       if (maxEnergy >100) maxEnergy = 100;
    }

    //setter method for maximum morale and energy
    public void setMax (int m, int e) {
      maxMorale = m;
      maxEnergy = e;
    }

   //method to change clothes
   public void changeClothes () {
      int r = (int) (Math.random() *5);
      Image n = outfits[r];
      while (you == n) {
         r = (int) (Math.random() *5);
         n = outfits[r];
      }
      you = n;
   }

    //method to move. change x and y by parameters, then repaint
    public void bouger (int r, int d) {
      //change coordinates
      x+=r;
      y+=d;
      System.out.println(x+", "+y);
      //reduce energy when you move
      changeEnergy(-2);
      //don't let the character move out of the floor
      if (x>460) x = 460;
      if (y>420) y = 420;
      if ( x<-80) x = -80;
      if (y<120) y = 120;
      //don't let it move through the bed
      if (x>=90 && x <=240 && y <=280) y = 280;
      else if (y >=120 && y <=280 && x <= 240 && x >=200) x = 240;
      else if (y >=120 && y <=280 && x <= 180 && x >=90) x =90;
      //checking for other interactions: in multiple lines for readability
      if (x>=350 && y<=200) interactable = true;
      else if (x>=40 && x<=100 && y >=295 && y <=360) interactable = true;
      else if (x<=50 && y>=135 && y <=260) interactable = true;
      else if (x >=410 && y >= 235 && y <= 295) interactable = true;
      else if (x>=-70 && x<=90 && y >=415) interactable = true;
      else if (y>=315 && x>=190) interactable = true;
      else interactable = false;
      repaint();
    }


    public void paint(Graphics g) {
      sup.drawBG(g);
      //character
      g.drawImage(you,x,y, null);
      //bars
      g.setColor(Color.black);
      g.fillRect(20,20,25, 102);
      g.fillRect(60,20,25, 102);
      //morale
      int fill = (int) (80* (morale/100.0));
      g.setColor(new Color(3,143,143,90));
      g.fillRect(25,30,15,80- fill);
      g.setColor(new Color(3,143,143));
      g.fillRect(25,110- fill,15,fill);
      //energy
      fill = (int) (80* (energy/100.0));
      g.setColor(new Color(255,194,14, 90));
      g.fillRect(65,30,15,80- fill);
      g.setColor(new Color(255,194,14));
      g.fillRect(65,110- fill,15,fill );
      //button
      if (interactable) {
         g.drawImage(eKey, x+85, y, null);
      }
    }


    //move when the key is released <-- so that it doesn't go too fast
    public void keyReleased(KeyEvent e) {
      //movement
      if (e.getKeyChar() == 'w') bouger(0,-20);
      else if (e.getKeyChar() == 'a') bouger(-20,0);
      else if (e.getKeyChar() == 's') bouger(0,20);
      else if (e.getKeyChar() == 'd') bouger(20,0);
      //interactions
      if (e.getKeyChar() == 'e' && interactable) {
         //computer
         if (x>=350 && y<=200) {
            sup.charInteract('c');
            prevInteract[0] = 1;
         }
         //phone
         else if (x>=40 && x<=100 && y >=295 && y <=360) {
            sup.charInteract('p');
            prevInteract[1] =1;
         }
         //clothes
         else if (x<=50 && y>=135 && y <=260) {
            sup.charInteract('l');
            prevInteract[2] = 1;
         }
         //yoga
         else if (x >=410 && y >= 235 && y <= 295) {
            sup.charInteract('y');
         }
         //door
         else if (x>=-70 && x<=90 && y >=415) {
            sup.charInteract('d');
         }
         //fridge
         else if (y>=315 && x>=190) {
            sup.charInteract('f');
            prevInteract[3] = 1;
         }
      }
    }


    //unimplemented keylistener methods
    public void keyPressed(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
    }
   
      //mouse listener method SOLELY for the pause screen yes I'm aware it's awful. however. i am very stupid
    public void mousePressed(MouseEvent e) {
      if (e.getX() >=520 && e.getX() <=570 && e.getY() >=10 && e.getY() <=60) sup.pause(this);
    }

    // mouse listener methods that I'm not using
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}


//class for the tutorial text boxes
class TutText extends JComponent implements MouseListener {
   private Font f;
   private Game sup;
   private ArrayList<String> text;
   int line = 0;
   
   //Constructor: gets variables, adds mouse listener, initializes text boxes
   public TutText (Font f, Game sup) {
      this.f = f;
      this.sup = sup;
      addMouseListener(this);
      initText();
   }
   
   //default text
   public void initText() {
      text = new ArrayList<String>();
      text.add("Use the WASD keys to    move");
      text.add("As you move around, you may be able to interact with things.");
      text.add("If you are able to      interact, an E key will appear above your head.");
      text.add("If you would like to    interact, press E.");
      text.add("Interacting costs       morale, but it increasesyour maximum morale.");
      text.add("This means that the day after you will start    with more morale.");
      text.add("Moving around uses up   energy");
      text.add("You can gain max energy by interacting with     certain objects.");
      text.add("The day will end once   your morale or energy   reach 0.");
      text.add("Your morale is measured by the blue bar in the  top left corner.");
      text.add("Your energy is measured in the yellow bar in thesame location.");
      text.add("Use the button in the   top right to pause the  game.");
      text.add("Good luck");
   }
   
   public void paint(Graphics g) {
      sup.drawBG(g);
      //box
      g.setColor(Color.black);
      g.fillRect(100, 200, 400, 200);
      g.setColor(Color.white);
      g.fillRect(250, 350, 100, 30);
      //text
      g.setColor(Color.black);
      g.setFont(f);
      g.drawString("Next", 270,375);
      g.setColor(Color.white);
      int l = text.get(line).length();
        if (l <=24) g.drawString(text.get(line),120,250);
        else {
            for (int i = 0; i <= l-24; i+= 24) {
               g.drawString(text.get(line).substring(i,i+24), 120,250 + (i/24)*31);
             }
            g.drawString(text.get(line).substring(l-l%24),120,250+(l/24)*31);
        }
   }
   
   //when the mouse is pressed: if it's within the bounds of the next button, move on
    public void mousePressed(MouseEvent e) {
    //pause screen
      if (e.getX() >=520 && e.getX() <=570 && e.getY() >=10 && e.getY() <=60) sup.pause(this);
    //System.out.println("mouse clicked");
    //System.out.println(e.getX()+" "+e.getY());
        if (e.getX() >= 250 && e.getX() <= 350 && e.getY() >= 350 && e.getY() <= 380) {
            line++;
            if (line >= text.size()) {
                  sup.moveOn(this);
            }
            else {
             repaint();
            }
        }
    }
   
    // mouse listener methods that I'm not using
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
   
}


//class for the dialogue boxes
class Dialogue extends JComponent implements MouseListener{
    private ArrayList<String> text;
    private ArrayList<Image> talk;
    private int line = 0;
    private Font font;
    private Image bg;
    private Game sup;
   
    //Constructor: gets variables, adds mouse listener
    public Dialogue(Image bg, ArrayList<String> text, ArrayList<Image> talk, Font font, Game sup) {
        addMouseListener(this);
        this.text = (ArrayList<String>)text.clone();
        this.talk = (ArrayList<Image>)talk.clone();
        this.font = font;
        this.sup = sup;
        this.bg = bg;
    }
   
   //method to reset line
   public void reset() {
      line = 0;
   }

    //method to switch to the next line
    public void next() {
      line++;
        if (line >= text.size()) {
            line = 0;
            sup.moveOn(this);
        }
        else {
         repaint();
        }
    }


    //paint method, draws the dialogue
    public void paint(Graphics g) {
         sup.drawBG(g, bg);
        //the talkerrrrrr
        if (talk != null && talk.get(line) !=null) {
            g.drawImage(talk.get(line), 150,70, null);
        }
         //box
        g.setColor(Color.black);
        g.fillRect(50, 300, 499, 200);
        g.setColor(Color.white);
        g.fillRect(450, 450, 80, 30);
        g.fillRect(70, 450, 80, 30);
        //text
        g.setFont(font);
        int l = text.get(line).length();
        if (l <=31) g.drawString(text.get(line),70,340);
        else {
            for (int i = 0; i <= l-31; i+= 31) {
               String s = text.get(line).substring(i, i+31);
               if (s.charAt(s.length()-1) != ' ' && text.get(line).charAt(i+31) != ' ') {
                  int j = s.length()-1;
                  while (s.charAt(j) != ' ') {
                     j-=1;
                  }
                  j++;
                  s = s.substring(0, j);
                  for (int h = 30; h>= j; h--) {
                     s+= " ";
                  }
                  text.set(line, text.get(line).substring(0, i) + s+ text.get(line).substring(i+j));
                  l = text.get(line).length();
               }
               else if (text.get(line).charAt(i+31) == ' ') {
                  text.set(line, text.get(line).substring(0, i+31) + text.get(line).substring(i+32));
                  l = text.get(line).length();
               }
               g.drawString(s, 70,340 + i);
             }
            g.drawString(text.get(line).substring(l-l%31),70,340+(l/31)*31);
        }
        //next
        g.setColor(Color.black);
        g.drawString("Next", 460, 472);
        g.drawString("Skip", 80, 472);
    }

    //when the mouse is pressed: if it's within the bounds of the next button, move on
    public void mousePressed(MouseEvent e) {
    //pause screen
      if (e.getX() >=520 && e.getX() <=570 && e.getY() >=10 && e.getY() <=60) sup.pause(this);
      //next button
        if (this.isVisible() && e.getX() >= 450 && e.getX() <= 530 && e.getY() >= 450 && e.getY() <= 480) {
            next();
        }
      //skip button
      if (e.getX()>=70 && e.getX() <= 150 && e.getX() <= 530 && e.getY() >= 450 && e.getY() <= 480) {
         line = 0;
         sup.moveOn(this);
      }
    }

    // mouse listener methods that I'm not using
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}


//method to end the day
class SleepyTime extends JComponent implements ActionListener {
   private Game sup;
   private Timer t;
   private int creepDown = 50;
   private int line = 0;
   private Font font;


  //constructor: font and game are passed
   public SleepyTime (Font font, Game sup) {
      this.sup = sup;
      this.font = font;
   }


   //run method, starts the timer for the creep down
   public void run () {
    t = new Timer(400, this);
    t.start();
   }


   //paint method.
   public void paint(Graphics g) {
         sup.drawBG(g);
         //if we're still creeping down, animate the creep dwon
      if (creepDown <300) {
         g.setColor(Color.black);
         g.fillRect(0, 0, 600, creepDown);
         g.fillRect(0, 550-creepDown, 600, creepDown+50);
      }
      // otherwise, show the text
      else {
         g.setColor(Color.black);
         g.fillRect(0,0,600,600);
         g.setFont(font);
         g.setColor(Color.red);
         int w = 35;
         int b = 300 - (9*35)/2;
         if (line >0) g.drawString("Your limbs grow tired.", 60,b);
         if (line >1) g.drawString("Your eyes sting.", 60,b+w);
         if (line >2) g.drawString("Your HEAD hurts. BADLY.", 60,b+2*w );
         if (line >3) g.drawString("And WORSE, you're BORED.", 60,b+3*w);
         if (line >4) g.drawString("This is too much effort.", 60, b+4*w);
         if (line >5) {
            g.drawString("You just want to crawl back into", 60, b+5*w);
            g.drawString("bed, and slide under the covers.", 60, b+5*w + 30);
         }
         if (line>6) {
            g.drawString("Disheartened, you slink back", 60, b+7*w);
            g.drawString("into your bed and close your eyes.", 60, b+7*w+30);
         }
      }
    }

    //neccesary for timer
   public void actionPerformed(ActionEvent e) {
    if (creepDown < 300) {
      creepDown += 50;
      repaint();
      if (creepDown == 300) {
         t.stop();
         t = new Timer(1700,this);
         t.setInitialDelay(500);
         t.start();
      }
    }
    else if (line <=6) {
      line++;
      repaint();
    }
    else {
      t.stop();
      sup.moveOn(this);
    }
}
}




class NewDay extends JComponent implements ActionListener {
   private Game sup;
   private Timer t;
   private int creepUp = 1000;
   private Font font;
   private int day;

   public NewDay (int day, Font font, Game sup) {
      this.sup = sup;
      this.font = font;
      this.day = day;
   }

   public void run () {
    t = new Timer(500, this);
    t.setInitialDelay(2000);
    t.start();
   }

   public void paint(Graphics g) {
         sup.drawBG(g);
      if (creepUp ==1000) {
      g.setColor(Color.black);
      g.fillRect(0,0,600,600);
         g.setColor(Color.red);
         g.setFont(font);
         g.drawString("DAY "+day,250,275);
      }
      else if (creepUp >=0) {
         g.setColor(Color.black);
         g.fillRect(0, 0, 600, creepUp-50);
         g.fillRect(0, 600-creepUp, 600, creepUp);
      }
    }

   public void actionPerformed(ActionEvent e) {
   if (creepUp == 1000) {
      repaint();
      creepUp=300;
   }
    else if (creepUp >0) {
      creepUp -= 50;
      repaint();
    }
    else {
      t.stop();
      sup.moveOn(this);
    }
  }
}


//class to draw the tutorial screen
class Tutorial extends JComponent implements MouseListener {
   private ArrayList<Image> slides;
   private int slide;
   private Game sup;

   public Tutorial (Game sup) {
      addMouseListener(this);
      this.sup = sup;
      //load the slides
      slides = new ArrayList<Image>();
      try {
         slides.add(ImageIO.read(new File("images\\tutorial\\TutSlide1.png")).getScaledInstance(550, 550, Image.SCALE_SMOOTH));
         slides.add(ImageIO.read(new File("images\\tutorial\\TutSlide2.png")).getScaledInstance(550, 550, Image.SCALE_SMOOTH));
         slides.add(ImageIO.read(new File("images\\tutorial\\TutSlide3.png")).getScaledInstance(550, 550, Image.SCALE_SMOOTH));
         slides.add(ImageIO.read(new File("images\\tutorial\\TutSlide4.png")).getScaledInstance(550, 550, Image.SCALE_SMOOTH));
         slides.add(ImageIO.read(new File("images\\tutorial\\TutSlide5.png")).getScaledInstance(550, 550, Image.SCALE_SMOOTH));
         slides.add(ImageIO.read(new File("images\\tutorial\\TutSlide6.png")).getScaledInstance(550, 550, Image.SCALE_SMOOTH));
         slides.add(ImageIO.read(new File("images\\tutorial\\TutSlide7.png")).getScaledInstance(550, 550, Image.SCALE_SMOOTH));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void paint(Graphics g) {
      g.setColor(new Color(247, 184, 193));
      g.fillRect(0,0,600,600);
      g.drawImage(slides.get(slide), 25,5,null);
   }

   public void mousePressed(MouseEvent e) {
      if (e.getX() >= 217 && e.getX() <=380 && e.getY() >= 460 && e.getY() <= 505) {
         slide++;
         if (slide >= slides.size()) {
            sup.returnToMainMenu();
         }
      }
      repaint();
    }

    // mouse listener methods that I'm not using
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}

//class to draw the loading screen
class Loading extends JComponent implements ActionListener {
   private Timer t;
   private int sq = 1;
   private Image l = null;

   //default, empty constructor, starts timer, loads image
   public Loading () {
      t = new Timer(300, this);
      t.start();
      try {
         l = ImageIO.read(new File("images/backgrounds/loading.png"));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public void paint (Graphics g) {
      g.drawImage(l,0,0, null);
      //boxes
      g.setColor(Color.white);
      for (int i = 0; i <sq; i++) {
         g.fillRect(370+30*i,270,10,10);
      }
      sq++;
      if (sq >= 5) sq = 1;
   }

   public void actionPerformed(ActionEvent e) {
      repaint();
   }
}

//class for the aerobics interaction. does it implement three different types of listeners? Yes. It is a class of many talents. 
class Aerobics extends JComponent implements ActionListener, MouseListener, KeyListener {
   private Game sup;
   private Font intro;
   private Font letters;
   //images
   private Image guy;
   private Image stand;
   private Image shimmy1;
   private Image shimmy2;
   private Image bg;
   private Image life;
   private Image death;
   //intro
   private ArrayList<String> introScript;
   private int iLine = 0;
   private ArrayList<String> concScript;
   private int cLine = 0;
   //game
   private Timer t;
   private char keyChoice = ' ';
   private char actualChoice = 'w';
   private char[] choices = {'k', 'w', 'r', 'p', 'e', 't', 'l', 'g', 'x'};
   private int speed = 10;
   private int y = 0;
   private int livesLost = 0;
   private int points = 0;
   private Clip clip;
   private boolean shimmyLeft = true;
   private int lastShimmy = 0;
   
   //Constructor: starts tutorial text
   public Aerobics (Font f, Game sup) {
      this.sup = sup;
      this.intro = f;
      initText();
      addMouseListener(this);
      addKeyListener(this);
      this.setFocusable(true);
      this.requestFocusInWindow();
      //load
      try {
         bg = ImageIO.read(new File("images/aerobics/aerobics.png"));
         stand = ImageIO.read(new File("images/aerobics/alexAEROBICS.png"));
         shimmy1 = ImageIO.read(new File("images/aerobics/alexSHIMMY.png"));
         shimmy2 = ImageIO.read(new File("images/aerobics/alexSHIMMY2.png"));
         life = ImageIO.read(new File("images/aerobics/LIFE.png"));
         death = ImageIO.read(new File("images/aerobics/DEATH.png"));
         File fontFile = new File("fonts/SuperFunky-lgmWw.ttf");
         letters = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(100f);
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont(letters);
      } catch (Exception e) {
         e.printStackTrace();
      }
      guy = stand;       
  }
   
   public void initText() {
      introScript = new ArrayList<String>();
      introScript.add("Welcome to the aerobics class!");
      introScript.add("You... well... to put itgently....");
      introScript.add("You look like you are   in need of some good,   tough-love excersize!");
      introScript.add("And that\'s why you're   here!");
      introScript.add("So let's begin. In this class, you will recieve instructions in the formof keys to press.");
      introScript.add("You will have to press  those keys before the   letters reach the black  line.");
      introScript.add("If you don't press them quickly enough, you\'ll  lose a life.");
      introScript.add("You have 3 lives. If youlose all 3, the next onewill end the excersize.");
      introScript.add("Good luck!");
      concScript = new ArrayList<String>();
      concScript.add("YOU DID SUCH A GOOD JOB!");
      concScript.add("Unfortunately, we'll    have to stop now. It's  not good to push        yourself too hard.");
      concScript.add("Thank you so much for   participating!");
   }
   
   public void paint(Graphics g) {
      sup.drawBG(g);
      //if we're still displaying the tutorial text
      if (iLine < introScript.size()) {
         //box
         g.setColor(Color.black);
         g.fillRect(100, 200, 400, 200);
         g.setColor(Color.white);
         g.fillRect(250, 350, 100, 30);
         //text
         g.setColor(Color.black);
         g.setFont(intro);
         g.drawString("Next", 270,375);
         g.setColor(Color.white);
         int l = introScript.get(iLine).length();
         if (l <=24) g.drawString(introScript.get(iLine),120,250);
         else {
               for (int i = 0; i <= l-24; i+= 24) {
                  g.drawString(introScript.get(iLine).substring(i,i+24), 120,250 + (i/24)*31);
               }
               g.drawString(introScript.get(iLine).substring(l-l%24),120,250+(l/24)*31);
         }
         }
      //if the game isn't over yet
      else if (livesLost <=3) {
         //images
         g.drawImage(bg, 0,0, null);
         g.drawImage(guy, 268, 316,null);
         //end line
         g.setColor(Color.white);
         g.fillRect(200, 228, 200, 5);
         //lives
         for (int i = 1; i <=3; i++) {
            if (livesLost >=i) g.drawImage(death, 40*i, 20, null);
            else g.drawImage(life, 40*i, 20, null);
         }
         //falling letters
         g.setColor(Color.white);
         g.setFont(letters);
         g.drawString(""+actualChoice, 250, y);
      }
      //game over screen
      else if (livesLost == 4) {
         g.setColor(Color.black);
         g.fillRect(0,0,600,600);
         g.setColor(Color.red);
         g.setFont(intro);
         g.drawString("GAME OVER", 232,275);
         livesLost++;
      }
      else {
         if (livesLost == 5) {
            t.stop();
            livesLost++;
         }//box
         g.setColor(Color.black);
         g.fillRect(100, 200, 400, 200);
         g.setColor(Color.white);
         g.fillRect(250, 350, 100, 30);
         //text
         g.setColor(Color.black);
         g.setFont(intro);
         g.drawString("Next", 270,375);
         g.setColor(Color.white);
         int l = concScript.get(cLine).length();
         if (l <=24) g.drawString(concScript.get(cLine),120,250);
         else {
               for (int i = 0; i <= l-24; i+= 24) {
                  g.drawString(concScript.get(cLine).substring(i,i+24), 120,250 + (i/24)*31);
               }
               g.drawString(concScript.get(cLine).substring(l-l%24),120,250+(l/24)*31);
         }
      }
   }

   //getter method for points
   public int getPoints() {
      return points;
   }
   
   
   
   public void actionPerformed (ActionEvent e) {
      if (y>=210) {
         livesLost++;
         y=0;
         keyChoice = ' ';
         actualChoice = choices[(int) (Math.random() * choices.length)];
         if (livesLost ==4) {
            //end the music
            clip.stop();
            //end the timer
            t.stop();
            t = new Timer(2000,this);
            t.start();
         }
      }
      else {
         y+=speed;
         if (y>=210) y= 210;
      }
      if (lastShimmy >=3) {
         guy = stand;
      }
      else {
         lastShimmy++;
      }
      repaint();
   }
   
   
   //when the mouse is pressed: if it's within the bounds of the next button, move on
    public void mousePressed(MouseEvent e) {
     //only if we're still showing intro text
     if (iLine < introScript.size() && e.getX() >= 250 && e.getX() <= 350 && e.getY() >= 350 && e.getY() <= 380) {
         repaint();
         iLine++;
         //move on to the minigame aspect
         if (iLine >= introScript.size()) {
            //start the music
            try {
               File file = new File("music/Conga.wav");
               AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
               clip = AudioSystem.getClip();
               clip.open(audioIn);
               clip.loop(Clip.LOOP_CONTINUOUSLY);
               clip.start();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
            //above code from https://www.youtube.com/watch?v=Q6yl-7ayn1w
            //timer
            t = new Timer(100, this);
            t.start();
        }
    }
    //if we're stilll showing conc text
     if (livesLost >4 &&  e.getX() >= 250 && e.getX() <= 350 && e.getY() >= 350 && e.getY() <= 380) {
         repaint();
         cLine++;
         //move on to the minigame aspect
         if (cLine >= concScript.size()) {
            sup.moveOn(this);
        }
    }
    }
    
   //update the choice when the key is pressed
    public void keyPressed (KeyEvent e) {
      //only if we're in the game stage 
      if (iLine >= introScript.size() && livesLost <4) {
         keyChoice = e.getKeyChar();
         if (keyChoice == actualChoice) {
            points++;
            keyChoice = ' ';
            actualChoice = choices[(int) (Math.random() * choices.length)];
            y = 0;
            if (points %2 ==0) speed += 1.2*Math.log(speed);
            if (shimmyLeft) guy = shimmy1;
            else guy = shimmy2;
            shimmyLeft = !shimmyLeft;
            lastShimmy=0;
         }
      }
    }
    
    //mandatory methods that I'm not using
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    
}

//class that draws the end screen
class EndScreen extends JComponent implements ActionListener {
   private Font f;
   private Game sup;
   private Timer t;
   private ArrayList<String> text;
   private int line = 0;
   private ArrayList<String> credit;
   private int creditLine = 0;
   private int creep = 300;
   private Image bg;

   public EndScreen (Font f, Game sup) {
      this.f = f;
      this.sup = sup;
      initText();
      bg = null;
      try {
         bg = ImageIO.read(new File("images/backgrounds/panama.png"));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void run () {
      t = new Timer(900,this);
      t.start();
   }
   
   public void initText() {
      text = new ArrayList<String>();
      text.add("Birds are chirping.");
      text.add("The sun is shining.");
      text.add("People pass by.");
      text.add("The world is so...");
      text.add("so....");
      text.add("...beautiful.");
      credit = new ArrayList<String>();
      credit.add("SHRUMP INC.");
      credit.add("Maya Peski: Lead Programmer");
      credit.add("Fareeha Hashir: Graphic Design");
      credit.add("Special Thanks To Tara Jankovic");
   }
   
   public void paint(Graphics g) {
      g.drawImage(bg, 0,0, null);
      //if we're still creeping down, animate the creep dwon
      if (line == 0 && creep >=0) {
         g.setColor(Color.black);
         g.fillRect(0, 0, 600, creep-50);
         g.fillRect(0, 600-creep, 600, creep);
      } 
      // otherwise if we're still showing the text, show the text
      else if (line < text.size()) {
      //box
      g.setColor(Color.black);
      g.fillRect(96, 196, 408, 208);
      g.setColor(Color.white);
      g.fillRect(100, 200, 400, 200);
      //text
      g.setColor(Color.black);
      g.setFont(f);
      int l = text.get(line).length();
        if (l <=24) g.drawString(text.get(line),120,250);
        else {
            for (int i = 0; i <= l-24; i+= 24) {
               g.drawString(text.get(line).substring(i,i+24), 120,250 + (i/24)*31);
             }
            g.drawString(text.get(line).substring(l-l%24),120,250+(l/24)*31);
        }
      }
      //otherwise creep up if still creeping up
      else if (creep <300) {
         g.setColor(Color.black);
         g.fillRect(0, 0, 600, creep);
         g.fillRect(0, 550-creep, 600, creep+50);
      }
      //otherwise show credits
      else {
         g.setColor(Color.black);
         g.fillRect(0,0,600,600);
         g.setFont(f);
         g.setColor(Color.red);
         for (int i = 0; i <= creditLine; i++) {
            g.drawString(credit.get(i), 300- (int) (credit.get(i).length()*15.0/2.0), 300 - (credit.size()/2 - i)*35);
         }
      } 
   }
   
   public void actionPerformed (ActionEvent e) {
   if (line == 0 && creep >=0) {
      repaint();
      creep-=50;
      if (creep <0){
         t.stop();
         t = new Timer(2000, this);
         t.start();
      }
   }
    else if (line < text.size()) {
      repaint();
      line++;
      if (line >=text.size()){
         t.stop();
         t = new Timer(900, this);
         t.start();
      }
    }
    else if (creep <=300) {
      repaint();
      creep +=50;
      if (creep >300){
         t.stop();
         t = new Timer(2000, this);
         t.start();
      }
    }
    else if (creditLine < credit.size()-1) {
      repaint();
      creditLine++;
      if (creditLine >= credit.size()){
         t.stop();
         t = new Timer(4000, this);
         t.start();
      }
    }
    else {
      repaint();
      t.stop();
      sup.returnToMainMenu();
    }
   }
   
}


//class for the fridge interactions
class Fridge extends JComponent implements MouseListener {
      private Game sup;
      private Font f;
      private int[] available = {1,1,1,1,1,1};
      private Image[] foodstuff = new Image[6];
      private Image bg = null;
      private int[][] foodCoords = new int[6][2];
      private String[] foodText = new String[6];
      private int[][] value = new int[6][2];
      private int selected = -1;
      private boolean starting = true;


      public Fridge (Font f, Game sup) {
         this.f = f;
         this.sup = sup;
         addMouseListener(this);
         //load images
         try {
            bg = ImageIO.read(new File("images\\backgrounds\\fridgeOPEN.png"));
            foodstuff[0] = ImageIO.read(new File("images\\foodstuff\\mayonnaise.png"));
            foodstuff[1] = ImageIO.read(new File("images\\foodstuff\\eggs.png"));
            foodstuff[2] = ImageIO.read(new File("images\\foodstuff\\fridge pizza.png"));
            foodstuff[3] = ImageIO.read(new File("images\\foodstuff\\apple.png"));
            foodstuff[4] = ImageIO.read(new File("images\\foodstuff\\icecream.png"));
            foodstuff[5] = ImageIO.read(new File("images\\foodstuff\\lettuce.png"));
         } catch (IOException e) {
            e.printStackTrace();
         }
         //coords
         foodCoords[0][0] = 213;
         foodCoords[0][1] = 185;
         foodCoords[1][0] = 327;
         foodCoords[1][1] = 193;
         foodCoords[2][0] = 184;
         foodCoords[2][1] = 296;
         foodCoords[3][0] = 363;
         foodCoords[3][1] = 281;
         foodCoords[4][0] = 194;
         foodCoords[4][1] = 393;
         foodCoords[5][0] = 334;
         foodCoords[5][1] = 389;
         //names
         foodText[0] = "Nothing wrong with good old mayo straight from  the jar, right?";
         foodText[1] = "MMMMMMM EGGS. Wait. You don't have anything to  cook them with... Well, the microwave works,    right?";
         foodText[2] = "Ah, some good pizza.    Probably not good for   your health, but certainly good for your heart. YUM!";
         foodText[3] = "A nice, crisp apple.    Why was this in your    fridge??";
         foodText[4] = "It's been a hard day.   You deserve some nice,  cool, ice cream.";
         foodText[5] = "Ewwwwwwwwwwwwwwwwww.    EEEEEEEWWWWWWWWWWWW.    VEGETABLES. Gross.";
         //values
         value[0][0] = 10;
         value[0][1] = 5;
         value[1][0] = 10;
         value[1][1] = 10;
         value[2][0] = 0;
         value[2][1] = 20;
         value[3][0] = 10;
         value[3][1] = 5;
         value[4][0] = -10;
         value[4][1] = 20;
         value[5][0] = 20;
         value[5][1] = -5;
      }

      public void reset () {
         selected = -1;
      }

      public void paint (Graphics g) {
         g.drawImage(bg, 0,0, null);
         for (int i =0; i < foodstuff.length; i++) {
            if (available[i] == 1) {
               g.drawImage(foodstuff[i], foodCoords[i][0], foodCoords[i][1], null);
            }
         }
         //if you just started, tell you what to do
         if (starting) {
            //box
            g.setColor(Color.black);
            g.fillRect(100, 200, 400, 200);
            g.setColor(Color.white);
            g.fillRect(250, 350, 100, 30);
            //text
            g.setColor(Color.black);
            g.setFont(f);
            g.drawString("Begin", 262,375);
            g.setColor(Color.white);
            String s = "Click on an ingredient  to view its description.Click select to eat it.";
            for (int i = 0; i <= s.length()-24; i+= 24) {
               g.drawString(s.substring(i,i+24), 120,250 + (i/24)*31);
             }
            g.drawString(s.substring(s.length()-s.length()%24),120,250+(s.length()/24)*31);
         }
         //if you're selecting, display the text
         else if (selected != -1) {
            //box
            g.setColor(Color.black);
            g.fillRect(100, 150, 400, 250);
            g.setColor(Color.white);
            g.fillRect(150, 350, 100, 30);
            g.fillRect(350, 350, 100, 30);
            //text
            g.setColor(Color.black);
            g.setFont(f);
            g.drawString("Cancel", 155,375);
            g.drawString("Select", 355,375);
            g.setColor(Color.white);
            String s = foodText[selected];
            for (int i = 0; i <= s.length()-24; i+= 24) {
               g.drawString(s.substring(i,i+24), 120,190 + (i/24)*31);
             }
            g.drawString(s.substring(s.length()-s.length()%24),120,190+(s.length()/24)*31);
         }
      }

      //returns whether or not it's the first time
      public boolean isFirstTime() {
         for (int i: available) {
            if (i != 1) return false;
         }
         return true;
      }

      //getter and setter method for availability
      public int[] getAvailability () {
         return available;
      }

      public void setAvailability(int[] temp) {
         available = temp;
      }

      //getter method for value of selected object: for game class
      public int[] getNutrition () {
         return value[selected];
      }


      public void mousePressed(MouseEvent e) {
         System.out.println(e.getX() +", "+e.getY());
      //pause button
      if (e.getX() >=520 && e.getX() <=570 && e.getY() >=10 && e.getY() <=60) sup.pause(this);
      //if starting and next clicked
      else if (starting && e.getX() >= 250 && e.getX() <= 350 && e.getY() >= 350 && e.getY() <= 380) starting = false;
      //if selecting and cancel clicked
      else if (selected != -1 && e.getX() >= 150 && e.getX() <= 250 && e.getY() >= 350 && e.getY() <= 380) selected = -1;
      //if selecting and select clicked
      else if (selected != -1 && e.getX() >= 350 && e.getX() <= 450 && e.getY() >= 350 && e.getY() <= 380) {
         available[selected] = 0;
         sup.moveOn(this);
      }
      //otherwise check if a food has been clicked
      else {
         for (int i = 0; i < foodstuff.length; i++) {
            if (e.getX() >= foodCoords[i][0] && e.getX() <= foodCoords[i][0] + foodstuff[i].getWidth(null) && e.getY() >= foodCoords[i][1] && e.getY() <= foodCoords[i][1] + foodstuff[i].getHeight(null) ) {
               selected = i;
            }
         }
      }
      repaint();
      }

      // mouse listener methods that I'm not using
      public void mouseClicked(MouseEvent e) {}
      public void mouseReleased(MouseEvent e) {}
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}


}