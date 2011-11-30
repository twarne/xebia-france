var scrollComponentFunction = function (options) {
	// Identifiant du composant scroll
	var componentId = options.componentId;
	// Componsant scroll
	var scrollComponent = jQuery("#scrollComponent"+componentId);
	var scrollBarContainer = jQuery("#scrollBarContainer"+componentId);
	// Composant scrollbar
	var scrollBar = jQuery("#scrollBar"+componentId);
	// Label pour afficher le numéro de page en cours
	var pageNumberLabel = jQuery("#pageNumberLabel"+componentId);
	// Bouton de navigation (Reculer d’une page)
	var scrollUpButton = jQuery("#scrollUpButton"+componentId);
	// Bouton de navigation (avancer d'une page)
	var scrollBottomButton = jQuery("#scrollBottomButton"+componentId);

	// Nombre total de pages
	var totalPages = options.totalPages;
	// Nombre total de lignes
	var maxRows = options.maxRows;
	// Nombre de lignes contenues dans l'espace scrollable
	var totalRows = maxRows - 2;
	// Hauteur d'une ligne. Cette valeur correspond à la hauteur des boutons de
	// navigation (42 pixels dans notre cas)
	var rowHeight = 42;
	// Page en cours
	var currentPage = options.currentPage;
	// Variable utilisée pour détecter le changement de la page en cours d'affichage
	var alreadyLoadedPage = currentPage;


	// Positionnement du scrollBar
	var scrollBarTop = parseFloat(scrollUpButton.position().top)+rowHeight;
	var scrollBarHeight, sensibility;
	if (totalPages < 6) {
        // Hauteur dynamique du composant scollbar selon le nombre de pages
        scrollBarHeight = (totalRows * rowHeight) / totalPages;
        // Sensibilité de déplacement du composant scrollbar
        sensibility = (totalRows * rowHeight) / totalPages;
    } else {
        // Hauteur fixe du composant scrollBar (1)
        scrollBarHeight = (rowHeight * totalRows) / 6;
        var oldsensibility = (((totalRows  * rowHeight) / totalPages) - (scrollBarHeight / totalPages));
        var diff = (((rowHeight * maxRows) - (rowHeight+scrollBarHeight)) -
                       (rowHeight + (oldsensibility * (totalPages - 1))) ) / (totalPages - 1);
        // Sensibilité de déplacement du composant scrollbar
        sensibility = oldsensibility + diff;
    }

	// Initialisation du composant scroll
	initScrollComponent();
	
	function initScrollComponent() {
        // Positionnement du scrollbar selon la page courante
        var initialScrollBarTop = scrollBarTop + ((currentPage * sensibility) - sensibility);
        scrollBar.css('top',initialScrollBarTop+'px');

        // Définition de la hauteur du composant scroll
        scrollComponent.css('height', maxRows * rowHeight+'px');
        // Définition de la hauteur de la barre du scroll
        scrollBarContainer.css('height', totalRows * rowHeight+'px');

        // Définition de la hauteur des boutons de navigation
        scrollUpButton.css('height',rowHeight+'px');
        scrollBottomButton.css('height',rowHeight+'px');
        // Définition de la hauteur du composant scrollBar
        scrollBar.css('height',scrollBarHeight+'px');
        // Numéro de page courante (7)
        pageNumberLabel.html(currentPage + '/' + totalPages);

        // Affichage du composant scrollBar
        scrollBar.show();
    }

	// Evènement OnClick sur le bouton de navigation (Reculer d'une page)
	scrollUpButton.click(function(){
       previousPage();
    });

	// Evènement OnClick sur le bouton de navigation (Avancer d'une page)
	scrollBottomButton.click(function(){
       nextPage();
    });

	// Evènement DragAndDrop sur le scrollbar
	scrollBar.draggable({ axis:'y', opacity: 0.70, currentPage: 'pointer', containment: "#scrollBarContainer"+componentId,
		stop: function(event, ui) {
            goToPage();
            loadPage();
        },
        drag: function(event, ui) {
            currentPage = Math.round((getScrollBarPosition() - scrollBarTop) / sensibility);
            pageNumberLabel.html(currentPage + '/'+ totalPages);
        }
	});

	// Evènement molette souris sur le composant scroll
	scrollComponent.bind('mousewheel', function(event, delta) {
		var dir = delta > 0 ? 'Up' : 'Down';
		if (dir =='Up') {
			previousPage();
		}
		else if  (dir =='Down') {
			nextPage();
		}

		clearTimeout(jQuery.data(this, 'timer'));
		jQuery.data(this, 'timer', setTimeout(function() {
		  	 loadPage();
		}, 250));
	});

	// Avancer d'une page
	function nextPage() {
	   if (currentPage == totalPages) {
            return false;
       }
	   currentPage = currentPage + 1;
	   goToPage();
	}

	// Reculer d'une page
	function previousPage() {
		if (currentPage == 1) {
			return false;
		}
		currentPage = currentPage - 1;
		goToPage();
	}

	// Changer les propriétés du scrollbar en cas de changement de page
	function goToPage() {
       // Nouvelle position selon la page courante
       var newTop = scrollBarTop + ((sensibility * currentPage) - (sensibility));
       scrollBar.css('top',newTop+'px');
       // Mise à jour du numéro de la page
       pageNumberLabel.html(currentPage + '/' + totalPages);
    }

	// Charger la page courante
	function loadPage() {
		if (alreadyLoadedPage != currentPage) {
           alreadyLoadedPage = currentPage;
        }
	}

	// Récupérer la position bottom du scrollbar
	function getScrollBarPosition() {
       var p = scrollBar.position();
       return parseFloat(p.top+sensibility);
    }
};