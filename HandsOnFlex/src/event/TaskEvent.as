package event
{
	import flash.events.Event;

	public class TaskEvent extends Event
	{
		private var _taskName:String;
		
		private var _description:String;
		
		private var _priority:int;
		
		public function TaskEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false)
		{
			super(type, bubbles, cancelable);
		}
		
		public function get priority():int
		{
			return _priority;
		}

		public function set priority(value:int):void
		{
			_priority = value;
		}

		public function get description():String
		{
			if(_description == null)
				_description = "";
			return _description;
		}

		public function set description(value:String):void
		{
			_description = value;
		}

		public function get taskName():String
		{
			return _taskName;
		}

		public function set taskName(value:String):void
		{
			_taskName = value;
		}

	}
}