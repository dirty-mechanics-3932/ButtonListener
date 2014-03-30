/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dirtymechanics.event;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author agresh
 */
public class ButtonListenerTest {
    
    public ButtonListenerTest() {
    }
    
    @Before
    public void setUp() {
    }


    @Test
    public void testInstantiation() {
        ButtonListener listener = new ButtonListener();
    }
    
    @Test
    public void initialStateIsUnpressed() {
        ButtonListener listener = new ButtonListener();
        assertEquals("initial state is open", listener.getState(), ButtonListener.NEUTRAL);
    }
    
    @Test
    public void isPressIfAllOtherEventsZeroAndPress() {
        ButtonListener listener = new ButtonListener();
        listener.updateState(true, 0);
        assertEquals("after a single click state is press", listener.getState(), ButtonListener.PRESS);
    }
    
    @Test
    public void updateLastPolltimeTests() {
        ButtonListener listener = new ButtonListener();
        listener.updatePollTime(1000);
        assertEquals("at first poll time should be 0", listener.getTimeElapsedSinceLastPoll(), 0);
        listener.updatePollTime(1001);
        assertEquals("time between 1 mill interval should be 1", listener.getTimeElapsedSinceLastPoll(), 1);
        listener.updatePollTime(1002);
        assertEquals("time between 1 mill interval should be 1", listener.getTimeElapsedSinceLastPoll(), 1);
        listener.updatePollTime(1004);
        assertEquals("time between 2 mill interval should be 1", listener.getTimeElapsedSinceLastPoll(), 2);
    }

    
    @Test
    public void isNeutralPressedOnFirstFalse()  {
        ButtonListener listener = new ButtonListener();
        listener.updateState(false, 0);
        assertEquals("initial update with unpressed button is NEUTRAL", listener.getState(), ButtonListener.NEUTRAL);
    }
    
    @Test
    public void isPressedOnFirstTrue()  {
        ButtonListener listener = new ButtonListener();
        listener.updateState(true, 0);
        assertEquals("when first clicked state is pressed", listener.getState(), ButtonListener.PRESS);
    }
    
    @Test
    public void isPressedOnFalseToTrueTransition()  {
        ButtonListener listener = new ButtonListener();
        listener.updateState(false, 0);
        listener.updateState(true, 1);
        assertEquals("when first clicked state is pressed", listener.getState(), ButtonListener.PRESS);
    }

    
    @Test
    public void isClickIfFirstReleaseTimeGreaterThanClickMillis()  {
        ButtonListener listener = new ButtonListener();
        listener.updateState(true, 0);
        listener.updateState(false, 1);
        assertEquals("after a single click and release state is click", ButtonListener.SINGLE_CLICK, listener.getState());
    }
    
    @Test
    public void holdIfPressTimeMoreThanClickMillis() {
        ButtonListener listener = new ButtonListener();
        listener.updateState(true, 0);
        listener.updateState(true, 251);
        assertEquals("after a single click and hold state is hold", ButtonListener.HOLD, listener.getState());
    }
    
    @Test 
    public void inOrderToHaveNeutralTimeMustSpend1Cycle() {
        ButtonListener listener = new ButtonListener();
        listener.updateState(false, 0);
        assertEquals("neutraltime is 0 after 1 cycle", 0l, listener.getNeutralTime());
        listener.updateState(false, 1);
        assertEquals("neutraltime is 1 after 1 cycle", 1l, listener.getNeutralTime());
        
    }
    
    @Test
    public void doubleClickIf2ndClickCycle() {
        ButtonListener listener = new ButtonListener();
        listener.updateState(true, 0);
        listener.updateState(false, 200);
        listener.updateState(true, 201);
        listener.updateState(false, 202);
        assertEquals("after a double click state is double click", ButtonListener.DOUBLE_CLICK, listener.getState());
    }
    

    
    @Test
    public void eventHandlersGetNotified() {
        ButtonListener listener = new ButtonListener();
        final MyButtonEventHandler myButtonEventHandler = new MyButtonEventHandler();
        listener.addListener(myButtonEventHandler);
        listener.updateState(true, 0);
        assertEquals("press", ButtonListener.PRESS, myButtonEventHandler.event);
        listener.updateState(false, 200);
        assertEquals("click", ButtonListener.SINGLE_CLICK, myButtonEventHandler.event);
        listener.updateState(true, 201);
        assertEquals("press", ButtonListener.PRESS, myButtonEventHandler.event);
        listener.updateState(false, 202);
        assertEquals("double click", ButtonListener.DOUBLE_CLICK, myButtonEventHandler.event);
    }
    
    @Test
    public void trippleClickRegistersAsDoubleClickThenSingleClick() {
        ButtonListener listener = new ButtonListener();
        final MyButtonEventHandler myButtonEventHandler = new MyButtonEventHandler();
        listener.addListener(myButtonEventHandler);
        listener.updateState(true, 0);
        assertEquals(ButtonListener.PRESS, myButtonEventHandler.event);
        listener.updateState(false, 200);
        assertEquals(ButtonListener.SINGLE_CLICK, myButtonEventHandler.event);
        listener.updateState(true, 201);
        assertEquals(ButtonListener.PRESS, myButtonEventHandler.event);
        listener.updateState(false, 202);
        assertEquals(ButtonListener.DOUBLE_CLICK, myButtonEventHandler.event);
        listener.updateState(true, 203);
        assertEquals(ButtonListener.PRESS, myButtonEventHandler.event);
        listener.updateState(false, 204);
        assertEquals(ButtonListener.SINGLE_CLICK, myButtonEventHandler.event);
        listener.updateState(true, 205);
        assertEquals(ButtonListener.PRESS, myButtonEventHandler.event);
        listener.updateState(false, 206);
        assertEquals(ButtonListener.DOUBLE_CLICK, myButtonEventHandler.event);
    }
    
    @Test
    public void holdStateRetainedInManyCycles() {
        final MyButtonEventHandler myButtonEventHandler = new MyButtonEventHandler();
        ButtonListener listener = new ButtonListener();
        listener.addListener(myButtonEventHandler);
        listener.updateState(true, 0);
        assertTrue(myButtonEventHandler.event==ButtonListener.PRESS);
        assertTrue(listener.getState()==ButtonListener.PRESS);
        listener.updateState(true, 1);
        assertTrue(myButtonEventHandler.event==ButtonListener.PRESS);
        assertTrue(listener.getState()==ButtonListener.PRESS);
        listener.updateState(true, 2);
        assertTrue(myButtonEventHandler.event==ButtonListener.PRESS);
        assertTrue(listener.getState()==ButtonListener.PRESS);
        listener.updateState(true, 10);
        assertTrue(myButtonEventHandler.event==ButtonListener.PRESS);
        assertTrue(listener.getState()==ButtonListener.PRESS);
        listener.updateState(true, 249);
        assertTrue(myButtonEventHandler.event==ButtonListener.HOLD);
        assertTrue(listener.getState()==ButtonListener.HOLD);
        listener.updateState(true, 251);
        assertTrue(myButtonEventHandler.event==ButtonListener.HOLD);
        assertTrue(listener.getState()==ButtonListener.HOLD);
        listener.updateState(true, 275);
        listener.updateState(true, 502);
        assertTrue(myButtonEventHandler.event==ButtonListener.HOLD);
        assertTrue(listener.getState()==ButtonListener.HOLD);
        listener.updateState(false, 503);
        assertTrue(myButtonEventHandler.event==ButtonListener.NEUTRAL);
        assertTrue(listener.getState()==ButtonListener.NEUTRAL);
        listener.updateState(false, 504);
        assertTrue(myButtonEventHandler.event==ButtonListener.NEUTRAL);
        assertTrue(listener.getState()==ButtonListener.NEUTRAL);
        listener.updateState(true, 505);
        assertTrue(myButtonEventHandler.event==ButtonListener.PRESS);
        assertTrue(listener.getState()==ButtonListener.PRESS);
        listener.updateState(false, 506);
        assertTrue(myButtonEventHandler.event==ButtonListener.SINGLE_CLICK);
        assertTrue(listener.getState()==ButtonListener.SINGLE_CLICK);
        listener.updateState(true, 507);
    }
    
    private static class MyButtonEventHandler implements ButtonEventHandler {
        public long event = 0;

        @Override
        public void onEvent(long buttonEvent) {
            event = buttonEvent;
            
        }
        
        
    }
}
