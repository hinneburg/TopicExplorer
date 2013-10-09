//Initialize your plugins
plugin.setTab = true;
plugin.tabName = 'Volltext';
plugin.tabCanClose = true;
plugin.view = '';

plugin.init = function() {	
	gui.drawTab('lalelu',true,true, 'Ich bin content');
};