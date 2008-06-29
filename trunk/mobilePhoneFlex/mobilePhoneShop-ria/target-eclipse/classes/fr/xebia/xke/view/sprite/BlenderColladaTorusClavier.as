package fr.xebia.xke.view.sprite {
	import flash.display.Bitmap;
	import flash.display.*;
	import flash.events.*;
	import flash.ui.*;
	import flash.net.*;
	
	import org.papervision3d.cameras.Camera3D;
	import org.papervision3d.materials.*;
	import org.papervision3d.materials.utils.*;
	import org.papervision3d.objects.Collada;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.scenes.Scene3D;

   
	[SWF(backgroundColor="#000000", frameRate="40")]
	public class BlenderColladaTorusClavier extends Sprite
	{
		//[Embed(source="./grid.png")] 
		//private var Material_001:Class;
		//[Embed(source="./RAZR_V3_simple_Plane.002.png")] private var Material_001:Class;
		
		[Embed(source="./torus.dae", mimeType="application/octet-stream")] 
		private var ColladaObject:Class;
		private var myMaterials:Object;
		private var container:Sprite;
		private var scene:Scene3D;
		private var camera:Camera3D;
		
		private var cola:Collada;
		private var colaHolder:DisplayObject3D;
		private var zrot:Boolean;
		private var zspeed:Number;
	 
		public function BlenderColladaTorusClavier()
		{
			// PLO Loader
			var loader:Loader=new Loader();
			var request:URLRequest =  new URLRequest("collada/grid.png");
			loader.load(request);
			
			
			//ColladaObject.source="collada/torus.dae";
			//Material_001.source="collada/grid.png";
			
			//var Material_001:Bitmap = new Material_001() as Bitmap;
			var Material_001:Bitmap = Bitmap(loader.content);
			
			myMaterials = {
				Material_001: new BitmapMaterial( Material_001.bitmapData )
			};
			
			init3D();
			
			addEventListener(Event.ENTER_FRAME, render);
		   	stage.addEventListener(KeyboardEvent.KEY_DOWN , onkey);
		   	stage.addEventListener(KeyboardEvent.KEY_UP , onkeyrelease);
		   	zrot = false;
		   	zspeed = 0;
		}

		private function init3D():void {
			container = new Sprite();
			addChild( container );
			
			container.x = 200;
			container.y = 200;
			

			scene = new Scene3D( container );
			

			camera = new Camera3D();	
			camera.x = 3000;
			camera.z = -50;
			camera.zoom = 150;
			camera.focus = 10;
			
			colaHolder = scene.addChild( new DisplayObject3D("rootNode") );
			cola= new Collada( XML( new ColladaObject() ), new MaterialsList( myMaterials ) );
			colaHolder.addChildren( cola );
			
		}
			
		private function loop3D( event:Event ):void {
			var screen: DisplayObject3D = scene.getChildByName("rootNode");
			var rotationY: Number = -(mouseX / stage.stageWidth * 360);
			var rotationX: Number = -(mouseY / stage.stageHeight * 360);
			screen.rotationY += (rotationY - screen.rotationY) / 12;
			screen.rotationX += (rotationX - screen.rotationX) / 12;
			scene.renderCamera(camera);
		}

		private function onkey(event:KeyboardEvent):void {
            switch(event.keyCode) {
				case Keyboard.UP: 
					colaHolder.rotationX += 3;
					break;
				case Keyboard.DOWN: 
					colaHolder.rotationX -= 3;
					break;
				case Keyboard.LEFT: 
					colaHolder.rotationY += 3;
					break;
				case Keyboard.RIGHT: 
					colaHolder.rotationY -= 3;
					break;
				case 65: 
					camera.moveBackward(40);
					break;
				case 66: 
					camera.moveForward(40);
					break;
				case 32:
					zrot = true;
					break;
				default:
					trace(event.keyCode);
					break;
			}
			
			
        }
		
		private function onkeyrelease(event:KeyboardEvent):void {
            switch(event.keyCode) {
				case 32:
					zrot = false;
					break;
			}
			
			
        }

		private function render(e:Event):void {
			if (zrot) {
				if (zspeed < 90) zspeed++;
				cola.roll(zspeed);
			} else {
				if (zspeed > 0) {
					zspeed--;
					cola.roll(zspeed);
				}
			}
			scene.renderCamera(camera);
        }
	}
}

	