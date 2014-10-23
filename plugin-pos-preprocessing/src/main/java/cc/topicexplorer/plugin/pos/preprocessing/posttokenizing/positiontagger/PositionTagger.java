package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.positiontagger;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;


public class PositionTagger 
{
	int firstCharPosition = 0;
	int lastCharPosition = 0;
	
	public void getBeginAndEnd(Token token, String plainText)
	{
		if(!Character.isLetter(token.token.charAt(0)))
		{
			return;
		}
		
		char currentChar;
		
		lastCharPosition = firstCharPosition+token.token.length()-1;
		
		/*
		for(;lastCharPosition<plainText.length();lastCharPosition++)
		{
			currentChar = plainText.charAt(lastCharPosition);
			if(!Character.isLetter(currentChar))
			{
				//Test for words like "Halle-Saale" or wordwraps
				if(currentChar == '-' && firstCharPosition != lastCharPosition
						&& lastCharPosition+1<plainText.length())
					//Test for words like "Halle-Saale"
					if(Character.isLetter(plainText.charAt(lastCharPosition+1)))
					{
						continue;
					}
					else
					{
						//test for wordwraps
						if(Character.isSpaceChar(plainText.charAt(lastCharPosition+1)) &&
								lastCharPosition+2<plainText.length())
						{
							if(Character.isLetter(plainText.charAt(lastCharPosition+2)))
							{
								lastCharPosition++;
								continue;
							}
						}
						else break;
					}
					
			}
			//word has obviously ended
			break;
		}
		*/
		
		
		//TODO: Set the firstChar and lastChar positions to token
		
		for(;lastCharPosition<plainText.length();lastCharPosition++)
		{
			//scroll until next word begins
			currentChar = plainText.charAt(lastCharPosition);
			if(Character.isLetter(currentChar))
				break;
		}
		
		firstCharPosition = lastCharPosition;
	}
	
}
	

