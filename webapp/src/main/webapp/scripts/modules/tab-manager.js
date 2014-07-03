define(["knockout", "jquery"],
function(ko, $) {
	
	$(document).delegate(".tab", 'mouseover', function() {
		$(this).children('img.close').show();
	}).delegate(".tab", 'mouseout', function() {
		$(this).children('img.close').hide();
	});
	// the variable self is newly created, each time the module is invoked.
	var self = {};
	
	self.windowHeight = ko.observable(Math.max(400, $(window).height(), /* For opera: */ document.documentElement.clientHeight)).subscribeTo("windowHeight");
	
	self.desktopHeight = ko.computed(function() {
		return ((self.windowHeight() - 186) * 0.7);
	});
	
	self.invisibleTabs = ko.observableArray([]);
	self.visibleTabs = ko.observableArray(['tab0']);
	self.allTabs = new Object({
		tab0 : {moduleName:"document-browse-tab",tabHeading:"Topic 0", data: {topicId: 0, getParam: "bestDocs&TopicId=0"}}
	});

	self.active = ko.observable("tab0");
	self.nextTabIndex = 1;
	
	
	ko.postbox.subscribe("openNewTab", function(newTabData) {
		// Here we could test, whether newTab conforms to some interface that we require for new tabs
		for(tab in self.allTabs) {
			if(newTabData.tabHeading == self.allTabs[tab].tabHeading) {
				self.switchActive(tab);
				return;
			}
		}
		
		self.allTabs['tab' + self.nextTabIndex] = newTabData;
		self.visibleTabs.push('tab' + self.nextTabIndex);
		self.allTabs[self.active()].scrollPos = $('#desktop').scrollTop();
		self.active('tab' + self.nextTabIndex);
		self.rearrangeTabs();
		self.nextTabIndex++;
	});
	
    self.switchActive = function(tab) {
    	var tabIndex = $.inArray(tab, self.invisibleTabs());
    	if(tabIndex > -1) {
    		var newInvisbleTab = self.visibleTabs.shift();
    		self.invisibleTabs.push(newInvisbleTab);
    		self.invisibleTabs.splice(tabIndex, 1);
    		self.visibleTabs.unshift(tab);
    	}
    	$('#invisbleTabMenu').hide();
    	self.allTabs[self.active()].scrollPos = $('#desktop').scrollTop();
    	$(".tab").removeClass('active');
    	$("#" + tab).addClass('active');
    	self.active(tab);
    	$('#desktop').scrollTop(self.allTabs[tab].scrollPos);
    };
   
    self.deleteTab = function(tab) {
    	if(Object.keys(self.allTabs).length < 2) {
			alert("Ein Tab muss bleiben!");
			return;
		}

		var tabIndex = $.inArray(tab, self.visibleTabs());
		self.visibleTabs.splice(tabIndex, 1);
		
		if(self.invisibleTabs().length > 0) {
			var newTabIndex = self.invisibleTabs.shift();
			self.visibleTabs.unshift(newTabIndex);
		}
			 
		if(self.active() == tab) {
			if(tabIndex > self.visibleTabs().length - 1) {
				tabIndex = self.visibleTabs().length - 1;			
			}
			self.switchActive(self.visibleTabs()[tabIndex]);
		}		
		
		delete self.allTabs[tab];
    };
    self.deleteAllHidden = function() {
		if(confirm("Close all invisible tabs?")) {
			for(key in self.invisibleTabs()) {
				delete(self.allTabs[key]);
			}
			self.invisibleTabs([]);		
		}
	};
	
	self.rearrangeTabs = function() {
		var desktopWidth = self.windowWidth() - 10;
		var allTabIndexes = self.invisibleTabs().concat(self.visibleTabs());
		if(desktopWidth / allTabIndexes.length >= 200) {
			self.visibleTabs(allTabIndexes);
			self.invisibleTabs([]);
		} else {
			var numVisibleTabs = Math.floor((desktopWidth - 76) / 200);
			self.invisibleTabs(allTabIndexes.slice(0, allTabIndexes.length - numVisibleTabs));
			self.visibleTabs(allTabIndexes.slice(allTabIndexes.length - numVisibleTabs));			
		}
		self.switchActive(self.active());
	};
	
	self.toggleInvisbleTabs = function() {
		$('#invisbleTabMenu').toggle('blind');
	};
	
	self.windowWidth = ko.observable(1024).subscribeTo("windowWidth");

	self.windowWidth.subscribe(self.rearrangeTabs);
		
	return self;
});

