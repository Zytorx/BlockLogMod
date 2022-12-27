package net.zytorx.minecraft.blocklog.cache.model.common;

import java.io.Serializable;

public class OldNewTuple implements Serializable {

    private String oldState;
    private String newState;

    public OldNewTuple() {
    }

    public OldNewTuple(String oldState, String newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public String getOldState() {
        return oldState;
    }

    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }
}
