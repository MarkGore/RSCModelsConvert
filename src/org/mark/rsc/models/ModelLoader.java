package org.mark.rsc.models;

import java.io.IOException;

import org.mark.rsc.io.DataOperations;
import org.mark.rsc.io.archive.NamedArchive;
import org.mark.rsc.utils.DataUtils;

public class ModelLoader {
	private static NamedArchive archive;
	public static Models[] models;

	public ModelLoader() {
		try {
			setArchive(new NamedArchive(DataUtils.readFile("./res/cache/models36.jag")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		models = new Models[getArchive().getNames().size()];

		for (int i = 0; i < getArchive().getNames().size(); i++) {
			models[i] = new Models();
			readValues(models[i], Integer.parseInt(getArchive().getNames().get(i)));
		}
	}

	public void readValues(Models model, int s) {
		model.name = s;
		byte[] bytes = getArchive().getFile(s);
		int index = 0;
		int j = DataOperations.getUnsigned2Bytes(bytes, index);
		index += 2;
		int k = DataOperations.getUnsigned2Bytes(bytes, index);
		index += 2;
		model.vert_x = new int[j];
		model.vert_y = new int[j];
		model.vert_z = new int[j];
		model.triangle_vert_cnt = new int[k];
		model.texture_front = new int[k];
		model.texture_back = new int[k];
		model.triangle_shade = new int[k];
		model.triangle_verts = new int[k][];
		for (int l = 0; l < j; l++) {
			model.vert_x[l] = -DataOperations.getSigned2Bytes(bytes, index);
			index += 2;
		}
		for (int l = 0; l < j; l++) {
			model.vert_y[l] = -DataOperations.getSigned2Bytes(bytes, index);
			index += 2;
		}
		for (int l = 0; l < j; l++) {
			model.vert_z[l] = DataOperations.getSigned2Bytes(bytes, index);
			index += 2;
		}
		model.vert_cnt = j;
		for (int l = 0; l < k; l++)
			model.triangle_vert_cnt[l] = bytes[index++] & 0xff;

		for (int l1 = 0; l1 < k; l1++) {
			model.texture_front[l1] = DataOperations.getSigned2Bytes(bytes,
					index);
			index += 2;
			if (model.texture_front[l1] == 32767)
				model.texture_front[l1] = model.use_gourad;
		}
		for (int l1 = 0; l1 < k; l1++) {
			model.texture_back[l1] = DataOperations.getSigned2Bytes(bytes,
					index);
			index += 2;
			if (model.texture_back[l1] == 32767)
				model.texture_back[l1] = model.use_gourad;
		}
		for (int l1 = 0; l1 < k; l1++) {
			int k2 = bytes[index++] & 0xff;
			if (k2 == 0)
				model.triangle_shade[l1] = 0;
			else
				model.triangle_shade[l1] = model.use_gourad;
		}
		for (int l2 = 0; l2 < k; l2++) {
			model.triangle_verts[l2] = new int[model.triangle_vert_cnt[l2]];
			for (int i3 = 0; i3 < model.triangle_vert_cnt[l2]; i3++)
				if (j < 256) {
					model.triangle_verts[l2][i3] = bytes[index++] & 0xff;
				} else {
					model.triangle_verts[l2][i3] = DataOperations
							.getUnsigned2Bytes(bytes, index);
					index += 2;
				}
		}

		model.triangle_cnt = k;

	}

	public void setArchive(NamedArchive archive) {
		ModelLoader.archive = archive;
	}

	public static NamedArchive getArchive() {
		return archive;
	}

}
