import java.util.Random;

public class Deck {
    public static String[] suitsInOrder = {"clubs", "diamonds", "hearts", "spades"};
    public static Random gen = new Random();

    public int numOfCards; // contains the total number of cards in the deck
    public Card head; // contains a pointer to the card on the top of the deck

    public Deck(int numOfCardsPerSuit, int numOfSuits) {
        // check for input validation
        if (numOfCardsPerSuit > 13 || numOfCardsPerSuit < 1 || numOfSuits >= suitsInOrder.length || numOfSuits <= 1) {
            throw new IllegalArgumentException();
        }
        // place the first card in the deck: AC
        PlayingCard last = new PlayingCard(suitsInOrder[0], 1);
        this.head = last;
        this.numOfCards = 1;

        // add all remaining cards
        for (int suit = 0; suit < numOfSuits; suit++) {
            for (int rank = 1; rank <= numOfCardsPerSuit; rank++) {
                if (rank == 1 && suit == 0) {
                    continue;
                } else {
                    // create new card
                    PlayingCard newCard = new PlayingCard(suitsInOrder[suit], rank);
                    // add card to deck
                    newCard.prev = last;
                    last.next = newCard;
                    last = newCard;
                    this.numOfCards++;
                }
            }
        }

        // create new joker cards
        Joker redJoker = new Joker("red");
        Joker blackJoker = new Joker("black");
        // add joker cards to the deck
        last.next = redJoker;
        redJoker.next = blackJoker;
        redJoker.prev = last;
        blackJoker.prev = redJoker;
        this.numOfCards = this.numOfCards + 2;

        // make the deck circular
        this.head.prev = blackJoker;
        blackJoker.next = this.head;
    }

    /*

    public Deck(Deck d) {
        // input validation
        if (d.head == null) {
            return;
        }

        // create the new first card
        Card currentOG = d.head;
        Card currentNew = currentOG.getCopy();
        this.head = currentNew;
        Card nextOG = currentOG.next;
        Card nextNew = nextOG.getCopy();

        // create remaining cards
        for (int i = 2; i <= d.numOfCards; i++) {
            // create cards
            nextOG = currentOG.next;
            nextNew = nextOG.getCopy();
            // update pointers
            currentNew.next = nextNew;
            nextNew.prev = currentNew;
            // update variable
            currentOG = nextOG;
            currentNew = nextNew;
        }
        // make deck circular
        this.head.prev = nextNew;
        nextNew.next = this.head;
        this.numOfCards = d.numOfCards;
    }

    public Deck() {
    }

    public void addCard(Card c) {
        if (head == null) {
            this.head = c;
            c.prev = c;
            c.next = c;
        } else {
            Card tail = this.head.prev;
            tail.next = c;
            c.prev = tail;
            c.next = this.head;
            this.head.prev = c;
        }
        this.numOfCards++;
    }

    public void shuffle() {
        // input validation
        if (this.head == null) {
            return;
        }
        // copy all the cards inside an array
        Card[] arrayCopy = new Card[this.numOfCards];
        Card nextCard = this.head;

        for (int i = 0; i < this.numOfCards; i++) {
            arrayCopy[i] = nextCard;
            nextCard = nextCard.next;
        }

        // shuffle the array
        for (int i = this.numOfCards - 1; i >= 1; i--) {
            int j = gen.nextInt(i + 1);
            Card temp = arrayCopy[j];
            arrayCopy[j] = arrayCopy[i];
            arrayCopy[i] = temp;
        }

        // use the array to rebuild the shuffled deck
        this.head = null;
        this.numOfCards = 0;
        for (Card c : arrayCopy) {
            this.addCard(c);
        }
    }

    public Joker locateJoker(String color) {
        // input validation
        if (this.head == null) {
            return null;
        } else {
            // check every card
            Card current = this.head;
            for (int i = 1; i <= this.numOfCards; i++) {
                if (current instanceof Joker && ((Joker) current).redOrBlack == color) {
                    return (Joker) current;
                } else {
                    current = current.next;
                }
            }
            return null;
        }
    }

    public void moveCard(Card c, int p) {
        // check if head is affected
        if (c == this.head) {
            this.head = this.head.next;
        }

        // remove card c from deck
        Card previousCard = c.prev;
        Card nextCard = c.next;
        previousCard.next = nextCard;
        nextCard.prev = previousCard;

        // find card c's new position
        for (int i = 1; i <= p; i++) {
            previousCard = previousCard.next;
        }

        // add card c back at the new position
        nextCard = previousCard.next;
        previousCard.next = c;
        c.prev = previousCard;
        c.next = nextCard;
        nextCard.prev = c;

    }

    public void tripleCut(Card firstCard, Card secondCard) {
        if (firstCard == null || secondCard == null) {
            return;
        }
        if (firstCard == this.head) {
            this.head = secondCard.next;
        } else if (secondCard == this.head.prev) {
            this.head = firstCard;
        } else {
            // establish groups
            Card firstHead = this.head;
            Card firstTail = firstCard.prev;
            Card secondHead = secondCard.next;
            Card secondTail = this.head.prev;

            // adjust pointers
            firstHead.prev = secondCard;
            secondCard.next = firstHead;
            secondTail.next = firstCard;
            firstCard.prev = secondTail;
            secondHead.prev = firstTail;
            firstTail.next = secondHead;
            this.head = secondHead;
        }
    }

    public void countCut() {
        // input validation
        if (this.head == null) {
            return;
        }

        Card bottomCard = this.head.prev;
        int num = bottomCard.getValue() % numOfCards;

        // remove the last card
        this.head.prev = bottomCard.prev;
        bottomCard.prev.next = this.head;
        this.numOfCards--;

        // shift the head "num" times down
        for (int i = 1; i <= num; i++) {
            this.head = this.head.next;
        }

        // add the bottom card back at the bottom
        this.addCard(bottomCard);
    }

    public Card lookUpCard() {
        // input validation
        if (this.head == null) {
            return null;
        }
        int num = this.head.getValue() % numOfCards;
        Card current = this.head;
        for (int i = 1; i <= num; i++) {
            current = current.next;
        }
        if (current instanceof Joker) {
            return null;
        } else {
            return current;
        }
    }

    public int generateNextKeystreamValue() {
        boolean isNull = true;

        while (isNull) {
            // locate the red joker and move it one card down
            Card redJoker = this.locateJoker("red");
            moveCard(redJoker, 1);

            // locate the black joker and move it two cards down
            Card blackJoker = this.locateJoker("black");
            moveCard(blackJoker, 2);

            // perform a triple cut
            Card current = this.head;
            for (int i = 1; i <= this.numOfCards; i++) {
                if (current == redJoker) {
                    this.tripleCut(redJoker, blackJoker);
                    break;
                } else if (current == blackJoker) {
                    this.tripleCut(blackJoker, redJoker);
                    break;
                }
                current = current.next;
            }

            // perform a count cut
            this.countCut();

            // find the value of the keystream
            if (this.lookUpCard() != null) {
                isNull = false;
            }
        }
        int key = this.lookUpCard().getValue();
        return key;
    }


    public abstract class Card {
        public Card next;
        public Card prev;

        public abstract Card getCopy();

        public abstract int getValue();

    }

    public class PlayingCard extends Card {
        public String suit;
        public int rank;

        public PlayingCard(String s, int r) {
            this.suit = s.toLowerCase();
            this.rank = r;
        }

        public String toString() {
            String info = "";
            if (this.rank == 1) {
                //info += "Ace";
                info += "A";
            } else if (this.rank > 10) {
                String[] cards = {"Jack", "Queen", "King"};
                //info += cards[this.rank - 11];
                info += cards[this.rank - 11].charAt(0);
            } else {
                info += this.rank;
            }
            //info += " of " + this.suit;
            info = (info + this.suit.charAt(0)).toUpperCase();
            return info;
        }

        public PlayingCard getCopy() {
            return new PlayingCard(this.suit, this.rank);
        }

        public int getValue() {
            int i;
            for (i = 0; i < suitsInOrder.length; i++) {
                if (this.suit.equals(suitsInOrder[i]))
                    break;
            }

            return this.rank + 13 * i;
        }

    }

    public class Joker extends Card {
        public String redOrBlack;

        public Joker(String c) {
            if (!c.equalsIgnoreCase("red") && !c.equalsIgnoreCase("black"))
                throw new IllegalArgumentException("Jokers can only be red or black");

            this.redOrBlack = c.toLowerCase();
        }

        public String toString() {
            //return this.redOrBlack + " Joker";
            return (this.redOrBlack.charAt(0) + "J").toUpperCase();
        }

        public Joker getCopy() {
            return new Joker(this.redOrBlack);
        }

        public int getValue() {
            return numOfCards - 1;
        }

        public String getColor() {
            return this.redOrBlack;
        }
    }

}
