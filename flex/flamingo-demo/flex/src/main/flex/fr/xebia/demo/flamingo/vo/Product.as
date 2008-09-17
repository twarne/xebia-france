package fr.xebia.demo.flamingo.vo
{

	[Bindable]
	[RemoteClass(alias="fr.xebia.demo.flamingo.Product")]
	public class Product
	{

        public var id:Number;
        
        public var version:Number;
        
        public var name:String;
        
        public var description:String;
        
        public var price:Number;
        
        public var avaibility:Date;
           
	}
}