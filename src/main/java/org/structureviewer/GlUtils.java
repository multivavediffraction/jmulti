package org.structureviewer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class GlUtils {
    public static void printShaderInfoLog(GLAutoDrawable drawable, int obj)
    {
        GL3 gl = drawable.getGL().getGL3(); // get the OpenGL 3 graphics context
        IntBuffer infoLogLengthBuf = IntBuffer.allocate(1);
        int infoLogLength;
        gl.glGetShaderiv(obj, GL2.GL_INFO_LOG_LENGTH, infoLogLengthBuf);
        infoLogLength = infoLogLengthBuf.get(0);
        if (infoLogLength > 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(infoLogLength);
            gl.glGetShaderInfoLog(obj, infoLogLength, infoLogLengthBuf, byteBuffer);
            for (byte b:byteBuffer.array()){
                System.err.print((char)b);
            }
        }
    }

    public static void printProgramInfoLog(GLAutoDrawable drawable, int obj)
    {
        GL3       gl = drawable.getGL().getGL3(); // get the OpenGL 3 graphics context
        IntBuffer infoLogLengthBuf = IntBuffer.allocate(1);
        int infoLogLength;
        gl.glGetProgramiv(obj, GL2.GL_INFO_LOG_LENGTH, infoLogLengthBuf);
        infoLogLength = infoLogLengthBuf.get(0);
        if (infoLogLength > 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(infoLogLength);
            gl.glGetProgramInfoLog(obj, infoLogLength, infoLogLengthBuf, byteBuffer);
            for (byte b:byteBuffer.array()){
                System.err.print((char)b);
            }
        }
    }

    public static ByteBuffer clone(float[] data)
    {
        int         len    = data.length;
        ByteBuffer  direct = ByteBuffer.allocateDirect(len*4);
        direct.order(ByteOrder.nativeOrder()); // very important!
        for (int i=0; i<len; ++i) {
            direct.putFloat(data[i]);
        }
        direct.rewind();
        return direct;
    }

    public static ByteBuffer clone(int[] data)
    {
        int         len    = data.length;
        ByteBuffer  direct = ByteBuffer.allocateDirect(len*4);
        direct.order(ByteOrder.nativeOrder()); // very important!
        for (int i=0; i<len; ++i) {
            direct.putInt(data[i]);
        }
        direct.rewind();
        return direct;
    }

    private static Map<String, Float> elements = new HashMap<>(115);
    static {
        elements.put("H",  0.46f);
        elements.put("He", 1.22f);
        elements.put("Li", 1.57f);
        elements.put("Be", 1.12f);
        elements.put("B",  0.81f);
        elements.put("C",  0.77f);
        elements.put("N",  0.74f);
        elements.put("O",  0.74f);
        elements.put("F",  0.72f);
        elements.put("Ne", 1.60f);
        elements.put("Na", 1.91f);
        elements.put("Mg", 1.60f);
        elements.put("Al", 1.43f);
        elements.put("Si", 1.18f);
        elements.put("P",  1.10f);
        elements.put("S",  1.04f);
        elements.put("Cl", 0.99f);
        elements.put("Ar", 1.92f);
        elements.put("K",  2.35f);
        elements.put("Ca", 1.97f);
        elements.put("Sc", 1.64f);
        elements.put("Ti", 1.47f);
        elements.put("V",  1.35f);
        elements.put("Cr", 1.29f);
        elements.put("Mn", 1.37f);
        elements.put("Fe", 1.26f);
        elements.put("Co", 1.25f);
        elements.put("Ni", 1.25f);
        elements.put("Cu", 1.28f);
        elements.put("Zn", 1.37f);
        elements.put("Ga", 1.53f);
        elements.put("Ge", 1.22f);
        elements.put("As", 1.21f);
        elements.put("Se", 1.04f);
        elements.put("Br", 1.14f);
        elements.put("Kr", 1.98f);
        elements.put("Rb", 2.50f);
        elements.put("Sr", 2.15f);
        elements.put("Y", 1.82f);
        elements.put("Zr", 1.60f);
        elements.put("Nb", 1.47f);
        elements.put("Mo", 1.40f);
        elements.put("Tc", 1.35f);
        elements.put("Ru", 1.34f);
        elements.put("Rh", 1.34f);
        elements.put("Pd", 1.37f);
        elements.put("Ag", 1.44f);
        elements.put("Cd", 1.52f);
        elements.put("In", 1.67f);
        elements.put("Sn", 1.58f);
        elements.put("Sb", 1.41f);
        elements.put("Te", 1.37f);
        elements.put("I", 1.33f);
        elements.put("Xe", 2.18f);
        elements.put("Cs", 2.72f);
        elements.put("Ba", 2.24f);
        elements.put("La", 1.88f);
        elements.put("Ce", 1.82f);
        elements.put("Pr", 1.82f);
        elements.put("Nd", 1.82f);
        elements.put("Pm", 1.81f);
        elements.put("Sm", 1.81f);
        elements.put("Eu", 2.06f);
        elements.put("Gd", 1.79f);
        elements.put("Tb", 1.77f);
        elements.put("Dy", 1.77f);
        elements.put("Ho", 1.76f);
        elements.put("Er", 1.75f);
        elements.put("Tm", 1.00f);
        elements.put("Yb", 1.94f);
        elements.put("Lu", 1.72f);
        elements.put("Hf", 1.59f);
        elements.put("Ta", 1.47f);
        elements.put("W", 1.41f);
        elements.put("Re", 1.37f);
        elements.put("Os", 1.35f);
        elements.put("Ir", 1.36f);
        elements.put("Pt", 1.39f);
        elements.put("Au", 1.44f);
        elements.put("Hg", 1.55f);
        elements.put("Tl", 1.71f);
        elements.put("Pb", 1.75f);
        elements.put("Bi", 1.82f);
        elements.put("Po", 1.77f);
        elements.put("At", 0.62f);
        elements.put("Rn", 0.80f);
        elements.put("Fr", 1.00f);
        elements.put("Ra", 2.35f);
        elements.put("Ac", 2.03f);
        elements.put("Th", 1.80f);
        elements.put("Pa", 1.63f);
        elements.put("U", 1.56f);
        elements.put("Np", 1.56f);
        elements.put("Pu", 1.64f);
        elements.put("Am", 1.73f);
        // all equal in vesta
        elements.put("Cm", 0.80f);
        elements.put("Bk", 0.80f);
        elements.put("Cf", 0.80f);
        elements.put("Es", 0.80f);
        elements.put("Fm", 0.80f);
        elements.put("Md", 0.80f);
        elements.put("No", 0.80f);
        elements.put("Lr", 0.80f);
        elements.put("Rf", 0.80f);
        elements.put("Db", 0.80f);
        elements.put("Sg", 0.80f);
        elements.put("Bh", 0.80f);
        elements.put("Hs", 0.80f);
        elements.put("Mt", 0.80f);
        elements.put("Ds", 0.80f);
        elements.put("Rg", 0.80f);
    }

    private static Map<String, float[]> colors = new HashMap<>(115);
    static {
        colors.put("H",  new float[] {255f/255f, 204f/255f, 204f/255f});
        colors.put("He", new float[] {252f/255f, 232f/255f, 206f/255f});
        colors.put("Li", new float[] {134f/255f, 224f/255f, 116f/255f});
        colors.put("Be", new float[] { 94f/255f, 215f/255f, 123f/255f});
        colors.put("B",  new float[] { 31f/255f, 162f/255f,  15f/255f});
        colors.put("C",  new float[] {128f/255f,  73f/255f,  41f/255f});
        colors.put("N",  new float[] {176f/255f, 185f/255f, 230f/255f});
        colors.put("O",  new float[] {254f/255f,   3f/255f,   0f/255f});
        colors.put("F",  new float[] {176f/255f, 185f/255f, 230f/255f});
        colors.put("Ne", new float[] {254f/255f,  55f/255f, 181f/255f});
        colors.put("Na", new float[] {249f/255f, 220f/255f,  60f/255f});
        colors.put("Mg", new float[] {251f/255f, 123f/255f,  21f/255f});
        colors.put("Al", new float[] {129f/255f, 178f/255f, 214f/255f});
        colors.put("Si", new float[] { 27f/255f,  59f/255f, 250f/255f});
        colors.put("P",  new float[] {192f/255f, 156f/255f, 194f/255f});
        colors.put("S",  new float[] {255f/255f, 250f/255f,   0f/255f});
        colors.put("Cl", new float[] { 49f/255f, 252f/255f,   2f/255f});
        colors.put("Ar", new float[] {207f/255f, 254f/255f, 196f/255f});
        colors.put("K",  new float[] {161f/255f,  33f/255f, 246f/255f});
        colors.put("Ca", new float[] { 90f/255f, 150f/255f, 189f/255f});
        colors.put("Sc", new float[] {181f/255f,  99f/255f, 171f/255f});
        colors.put("Ti", new float[] {120f/255f, 202f/255f, 255f/255f});
        colors.put("V",  new float[] {229f/255f,  25f/255f,   0f/255f});
        colors.put("Cr", new float[] {  0f/255f,   0f/255f, 158f/255f});
        colors.put("Mn", new float[] {168f/255f,   8f/255f, 158f/255f});
        colors.put("Fe", new float[] {181f/255f, 113f/255f,   0f/255f});
        colors.put("Co", new float[] {  0f/255f,   0f/255f, 175f/255f});
        colors.put("Ni", new float[] {183f/255f, 187f/255f, 189f/255f});
        colors.put("Cu", new float[] { 34f/255f,  71f/255f, 220f/255f});
        colors.put("Zn", new float[] {143f/255f, 143f/255f, 129f/255f});
        colors.put("Ga", new float[] {158f/255f, 227f/255f, 115f/255f});
        colors.put("Ge", new float[] {126f/255f, 110f/255f, 166f/255f});
        colors.put("As", new float[] {116f/255f, 208f/255f,  87f/255f});
        colors.put("Se", new float[] {154f/255f, 239f/255f,  15f/255f});
        colors.put("Br", new float[] {126f/255f,  49f/255f,   2f/255f});
        colors.put("Kr", new float[] {250f/255f, 193f/255f, 243f/255f});
        colors.put("Rb", new float[] {255f/255f,   0f/255f, 153f/255f});
        colors.put("Sr", new float[] {  0f/255f, 255f/255f,  38f/255f});
        colors.put("Y",  new float[] {102f/255f, 152f/255f, 142f/255f});
        colors.put("Zr", new float[] {  0f/255f, 255f/255f,   0f/255f});
        colors.put("Nb", new float[] { 76f/255f, 178f/255f, 118f/255f});
        colors.put("Mo", new float[] {179f/255f, 134f/255f, 175f/255f});
        colors.put("Tc", new float[] {205f/255f, 175f/255f, 202f/255f});
        colors.put("Ru", new float[] {207f/255f, 183f/255f, 173f/255f});
        colors.put("Rh", new float[] {205f/255f, 209f/255f, 171f/255f});
        colors.put("Pd", new float[] {193f/255f, 195f/255f, 184f/255f});
        colors.put("Ag", new float[] {183f/255f, 187f/255f, 189f/255f});
        colors.put("Cd", new float[] {242f/255f,  30f/255f, 220f/255f});
        colors.put("In", new float[] {215f/255f, 128f/255f, 187f/255f});
        colors.put("Sn", new float[] {154f/255f, 142f/255f, 185f/255f});
        colors.put("Sb", new float[] {215f/255f, 131f/255f,  79f/255f});
        colors.put("Te", new float[] {173f/255f, 162f/255f,  81f/255f});
        colors.put("I",  new float[] {142f/255f,  31f/255f, 138f/255f});
        colors.put("Xe", new float[] {154f/255f, 161f/255f, 248f/255f});
        colors.put("Cs", new float[] { 14f/255f, 254f/255f, 185f/255f});
        colors.put("Ba", new float[] { 30f/255f, 239f/255f,  44f/255f});
        colors.put("La", new float[] { 90f/255f, 196f/255f,  73f/255f});
        colors.put("Ce", new float[] {209f/255f, 252f/255f,   6f/255f});
        colors.put("Pr", new float[] {252f/255f, 225f/255f,   5f/255f});
        colors.put("Nd", new float[] {251f/255f, 141f/255f,   6f/255f});
        colors.put("Pm", new float[] {  0f/255f,   0f/255f, 244f/255f});
        colors.put("Sm", new float[] {252f/255f,   6f/255f, 125f/255f});
        colors.put("Eu", new float[] {250f/255f,   7f/255f, 213f/255f});
        colors.put("Gd", new float[] {192f/255f,   3f/255f, 255f/255f});
        colors.put("Tb", new float[] {113f/255f,   4f/255f, 254f/255f});
        colors.put("Dy", new float[] { 49f/255f,   6f/255f, 252f/255f});
        colors.put("Ho", new float[] {  7f/255f,  65f/255f, 251f/255f});
        colors.put("Er", new float[] { 73f/255f, 114f/255f,  58f/255f});
        colors.put("Tm", new float[] {  0f/255f,   0f/255f, 224f/255f});
        colors.put("Yb", new float[] { 39f/255f, 252f/255f, 244f/255f});
        colors.put("Lu", new float[] { 38f/255f, 253f/255f, 181f/255f});
        colors.put("Hf", new float[] {180f/255f, 179f/255f,  89f/255f});
        colors.put("Ta", new float[] {183f/255f, 154f/255f,  86f/255f});
        colors.put("W",  new float[] {141f/255f, 138f/255f, 127f/255f});
        colors.put("Re", new float[] {179f/255f, 176f/255f, 142f/255f});
        colors.put("Os", new float[] {200f/255f, 177f/255f, 120f/255f});
        colors.put("Ir", new float[] {201f/255f, 206f/255f, 114f/255f});
        colors.put("Pt", new float[] {203f/255f, 197f/255f, 191f/255f});
        colors.put("Au", new float[] {254f/255f, 178f/255f,  56f/255f});
        colors.put("Hg", new float[] {211f/255f, 183f/255f, 203f/255f});
        colors.put("Tl", new float[] {149f/255f, 137f/255f, 108f/255f});
        colors.put("Pb", new float[] { 82f/255f,  83f/255f,  91f/255f});
        colors.put("Bi", new float[] {210f/255f,  47f/255f, 247f/255f});
        colors.put("Po", new float[] {  0f/255f,   0f/255f, 255f/255f});
        colors.put("At", new float[] {  0f/255f,   0f/255f, 255f/255f});
        colors.put("Rn", new float[] {255f/255f, 255f/255f,   0f/255f});
        colors.put("Fr", new float[] {  0f/255f,   0f/255f,   0f/255f});
        colors.put("Ra", new float[] {109f/255f, 169f/255f,  88f/255f});
        colors.put("Ac", new float[] {100f/255f, 158f/255f, 114f/255f});
        colors.put("Th", new float[] { 37f/255f, 253f/255f, 120f/255f});
        colors.put("Pa", new float[] { 41f/255f, 250f/255f,  53f/255f});
        colors.put("U",  new float[] {121f/255f, 161f/255f, 170f/255f});
    }

    public static float radiusFromElementName(String element){
        return elements.getOrDefault(element, 0.80f);
    }

    public static float[] colorFromElementName(String element){
        return colors.getOrDefault(element, new float[] {76f/255f, 76f/255f, 76f/255f});
    }
}
