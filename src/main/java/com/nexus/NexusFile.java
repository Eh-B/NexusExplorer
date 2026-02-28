package com.nexus;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NexusFile {
    private final StringProperty name;
    private final StringProperty path;
    private final StringProperty type;
    private final StringProperty tag;

    public NexusFile(String name, String path, String type, String tag){
        this.name = new SimpleStringProperty(name);
        this.path = new SimpleStringProperty(path);
        this.type = new SimpleStringProperty(type);
        this.tag = new SimpleStringProperty(tag);
    }
    public StringProperty nameProperty() { return name; }
    public StringProperty pathProperty() { return path; }
    public StringProperty typeProperty() { return type; }
    public StringProperty tagProperty() { return tag; }

    public String getName() { return name.get(); }
    public String getPath() { return path.get(); }
    public String getType() { return type.get(); }
    public String getTag() { return tag.get(); }
}
