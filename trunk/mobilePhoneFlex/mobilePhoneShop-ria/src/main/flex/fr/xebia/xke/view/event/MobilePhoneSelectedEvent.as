package fr.xebia.xke.view.event {

import flash.events.Event;
import fr.xebia.xke.view.entity.MobilePhone;

public class MobilePhoneSelectedEvent extends Event {
	var mobilePhone:Object;
	
	public function getMobilePhone():Object{
		return this.mobilePhone;
		}
	
	public function MobilePhoneSelectedEvent(mobilePhone:Object){
		super("choosenItemEvent", true);
		this.mobilePhone=mobilePhone;
	}
}
}
