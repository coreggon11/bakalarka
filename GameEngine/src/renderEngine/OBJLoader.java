package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

public class OBJLoader {

	public static RawModel loadObjModel(String fileName, Loader loader) {

		try {

			FileReader fr = new FileReader(new File("res/" + fileName + ".obj"));
			BufferedReader reader = new BufferedReader(fr);
			String line;
			List<Vector3f> vertices = new ArrayList<>();
			List<Vector2f> textures = new ArrayList<>();
			List<Vector3f> normals = new ArrayList<>();
			List<Integer> indices = new ArrayList<>();
			float[] verticesArray = null;
			float[] texturesArray = null;
			float[] normalsArray = null;
			int[] indicesArray = null;
			try {
				Vector3f vertex;
				Vector2f texture;
				Vector3f normal;
				String[] currentLine;
				while (true) {
					line = reader.readLine();
					currentLine = line.split(" ");
					if (line.startsWith("v ")) {
						vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
								Float.parseFloat(currentLine[3]));
						vertices.add(vertex);
					} else if (line.startsWith("vt ")) {
						texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
						textures.add(texture);
					} else if (line.startsWith("vn ")) {
						normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
								Float.parseFloat(currentLine[3]));
						normals.add(normal);
					} else if (line.startsWith("f ")) {
						texturesArray = new float[vertices.size() * 2];
						normalsArray = new float[vertices.size() * 3];
						break;
					}
				}
				String[] vertex1;
				String[] vertex2;
				String[] vertex3;
				while (line != null) {
					if (!line.startsWith("f ")) {
						line = reader.readLine();
						continue;
					}
					currentLine = line.split(" ");
					vertex1 = currentLine[1].split("/");
					vertex2 = currentLine[2].split("/");
					vertex3 = currentLine[3].split("/");
					proccessVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
					proccessVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
					proccessVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);
					line = reader.readLine();
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			verticesArray = new float[vertices.size() * 3];
			indicesArray = new int[indices.size()];
			int vertexPointer = 0;
			for (Vector3f vertex : vertices) {
				verticesArray[vertexPointer++] = vertex.x;
				verticesArray[vertexPointer++] = vertex.y;
				verticesArray[vertexPointer++] = vertex.z;
			}
			for (int i = 0; i < indices.size(); i++) {
				indicesArray[i] = indices.get(i);
			}
			return loader.loadToVao(verticesArray, texturesArray, indicesArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	private static void proccessVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
			List<Vector3f> normals, float[] texturesArray, float[] normalsArray) {

		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
		texturesArray[currentVertexPointer * 2] = currentTex.x;
		texturesArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[currentVertexPointer * 3] = currentNorm.x;
		normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
		normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;

	}

}
