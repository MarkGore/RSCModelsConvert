package org.mark.rsc.models;

import java.io.File;

import org.mark.rsc.cache.ModelLoader;
import org.mark.rsc.cache.Models;
import org.mark.rsc.utils.DataUtils;
import org.mark.rsc.utils.Type;
import org.mark.rsc.utils.Utils;

public class Converter {

	public static void main(final String args[]) throws Exception {
		if (args[0].equalsIgnoreCase("convert") && args.length == 3) {
			if (args[1].equalsIgnoreCase("x3d")) {
				new X3DConvertion(new File(args[2]));
			}
		} else if (args[0].equalsIgnoreCase("insert") && args.length == 3) {
			new ModelLoader();
			ModelLoader.getArchive().updateFile(
					ModelLoader.getArchive().getIndex(
							Integer.toString(Utils.getHash(args[2]))),
					DataUtils.readFile(args[1]));
				DataUtils.writeFile(new File("models36.jag"), ModelLoader
						.getArchive().recompile());
		} else if (args[0].equalsIgnoreCase("exportall") && args.length == 2) {
			new ModelLoader();
			Utils.loadModels();
			for (Models models : ModelLoader.models) {
				if (Utils.getModel(models.name) != null)
					new OB3Converter(models, Type.X3D, new File("./"+args[1]+"/"
							+ Utils.getModel(models.name).getName()));
				else {
					new OB3Converter(models, Type.X3D, new File("./"+args[1]+"/"
							+ models.name+".OB3"));
				}
			}
		}
	}
}
