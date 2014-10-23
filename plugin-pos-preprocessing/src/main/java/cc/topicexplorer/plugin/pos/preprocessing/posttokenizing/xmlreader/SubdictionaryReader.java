package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.xmlreader;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

	import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import auxiliaryClasses.Subdictionary;
import MatchingElementsClasses.Combotype;
import MatchingElementsClasses.MatchingElement;
import MatchingElementsClasses.Word;
import MatchingElementsClasses.Wordtype;
	
public class SubdictionaryReader {
	
		String configFile = "";
		Subdictionary subDictionary = null;
		RuleReader ruleReader;
		
		public SubdictionaryReader()
		{
			ruleReader = new RuleReader();
		}
	
		public void updateConfiguration(String configFile, Subdictionary subDictionary)
		{
			this.configFile = configFile;
			this.subDictionary = subDictionary;
		}
		
		@SuppressWarnings({ "unchecked", "null" })
		public void readSubdictionaryConfig() {
			try {
				// First, create a new XMLInputFactory
				XMLInputFactory inputFactory = XMLInputFactory.newInstance();
				// Setup a new eventReader
				InputStream in = new FileInputStream(configFile);
				XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
				// read the XML document

				while (eventReader.hasNext()) {
					XMLEvent event = eventReader.nextEvent();
					checkEvent(event, eventReader);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
			
		}
		
		void checkEvent(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException{
			if (event.isStartElement()) 
			{
				StartElement startElement = event.asStartElement();

				if (startElement.getName().getLocalPart() == ("ruleSection")) 
				{
					event = eventReader.nextEvent();
					//passing begin event of the MatchingElement
					event = eventReader.nextEvent();
					
					subDictionary.insertNewMatchingElement(getMatchingElement(event, eventReader));
				}
				if (startElement.getName().getLocalPart() == ("ruleFile")){
					
					event = eventReader.nextEvent();
					
					ruleReader.updateConfiguration(event.asCharacters().getData());
					subDictionary.insertNewRuleList(ruleReader.readConfig());
				}
			}
		}
		
		
		MatchingElement getMatchingElement(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException
		{
			if((event.asStartElement().getName().getLocalPart()
					.equals("word")))
			{
				event = eventReader.nextEvent();
				
				Word matchingElement = new Word(new String[]{event.asCharacters().getData()});
				event = eventReader.nextEvent();
				event = eventReader.nextEvent();
				return matchingElement;
			}
			
			if((event.asStartElement().getName().getLocalPart()
					.equals("wordtype"))){
				event = eventReader.nextEvent();
				
				Wordtype matchingElement = new Wordtype(new String[]{event.asCharacters().getData()});
				event = eventReader.nextEvent();
				event = eventReader.nextEvent();
				return matchingElement;
			}
			
			if((event.asStartElement().getName().getLocalPart()
					.equals("combotype"))){
				event = eventReader.nextEvent();
				String type = event.asCharacters().getData();
				event = eventReader.nextEvent();
				event = eventReader.nextEvent();
				String word = event.asCharacters().getData();
				
				Combotype matchingElement = new Combotype(new String[]{type, word});
				event = eventReader.nextEvent();
				event = eventReader.nextEvent();
				return matchingElement;
			}
			//exception
			return null;
		}


}
