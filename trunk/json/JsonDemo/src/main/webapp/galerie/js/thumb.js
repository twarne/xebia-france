

/**
 * La classe ThumbBar est notre barre de naviguation
 * 
 *
 */
var ThumbBar = Class.create({

/**
 * Constructeur de barre de naviguation
 * 
 * elem		element conteneur de la barre de naviguation
 * images	tableau d'images js au format {name: .., url: ..., desc: ...}
 * 
 */
initialize: function(elem, images, viewer){
	this.container = $(elem);	// Le conteneur de preview 
	this.images = images;		// le tableau d'images
	this.viewer = viewer; 		// Le controlleur de vue grd format
	this.thumbs = new Array();		//	les preview
	this.dimensions =new Array();	// les dimensions des preview
   	this.index = -1;				// L'indice de la derniere image agrandie
	this.effects = new Array();		// Stock tout les effets en cours par preview
	this.firstDisplayedId = 0;		// Indice de la premiere preview affichée
   
     /** Creer les eventListener dont on a besoin **/
   this.eventHover = this.onHover.bindAsEventListener(this);
   this.eventClick = this.onClick.bindAsEventListener(this);
   this.eventOut = this.onOut.bindAsEventListener(this);
   this.afterUpdateEvent = this.afterUpdate.bind(this); 
   
   	/** recuperer les options eventuelles et initialiser les valeures par defaut **/
   this.options = Object.extend({ debug:false,
                                   sizeMin: 70,
                                   sizeMax: 150,
                                   nodeType: 'div',
                                   displayCount: 6,
                                   buttonOpacity: 0.3,
                                   effectDuration: 0.5
                                }, arguments[2] || {});
                                
   this.sizeMin = this.options.sizeMin;
   this.sizeMax = this.options.sizeMax;
   this.nodeType= this.options.nodeType;
   
   	/** Construire les preview **/

   this.size = this.buildThumbs(images);
   if (this.size < this.options.displayCount)
		this.options.displayCount = this.size;
   
    /** Construire les boutons **/
   this.buildButtons();
   
   this.log("thumb size is "+this.size);
   
   /** calculer la taille initiale de la barre **/
   this.containerWidth = ((this.options.displayCount * this.dimensions[0].width) + (this.buttonPrev.getWidth() * 2))+((this.options.displayCount + 2)*10 )+2;
   
   this.container.style.width=this.containerWidth+'px'; 
   
   this.log("container size is "+(this.containerWidth)+'px');
   
   /** Rendre la barre en position absolu pour eviter des déformations **/
   this.containerClone = $("preview").cloneNode(false);
   this.container.absolutize();
   this.log("container left is "+this.container.style.left);
   this.container.parentNode.insertBefore(this.containerClone, $("preview"));
   this.containerClone.style.margin= '20px';
   this.containerClone.style.background='none';
   this.containerClone.style.border= 'none';
  
  
 
   this.middlePointX = this.container.positionedOffset().left + ((this.containerWidth)/2).round();
   this.viewer.setNextImage(this.images[0]);
}, 

/**
 * active/desactive un bouton de naviguation 
 * par un effet de transparence.
 */
enableButton: function (button, on){
	if (on)
		new Effect.Opacity(button, { from: this.options.buttonOpacity, to: 1});
	else
  		new Effect.Opacity(button, { from: 1, to: this.options.buttonOpacity});
},


/**
 * Methode appellée pour construire et ajouter à la barre,
 * les boutons de naviguation
 */
buildButtons: function(){
	     // Cree le bouton précédent
	var node = Builder.node(this.nodeType, { id: 'nav_prev', className: 'button'},[
	       Builder.node('img', {src:'img/icon_prev.png'})
	       ]);
	this.container.insert({top: node});
	this.buttonPrev = $(node);
	
	      // Cree le bouton suivant
	node = Builder.node(this.nodeType, { id: 'nav_next', className: 'button'},[
	       Builder.node('img', {src:'img/icon_next.png'})
	       ]);
	this.container.appendChild(node);
	this.buttonNext = $(node);
	
	     // Ecouter le clique de souris sur les 2 boutons 
	this.buttonPrev.observe('click', this.eventClick);
	this.buttonNext.observe('click', this.eventClick);
 
    	// Initialiser les boutons dans le bon état
	this.enableButton(this.buttonPrev, false);
	if (this.options.displayCount > this.size)
		this.enableButton(this.buttonNext, false);
},


/**
 * Construit les preview a partir du tableau d'images
 * chaque preview est stockée a la fin de this.thumb
 * les dimensions reelle du preview dans la page sont stockées dans this.dimensions
 * les preview au-dela de options.displayCount sont cachées
 *	
 * images	le tableau d'images
 * return	taille du tableau
 */
 buildThumbs: function (images){
 	var id = 0;
	while (id < images.length){
	     var e = images [id];
	     	// Construit une image 
	     var node = $(Builder.node(this.nodeType, { id: 'thumb_img_'+id, href:'#'},[
			            Builder.node('img', {id:'img_'+id, src:e.url, name:e.name}),
			            Builder.node('span', {}, e.name)
			            ]));
   
	      this.thumbs.push(node);
	      
	      if(this.options.displayCount <= id)
	         node.style.display='none';
	       
	      this.container.appendChild(node);
	      
	      		// Ecouter les evenements souhaité over, out, click
	      node.observe('mouseover', this.eventHover);
	      node.observe('mouseout', this.eventOut);
	      node.observe('click', this.eventClick);
	      node.thumbIndex = id;
	      this.dimensions.push(node.getDimensions());
	      id++;
	}
	return id;
 },

/**
 * Listener appellé qd la souris passe par dessus 
 * une image
 */
onHover: function(event){
 var elem = event.findElement(this.nodeType);
 var span = elem.down('span');
 
   // Afficher le nom de l'image
 span.style.display="block";
 
 this.log("onHover: thumb "+elem.thumbIndex);
 
 
  // Agrandir l'image sous la souris
 this.buildScale(elem.thumbIndex, this.sizeMax);

 if (elem.thumbIndex > this.firstDisplayedId)
    // Agrandir de la moitié l'image a gauche la cas échéant
  this.buildScale(elem.thumbIndex -1, this.sizeMin + ((this.sizeMax - this.sizeMin)/2 ));
  
 if (elem.thumbIndex < this.firstDisplayedId + this.options.displayCount - 1)
    // Agrandir de la moitié l'image a droite la cas échéant
  this.buildScale(elem.thumbIndex +1, this.sizeMin + ((this.sizeMax - this.sizeMin)/2));

 if (this.index != -1){ 
  
  if (elem.thumbIndex > this.index && this.index >  this.firstDisplayedId)
     // retrecir l'ancienne image de gauche
   this.buildScale(this.index -1, this.sizeMin );
  
  if (elem.thumbIndex < this.index && this.index < this.firstDisplayedId + this.options.displayCount - 1)
       // retrecir l'ancienne image de droite
   this.buildScale(this.index +1, this.sizeMin );
 
 }
 
 this.index = elem.thumbIndex;
 

 //event.stop();
}, 

/**
 * Annule tout effets en cours sur l'image thumbs[index] 
 * et lance un morph sur l'image et la div qui la contient
 *
 * width: taille cible de l'element
 */
buildScale: function (index, width){
	if (typeof this.effects[index] != 'undefined' ){
		this.effects[index].each(function(effect){
			effect.cancel();
		});
	}
	this.effects[index] = new Array (new Effect.Morph(this.thumbs[index],{style: {width:width+'px'}, duration: this.options.effectDuration, afterUpdate:this.afterUpdateEvent}),
									 new Effect.Morph(this.thumbs[index].down('img'),{style: {width:width+'px'}, duration:  this.options.effectDuration})
									);
},

/**
 * called for a click on a preview image or 
 * on the navigation buttons
 *
 *	event	the extended event
 */
onClick: function(event){
	 var elem = event.findElement(this.nodeType);
	 
	 if (typeof elem.thumbIndex != 'undefined') {
	  			// clique sur une image
	 	this.viewer.setNextImage(this.images[elem.thumbIndex]);
	  
	 }
	 else { // Nav button
	
		  if (elem.id == 'nav_prev'){
			   if(this.firstDisplayedId > 0){
				    this.thumbs[this.firstDisplayedId -1].style.display='block';
				    this.thumbs[this.firstDisplayedId + this.options.displayCount -1].style.display='none';
				    this.firstDisplayedId --;
				    
				    if (this.firstDisplayedId + this.options.displayCount == this.size - 1)
				      	this.enableButton(this.buttonNext, true);
				    
				    
				    if (this.firstDisplayedId == 0)
				    	this.enableButton(this.buttonPrev, false);
			   }
		  }else {
			 if(this.firstDisplayedId + this.options.displayCount < this.size){
				   this.thumbs[this.firstDisplayedId ].style.display='none';
				   this.thumbs[this.firstDisplayedId + this.options.displayCount].style.display='block';
				   this.firstDisplayedId ++;
				   
				   if (this.firstDisplayedId == 1)
				     	this.enableButton(this.buttonPrev, true);
				   
				   if (this.firstDisplayedId+ this.options.displayCount >= this.size)
				    	this.enableButton(this.buttonNext, false);
			 }
			 
		 }
	 }
},

/**
 * Listener appelle chaque fois que la souris sors d'un preview
 *
 */
onOut: function(event){
	 var elem = event.findElement(this.nodeType);
	 	// cacher le nom
	 var span = elem.down('span');
	 span.style.display="none";
	 	// reduire la preview
	 this.buildScale(elem.thumbIndex, this.sizeMin );
	 this.log("onOut: thumb "+elem.thumbIndex);
	 
	 if (elem.thumbIndex > 0)
	 		// reduire la preview de gauche
	 	this.buildScale(elem.thumbIndex -1, this.sizeMin);

	 if (elem.thumbIndex < this.size - 1)
	 	 	// reduire la preview de droite
	 	this.buildScale(elem.thumbIndex +1, this.sizeMin);

},



/**
 * called after each effect iteration 
 * allows to keep the bar with correct width and left coordinate
 * according to effect element width change.
 */
afterUpdate: function(effect){
	 this.log('AfterUpdate '+effect.element.thumbIndex);
	 var w = effect.element.getWidth();
	 if (this.dimensions[effect.element.thumbIndex].width+'px' != w){
		  this.containerWidth += (w - this.dimensions[effect.element.thumbIndex].width);
		  this.dimensions[effect.element.thumbIndex].width = w;
		  this.container.style.width=this.containerWidth+'px';
		  this.container.style.left= Math.round(this.middlePointX - (this.containerWidth/2))+'px';
	 }
},


// For logging
log: function(msg){
 if (this.options.debug)
  Log("[DEBUG] Thumbs : "+msg);
}

});

/**
 * classe fournissant le support pour la vue 
 * permet de changer l'image affiché 
 **/
var ImageViewer = Class.create ({

/**
 * constructor
 * elem			the big image block
 * img 			the image itself
 */
initialize: function (elem, images){

 this.container		= $(elem);
 this.image			= this.container.down('img');;
 this.desc			= this.container.down('p');
 this.currentImage	= null;
 
 // TODO: construire les images
 
 // TODO: ajouter un effet pour changer d'image
 
 // TODO: ajouter un editeur pour la description
},

/**
 * appellé pour chaque changement d'image requis
 */
setNextImage: function (image){
	this.currentImage	= image;
	this.image.src		= this.currentImage.url;
	this.desc.update (this.currentImage.desc);
}

});

/**
 * Fonction de log ajoute un message entouré par le tag <p>
 * dans la div d'id 'debug'
 */
function Log(msg){
 $("debug").innerHTML += "<p>"+msg+"</p>";
}
