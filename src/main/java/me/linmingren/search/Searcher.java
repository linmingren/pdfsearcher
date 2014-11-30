package me.linmingren.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Searcher {
	IndexSearcher indexSearcher;
	
	public  Searcher(String indexDir) throws IOException {
		File indexDirFile = new File(indexDir);
		Directory dir = FSDirectory.open(indexDirFile);
		IndexReader indexReader = DirectoryReader.open(dir);
		indexSearcher = new IndexSearcher(indexReader);
	}
	
	public List<Document> search(String value) throws ParseException, IOException {
		Analyzer analyzer = new IKAnalyzer(true);
		QueryParser parser = new QueryParser("content",
				analyzer);
		List<Document> docList = new ArrayList<Document>();
		
		Query query = parser.parse(value);

		int numResults = 100;
		ScoreDoc[] hits = indexSearcher.search(query, numResults).scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document doc = indexSearcher.doc(hits[i].doc);
			docList.add(doc);
		}

		return docList;
	}
}
