package studentmanger;

import javafx.beans.property.*;

public class AttendanceHistory {
    private final IntegerProperty id;
    private final StringProperty name;
    private final BooleanProperty present;
    private final StringProperty date;

    public AttendanceHistory(int id, String name, boolean present, String date) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.present = new SimpleBooleanProperty(present);
        this.date = new SimpleStringProperty(date);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public BooleanProperty presentProperty() { return present; }
    public StringProperty dateProperty() { return date; }
    public int getId() {
    return id.get();
}

public String getDate() {
    return date.get();
}

}
