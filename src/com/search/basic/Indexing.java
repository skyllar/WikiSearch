package com.search.basic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Indexing 
{
	long skipLinesSecondary = 200;
	long skipLinesTertiary = 200;
	long skipLinesTitle = 50;
	
	RandomAccessFile[] rand = new RandomAccessFile[26];
	BufferedReader[] reader = new BufferedReader[26];
	FileReader[] file = new  FileReader[26];
	String lineFetched;
	long totalbytes=0;
	File Index[] = new File[26];
	
	
	FileWriter fdWriter[] = new FileWriter[26];
	BufferedWriter bdWriter[] = new BufferedWriter[26];
	
	public void read(long fileOffset,String indexPath,String fileName) 
	{
		try {
			FileReader file=new FileReader( indexPath +"/"+fileName);
			FileReader fdReader = new FileReader( indexPath +"/"+fileName);
			BufferedReader bdReader = new BufferedReader(fdReader);
			bdReader.skip(fileOffset);
			//System.out.println("$"+bdReader.readLine());
			fdReader.close();
			bdReader.close();
			file.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
	public void creatingPrimaryIndex(String indexPath)
	{
		try {
			for (int i = 0 ; i < 26 ; i++)
			{
				 file[i]=new FileReader( indexPath +"/d_"+i+".txt");
				 reader[i] = new BufferedReader (file[i]);
				
				 //rand[i] = new RandomAccessFile(indexPath+"/d_"+i+".txt","r");
				//creating new file creating primary index
				Index[i] = new File( indexPath+"/d__"+i+".txt");
				Index[i].createNewFile();
				fdWriter[i] = new FileWriter( indexPath+"/d__"+i+".txt");
				bdWriter[i] = new BufferedWriter(fdWriter[i]);
			}
			int j = 0 ;
			for ( int i = 0 ; i < 26 ; i++)
			{	
				totalbytes = 0;
				while ( (lineFetched=reader[i].readLine()) != null )
				{
					j=0;
					while( lineFetched.charAt(j++) != ',' );
					
					//System.out.println("$$"+totalbytes+"%%"+lineFetched.substring(0,j));
					bdWriter[i].write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
					//bdWriter[i].write(lineFetched.substring(0,j-1)+"\n");
					totalbytes += lineFetched.length() + 1 ;
				}
				bdWriter[i].close();
				fdWriter[i].close();
				reader[i].close();
				file[i].close();
			}
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		System.out.println("In primary index:");
		//read(348,indexPath,"d_0.txt");
	}
	
	
	public void creatingSecondaryIndex(String indexPath)
	{
		int currentLines,i,j;
		
		try 
		{
			for (i = 0 ; i < 26 ; i++)
			{
				 file[i]=new FileReader( indexPath +"/d__"+i+".txt");
				 reader[i] = new BufferedReader (file[i]);
				
				 //rand[i] = new RandomAccessFile(indexPath+"/d_"+i+".txt","r");
				//creating new file creating primary index
				Index[i] = new File( indexPath+"/d___"+i+".txt");
				Index[i].createNewFile();
				fdWriter[i] = new FileWriter( indexPath+"/d___"+i+".txt");
				bdWriter[i] = new BufferedWriter(fdWriter[i]);
			}
		
			for ( i = 0 ; i < 26 ; i++)
			{	
				currentLines = 1;
				totalbytes = 0;
				
				lineFetched=reader[i].readLine();
				j=0;
				while( lineFetched.charAt(j++) != ',' );
				bdWriter[i].write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
				totalbytes += lineFetched.length() + 1 ;
				
				while ( (lineFetched=reader[i].readLine()) != null)
				{
					//System.out.println("current line:"+currentLines+":"+lineFetched);
					if ( (++currentLines) % skipLinesSecondary == 0 )
					{	
						j=0;
						while( lineFetched.charAt(j++) != ',' );
						bdWriter[i].write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
						//bdWriter[i].write(lineFetched.substring(0,j-1)+"\n");
					}
					totalbytes += lineFetched.length() + 1 ;
				}
				
				file[i].close();
				bdWriter[i].close();
				fdWriter[i].close();
				reader[i].close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("In secondary index:");
		//read(6,indexPath,"d__0.txt");
	}
	
	public void creatingTertiaryIndex(String indexPath)
	{
		int currentLines,i,j;
		
		try 
		{
			for (i = 0 ; i < 26 ; i++)
			{
				 file[i]=new FileReader( indexPath +"/d___"+i+".txt");
				 reader[i] = new BufferedReader (file[i]);
				
				 //rand[i] = new RandomAccessFile(indexPath+"/d_"+i+".txt","r");
				//creating new file creating primary index
				Index[i] = new File( indexPath+"/d____"+i+".txt");
				Index[i].createNewFile();
				fdWriter[i] = new FileWriter( indexPath+"/d____"+i+".txt");
				bdWriter[i] = new BufferedWriter(fdWriter[i]);
			}
		
			for ( i = 0 ; i < 26 ; i++)
			{	
				currentLines = 1;
				totalbytes = 0;
				
				lineFetched=reader[i].readLine();
				j=0;
				while( lineFetched.charAt(j++) != ',' );
				bdWriter[i].write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
				totalbytes += lineFetched.length() + 1;
				
				while ( (lineFetched=reader[i].readLine()) != null)
				{
					if ( (++currentLines) % skipLinesTertiary == 0 )
					{	
						j=0;
						while( lineFetched.charAt(j++) != ',' );
						bdWriter[i].write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
						//System.out.println("%"+lineFetched.substring(0,j-1)+"\n");
					}
					totalbytes += lineFetched.length() + 1 ;
				}
				file[i].close();
				bdWriter[i].close();
				fdWriter[i].close();
				reader[i].close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("In tertiary index:");
		//read(0,indexPath,"d___0.txt");
	}
	
	public void creatingPrimaryIndexForTitle(String indexPath)
	{
		long totalbytes = 0;
		String lineFetched;
		int j;
		
		try {
			
				FileReader readTitle = new FileReader( indexPath +"/title.txt");
				BufferedReader readerTitle = new BufferedReader (readTitle);
				
				File titleIndex = new File( indexPath+"/title_.txt");
				titleIndex.createNewFile();
				FileWriter fdWriter = new FileWriter( indexPath+"/title_.txt");
				BufferedWriter bdWriter = new BufferedWriter(fdWriter);
			
				
				while ( (lineFetched = readerTitle.readLine()) != null && lineFetched != "\n")
				{
					
					j=0;
					while( lineFetched.charAt(j++) != ':' );
					
					//System.out.println("$$"+totalbytes+"%%"+lineFetched.substring(0,j));
					bdWriter.write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
					totalbytes += lineFetched.length() + 1 ;
				}	
				
				readerTitle.close();
				readTitle.close();
				bdWriter.close();
				fdWriter.close();
				
				FileReader readSec = new FileReader( indexPath +"/title_.txt");
				BufferedReader readerSec = new BufferedReader (readSec);
				
				File titleIndexSec = new File( indexPath+"/title__.txt");
				titleIndex.createNewFile();
				FileWriter fdWriterSec = new FileWriter( indexPath+"/title__.txt");
				BufferedWriter bdWriterSec = new BufferedWriter(fdWriterSec);
				
				
				long currentLines = 1;
				totalbytes = 0;
				
				lineFetched=readerSec.readLine();
				j=0;
				while( lineFetched.charAt(j++) != ',' );
				
				bdWriterSec.write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
				totalbytes += lineFetched.length() + 1 ;
				
				while ( (lineFetched=readerSec.readLine()) != null && lineFetched != null)
				{
					//System.out.println("current line:"+currentLines+":"+lineFetched);
					if ( (++currentLines) % skipLinesTitle == 0 )
					{	
						j=0;
						while( lineFetched.charAt(j++) != ',' );
						bdWriterSec.write(lineFetched.substring(0,j-1)+","+totalbytes+"\n");
						//bdWriter[i].write(lineFetched.substring(0,j-1)+"\n");
					}
					totalbytes += lineFetched.length() + 1 ;
				}
				
				readSec.close();
				readerSec.close();
				bdWriterSec.close();
				fdWriterSec.close();
				//read(176872,indexPath,"title_.txt");

				
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		System.out.println("In Title index:");
		//read(408786,indexPath,"title.txt");
	}
	public void startIndexing (String indexPath)
	{
		creatingPrimaryIndexForTitle(indexPath);
		
		// creating new primary index file {{ dense }}
		creatingPrimaryIndex(indexPath);
		
		//secondary will be {{ sparse }}
		creatingSecondaryIndex(indexPath);
		
		//tertiary will be {{ sparse }}
		creatingTertiaryIndex(indexPath);
		
	}
	
}
