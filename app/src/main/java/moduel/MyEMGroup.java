package moduel;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.adapter.EMAGroup;

import java.io.Serializable;

public class MyEMGroup extends EMGroup implements Serializable {
    public MyEMGroup(EMAGroup emaGroup) {
        super(emaGroup);
    }
}
