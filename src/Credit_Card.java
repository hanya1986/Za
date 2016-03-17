public class Credit_Card {
    public String cardNo;
    public String secNo;

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(cardNo);
        builder.append(' ');
        builder.append(secNo);
        return builder.toString();
    }
}