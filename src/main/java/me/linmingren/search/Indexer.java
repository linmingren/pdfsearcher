package me.linmingren.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Indexer {
	
	IndexWriter indexWriter;
	
	public Indexer(String indexDir) throws IOException {
		createIndexWriter(indexDir);
	}
	
	private void createIndexWriter(String indexDir) throws IOException {
		File indexDirFile = new File(indexDir);
		Directory dir = FSDirectory.open(indexDirFile);
		
		//使用IK Analyzer来分析中文
		Analyzer analyzer =new IKAnalyzer(true);
		
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2,
				analyzer);
		//往原有的索引追加，这意味着如果你把同一个文档通过addDocument索引两次，那么最后会查到2个结果
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		this.indexWriter = new IndexWriter(dir, iwc);
	}
	
	public void addPdfFile(String path) throws Exception {
		Document document = new Document();
		
		//我们就索引两个字段，一个是文件的路径，一个是文件的内容
		document.add(new StringField("path", path, Field.Store.YES));
		//不能用StringField,因为StringField是不会被分析的
		document.add(new TextField("content", getTextOfPdf(path),
				Field.Store.YES));
		
		//如果该文件的索引已经存在，删除它再添加
		Term term = new Term("path", path);
		try {
			indexWriter.deleteDocuments(term);
		} catch (Exception e) {
			//忽略
		}
		indexWriter.addDocument(document);
		
		indexWriter.commit();
		indexWriter.close();
	}
	
	private String getTextOfPdf(String path) throws FileNotFoundException, IOException {
		PDFParser parser = new PDFParser(new FileInputStream(new File(path)));
    	parser.parse();
    	PDDocument pdf = new PDDocument(parser.getDocument());
    	PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(pdf);
        pdf.close();
        return text;
	}
}
