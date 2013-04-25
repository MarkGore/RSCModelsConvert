package org.mark.rsc.io.archive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.mark.rsc.io.ByteArray;
import org.mark.rsc.io.Stream;
import org.mark.rsc.utils.DataUtils;

public class NamedArchive {

	public ArrayList<ByteArray> files;
	public byte finalBuffer[];
	private int totalFiles;
	public ArrayList<Integer> identifiers;
	public ArrayList<Integer> decompressedSizes;
	public ArrayList<Integer> compressedSizes;
	public ArrayList<Integer> startOffsets;
	public boolean compressedAsWhole;

	public NamedArchive(byte abyte0[]) {
		files = new ArrayList<ByteArray>();
		identifiers = new ArrayList<Integer>();
		decompressedSizes = new ArrayList<Integer>();
		compressedSizes = new ArrayList<Integer>();
		startOffsets = new ArrayList<Integer>();
		Stream stream = new Stream(abyte0);
		int decompressedSize = stream.readU24BitInt();
		int compressedSize = stream.readU24BitInt();
		if (compressedSize != decompressedSize) {
			byte abyte1[] = new byte[decompressedSize];
			BZip2Decompressor.decompressBuffer(abyte1, decompressedSize,
					abyte0, compressedSize, 6);
			finalBuffer = abyte1;
			stream = new Stream(finalBuffer);
			compressedAsWhole = true;
		} else {
			finalBuffer = abyte0;
			compressedAsWhole = false;
		}
		setTotalFiles(stream.readUShort());
		int offset = stream.caret + getTotalFiles() * 10;
		for (int l = 0; l < getTotalFiles(); l++) {
			identifiers.add(Integer.valueOf(stream.readUInt()));
			decompressedSizes.add(Integer.valueOf(stream.readU24BitInt()));
			compressedSizes.add(Integer.valueOf(stream.readU24BitInt()));
			startOffsets.add(Integer.valueOf(offset));
			offset += ((Integer) compressedSizes.get(l)).intValue();
			files.add(new ByteArray(getFileAt(l)));
		}

	}
	
	public byte[] recompile() throws IOException {
        byte[] compressedWhole = compileUncompressed();
        int compressedWholeDecompressedSize = compressedWhole.length;
        //compressedWhole = DataUtils.compress(compressedWhole);
        int compressedWholeSize = compressedWhole.length;
        byte[] compressedIndividually = compileCompressed();
        int compressedIndividuallySize = compressedIndividually.length;
        boolean compressedAsWhole = false;
        if (compressedWholeSize < compressedIndividuallySize) {
            compressedAsWhole = true;
        } // Disable this for now, For some reason its gay on me.
        ExtendedByteArrayOutputStream finalBuf = new ExtendedByteArrayOutputStream();
        if (compressedAsWhole) {
            finalBuf.write24Bytes(compressedWholeDecompressedSize);
            finalBuf.write24Bytes(compressedWholeSize);
            finalBuf.write(compressedWhole);
        } else {
            finalBuf.write24Bytes(compressedIndividuallySize);
            finalBuf.write24Bytes(compressedIndividuallySize);
            finalBuf.write(compressedIndividually);
        }
        finalBuf.close();
        return finalBuf.toByteArray();
    }
	
	   private byte[] compileUncompressed() throws IOException {
	        byte[] fileInfoSection;
	        byte[] filesSection;

	        ExtendedByteArrayOutputStream fileBuf = new ExtendedByteArrayOutputStream();
	        for (int i = 0; i < totalFiles; i++) {
	            decompressedSizes.set(i, files.get(i).length);
	            compressedSizes.set(i, files.get(i).length);
	            fileBuf.write(files.get(i).getBytes());
	        }
	        filesSection = fileBuf.toByteArray();
	        fileBuf.close();
	        ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
	        fileInfo.writeShort(totalFiles);
	        for(int i = 0; i < totalFiles; i++) {
	            fileInfo.writeInt(identifiers.get(i));
	            fileInfo.write24Bytes(decompressedSizes.get(i));
	            fileInfo.write24Bytes(compressedSizes.get(i));
	        }
	        fileInfoSection = fileInfo.toByteArray();
	        fileInfo.close();
	        ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
	        finalBuffer.write(fileInfoSection);
	        finalBuffer.write(filesSection);
	        finalBuffer.close();
	        return finalBuffer.toByteArray();
	    }

	    private byte[] compileCompressed() throws IOException {
	        byte[] fileInfoSection;
	        byte[] filesSection;

	        ExtendedByteArrayOutputStream fileBuf = new ExtendedByteArrayOutputStream();
	        for (int i = 0; i < totalFiles; i++) {
	            decompressedSizes.set(i, files.get(i).length);
	            //  byte[] compressed = DataUtils.bz2Compress(files.get(i).getBytes());
	            byte[] compressed = files.get(i).getBytes();
	            compressedSizes.set(i, compressed.length);
	            fileBuf.write(compressed);
	        }
	        filesSection = fileBuf.toByteArray();
	        fileBuf.close();
	        ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
	        fileInfo.writeShort(totalFiles);
	        for(int i = 0; i < totalFiles; i++) {
	            fileInfo.writeInt(identifiers.get(i));
	            fileInfo.write24Bytes(decompressedSizes.get(i));
	            fileInfo.write24Bytes(compressedSizes.get(i));
	        }
	        fileInfoSection = fileInfo.toByteArray();
	        fileInfo.close();
	        ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
	        finalBuffer.write(fileInfoSection);
	        finalBuffer.write(filesSection);
	        finalBuffer.close();
	        return finalBuffer.toByteArray();
	    }
	
	public int getIndex(String name) {
		for (int l = 0; l < getTotalFiles(); l++) {
			if(identifiers.get(l).toString().toLowerCase().equals(name.toLowerCase()))
				return l;
		}
		return -1;
	}

	public Vector<String> getNames() {
		Vector<String> names = new Vector<String>();
		for (int l = 0; l < getTotalFiles(); l++) {
			names.add(identifiers.get(l).toString());
		}
		return names;
	}

	public byte[] getFileAt(int at) {
		byte dataBuffer[] = new byte[((Integer) decompressedSizes.get(at))
				.intValue()];
		if (!compressedAsWhole) {
			BZip2Decompressor.decompressBuffer(dataBuffer,
					((Integer) decompressedSizes.get(at)).intValue(),
					finalBuffer,
					((Integer) compressedSizes.get(at)).intValue(),
					((Integer) startOffsets.get(at)).intValue());
		} else {
			System.arraycopy(finalBuffer,
					((Integer) startOffsets.get(at)).intValue(), dataBuffer, 0,
					((Integer) decompressedSizes.get(at)).intValue());
		}
		return dataBuffer;
	}

	public byte[] getFile(int identifier) {
		for (int k = 0; k < getTotalFiles(); k++) {
			if (((Integer) identifiers.get(k)).intValue() == identifier) {
				return getFileAt(k);
			}
		}

		return null;
	}

	public byte[] getFile(String identStr) {
		int identifier = 0;
		identStr = identStr.toUpperCase();
		for (int j = 0; j < identStr.length(); j++) {
			identifier = (identifier * 61 + identStr.charAt(j)) - 32;
		}
		return getFile(identifier);
	}

	public static int getHash(String s) {
		int identifier = 0;
		s = s.toUpperCase();
		for (int j = 0; j < s.length(); j++) {
			identifier = (identifier * 61 + s.charAt(j)) - 32;
		}

		return identifier;
	}

	public ByteArray getByteArray(String s) {
		return getByteArray(getHash(s));
	}

	public ByteArray getByteArray(int identifier) {
		int pos = identifiers.indexOf(Integer.valueOf(identifier));
		return (ByteArray) files.get(pos);
	}

	public int getIndexForName(String s) {
		return identifiers.indexOf(Integer.valueOf(getHash(s)));
	}

	public void updateFile(String s, byte data[]) {
		updateFile(getIndexForName(s), data);
	}

	public void removeFile(String s) {
		removeFile(getIndexForName(s));
	}

	public void renameFile(int index, int newName) {
		identifiers.set(index, Integer.valueOf(newName));
	}

	public void updateFile(int index, byte data[]) {
		((ByteArray) files.get(index)).setBytes(data);
	}

	public void removeFile(int index) {
		files.remove(index);
		identifiers.remove(index);
		compressedSizes.remove(index);
		decompressedSizes.remove(index);
		setTotalFiles(getTotalFiles() - 1);
	}

	public void addFile(String s, byte data[]) {
		addFile(getHash(s), data);
	}

	public void addFile(int identifier, byte data[]) {
		identifiers.add(Integer.valueOf(identifier));
		decompressedSizes.add(Integer.valueOf(0));
		compressedSizes.add(Integer.valueOf(0));
		files.add(new ByteArray(data));
		setTotalFiles(getTotalFiles() + 1);
	}

	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}

	public int getTotalFiles() {
		return totalFiles;
	}

	public int getIdentifierAt(int at) {
		return identifiers.get(at);
	}
}
