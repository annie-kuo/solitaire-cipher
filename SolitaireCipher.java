import java.util.Locale;

public class SolitaireCipher {
    public Deck key;

    public SolitaireCipher(Deck key) {
        this.key = new Deck(key); // deep copy of the deck
    }

    public int[] getKeystream(int size) {
        // Generates a keystream of the given size
        Deck copyDeck = new Deck(this.key);
        int[] keyStream = new int[size];
        for (int i = 0; i < size; i++) {
            int nextKey = copyDeck.generateNextKeystreamValue();
            keyStream[i] = nextKey;
        }
        return keyStream;
    }


    public String encode(String msg) {
        // format the message (remove all non-letters and capitalize)
        msg = msg.toUpperCase();
        String formattedMsg = "";
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) <= 90 && msg.charAt(i) >= 65) {
                formattedMsg += msg.charAt(i);
            }
        }

        // generate keystream
        int[] keyStream = this.getKeystream(formattedMsg.length());

        // encode message
        String code = "";
        char newChar;
        for (int i = 0; i < keyStream.length; i++) {
            int intermediate = (formattedMsg.charAt(i) - 64 + (keyStream[i] % 26)) % 26;
            if (intermediate == 0) {
                newChar = 'Z';
            } else {
                newChar = (char) (intermediate + 64);
            }
            code += newChar;
        }
        return code;
    }

    public String decode(String msg) {
        // generate keystream
        int[] keyStream = this.getKeystream(msg.length());

        // decode message
        String code = "";
        char newChar;
        for (int i = 0; i < keyStream.length; i++) {
            int intermediate = (msg.charAt(i) - 64 - (keyStream[i] % 26) + 26) % 26;
            if (intermediate == 0) {
                newChar = 'Z';
            } else {
                newChar = (char) (intermediate + 64);
            }
            code += newChar;
        }
        return code;
    }

}
