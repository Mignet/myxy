package com.v5ent.game.profile;

import com.badlogic.gdx.utils.Array;

public class ProfileSubject {

    private Array<ProfileObserver> _observers;

    public ProfileSubject(){
        _observers = new Array<ProfileObserver>();
    }

    public void addObserver(ProfileObserver profileObserver){
        _observers.add(profileObserver);
    }

    public void removeObserver(ProfileObserver profileObserver){
        _observers.removeValue(profileObserver, true);
    }

    public void removeAllObservers(){
    	try{
    		_observers.removeAll(_observers, true);
    	}catch(IndexOutOfBoundsException e){
    		
    	}
    }

    protected void notify(final ProfileManager profileManager, ProfileObserver.ProfileEvent event){
        for(ProfileObserver observer: _observers){
            observer.onNotify(profileManager, event);
        }
    }

}
