package signy.ide.core.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import signy.ide.utils.FileUtil;

public class SProject extends File {

	private String name;
	private Path path;
	private Path pathProperties;
	private Path pathBin;
	private Path pathSrc;
	private ArrayList<File> files;

	private boolean isOpen;

	public SProject() {
		super("");
	}

	public SProject(String name, Path path) {

		super(path.toString());

		files = new ArrayList<>();
		Path pathBin = Paths.get(path.toString(), "bin");
		Path pathSrc = Paths.get(path.toString(), "src");

		this.name = name;
		this.path = path;
		this.pathBin = pathBin;
		this.pathSrc = pathSrc;

	}

	public void initProject() {

		buildProject();
		initProperties();

	}

	private void initProperties() {
		pathProperties = path.resolve(".project");
		try {
			if (!Files.exists(pathProperties)) {
				Files.createFile(pathProperties);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buildProject() {
		FileUtil.createFolders(path, Paths.get(path.toString(), "src"), Paths.get(path.toString(), "src", "main"),
				Paths.get(path.toString(), "src", "main", "java"));
	}

	public ArrayList<File> getFilesSystem() {
		return files;
	}

	public String getTitle() {
		return name;
	}

	public Path getPathSource() {
		return pathSrc;
	}

	public Path getPathBin() {
		return pathBin;
	}

}
