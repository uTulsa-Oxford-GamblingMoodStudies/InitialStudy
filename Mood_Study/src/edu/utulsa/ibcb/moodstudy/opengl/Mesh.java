package edu.utulsa.ibcb.moodstudy.opengl;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.utulsa.ibcb.moodstudy.opengl.Solver;

public class Mesh {
	String name;
		
	float[][] vertices;
	float[][] tvertices;
	float[][] normals;
	float[][] fnormals;
	
	short[] faces;
	short[] tfaces;
	
	// opengl format
	
	FloatBuffer glVertexBuffer;
	FloatBuffer glTexCoordBuffer;
	FloatBuffer glNormalBuffer;
	ShortBuffer glDrawArray;
	
	
	public void computeFaceNormals(){
		// not implemented yet
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void init(int verts, int tverts, int facecnt){
		vertices = new float[verts][3];
		tvertices = new float[tverts][2];
		normals = new float[verts][3];
		fnormals = new float[facecnt][3];
		
		faces = new short[facecnt*3];
		tfaces = new short[facecnt*3];
		
	}
	
	public FloatBuffer getGLVertices(){
		return glVertexBuffer;
	}
	public FloatBuffer getGLTexCoords(){
		return glTexCoordBuffer;
	}
	public FloatBuffer getGLNormals(){
		return glNormalBuffer;
	}
	public ShortBuffer getGLIndices(){
		return glDrawArray;
	}
	
	int texture = -1;
	
	private static ShortBuffer allocateShortBuffer(short[] array){
		ByteBuffer ibb = ByteBuffer.allocateDirect(array.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(array);
		indexBuffer.position(0);
		return indexBuffer;
	}
	
	private static FloatBuffer allocateFloatBuffer(float []array){
		ByteBuffer vbb = ByteBuffer.allocateDirect(array.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(array);
		vertexBuffer.position(0);
		return vertexBuffer;
	}
	
	public void printArray(short []array, int offset, int length, int nline){
		System.out.print("[ ");
		for(int i = 0; i < length; i++){
			if(i > 0 && i % nline==0){
				System.out.println();
				if((i / nline) % 3 == 0){
					System.out.println();
				}
			}
			System.out.print(""+array[offset+i]+" ");
		}
		System.out.println("]");
	}
	
	public void printArray(float []array, int offset, int length, int nline){
		System.out.print("[ ");
		for(int i = 0; i < length; i++){
			if(i>0 && i%nline==0){
				System.out.println();
				if((i / nline) % 3 == 0){
					System.out.println();
				}
			}
			
			System.out.print(""+array[offset+i]+" ");
		}
		System.out.println("]");
	}
	
	public void createGLArray(){
		float[] glverts = new float[faces.length*3];
		float[] gltexcoords = new float[faces.length*2];
		float[] glnormals = new float[faces.length*3];
		short[] indices = new short[faces.length];
		
		System.out.println("Creating OpenGL Renderable: " + name);
		
		for(short i = 0; i < faces.length/3; i++){
			
			System.arraycopy(vertices[faces[3*i]], 0, glverts, i*9, 3);
			System.arraycopy(vertices[faces[3*i+1]], 0, glverts, i*9+3, 3);
			System.arraycopy(vertices[faces[3*i+2]], 0, glverts, i*9+6, 3);
			
			System.arraycopy(tvertices[tfaces[3*i]], 0, gltexcoords, i*6, 2);
			System.arraycopy(tvertices[tfaces[3*i+1]], 0, gltexcoords, i*6+2, 2);
			System.arraycopy(tvertices[tfaces[3*i+2]], 0, gltexcoords, i*6+4, 2);
			
			System.arraycopy(normals[faces[3*i]], 0, glnormals, i*9, 3);
			System.arraycopy(normals[faces[3*i+1]], 0, glnormals, i*9+3, 3);
			System.arraycopy(normals[faces[3*i+2]], 0, glnormals, i*9+6, 3);
			
			indices[i*3] = (short) (i*3);
			indices[i*3+1] = (short) (i*3+1);
			indices[i*3+2] = (short) (i*3+2);
			
		}
		//printArray(glverts, 0, glverts.length, 3);
		//printArray(gltexcoords, 0, gltexcoords.length, 2);
		//printArray(indices, 0, indices.length, 3);
		
		glVertexBuffer = allocateFloatBuffer(glverts);
		glTexCoordBuffer = allocateFloatBuffer(gltexcoords);
		glNormalBuffer = allocateFloatBuffer(glnormals);
		glDrawArray = allocateShortBuffer(indices);
	}
	
	public void setTexture(int texid){
		texture = texid;
	}
	public int getTexture(){
		return texture;
	}
	public String getName(){
		return name;
	}
	
	public void scale(float x, float y, float z){
		for(float[] v : vertices){
			v[0] *= x;
			v[1] *= y;
			v[2] *= z;
		}
	}
	
	public void scale(double factor){
		for(float[] v : vertices){
			v[0] *= factor;
			v[1] *= factor;
			v[2] *= factor;
		}
	}
	
	public int getVertexCount(){
		return vertices.length;
	}
	
	public int getFaceCount(){
		return faces.length/3;
	}
	
	public short[] getFaces(){
		return faces;
	}
	
	public float[][] getVertices(){
		return vertices;
	}
	public float[][] getNormals(){
		return normals;
	}
	public float[][] getTVertices(){
		return tvertices;
	}
	public short[] getTFaces(){
		return tfaces;
	}
	
	public static List<Mesh> loadASE(InputStream fis) throws IOException{
		
		DataInputStream dataIn = new DataInputStream(fis);
		
		LinkedList<String> stack = new LinkedList<String>();
		stack.add("GLOBAL");
		
		LinkedList<Mesh> objects = new LinkedList<Mesh>();
		Mesh cmesh=null;
		
		String lname = null;
		
		String line = "";
		while( (line = dataIn.readLine()) != null){
			line=line.trim().replaceAll("\\s+", " ");
			
			if(stack.getLast().equals("*GEOMOBJECT")){
				
				if(line.startsWith("*NODE_NAME")){
					String[] items = line.split("\\s+");
					
					String name = items[1];
					lname = name.substring(1, name.length()-1);
				}
				if(line.startsWith("*MESH")){
					cmesh = new Mesh();
					objects.add(cmesh);
					cmesh.name = lname;
				}
			}
			if(stack.getLast().equals("*MESH_VERTEX_LIST")){
				if(line.startsWith("*MESH_VERTEX")){
					String[] items = line.split("\\s+");
					
					int index = Integer.parseInt(items[1]);
					cmesh.vertices[index][0] = Float.parseFloat(items[2]);
					cmesh.vertices[index][1] = Float.parseFloat(items[3]);
					cmesh.vertices[index][2] = Float.parseFloat(items[4]);
				}				
			}
			if(stack.getLast().equals("*MESH_FACE_LIST")){
				if(line.startsWith("*MESH_FACE")){
					String mod = line;
					
					Pattern p = Pattern.compile("^\\*MESH_FACE ([0-9]+): A: ([0-9]+) B: ([0-9]+) C: ([0-9]+) .*");
					Matcher m = p.matcher(mod);

					if (m.find()) {
					    int index = 3*Integer.parseInt(m.group(1));
					    short a = Short.parseShort(m.group(2));
					    short b = Short.parseShort(m.group(3));
					    short c = Short.parseShort(m.group(4));
					    
					    cmesh.faces[index]   = a;
					    cmesh.faces[index+1] = b;
					    cmesh.faces[index+2] = c;
					}
				}							
			}
			if(stack.getLast().equals("*MESH_TVERTLIST")){
				if(line.startsWith("*MESH_TVERT")){
					String[] items = line.split("\\s+");
					
					int index = Integer.parseInt(items[1]);
					cmesh.tvertices[index][0] = Float.parseFloat(items[2]);
					cmesh.tvertices[index][1] = Float.parseFloat(items[3]);
					cmesh.tvertices[index][2] = Float.parseFloat(items[4]);
				}				
			}
			if(stack.getLast().equals("*MESH_TFACELIST")){
				if(line.startsWith("*MESH_TFACE")){
					String[] items = line.split("\\s+");
					
					int index = 3*Integer.parseInt(items[1]);
					short a = Short.parseShort(items[2]);
				    short b = Short.parseShort(items[3]);
				    short c = Short.parseShort(items[4]);
					
					cmesh.tfaces[index]   = a;
					cmesh.tfaces[index+1] = b;
					cmesh.tfaces[index+2] = c;
				}
			}
			if(stack.getLast().equals("*MESH_NORMALS")){
				if(line.startsWith("*MESH_FACENORMAL")){
					String[] items = line.split("\\s+");
					int index = Integer.parseInt(items[1]);
					cmesh.fnormals[index][0] = Float.parseFloat(items[2]);
					cmesh.fnormals[index][1] = Float.parseFloat(items[3]);
					cmesh.fnormals[index][2] = Float.parseFloat(items[4]);
				}
				if(line.startsWith("*MESH_VERTEXNORMAL")){
					String[] items = line.split("\\s+");
					int index = Integer.parseInt(items[1]);
					cmesh.normals[index][0] = Float.parseFloat(items[2]);
					cmesh.normals[index][1] = Float.parseFloat(items[3]);
					cmesh.normals[index][2] = Float.parseFloat(items[4]);
				}
			}
			if(stack.getLast().equals("*MESH")){
				
				if(line.startsWith("*MESH_NUMVERTEX")){
					String[] items = line.split("\\s+");
					int numverts = Integer.parseInt(items[1]);
					cmesh.vertices = new float[numverts][3];
					cmesh.normals = new float[numverts][3];
				}
				if(line.startsWith("*MESH_NUMFACES")){
					String[] items = line.split("\\s+");
					int numfaces = Integer.parseInt(items[1]);
					cmesh.faces = new short[3*numfaces];
					cmesh.fnormals = new float[numfaces][3];
				}
				if(line.startsWith("*MESH_NUMTVERTEX")){
					String[] items = line.split("\\s+");
					int numtverts = Integer.parseInt(items[1]);
					cmesh.tvertices = new float[numtverts][3];
				}
				if(line.startsWith("*MESH_NUMTVFACES")){
					String[] items = line.split("\\s+");
					int numtfaces = Integer.parseInt(items[1]);
					cmesh.tfaces = new short[3*numtfaces];
				}
			}
			
			if(line.contains("{")){
				String[] items = line.split("\\s+");
				stack.add(items[0]);
			}
			if(line.startsWith("}")){
				stack.removeLast();
			}
			
		}
		
		dataIn.close();
		
		for(Mesh object : objects){
			object.computeFaceNormals();
		}
		
		return objects;
	}
}
