package velix.id.mobile.model;

import java.io.Serializable;

/**
 * Created by User on 4/4/2018.
 */

public class Name implements Serializable {

    String value, valueHash;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
