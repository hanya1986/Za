import java.math.BigDecimal;

public class Menu_Item {
    public String name;
    public boolean type;
    public BigDecimal price;
    public int estPrepTime;

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(' ');
        builder.append(type);
        builder.append(' ');
        builder.append(price);
        builder.append(' ');
        builder.append(estPrepTime);
        return builder.toString();
    }
}
