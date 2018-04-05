package velix.id.mobile.model;

import java.io.Serializable;

/**
 * Created by User on 4/4/2018.
 */

public class Keys implements Serializable {

    String private_k, public_k;

    public String getPrivate_k() {
        return private_k;
    }

    public void setPrivate_k(String private_k) {
        this.private_k = private_k;
    }
}
