package org.dsc.metadataanalyser.dataClasses;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;

public class MetaData extends Node {
    public SimpleIntegerProperty count = new SimpleIntegerProperty();
    public  SimpleStringProperty metaDataKey = new SimpleStringProperty();
    public  SimpleStringProperty value = new SimpleStringProperty();

    public MetaData(int count, String metaDataKey, String value) {
        this.count.set(count);
        this.metaDataKey.set(metaDataKey);
        this.value.set(value);
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public int getCount() {
        return count.get();
    }
public IntegerProperty getCountProperty(){
        return this.count;
}
    public String getMetaDataKey() {
        return metaDataKey.get();
    }

    public void setMetaDataKey(String metaDataKey) {
        this.metaDataKey.set(metaDataKey);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }


}