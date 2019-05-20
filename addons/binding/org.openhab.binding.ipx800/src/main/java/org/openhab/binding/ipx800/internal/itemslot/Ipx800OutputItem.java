/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ipx800.internal.itemslot;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;

/**
 * Output item
 *
 * @author Seebag
 * @since 1.8.0
 */
public class Ipx800OutputItem extends Ipx800AstableSwitch {
    private boolean pulseMode = false;
    private Ipx800Item fromItem = null;

    public Ipx800OutputItem() {
    }

    public Ipx800OutputItem(boolean pulseMode) {
        this.pulseMode = pulseMode;
    }

    public boolean isPulseMode() {
        return pulseMode;
    }

    public void setFromItem(Ipx800Item fromItem) {
        this.fromItem = fromItem;
    }

    public void updateAndSend(State state) {
        // lastState = state; // Not posted ?
        switchState(false);
        getPort().getDevice().setOutput(this);
    }

    @Override
    protected boolean updateStateInternal(Type state) {
        boolean changed = false;
        if (state instanceof OnOffType) {
            OnOffType commandState = (OnOffType) state;
            if (!lastState.equals(commandState)) {
                changed = true;
                lastState = commandState;
            }
        }
        if (changed && fromItem != null) {
            fromItem.updateStateToCore(lastState);
        }
        return changed;
    }

}
