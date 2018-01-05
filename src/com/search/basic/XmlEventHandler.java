package com.search.basic;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.io.*;
import java.util.Map;
import java.util.Comparator;

class MapSort {
    public static Map sortByKey(Map unsortedMap){
        Map sortedMap = new TreeMap();
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }
}



class ValueComparator implements Comparator {

    Map map;

    public ValueComparator(Map map){
        this.map = map;
    }
    public int compare(Object keyA, Object keyB){

        Comparable valueA = (Comparable) map.get(keyA);
        Comparable valueB = (Comparable) map.get(keyB);

        System.out.println(valueA +" - "+valueB);

        return valueA.compareTo(valueB);

    }
}


public class XmlEventHandler extends DefaultHandler
{	
	//l = title , t=text , f=infobox , c= category , e = external links , i=id
	// reference tag not present
    Map<String,TreeMap>map = new HashMap<String, TreeMap>();
	public HashSet<String> hash = new HashSet<String>(Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your","null","aa","blank","ca","caption","categori","cdt","censu","com","edt"));
	String tag;
	int prevCount = 0;
	int nextTagFlag = 0;
	char newTag;
	char[] toBeStemmed = new char[400];
	int idTurn=0;
	String docId;
	int docCount=0;
	Scanner reader=null;
	int totalFiles=0;
    String folderPath = null;
    TreeMap<String, Integer[]> tree;
    Integer []a;
    Integer[] value ;
    StringBuilder title = new StringBuilder();
    
    Map<Character,Integer> m = new HashMap<Character,Integer>();
    
    File titleFile ;
    FileWriter  fdTitle;
    BufferedWriter bdTitle;
   
    
    public XmlEventHandler(String folderPath) 
    {
        this. folderPath= folderPath;
    }


    public void dumpIntoFile()
    {
		try 
		{
			File dataFile = new File( folderPath+"/d"+totalFiles+".txt");
			totalFiles++;
			if (!dataFile.exists())
			{
					dataFile.createNewFile();
			}
			else
			{
				//System.out.println("File Already Exists"); 
			}
			Map tree;
			FileWriter fd = new FileWriter(dataFile.getAbsoluteFile());
			BufferedWriter bd = new BufferedWriter(fd);
			String key;
            //tree = new TreeMap(map);
			
			tree = MapSort.sortByKey(map);
			//tree = new TreeMap(map);
            Map.Entry pair,innerPair;
            Iterator i2;
            Iterator i1 = tree.entrySet().iterator();
            Set keys;
            TreeMap innertreemap;

            
            
            while (i1.hasNext())
            {
                pair = (Map.Entry) i1.next();
                innertreemap = (TreeMap)pair.getValue();
                keys = innertreemap.keySet();
                bd.write((String) pair.getKey());
                //System.out.print((String) pair.getKey());
                i2 = innertreemap.entrySet().iterator();
                while (i2.hasNext())
                {
                    innerPair = (Map.Entry) i2.next();
                    key = (String)innerPair.getKey();
                    value = (Integer[]) innertreemap.get(key);
                    //System.out.print(","+key+"l"+value[0]+"t"+value[1]+"f"+value[2]+"c"+value[3]+"e"+value[4]);
                    //bd.write(","+key+"l"+value.l+"t"+value.t+"f"+value.f+"c"+value.c+"e"+value.e);
                    bd.write(","+key);
                    if (value[0]!= 0)
                    	bd.write("l"+value[0]);
                    if (value[1]!= 0)
                    	bd.write("t"+value[1]);
                    if (value[2]!= 0)
                    	bd.write("f"+value[2]);
                    if (value[3]!= 0)
                    	bd.write("c"+value[3]);
                    if (value[4]!= 0)
                    	bd.write("e"+value[4]);
                    //bd.write(","+key+"l"+value[0]+"t"+value[1]+"f"+value[2]+"c"+value[3]+"e"+value[4]);
                    //bd.write(","+key);
                }
                bd.write("\n");
                //System.out.println();
            }
		   bd.close();	
		   map.clear();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void startDocument()
	{
		m.put('l', 0);
		m.put('t', 1);
		m.put('f', 2);
		m.put('c', 3);
		m.put('e', 4);
		try 
		{
			titleFile= new File( folderPath+"/title.txt");
			titleFile.createNewFile();
			fdTitle = new FileWriter( folderPath+"/title.txt");
			bdTitle =new BufferedWriter(fdTitle);
		}
		catch (IOException e) 
		{
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		//System.out.println("Begin Parsing Document");
	}
	
	public void endDocument()
	{
	//	System.out.println("End Parsing Document");
		if (!map.isEmpty())
			dumpIntoFile();
		
		try 
		{
		  bdTitle.close();	
		  fdTitle.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		//ExternalMergeSort e = new ExternalMergeSort(totalFiles,totalFiles);
		//e.initialiseMerge(folderPath);
	}
	
	public void startElement(String nameSpaceURI, String localName, String tagName, Attributes atts)
	{
		tag = tagName;	
		//System.out.print("<" + tagName + ">");
	}
	
	public void endElement(String nameSpaceURI, String localName, String tagName)
	{
		//System.out.print("<" + tagName + "/>");
		if (tagName.equals("page"))
		{
			docCount++;
			prevCount = 0;
			nextTagFlag = 0;
			idTurn=0;
			
			try 
			{
				bdTitle.write(docId +":"+title.toString()+"\n");
				title.setLength(0);
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			title.setLength(0);
			if (docCount==10000)
			{
				dumpIntoFile();
				docCount=0;
			}
		}
		
	}
	
	public void printWord(char[] word, int len )
	{
		//System.out.println("word begin:");
		System.out.print("#"+newTag+">>");

		for ( int i = 0; i < len; i++){
			System.out.print(word[i]);
		}
		System.out.println(">>");
	}
	
	public void insertInMap(String stemmedWord)
	{
		if (map.containsKey(stemmedWord))
		{
			//System.out.println("i am here");
            if(map.get(stemmedWord).containsKey(docId))
            {
            	a = (Integer[]) map.get(stemmedWord).get(docId);
				a[m.get(newTag)]++;
            	map.get(stemmedWord).put(docId, a);
            }
            
			else
			{
				a=new Integer[5];
				for ( int i = 0 ; i  < 5 ; i ++)
					a[i]=0;
				a[m.get(newTag)]++;
				map.get(stemmedWord).put(docId,a);
			}
		}
		else
		{
			if (docId != null) 
			{
				a=new Integer[5];
				for ( int i = 0 ; i  < 5 ; i ++)
					a[i]=0;
				a[m.get(newTag)]++;
				tree = new TreeMap<String, Integer[]>();
				tree.put(docId, a);
				map.put(stemmedWord, tree );
			}
		}
		
	}
	
	public void stemmer(char[] character , int start, int length)
	{
		int i,l=0;
		Stemmer st = new Stemmer();
		/*System.out.println("In the stemmer:");
         for (int i = start; i < start + length; i++ )
 			System.out.print(character[i]);
         System.out.println(":End");
        */ 
		for ( i = start; i< start + length; i++){
			//if ( Character.isLetterOrDigit(character[i]) || character[i]=='.'|| character[i]=='_')
			if ( character[i] >='a' && character[i] <= 'z' ||  character[i] >='A' && character[i] <= 'Z')
			{
				//System.out.print("$"+character[i]+"$");
				if ( l < 50)
				{	toBeStemmed[l]=Character.toLowerCase(character[i]);
					//System.out.print("$"+toBeStemmed[l]+"$");
					l++;
				}
			}
		else
		{
			//printWord(toBeStemmed,l);
			if (l!=0 && l < 50)
			{
				//printWord(toBeStemmed,l);
				String newString = new String(toBeStemmed,0,l);
				if (!hash.contains(newString))
				{
					//printWord(toBeStemmed,l);
					st.add(toBeStemmed,l);
					st.stem();
					//System.out.println(newTag +":"+ st.toString());
					if (!hash.contains(st.toString()))
							insertInMap(st.toString());	
				}	
			}
			l=0;
		}
			
	}
		if (l!=0 && l < 100 )
		{
			//printWord(toBeStemmed,l);
			String newString = new String(toBeStemmed,0,l);
			if (!hash.contains(newString))
			{
				//printWord(toBeStemmed,l);
				st.add(toBeStemmed,l);
				st.stem();
				//System.out.println(newTag +":"+ st.toString());
				if (!hash.contains(st.toString()))
						insertInMap(st.toString());	
		}
	}
	
}
	
	public void idTagParser(char[] character, int start , int length)
	{
			//newTag='i';
			//System.out.println("im in id tag");
			if ( idTurn == 0)
			{	
				idTurn=1;
				docId=new String(character,start,length);
				//System.out.println("docId:"+docId);
			}
	}
	
	public void searchCategory(char[] character , int start ,int end)
	{
		int i;
		for ( i = start; i < end-5; i++)
		{
			if(character[i] == '[' && character[i+1] == '[' && character[i+2] == 'C' && character[i+3] == 'a' && character[i+4] == 't' && character[i+5] == 'e')
			{
				nextTagFlag = 5;
				newTag='e';
				stemmer(character,start,i-start);
				newTag='c';
				stemmer(character ,i+10, end-i-10);
				return;
			}
		}
		newTag='e';
		stemmer(character,start,end-start);
	}
	
	public void searchExternalLinks(char[] character , int start , int end)
	{
		int i;
		for ( i = start; i < end-5; i++)
		{
			if(character[i] == '=' && character[i+1] == '=' && character[i+2] == 'E' && character[i+3] == 'x' && character[i+4] == 't' && character[i+5] == 'e')
			{
				nextTagFlag = 4;
				newTag='t';
				stemmer(character,start,i-start);
				newTag='e';
				searchCategory(character , start+18 , end);
				return;
			}
		}
		newTag='t';
		stemmer(character,start,end-start);
	}

	public void searchApost(char[] character,int start,int end)
	{
		int j;
	/*	if (character[start] == '\''){
			if((prevCount == 2) || (prevCount == 1  && character[start+1] == '\'')){
				nextTagFlag=2;
				searchReferences(character , start +1 , end);
				return;
			}
		}
		*/			
		for ( j=start; j<end-2; j++)
		{
			//System.out.print(character[j]);
			if( character[j] == '\'' && character[j+1] == '\'' && character[j+2] == '\'')
					{
						nextTagFlag=3;
						newTag='f';
						stemmer(character , start , j-start);
						//searchReferences(character, j+3 , end);
						searchExternalLinks(character, j+3 , end);
						return;
					}
		}
		nextTagFlag=1;
		newTag='f';
		stemmer(character , start , end-start);
	}
	
	public void searchInfoBox(char[] character,int start, int end)
	{
		int i;
		for ( i = start ; i < end -9 ;i++){
			if(character[i] == '{' && character[i+1] == '{' && character[i+2] == 'I' && character[i+3] == 'n' && character[i+4] == 'f' && character[i+5] == 'o' && character[i+6] == 'b' && character[i+7] == 'o' && character[i+8] == 'x' )
			{
				nextTagFlag=1;
				searchApost(character,i+10,end);
				newTag='t';
				stemmer(character , start , i-start);
				return;
			}
		}
		newTag='t';
		nextTagFlag=0;
		stemmer(character , start , end-start);
	}
	
	
	public void textTagParser(char[] character, int start , int length)
	{
		int end = start + length;
		
		switch (nextTagFlag)
		{
		case 0: searchInfoBox(character, start , end);
				break;
		case 1: searchApost(character, start , end);
				break;
		//case 2: searchReferences(character, start , end);
				//break;
		case 3:searchExternalLinks(character , start , end);
				break;
		case 4:searchCategory(character , start , end);
			   break;
		case 5:newTag='c';
		   	  stemmer(character ,start, length);
		}
	}	
	
	public void characters(char[] character, int start, int length )
	{
		/*
		System.out.println("-------------------<"+tag+">----------------------------");
		for (int i = start; i < start + length; i++ ){
			System.out.print(character[i]);
		}
		System.out.println("-------------------</"+tag+">----------------------------");
		*/
        if (tag.equals("text")) 
        {
            textTagParser(character, start, length);

        } else if (tag.equals("id")) 
        {
            idTagParser(character, start, length);

        } 
        else if (tag.equals("title"))
        {
        	int i=start;
        	while (character[i] == ' ')
    			i++;
        	
        	for (; i < start + length && character[i]!= '\n'; i++ )
        	{
        		
        		title.append(character[i]);
    		} 
        
        	newTag = 'l';
            stemmer(character, start, length);
        }
	}
	
}
