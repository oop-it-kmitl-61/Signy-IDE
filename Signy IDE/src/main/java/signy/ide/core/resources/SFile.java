package signy.ide.core.resources;

import java.io.File;
import java.nio.file.Path;

public class SFile extends File {

	private SProject sProject;

	public SFile() {
		super("");
	}

	public SFile(SProject rootProject, Path path) {

		super(path.toString());
		sProject = rootProject;

	}

	public SProject getRootProject() {
		return sProject;
	}

}
