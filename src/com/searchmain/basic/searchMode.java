package com.searchmain.basic;

import java.io.*;
import java.util.*;

public class searchMode {
	HashSet<String> hash = new HashSet<String>();
	
	RandomAccessFile[] rand = new RandomAccessFile[26];
	RandomAccessFile[] pRand = new RandomAccessFile[26];
	RandomAccessFile[] sRand = new RandomAccessFile[26];
	RandomAccessFile[] tRand = new RandomAccessFile[26];
	BufferedReader[] pBuff = new BufferedReader[26];
	
	FileReader[] pfile=new FileReader[26];
	FileReader[] sfile=new FileReader[26];
	//FileReader[] tfile=new FileReader[26];
	
	FileReader[] pfdReader =new FileReader[26];
	FileReader[] sfdReader =new FileReader[26];
	//FileReader[] tfdReader =new FileReader[26];
	
	BufferedReader[] reader = new BufferedReader[26];
	FileReader[] file = new  FileReader[26];
	
	FileReader fileTitle;
	BufferedReader titleSecReader;
	RandomAccessFile randSec;
	
	RandomAccessFile randTitle ; 
	
	Stemmer st = new Stemmer();
	Scanner input = new Scanner(System.in);
	Scanner reader1;
	//XmlEventHandler check = new XmlEventHandler();
	int isPhraseQuery;
	long offset;
	long start;
    long end ;
    long  middle;
    int j,result;
    String lineFetched,temp;
    String searchFor;
    int k;
    long fileLength;
    String lineFetched1,lineFetched2;
    String temp1,temp2;
    int result1,result2;
    StringBuilder stBuild = new StringBuilder();
    HashMap<String , Long> map = new HashMap<String , Long>();
    HashMap<Long,Long> innerHashMap;
    HashMap<Character,HashMap<Long,Long>> hashCategory = new HashMap<Character, HashMap<Long, Long>>();
    HashMap<Long,Long> relevanceMap = new HashMap<Long,Long>();
    TreeMap<Long,Long> finalSortedMap = new TreeMap<Long,Long>();
    
    
    //HashMap<String , Long> mapTitle = new HashMap<String , Long>();
    Vector<Long> titleDocId =  new Vector<Long>();
	Vector<Long> titleOffset = new Vector<Long>();

	
    HashMap<String , Character> checkCategory = new HashMap<String,Character>();
    long intermediate;
  
    public String binarySearch(Long topId)
    {
    	//System.out.println("$$$$$");
    	int mid = 0;
    	int start = 0;
    	int s =0; 
    	long offset = -1;
		int end = titleDocId.size() - 1;
		int e = titleDocId.size() - 1;
		// binary search on vector
		while (start <= end)
		{
			mid = (start + end) / 2;
			
			if ( titleDocId.get(mid) == topId)
			{
				offset = titleOffset.get(mid);
				break;
			}
			
			if ( mid == s)
			{
				offset =titleOffset.get(s+1);
				break;
			}
			if (mid == e)
			{
				 offset = titleOffset.get(mid-1);
				break;
			}
			
			if ( titleDocId.get(mid) < topId &&  titleDocId.get(mid+1) > topId)
			{
				offset = titleOffset.get(mid);
				break;
			}
			if ( titleDocId.get(mid) > topId )
				end = mid-1;
			else
				start = mid +1;
		}
		
		if ( offset == -1)
		{
			System.out.println("returning null");
			return null;
		}
		else
		{
			//System.out.println("This is my closest doc id:"+titleDocId.get(mid));
			try 
			{
				randSec.seek(offset);
				offset = modifiedLinearSearch(topId.toString(),randSec);
				if ( offset == -1)
					return null;
				else
				{
					randTitle.seek(offset);
					lineFetched = randTitle.readLine();
					j= 0;
					while (lineFetched.charAt(j++) != ':' );
					return (lineFetched.substring(j));
				}
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
    }
    void loadOnRam(BufferedReader[] pBuff)
    {
    	try 
		{
    	for ( int i = 0 ; i < 26 ; i++)
    	{
    		
    			while ( (lineFetched=pBuff[i].readLine()) != null)
    			{	
    					//System.out.println("!!!"+lineFetched);
    					j=0;
    					while( lineFetched.charAt(j++) != ',' );
    					//temp = lineFetched.substring(0,j-1);
    					map.put(lineFetched.substring(0, j-1),Long.parseLong(lineFetched.substring(j)));		
    			}		
    	}
    	
    	while ( (lineFetched=titleSecReader.readLine()) != null)
		{	
				//System.out.println("!!!"+lineFetched);
				j=0;
				while( lineFetched.charAt(j++) != ',' );
				//temp = lineFetched.substring(0,j-1);
				//mapTitle.put(lineFetched.substring(0, j-1),Long.parseLong(lineFetched.substring(j)));
				//System.out.println(Long.parseLong(lineFetched.substring(0, j-1))+"**"+Long.parseLong(lineFetched.substring(j)));
				titleDocId.add(Long.parseLong(lineFetched.substring(0, j-1)));
				titleOffset.add(Long.parseLong(lineFetched.substring(j)));
				
				
		}
    	
    	titleSecReader.close();
    	fileTitle.close();
		}
    	
    	catch (NumberFormatException e)
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    
    String searchArray[];
    
    public String  retrieveLine(String searchKeyWord)
    {
    	//System.out.println(searchKeyWord);
    	try 
    	{
    	int k;
    	k = searchKeyWord.charAt(0)-'a';
		offset = 0;

		if (map.containsKey(searchKeyWord))
			offset = map.get(searchKeyWord);
		else
			offset = -1;
			
	/*	
		//search in tertiary index
		tRand[k].seek(0);
		fileLength=tRand[k].length();
		end = fileLength;
		start=0;
		offset = modifiedBinarySearch(searchKeyWord,tRand[k]);

		//search in secondary index
		//System.out.println("k:"+k);
		sRand[k].seek(offset);
		fileLength=sRand[k].length();
		end = fileLength;
		start=offset;
		
		offset = modifiedBinarySearch(searchKeyWord,sRand[k]);
		if ( offset == -1 )
		{ 
			// unlikely event , now check for error
			System.out.println("Secondary index failed");
			return null;
		}

		//search in primary index
		pRand[k].seek(offset);
		//fileLength=pRand[k].length();
		//end = fileLength;
		//start=offset;
		//System.out.println("--"+st.toString()+":offset:"+offset);
		offset = modifiedLinearSearch(st.toString(),pRand[k]);
	*/
		if ( offset == -1)
		{
			//System.out.println("Word Does not Exist!");
			return null;
		}
		else
		{
			//rand[k].seek(0);
			//reader[k].skip(0);
			//reader[k].skip(offset);
	
			rand[k].seek(offset);
			return (rand[k].readLine());
			///System.out.println("offset:"+offset);
			//System.out.println("Found!!");
			//System.out.println("Found:"+rand[k].readLine());
			//rand[k].readLine();
			//System.out.println("Found:"+pBuff[i].readLine());
			//long endTime   = System.currentTimeMillis();
			//long totalTime = endTime - startTime;
			//System.out.println("Last Search In:"+totalTime+"ms");
		}
		
    	} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//System.out.println("I should not be here");
		return null;
    }
    
    public void printHashMap(HashMap toPrint)
	{
		 Iterator it = toPrint.entrySet().iterator();
		// char key;
		 long  docid;
		 HashMap i;
		 
         while (it.hasNext())
         {

             Map.Entry pairs = (Map.Entry)it.next();
             i = (HashMap)pairs.getValue();
             Set keys = i.keySet();
             long k;
             
             System.out.println("--------"+pairs.getKey());
             
             for(Iterator j = keys.iterator(); j.hasNext();)
             {
                
             		docid = (long) j.next();
                     k = (long)i.get(docid);
                     System.out.println("docid:"+docid+"count:"+k);
             }
         }
	}
    
    public void printRelevanceMap()
    {
    	 Iterator it = relevanceMap.entrySet().iterator();		 
		 while (it.hasNext())
		 {
			 Map.Entry pairs = (Map.Entry)it.next();
              System.out.println("--------"+":docid::::"+pairs.getKey()+":value:"+ pairs.getValue());
    	 }
    }
    
    public void printTopResults()
    {
    	Iterator it;
    	long maxScore ;
    	long topId;
    	Map.Entry pairs ;
    	
    	for (int i = 1 ; i < 11 ; i++)
    	{
    		it = relevanceMap.entrySet().iterator();   		 
    		maxScore = -1;
    		topId = -1;
    		
   		 	while (it.hasNext())
   		 	{
   		 		pairs = (Map.Entry)it.next();	
   		 		
                 if ( (long )(pairs.getValue()) >= maxScore)
                 {
                	 maxScore = (long)pairs.getValue();
                	 topId= (long) pairs.getKey();
                 }
   		 	}
   		 	
   		 	if ( topId == -1)
   		 		break;
   		 	else
   		 	{
   		 		//System.out.println("Rank "+i+":"+topId+"Score "+":"+maxScore);
   		 		System.out.println("Rank "+i+":"+topId+"\n----"+binarySearch(topId));
   		 		relevanceMap.remove(topId);
   		 	}
    	}

    }
    
   
    public void scoreAccordingToRelevancy()
	{
		
		 Iterator it = hashCategory.entrySet().iterator();
		 long currentDocId = 0;
	     char currCategory;
	     HashMap innerHash;
	     Set keys;
	     long currentCount;
	     
        while (it.hasNext())
        {

            Map.Entry pairs = (Map.Entry)it.next();
            innerHash = (HashMap)pairs.getValue();
            keys = innerHash.keySet();
            
            //System.out.println("--------"+pairs.getKey());
            
            currCategory = (char) pairs.getKey() ;
            
            for(Iterator j = keys.iterator(); j.hasNext();)
            {
               
            	currentDocId = (long) j.next();
                currentCount = (long)innerHash.get(currentDocId);
              
                 //System.out.println("docid:"+currentDocId+"count:"+k);
             		
             		if(relevanceMap.containsKey(currentDocId))
             		{
             			intermediate = relevanceMap.get(currentDocId);
             		}
             		else
             		{
             			intermediate =0;
             		}
             			switch(currCategory)
             			{
             				case 't' : intermediate+=3*currentCount;
             						 break;
             				case 'l' : intermediate+=20*currentCount;
             						 break;
             				case 'f' : intermediate+=5*currentCount;
             						 break;
             				case 'c' : intermediate+=currentCount;
             						 break;
             				case 'e' : intermediate+=currentCount;
             			}
             			
             			relevanceMap.put(currentDocId, intermediate);
             		}
            }
        	//printRelevanceMap();
	}
    
    public void indexLine(String lineFetched)
    {
    	
    	//String lineFetched = "word,11122f1t123132,414132f2";    	
    	int j=0;
    	long currentDocId = 0;
    	char currCategory;
    	StringBuilder myWordBuilder = new StringBuilder();
    	String myWord;
    	
    	while( lineFetched.charAt(j) != ',' )
    	{
    		myWordBuilder.append(lineFetched.charAt(j));
    		j++;
    		//System.out.println("$$$"+lineFetched.substring(0,j));
    	}
    	j++;
    	
    	myWord = myWordBuilder.toString();
    	//System.out.println("myword:"+myWord);
    	
    	while( j < lineFetched.length() )
    	{
    		stBuild.setLength(0);
    		//System.out.println("Starting with--------------------------------------------"+lineFetched.charAt(j));
    		
    		while ( lineFetched.charAt(j) != 't' &&  lineFetched.charAt(j) != 'l' &&  lineFetched.charAt(j) != 'f' &&  lineFetched.charAt(j) != 'c' && lineFetched.charAt(j) != 'e' )
    		{
    			stBuild.append(lineFetched.charAt(j));
    			j++;
    		}
 
    		//System.out.println("###"+currentId.toString());
    		currentDocId += Long.parseLong(stBuild.toString());
    		//System.out.println("###"+ currentDocId);
    		
    		while (  j < lineFetched.length() )
    		{
    			currCategory = lineFetched.charAt(j++);
    			stBuild.setLength(0);
    			
    			while (  j <  lineFetched.length() && lineFetched.charAt(j) >= '0' && lineFetched.charAt(j) <= '9' )
    			{
    				stBuild.append(lineFetched.charAt(j));
    				j++;
    			}
			
    		//System.out.println("isPhraseQuery"+isPhraseQuery+"checkCategory.get(myWord)"+checkCategory.get(myWord));
    		
    		if ( isPhraseQuery== 0 || checkCategory.get(myWord) == currCategory )
    		{	
    			if(hashCategory.containsKey( currCategory))
				{
					if(hashCategory.get( currCategory ).containsKey( currentDocId ))
					{
						 intermediate = (Long) hashCategory.get( currCategory).get( currentDocId );
						
						 intermediate = Long.parseLong((stBuild.toString())) +  intermediate;
						//System.out.println("count="+termCount);
						hashCategory.get(currCategory).put(currentDocId,  intermediate);
		            }
		            else
		            {
						hashCategory.get(currCategory).put(currentDocId, Long.parseLong((stBuild.toString())));
		            }
		        }
		        else
		        {
		        	innerHashMap = new HashMap<Long, Long>();
		        	innerHashMap.put(currentDocId,Long.parseLong(stBuild.toString()));
		            hashCategory.put(currCategory,innerHashMap);          
		        }
    		}
    			if ( j >= lineFetched.length() || lineFetched.charAt(j) == ',')
    				break;
    	}	
    		//incrementing j for skipping comma
    		j++;
    	}
    	
    	//System.out.println("going out...............");
    	//printHashMap(hashCategory);
    }
    public void tokeniseSearchTerm(String searchFor)
    {
    	String lineFetched;
    	int count =0;
    	int i;
    	searchArray = searchFor.split(" ");	
    	
    	if ( searchArray[0].charAt(1) == ':')
    		isPhraseQuery=2;
    	else
    		isPhraseQuery=0;
    	
		for (i = 0 ; i < searchArray.length; i++ )
		{
				
			lineFetched = searchArray[i].substring(isPhraseQuery);
			
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+lineFetched);
			
			if (!hash.contains(lineFetched))
			{	
				st.add(lineFetched.toCharArray(),lineFetched.length());
				st.stem();
				
				checkCategory.put(st.toString(),searchArray[i].charAt(0));
				
				lineFetched = retrieveLine(st.toString());
				
				if ( lineFetched != null )
				{
					indexLine(lineFetched);
				}
				else
				{
					count ++;
					System.out.println(searchArray[i] + ":Not Found!");
				}
			}
			else
			{
				//System.out.println(lineFetched + ":Try Other Keywords");
				count ++;
			}
		}
		if ( count == searchArray.length)
			System.out.println("Try other keywords");
		else
		{
			//System.out.println("displayResult()");
			scoreAccordingToRelevancy();
			printTopResults();
			hashCategory.clear();
			relevanceMap.clear();
			finalSortedMap.clear();
			//checkCategory.clear();
			System.out.println("Search Completed!");
		}
    }
    
	public searchMode(String indexPath) throws IOException 
	{
		int i;
	    hash = new HashSet<String>(Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your","null","aa","blank","ca","caption","categori","cdt","censu","com","edt"));

		try
		{
			fileTitle = new FileReader(indexPath +"/title__.txt");
			titleSecReader = new BufferedReader(fileTitle);
			randSec = new RandomAccessFile(indexPath +"/title_.txt","r");
			
			randTitle = new RandomAccessFile(indexPath +"/title.txt","r");
			
			for (i = 0 ; i < 26 ; i++)
			{
				//access posting list
				// file[i]=new FileReader( indexPath +"/d_"+i+".txt");
				 //reader[i] = new BufferedReader (file[i]);
				 rand[i] = new RandomAccessFile(indexPath+"/d_"+i+".txt","r");
				
				//access primary index 
				pRand[i] = new RandomAccessFile(indexPath+"/d__"+i+".txt","r");
				//pfile[i]=new FileReader( indexPath +"/d__"+i+".txt");
				pfdReader[i] = new FileReader( indexPath +"/d__"+i+".txt");
				pBuff[i] = new BufferedReader(pfdReader[i] );
				
				
				//access secondary index
				sRand[i] = new RandomAccessFile(indexPath+"/d___"+i+".txt","r");
				//sfile[i]=new FileReader( indexPath +"/d___"+i+".txt");
				//sfdReader[i] = new FileReader( indexPath +"/d___"+i+".txt");
				
				//access tertiary index
				tRand[i] = new RandomAccessFile(indexPath+"/d____"+i+".txt","r");
				//tfile[i]=new FileReader( indexPath +"/d____"+".txt");
				//tfdReader[i] = new FileReader( indexPath +"/d____"+".txt");
			}
		}
		catch (Exception e)
		{
			System.out.println("Except"+e);
		}

        try
        {
        
         System.out.println("Loading on Ram...");	
         loadOnRam(pBuff);
         System.out.println("Loading Done.");
        long count = Integer.parseInt(input.nextLine());
        
        for ( i = 0 ; i < count ; i++)
		{
                searchFor = input.nextLine();  
                
                long startTime = System.currentTimeMillis();
                tokeniseSearchTerm(searchFor.toLowerCase());
                long endTime   = System.currentTimeMillis();
    			long totalTime = endTime - startTime;
    			//System.out.println("Last Search In:"+totalTime+"ms");
		}
          
        }	
        catch (Exception e)
        {
			e.printStackTrace();
		}
        finally
        {
        	for ( i = 0 ; i < 26 ; i++)
        	{
        		//pRand[i].close();
        		sRand[i].close();
        		//tRand[i].close();
        		
        		pfile[i].close();
        		sfile[i].close();
        		//tfile[i].close();
        		
        		pfdReader[i].close();
        		pBuff[i].close();
        		sfdReader[i].close();
        		//tfdReader[i].close();
        		
        		reader[i].close();
        		file[i].close();
        		
        		rand[i].close();
        		
        		randTitle.close();
        		randSec.close();
        	}
        }
	
	}
	
	public long modifiedLinearSearch(String searchFor,RandomAccessFile fd)
	{
		
		try 
		{
			while ( (lineFetched=fd.readLine()) != null)
			{	
					//System.out.println("!!!"+lineFetched);
					j=0;
					while( lineFetched.charAt(j++) != ',' );
					temp = lineFetched.substring(0,j-1);
					result = temp.compareTo(searchFor);
					if ( result > 0 )
						return -1;
					else if ( result == 0)
					{
						//System.out.println("#LinrearSearch:"+lineFetched.substring(j+1));
						return Long.parseLong(lineFetched.substring(j));
					}	
			}
			return -1;
		} 
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public long modifiedBinarySearch(String searchFor,RandomAccessFile fd) 
	{
		int k,j;
		try 
		{
			lineFetched1 = fd.readLine();
			
	        j=0;
			while( lineFetched1.charAt(j++) != ',' );
			temp1 = lineFetched1.substring(0,j-1);
			result1 = temp1.compareTo(searchFor);
			
	        if ( result1 == 0 )
	        {
	        	//System.out.println("Found:"+lineFetched1);
                //System.out.println("#"+lineFetched1.substring(j));
	        	return Long.parseLong(lineFetched1.substring(j));
	        }
	        
	        lineFetched2 = fd.readLine();
	       
            if(lineFetched2 != null)
            {
            	k=0;
     			while( lineFetched2.charAt(k++) != ',' );
     			temp2 = lineFetched2.substring(0,k-1);
     			result2 = temp2.compareTo(searchFor);
     			
                if( result2 == 0)
                {
                	//System.out.println("Found:"+lineFetched2);
                    //System.out.println("#"+lineFetched2.substring(k));
    	        	return Long.parseLong(lineFetched2.substring(k));
                }

                if(result1 < 0 && result2 > 0)
                {
                	//System.out.println("Found:"+lineFetched1);
                    //System.out.println("#"+lineFetched1.substring(j));
    	        	return Long.parseLong(lineFetched1.substring(j));
                }

            }
            else
            {
            	//System.out.println("Found:"+lineFetched1);
                //System.out.println("#"+lineFetched1.substring(j));
	        	return Long.parseLong(lineFetched1.substring(j));
            }

            while (true)
            {
            	//System.out.println("##");
                middle = (start + end) / 2 ;
               
                fd.seek(middle);
                fd.readLine();
                lineFetched1 = fd.readLine();
                
                j=0;
    			while( lineFetched1.charAt(j++) != ',' );
    			temp1 = lineFetched1.substring(0,j-1);
    			result1 = temp1.compareTo(searchFor);

                if(result1==0)
                {
                	//System.out.println("Found:"+lineFetched1);
                    //System.out.println("#"+lineFetched1.substring(j));
    	        	return Long.parseLong(lineFetched1.substring(j));

                }

                lineFetched2 = fd.readLine();

                if(lineFetched2 != null)
                {

                	k=0;
         			while( lineFetched2.charAt(k++) != ',' );
         			temp2 = lineFetched2.substring(0,k-1);
         			result2 = temp2.compareTo(searchFor);

                    if( result2 == 0)
                    {
                    	//System.out.println("Found:"+lineFetched2);
                        //System.out.println("#"+lineFetched2.substring(k));
        	        	return Long.parseLong(lineFetched2.substring(k));
                    }

                    if(result1 < 0 && result2 > 0)
                    {
                    	//System.out.println("Found:"+lineFetched1);
                        //System.out.println("#"+lineFetched1.substring(j));
        	        	return Long.parseLong(lineFetched1.substring(j));
                    }
                    
                    if(result1 < 0 && result2 < 0)
                    	start = middle + 1;
                    else if ( result1 > 0 &&  result2 > 0 ) 	
                    	end = middle -1;
                }
                else
                {
                	//System.out.println("Found:"+lineFetched1);
                    //System.out.println("#"+lineFetched1.substring(j));
    	        	return Long.parseLong(lineFetched1.substring(j));
                }
            }
		   
		}
		catch (IOException e)
		{
				// TODO Auto-generated catch block
				e.printStackTrace();
		}    
		return 0;
	}
		    
		    
	/*public void startBinarySearch(String searchFor) throws IOException
	{
		 	
		    lineFetched = fd[k].readLine();
	        j=0;
			while( lineFetched.charAt(j) != ',' )
				j++;
			temp = lineFetched.substring(0,j);
			result = temp.compareTo(searchFor);
			
	        if ( result == 0 )
	        {
	        	//System.out.println("Found:"+lineFetched);
                System.out.println(lineFetched.substring(j+1));
	        	return;
	        }
		    
		    while (start <= end) 
		    {
		      
		        middle = start + (end - start) / 2;
		        if (middle > 0 )
		        	fd[k].seek(middle-1);
		        else
		        	fd[k].seek(start);
		        fd[k].readLine();
		        lineFetched = fd[k].readLine();
		        //System.out.println("LineFetched:"+ lineFetched );
		        j=0;
				while( lineFetched.charAt(j) != ',' )
					j++;
				temp = lineFetched.substring(0,j);
				result = temp.compareTo(searchFor);
				
		        if ( result == 0 )
		        {
		        	//System.out.println("Found:"+lineFetched);
                    System.out.println(lineFetched.substring(j+1));
		        	return;
		        }
		        if ( result > 0)
		            end = middle -1;
		        else 
		            start = middle+1;
		        //fd.seek(start);
		   }
		  //System.out.println();
	}*/
}
