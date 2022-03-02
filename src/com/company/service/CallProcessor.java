package com.company.service;

import com.company.domain.ArrivingCall;
import com.company.domain.CallCounter;
import com.company.domain.CallSystem;
import com.company.domain.OngoingCall;

import java.util.ArrayList;
import java.util.List;

public class CallProcessor {

    public static void scheduleArrivingCalls(){
        List<ArrivingCall> removalFromArrivalList = new ArrayList<>();
        for(ArrivingCall arrivingCall: CallSystem.getArrivingCallList()){

            if(arrivingCall.getArrivingTime() == CallSystem.getClock()){
                assignArrivingCallToOngoingList(arrivingCall);
                removalFromArrivalList.add(arrivingCall);
            }

        }
        CallSystem.getArrivingCallList().removeAll(removalFromArrivalList);

    }

    public static void assignArrivingCallToOngoingList(ArrivingCall arrivingCall){
        if(CallSystem.getLineInUse()==CallSystem.getLine()||isCallerBusy(arrivingCall)){
            incrementBlockedInCallCounter();
            incrementProcessedInCallCounter();
            return;
        }
        OngoingCallHandler.assignArrivingCallToOngoingList(arrivingCall);
        incrementProcessedInCallCounter();
        CallSystem.setLineInUse(CallSystem.getLineInUse()+1);
    }

    public static void incrementProcessedInCallCounter(){
        CallCounter callCounter = CallSystem.getCallCounter();
        callCounter.setProcessed(callCounter.getProcessed()+1);
        CallSystem.setCallCounter(callCounter);
    }

    public static void incrementBlockedInCallCounter(){
        CallCounter callCounter = CallSystem.getCallCounter();
        callCounter.setBlocked(callCounter.getBlocked()+1);
        CallSystem.setCallCounter(callCounter);
    }

    public static void incrementCompletedInCallCounter(){
        CallCounter callCounter = CallSystem.getCallCounter();
        callCounter.setCompleted(callCounter.getCompleted()+1);
        CallSystem.setCallCounter(callCounter);
    }

    public static boolean isCallerBusy(ArrivingCall arrivingCall){
        int arrTo = arrivingCall.getTo();
        int arrFrom = arrivingCall.getFrom();
        for(OngoingCall ongoingCall: CallSystem.getOngoingCallList()){
            int onTo = ongoingCall.getTo();
            int onFrom = ongoingCall.getFrom();
            if(arrFrom==onFrom||arrFrom==onTo||arrTo==onTo||arrTo==onFrom){
                return true;
            }
        }
        return false;
    }

    public static void completeOngoingCalls(){
        List<OngoingCall> ongoingCallList = CallSystem.getOngoingCallList();
        List<OngoingCall> removeFromOngoingCallList = new ArrayList<>();
        if(ongoingCallList.isEmpty()){
            return;
        }
        for(OngoingCall ongoingCall: ongoingCallList){
            if(ongoingCall.getEnd()==CallSystem.getClock()){
                removeFromOngoingCallList.add(ongoingCall);
                incrementCompletedInCallCounter();
                CallSystem.setLineInUse(CallSystem.getLineInUse()-1);
            }
        }
        CallSystem.getOngoingCallList().removeAll(removeFromOngoingCallList);
    }
}
