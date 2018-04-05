package velix.id.mobile.model;

import java.io.Serializable;

/**
 * Created by User on 4/4/2018.
 */

public class Emails implements Serializable {

    String label, value, valueHash;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueHash() {
        return valueHash;
    }

    public void setValueHash(String valueHash) {
        this.valueHash = valueHash;
    }
}
