package validation
{
    import mx.validators.NumberValidator;
    import mx.validators.ValidationResult;
    
    public class CustomNumberValidator extends NumberValidator
    {
        public var minLength:Number;
        
        public var maxLength:Number;
		
		public var lengthFieldError:String;
		
		public function CustomNumberValidator()
		{
			super();
		}
		
		override protected function doValidation(value:Object):Array
    	{
    		var results:Array = super.doValidation(value);
		
			var val:String = value ? String(value) : "";
			if ((val.length >= minLength && val.length <= maxLength) || ((val.length == 0) && !required))
				return results;
			else
			{
				return checkForLength(value);		
			}	
    	}
    	
		public function checkForLength(value:Object):Array
		{
			var results:Array = [];
			results.push(new ValidationResult(
						true, null, "invalidChar",
						lengthFieldError));
			
			return results;
		}
    }
}