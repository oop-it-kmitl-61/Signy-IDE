package signy.ide.core.resources;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import signy.ide.utils.FileUtil;

public class Project {

	private String name;
	private Path path;
	private Path pathBin;
	private Path pathSrc;
	private ArrayList<File> files;

	private boolean isOpen;

	public Project() {

	}

	public Project(String name, Path path) {

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

	}

	public void buildProject() {
		FileUtil.createFolders(path, pathBin, pathSrc);
	}

	public ArrayList<File> getFilesSystem() {
		return files;
	}

	public Path getPath() {
		return path;
	}

	public String getTitle() {
		return name;
	}
}
