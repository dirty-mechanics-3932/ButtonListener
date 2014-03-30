/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dirtymechanics.event;

import java.util.ArrayList;

/**
 *
 * @author agresh
 * ButtonListener assumes a "continuous" (high frequency) polling process is
 * listening to the state of the button and will interpret the changes in 
 * state as clicks, holds or double clicks.
 * 
 * State is returned as a long due to the fact that enums aren't available
 * in the target java version - 1.4
 */
public class ButtonListener {
    public static final long PRESS_MILLIS = 200;
    public static final long NEUTRAL = 0;
    public static final long PRESS = 1;
    public static final long SINGLE_CLICK = 2;
    public static final long HOLD = 3;
    public static final long DOUBLE_CLICK = 4;
    
    
    private long neutralTime = 0l;
    private long firstClickTime = 0l;
    private long firstReleaseTime = 0l;
    private long timeSinceLastClick = 0;
    private long lastPollTime = -1;
    private long timeElapsedSinceLastPoll = 0;
    
    private long state = NEUTRAL;
    private long lastState = NEUTRAL;
    ArrayList listeners = new ArrayList();
    
    public void addListener(ButtonEventHandler listener) {
        listeners.add(listener);
    }
            
    
    public long getState() {
        return state;
    }
    
    public void updateState(boolean buttonState, long currentTime) {
        //Order IS important here.
        updatePollTime(currentTime);
        updateNeutral(buttonState);
        updateDoubleClick(buttonState);
        updatePress(buttonState);
        updateClick(buttonState);
        updateHold(buttonState);
        if (lastState != state) {
            //State changed, notify listeners
           for (int x=0; x < listeners.size(); x++) {
               ((ButtonEventHandler) listeners.get(0)).onEvent(state);
           }
           lastState = state;
        }
        
    }

    private void updatePress(boolean buttonState) {
        //Don't need a timer for the press time
        if (state == NEUTRAL || state == SINGLE_CLICK) {
            //If we're at single click it means the user has let go of the 
            //  button so we're on our way again.  If the button state is
            //  on then it's a new press
            if (buttonState) {
                state = PRESS;
            }
        }
    }

    void updateNeutral(boolean buttonState) {
        if (buttonState) {
            neutralTime = 0;
        } else {
            if (state == NEUTRAL) {
                neutralTime += timeElapsedSinceLastPoll;
            }
            if (state == SINGLE_CLICK) {
                timeSinceLastClick += timeElapsedSinceLastPoll;
            }
        }
    }
    
    private void updateClick(boolean buttonState) {
        if (state > SINGLE_CLICK) return;
        //Either advancing to single click or waiting for release on single click
        //Update the timer to see how long we've been pressed
        this.firstClickTime = firstClickTime + timeElapsedSinceLastPoll;
        if (!buttonState) {
            //The user released the button.  Is it a click?
            if (firstClickTime > PRESS_MILLIS) {
                //This is a release, not a click
                state = NEUTRAL;
                timeSinceLastClick = 0;
                resetTimers();
                return;
            } 
            if (lastState == PRESS && firstClickTime > 0 && timeSinceLastClick == 0) {
                //The button has to have been pressed for some period of time 
                //  in order to be a click.  It also can't be waiting for a
                //  second click so that time has to be 0 too.
                state = SINGLE_CLICK;
                resetTimers();
                //Make it so that the time since last click kicks off without delay
                //  so that in the next cycle double click knows a click happened
                timeSinceLastClick = 1;
            }
        }
    }
    
    void updateHold(boolean buttonState) {
       if (state > HOLD) return;
        //Either advancing to single click or waiting for release on single click
        if (state == HOLD) {
            //we're currently responsible for updating things...
            this.firstClickTime = firstClickTime + timeElapsedSinceLastPoll;
            if (!buttonState) {
                state = NEUTRAL;
                resetTimers();
                return;
            }
        }
        if (buttonState) {
            if (firstClickTime > PRESS_MILLIS) {
                //we're now holding
                state = HOLD;
            }
        }
        
    }

    private void updateDoubleClick(boolean buttonState) {
        if (!buttonState) {
            if (timeSinceLastClick > 0 && timeSinceLastClick < PRESS_MILLIS) {
                state = DOUBLE_CLICK;
                timeSinceLastClick = 0;
            } else {
                state = NEUTRAL;
            }
        }   
    }

    

    /**
     * @return the firstClick
     */
    public long getFirstClickTime() {
        return firstClickTime;
    }


    /**
     * @return the firstRelease
     */
    public long getFirstReleaseTime() {
        return firstReleaseTime;
    }


    void getStatus() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void resetTimers() {
        this.firstClickTime = 0l;
        setFirstReleaseTime(0l);
    }


    /**
     * @param firstReleaseTime the firstReleaseTime to set
     */
    public void setFirstReleaseTime(long firstReleaseTime) {
        this.firstReleaseTime = firstReleaseTime;
    }

    /**
     * @return the lastPollTime
     */
    public long getLastPollTime() {
        return lastPollTime;
    }

    /**
     * @param lastPollTime the lastPollTime to set
     */
    public void setLastPollTime(long lastPollTime) {
        this.lastPollTime = lastPollTime;
    }

    
    

    void updatePollTime(long currentTime) {
        if (lastPollTime < 0) {
            lastPollTime = currentTime;
        }
        timeElapsedSinceLastPoll = currentTime - lastPollTime;
        lastPollTime = currentTime;
    }
    
    public long getTimeElapsedSinceLastPoll() {
        return timeElapsedSinceLastPoll;
    }

    public long getNeutralTime() {
        return neutralTime;
    }

    /**
     * @return the timeSinceLastClick
     */
    public long getTimeSinceLastClick() {
        return timeSinceLastClick;
    }

    /**
     * @param timeSinceLastClick the timeSinceLastClick to set
     */
    public void setTimeSinceLastClick(long timeSinceLastClick) {
        this.timeSinceLastClick = timeSinceLastClick;
    }

    
    
}
