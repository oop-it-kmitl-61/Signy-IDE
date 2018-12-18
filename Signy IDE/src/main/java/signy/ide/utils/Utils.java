package signy.ide.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import javafx.stage.DirectoryChooser;
import signy.ide.LoadingController;
import signy.ide.core.resources.SProject;

public class Utils {

	public Utils() {

	}

	public static String fileToString(String path, Charset encoding) {
		String content = "";
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			content = new String(encoded, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	public static void writeToFile(String fileContent, Path path) {
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(fileContent, 0, fileContent.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String listToString(List<?> list) {
		String result = "";
		for (int i = 0; i < list.size(); ++i) {
			result += list.get(i) + " ";
		}
		return result.trim();
	}

	public static void copyProject() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setInitialDirectory(LoadingController.getWorkspacePath().toFile());
		File selectedDir = dirChooser.showDialog(null);

		if (selectedDir == null) {
			return;
		}
		System.out.println(selectedDir.getAbsolutePath());

		LoadingController.getController().getTerminalPane().getConsolePane()
				.runCommand("xcopy \"" + selectedDir.getAbsolutePath() + "\"\\*.* \""
						+ LoadingController.getWorkspacePath().toFile().getAbsolutePath() + "\"\\"
						+ selectedDir.getName() + "\\ /e /y");
	}

	public static ArrayList<File> scanMainClass(SProject project) {
		ArrayList<File> results = traverse(new ArrayList<File>(), project);
		return results;
	}

	public static ArrayList<File> traverse(ArrayList<File> results, File root) {
		for (File child : root.listFiles()) {
			if (child.isDirectory()) {
				traverse(results, child);
			} else if (FilenameUtils.getExtension(child.getName()).equals("java")) {
				createAST(results, child);
			}
		}
		return results;
	}

	public static void createAST(ArrayList<File> results, File input) {
		if (input == null) {
			return;
		}

		ASTParser parser = ASTParser.newParser(AST.JLS10);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		parser.setCompilerOptions(JavaCore.getOptions());

		parser.setSource(fileToString(input.getAbsolutePath(), StandardCharsets.UTF_8).toCharArray());

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		Stack<PackageDeclaration> stackPackage = new Stack<PackageDeclaration>();
		Stack<TypeDeclaration> stackType = new Stack<TypeDeclaration>();

		cu.accept(new ASTVisitor() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean visit(PackageDeclaration node) {
				stackPackage.push(node);
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				stackType.push(node);
				return super.visit(node);
			}

			@Override
			public void endVisit(TypeDeclaration node) {
				for (TypeDeclaration rootClass : stackType) {
					for (MethodDeclaration method : rootClass.getMethods()) {
						if (method.getName().getFullyQualifiedName().equals("main")) {
							results.add(input);
						}
					}
				}
				super.endVisit(node);
			}

		});

	}

	public String parseJavaFile(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

//	static boolean isStaticDeclaration(List<?> modifiers) {
//		for (Object item : modifiers) {
//			if (item.toString().equals("static")) {
//				return true;
//			}
//		}
//		return false;
//	}

}
