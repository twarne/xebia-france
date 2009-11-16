package renderer
{
	 import flash.display.Graphics;
	 
	 import mx.controls.Alert;
	 import mx.controls.DataGrid;
	 import mx.controls.Label;
	 import mx.controls.dataGridClasses.*;
	  
     public class CustomCell extends Label  
     { 

      override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void 
        { 
            super.updateDisplayList(unscaledWidth, unscaledHeight); 
            var g:Graphics = graphics; 
            g.clear();

	    	if (DataGridListData(listData).rowIndex % 2 == 0) {
	        	g.beginFill(0xffdfbf);
	     	} else {
	     		g.beginFill(0xffc080);
	     		
	     	}
	        g.drawRect(0, -2, unscaledWidth+1, unscaledHeight+4);
	        g.endFill();  
        }    
     } 
  
} 