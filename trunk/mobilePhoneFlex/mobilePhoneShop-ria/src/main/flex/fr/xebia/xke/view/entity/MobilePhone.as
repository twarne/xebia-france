package fr.xebia.xke.view.entity
{
  [RemoteClass(alias="fr.xebia.xke.domain.MobilePhone")]
  [Bindable]
  public class MobilePhone
  {
    public var id:Number;
    public var name:String;
    public var image:String;
    public var description:String;
    public var price:Number;
    public var stock:int;
  }
}