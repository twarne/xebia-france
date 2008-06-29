package org{
	import flash.display.Bitmap;
	import flash.display.Sprite;
	import flash.events.Event;
	
	import org.papervision3d.cameras.Camera3D;
	import org.papervision3d.materials.BitmapMaterial;
	import org.papervision3d.materials.MaterialsList;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.scenes.Scene3D;
	import org.papervision3d.objects.Collada;

	[SWF(backgroundColor="#000000", frameRate="60")]
	
	/**
	 * Papervision3dTutorial.as
	 * 25 March 2007
	 * @author Dennis Ippel - http://www.rozengain.com
	 */	
	public class colladaTestCLass extends Sprite
	{
		[Embed(source="/users/clgibb/Documents/Flex Builder 2/carousel/duckCM.jpg")] private var MyPhotoMaterial:Class;
		[Embed(source="/users/clgibb/Documents/Flex Builder 2/carousel/duck_triangulate.dae", mimeType="application/octet-stream")] private var MyCone:Class;
		private var myMaterials:Object;
		private var container:Sprite;
		private var scene:Scene3D;
		private var camera:Camera3D;
		private var rootNode:DisplayObject3D;
		
		/**
		 * Constructor
		 * @return 
		 */		 
		public function colladaTestCLass()
		{
			var myPhotoMaterial:Bitmap = new MyPhotoMaterial() as Bitmap;
			
			// create an object containing the material(s)
			myMaterials = {
				// the property name needs to correspond with the name you gave it
				// in 3ds Max. you can also look it up in the collada (*.dae) file:
				// <material id="myPhotoMaterial" name="myPhotoMaterial">
				myPhotoMaterial: new BitmapMaterial( myPhotoMaterial.bitmapData )
			};
			
			// initialize the objects
			init3D();
			
			// add a listener for the 3D loop
			addEventListener(Event.ENTER_FRAME, loop3D);
		}
		
		/**
		 * Creates the container, scene, camera and root node
		 */		
		private function init3D():void {
			// create the container, add it to the stage, position it
			container = new Sprite();
			addChild( container );
			
			container.x = 200;
			container.y = 200;
			
			// create a new scene and use the container
			scene = new Scene3D( container );
			
			// create a new camera and position it
			camera = new Camera3D();	
			camera.x = 3000;
			camera.z = -300;
			camera.zoom = 5;
			camera.focus = 10;
			
			// add a root node to the scene and add our 3d object by
			// reading the Collada (*.dae) file
			rootNode = scene.addChild( new DisplayObject3D("rootNode") );
			rootNode.addChildren( new Collada( XML( new MyCone() ), new MaterialsList( myMaterials ) ) );
		}
		
		/**
		 * The 3D animation loop
		 * @param event
		 */		
		private function loop3D( event:Event ):void {
			// get our cone object which resides in the root node
			var myCone:DisplayObject3D = rootNode.getChildByName("Cone01");
			
			if(myCone) {
				// object exists, now rotate it
				myCone.rotationY += 1;
			}

			// render the camera to see the changes
			scene.renderCamera( camera );
		}
	}
}

