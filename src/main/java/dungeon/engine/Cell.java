package dungeon.engine;

import javafx.scene.layout.StackPane;

public class Cell extends StackPane {
    private MapItem item;

    public Cell() {
        super();
    }

    public MapItem getItem() {
        return item;
    }

    public void setItem(MapItem item) {
        this.item = item;
    }

    public char getSymbol() {
        return item != null ? item.getSymbol() : '.';
    }
}


