package benchmark;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import LTLparser.LTLNode;
import LTLparser.LTLParserProvider;
import benchmark.ltlconverter.LTLConverter;


public class TestConverter {
	
	static String containedString = "";
	static String notContainedString = "aposijfwo";

	public static void main(String[] args) throws InterruptedException {
		Run();
	}

	public static void Run() {
		try {
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath) && (filePath.toString().endsWith(".ltl") || filePath.toString().endsWith(".form"))) {
					try {
						List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));
						System.out.println(filePath);
						for (LTLNode ltl : nodes) {
							LTLConverter.toMona(ltl, filePath.toString());
							LTLConverter.formulaCounter++;
						}
						LTLConverter.formulaCounter=0;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
