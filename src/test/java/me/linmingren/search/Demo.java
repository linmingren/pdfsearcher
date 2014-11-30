package me.linmingren.search;

import java.util.List;

import org.apache.lucene.document.Document;

public class Demo {

	public static void main(String[] args) throws Exception {
		Indexer indexer = new Indexer("./index");
		indexer.addPdfFile("./doc/IKAnalyzer中文分词器V2012_FF使用手册.pdf");
		
		Searcher searcher = new Searcher("./index");
		List<Document> docList = searcher.search("停止词典");
		
		System.out.println("found " + docList.size() + " documents");
		
		for (Document d : docList) {
			System.out.println("path: " + d.getField("path"));
		}
	}

}
