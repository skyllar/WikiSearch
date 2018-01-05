package com.search.basic;

import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class xmlParserMain
{
	
	public static void main(String[] args) throws IOException, SAXException
	{
		long startTime = System.currentTimeMillis();
		System.out.println("Document is being scanned....");
		
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(new XmlEventHandler("../wikidump1/index"));
		xmlReader.parse("../wikidump1/sample.xml");
		ExternalMergeSort e = new ExternalMergeSort(1405, 1405);
		//ExternalMergeSort e = new ExternalMergeSort(2,2);
		//ExternalMergeSort e = new ExternalMergeSort(1,1);
		e.initialiseMerge("../wikidump1/index");
		
		
		System.out.println("Starting Indexing..");
		Indexing index = new Indexing();
		index.startIndexing("../wikidump1/index");
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Document scanned! Time In Indexing And Merging:"+totalTime+"ms");
	}

}
