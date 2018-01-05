package com.search.basic;

import java.io.*;
import java.util.*;

public class ExternalMergeSort {
	
	int totalFiles;
	int fragments;
	
	public ExternalMergeSort (int totalFiles,int fragments)
	{
		this.totalFiles=totalFiles;
		this.fragments=fragments;
	}
	
	StringBuilder st;
	String [] s ;
	long num;
	long num1;
	long prev;
	
	public StringBuilder compress(String toCompress)
	{
		int i,j;
		st = new StringBuilder();
		s = toCompress.split(",");
		st.append(s[0]);
		st.append(","+s[1]);
		
		j=0;
		while ( s[1].charAt(j) >= '0' &&  s[1].charAt(j) <= '9')
			j++;
		num1 = Integer.parseInt(s[1].substring(0,j));		
		for ( i = 2 ; i < s.length ; i++)
		{
			//if ( s[i].length()>1)
			//{
			j=0;
			while ( s[i].charAt(j) >= '0' &&  s[i].charAt(j) <= '9')
				j++;
			num = Integer.parseInt(s[i].substring(0,j));
			prev = num;
			num = num - num1;
			st.append(","+ num + s[i].substring(j));
			num1 = prev;
			//}
		}
		return st;	
	}
	
	public void initialiseMerge(String folderPath){
		int i,j;
		String[] dataLines = new String[fragments];
		//Integer[] hash = new Integer[fragments];
		String min,temp;
		int result; 
		int minIndex = 0;
		Iterator<Integer> itr;
	    Set<Integer> set = new HashSet<Integer>() ;
        FileReader[] file = new  FileReader[fragments];
        File[] del = new  File[fragments];
        File dataFile[] = new File[26];
        FileWriter fd[] = new FileWriter[26];
		BufferedWriter bd[] = new BufferedWriter[26];
	    
		try
		{
			
			for ( i = 0 ; i < 26 ; i++)
			{
				dataFile[i] = new File( folderPath+"/d_"+i+".txt");
				dataFile[i].createNewFile();
				fd[i] = new FileWriter( folderPath+"/d_"+i+".txt");
				bd[i] = new BufferedWriter(fd[i]);
			}
			
			BufferedReader[] reader = new BufferedReader[totalFiles];
			for (i = 0 ; i < fragments; i++){
				set.add(i);
                file[i]=new FileReader( folderPath +"/d"+Integer.toString(i)+".txt");
                del[i] = new File(folderPath + "/d"+Integer.toString(i)+".txt");
				reader[i] = new BufferedReader (file[i]);
				dataLines[i]=reader[i].readLine();
			}
			//find minimum among all of them
			while ( !set.isEmpty() )
			{
				itr = set.iterator();
				i = itr.next();
				j=0;
				while( dataLines[i].charAt(j) != ',' )
					j++;
				min = dataLines[i].substring(0,j);
				minIndex=i;
				//System.out.println("above while");
					while (itr.hasNext()) 
					{
						i = itr.next();
						//System.out.println("i inside:"+i);
						j=0;
						while( dataLines[i].charAt(j) != ',' )
							j++;
						temp = dataLines[i].substring(0,j);
						result = temp.compareTo(min);
						if ( result< 0 )
						{
							min = temp;
							minIndex=i;
						}
						else if (result == 0)
						{
							dataLines[minIndex] += dataLines[i].substring(j);
							dataLines[i]=reader[i].readLine();
							if ( dataLines[i] == null )
								itr.remove();
                                del[i].delete();
						}
					}
					// trying compression
					st  = compress(dataLines[minIndex]);
					temp = st.toString();
					bd[temp.charAt(0) -'a'].write(temp+"\n");
					
					//bd[dataLines[minIndex].charAt(0) -'a'].write(dataLines[minIndex]+"\n");
					//System.out.println("$$"+dataLines[minIndex].toString());
					//System.out.println("Gonna Write:"+dataLines[minIndex]+"\n");
					dataLines[minIndex]=reader[minIndex].readLine();
					if ( dataLines[minIndex] == null )
                    {
                        set.remove(minIndex);
                        del[minIndex].delete();
                    }
			} 
			for ( i = 0 ; i < 26 ; i++)
				bd[i].close();
			//for(i = 0; i < fragments;i++)
				//reader[minIndex].close();
		}		
		catch (Exception e){
		//System.out.println("Except"+e);
		}
	}
	//searchMode search = new  searchMode();
	//search.startSearch();
}		    
