package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.xmlreader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.auxillaryclasses.Gap;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.auxillaryclasses.Rule;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements.MatchingElement;


public class RuleReader {

	String configFile="";
	
	public void updateConfiguration(String configFile)
	{
		this.configFile = configFile; 
	}
	
	List<Rule> rules = new ArrayList<Rule>();
	
	int priority=0;
	List<List<MatchingElement>> allRuleElements;
	List<Gap> allRuleGaps;
	String continuationPOS="";

	void resetAtributes()
	{
		priority =0;
		allRuleElements=new ArrayList<List<MatchingElement>>();
		allRuleGaps=new ArrayList<Gap>();
		continuationPOS="";
	}
	
	@SuppressWarnings({ "unchecked", "null" })
	public List<Rule> readConfig() {
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

		return rules;
	}
	
	void checkEvent(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		if (event.isStartElement()) 
		{
			StartElement startElement = event.asStartElement();

			if (startElement.getName().getLocalPart() == ("rule")) 
			{
				//a new rule has been found
				
				Iterator<Attribute> attributes = startElement.getAttributes();
				while (attributes.hasNext()) {
					Attribute attribute = attributes.next();
					if (attribute.getName().toString().equals("priority")) {
						priority = Integer.parseInt(attribute.getValue());
					}
					if (attribute.getName().toString().equals("continuationPOS")) {
						continuationPOS = attribute.getValue();
					}
				}
				return;
			}

			if (startElement.getName().getLocalPart().equals("gap")) 
			{
				booalean lasting;
				
				Iterator<Attribute> attributes = startElement.getAttributes();
				while (attributes.hasNext()) {
					Attribute attribute = attributes.next();
					if (attribute.getName().toString().equals("priority")) {
						priority = Integer.parseInt(attribute.getValue());
					}
				}
				event = eventReader.nextEvent();


				List<String> gapElements = new ArrayList<String>();
				
				while(event.isStartElement())
				{
					if((event.asStartElement().getName().getLocalPart()
						.equals("wordtype")))
					{
						event = eventReader.nextEvent();
					
						gapElements.add(event.asCharacters().getData());
						event = eventReader.nextEvent();
						event = eventReader.nextEvent();
					}
				}
				
				allRuleGaps.add(new Gap(gapElements));
				return;
			}
			
			if (startElement.getName().getLocalPart().equals("ruleElement")) {
				event = eventReader.nextEvent();

				List<MatchingElement> wordsToMatch = new ArrayList<MatchingElement>();
				
				while(event.isStartElement())
				{
					wordsToMatch.add(getMatchingElement(event, eventReader));
				}

				allRuleElements.add(wordsToMatch);
				return;
			}
		}
		
		if(event.isEndElement())
		{
			if(event.asEndElement().getName().getLocalPart() == ("rule")){
				rules.add(new Rule(allRuleElements, allRuleGaps, continuationPOS, priority));
				resetAtributes();
				return;
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
