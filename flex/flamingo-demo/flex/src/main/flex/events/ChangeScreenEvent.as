package events
{
    import flash.events.Event;

    public class ChangeScreenEvent extends Event
    {
        
        public var toScreen:String;
        public var payload:Object;
        
        public function ChangeScreenEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
        {
            super(type, bubbles, cancelable);
        }
        
    }
}