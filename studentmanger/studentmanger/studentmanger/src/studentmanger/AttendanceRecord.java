package studentmanger;

import javafx.beans.property.*;

public class AttendanceRecord {
    private final IntegerProperty id;
    private final StringProperty name;
    private final BooleanProperty present;

    public AttendanceRecord(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.present = new SimpleBooleanProperty(false);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public boolean isPresent() { return present.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public BooleanProperty presentProperty() { return present; }

    public void setPresent(boolean present) { this.present.set(present); }
}
