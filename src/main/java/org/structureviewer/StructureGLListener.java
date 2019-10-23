package org.structureviewer;

import com.jogamp.opengl.*;
import org.jmol.adapter.smarter.Atom;
import org.jmol.adapter.smarter.AtomSetCollection;
import org.jmol.api.SymmetryInterface;

import javajs.util.P3;
import org.jmulti.UnitCell;
import org.jmulti.calc.AtomDescr;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.structureviewer.Utils.epsilonEquals;

public class StructureGLListener implements GLEventListener {
    public enum ShowAtoms {ALL, UNEQUIVALENT}

    private static final int ATOM_DESCR_LEN = 7;
    private int programId;
    private int vertId;
    private int tescId;
    private int teseId;
    private int geomId;
    private int fragId;

    private int lightId;
    private int transId;
    private int modelMatId;
    private int projMatId;

    private int vbo;
    private int bufferId = -1;
    private int vertArrayId = -1;
    private int ixdArrayId = -1;

    private boolean atomsLoaded = false;
    private float[] atoms = {
            //  Coordinates            Color              Radius
            0.00f, 0.00f, 0.00f,   0.8f, 0.8f, 0.8f,  1f,
            3.00f, 0.00f, 0.00f,   0.8f, 0.8f, 0.8f,  1f,
    };

    private int atomCount = atoms.length / ATOM_DESCR_LEN;

    private float[] lightDirection = {0.5f, 0.5f, -1.0f};

    private float[] modelMatrix = {
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
    };

    private float[] transformMatrix = {
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
    };

    private float[] projectionMatrix = {
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
    };

    private final float X = 0.525731112119133606f;
    private final float Z = 0.850650808352039932f;

    private final float[] vertices = {
            -X, 0.0f,    Z,
            X, 0.0f,    Z,
            -X, 0.0f,   -Z,
            X, 0.0f,   -Z,
            0.0f,    Z,    X,
            0.0f,    Z,   -X,
            0.0f,   -Z,    X,
            0.0f,   -Z,   -X,
            Z,    X, 0.0f,
            -Z,    X, 0.0f,
            Z,   -X, 0.0f,
            -Z,   -X, 0.0f
    };

    private final int[] indices = {
            7,3,10,
            10,3,8,
            0,1,4,
            8,1,10,
            10,6,7,
            6,11,7,
            7,11,2,
            2,5,3,
            3,5,8,
            10,1,6,
            5,4,8,
            4,1,8,
            0,9,11,
            11,6,0,
            1,0,6,
            4,9,0,
            9,5,2,
            2,11,9,
            7,2,3,
            4,5,9
    };

    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL4(drawable.getGL().getGL4()));

        GL4 gl = drawable.getGL().getGL4();

        gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        gl.glClearDepth(1.0f);

        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);

        gl.glEnable(GL4.GL_BLEND);
        gl.glBlendEquationSeparate(GL4.GL_FUNC_ADD, GL4.GL_FUNC_ADD);
        gl.glBlendFuncSeparate(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA, GL4.GL_ONE, GL4.GL_ZERO);

        gl.glPixelStorei(GL4.GL_UNPACK_ALIGNMENT, 1);

        gl.glEnable(GL4.GL_MULTISAMPLE);

        initShader(drawable);

        initBuffers(drawable);
    }

    private void initBuffers(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        int[] bufferIds = new int[3];
        gl.glGenBuffers(3, bufferIds, 0);
        bufferId = bufferIds[0];
        vertArrayId = bufferIds[1];
        ixdArrayId = bufferIds[2];

        int[] vboArray = new int[1];
        gl.glGenVertexArrays(1, vboArray, 0);
        vbo = vboArray[0];

        gl.glBindVertexArray(vbo);

        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ixdArrayId);
        ByteBuffer iData = GlUtils.clone(indices);
        gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, indices.length*Integer.BYTES, iData, GL4.GL_STATIC_DRAW);

        int vertAttrId = gl.glGetAttribLocation(programId, "vertex");
        int posAttrId = gl.glGetAttribLocation(programId, "pos");
        int colorAttrId = gl.glGetAttribLocation(programId, "color");
        int radiusAttrId = gl.glGetAttribLocation(programId, "radius");
        lightId = gl.glGetUniformLocation(programId, "lightDir");
        transId = gl.glGetUniformLocation(programId, "T");
        modelMatId = gl.glGetUniformLocation(programId, "M");
        projMatId = gl.glGetUniformLocation(programId, "P");

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertArrayId);
        ByteBuffer vData = GlUtils.clone(vertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.length*Float.BYTES, vData, GL4.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(vertAttrId, 3, GL4.GL_FLOAT, false, 3*Float.BYTES, 0);
        gl.glVertexAttribDivisor(vertAttrId, 0);
        gl.glEnableVertexAttribArray(vertAttrId);

        setAtoms(atoms ,drawable);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bufferId);

        gl.glVertexAttribPointer(posAttrId, 3, GL4.GL_FLOAT, false, ATOM_DESCR_LEN*Float.BYTES, 0);
        gl.glVertexAttribDivisor(posAttrId, 1);
        gl.glEnableVertexAttribArray(posAttrId);

        gl.glVertexAttribPointer(colorAttrId, 3, GL4.GL_FLOAT, false, ATOM_DESCR_LEN*Float.BYTES, 3*Float.BYTES);
        gl.glVertexAttribDivisor(colorAttrId, 1);
        gl.glEnableVertexAttribArray(colorAttrId);

        gl.glVertexAttribPointer(radiusAttrId, 1, GL4.GL_FLOAT, false, ATOM_DESCR_LEN*Float.BYTES, 6*Float.BYTES);
        gl.glVertexAttribDivisor(radiusAttrId, 1);
        gl.glEnableVertexAttribArray(radiusAttrId);

        gl.glBindVertexArray(0);
    }

    private void setAtoms(float[] atoms, GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        atomCount = atoms.length / ATOM_DESCR_LEN;

        float[] center = {0f, 0f, 0f};
        float[] min = {1e20f, 1e20f, 1e20f};
        float[] max = {-1e20f, -1e20f, -1e20f};

        for(int i = 0; i < atomCount; ++i){
            float r = atoms[i * ATOM_DESCR_LEN + 6];
            for(int j = 0; j < 3; ++j) {
                float coord = atoms[i * ATOM_DESCR_LEN + j];
                center[j] += coord;
                min[j] = Math.min(min[j], Math.min(coord+r,coord-r));
                max[j] = Math.max(max[j], Math.max(coord+r,coord-r));
            }
        }

        float maxDelta = Math.max(max[0]-min[0], Math.max(max[1]-min[1], max[2]-min[2]));

        projectionMatrix[15] = maxDelta;

        modelMatrix[12] = -center[0]/atomCount;
        modelMatrix[13] = -center[1]/atomCount;
        modelMatrix[14] = -center[2]/atomCount;

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bufferId);
        ByteBuffer data = GlUtils.clone(atoms);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, atoms.length*4, data, GL4.GL_STATIC_DRAW);

        atomsLoaded = true;
    }

    private void initShader (GLAutoDrawable d)
    {
        GL4 gl = d.getGL().getGL4(); // get the OpenGL 3 graphics context

        vertId = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        tescId = gl.glCreateShader(GL4.GL_TESS_CONTROL_SHADER);
        teseId = gl.glCreateShader(GL4.GL_TESS_EVALUATION_SHADER);
        geomId = gl.glCreateShader(GL4.GL_GEOMETRY_SHADER);
        fragId = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);

        String sourceVS = Utils.readResource("/org/structureviewer/struct.vertex");
        String sourceTC = Utils.readResource("/org/structureviewer/struct.tesscontrol");
        String sourceTE = Utils.readResource("/org/structureviewer/struct.tesseval");
        String sourceGS = Utils.readResource("/org/structureviewer/struct.geometry");
        String sourceFS = Utils.readResource("/org/structureviewer/struct.fragment");

        String[] vs = { sourceVS };
        String[] tcs = { sourceTC };
        String[] tes = { sourceTE };
        String[] gs = { sourceGS };
        String[] fs = { sourceFS };

        gl.glShaderSource(vertId, 1, vs, null, 0);
        gl.glShaderSource(tescId, 1, tcs, null, 0);
        gl.glShaderSource(teseId, 1, tes, null, 0);
        gl.glShaderSource(geomId, 1, gs, null, 0);
        gl.glShaderSource(fragId, 1, fs, null, 0);

        // compile the shader
        gl.glCompileShader(vertId);
        GlUtils.printShaderInfoLog(d, vertId);

        gl.glCompileShader(tescId);
        GlUtils.printShaderInfoLog(d, tescId);

        gl.glCompileShader(teseId);
        GlUtils.printShaderInfoLog(d, teseId);

        gl.glCompileShader(geomId);
        GlUtils.printShaderInfoLog(d, geomId);

        gl.glCompileShader(fragId);
        GlUtils.printShaderInfoLog(d, fragId);

        // create program and attach shaders
        programId = gl.glCreateProgram();
        gl.glAttachShader(programId, vertId);
        gl.glAttachShader(programId, tescId);
        gl.glAttachShader(programId, teseId);
        gl.glAttachShader(programId, geomId);
        gl.glAttachShader(programId, fragId);

        // link the program
        gl.glLinkProgram(programId);
        GlUtils.printProgramInfoLog(d, programId);
    }


    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glBindVertexArray(0);
        gl.glUseProgram(0);

        gl.glDetachShader(programId, vertId);
        gl.glDetachShader(programId, tescId);
        gl.glDetachShader(programId, teseId);
        gl.glDetachShader(programId, geomId);
        gl.glDetachShader(programId, fragId);

        gl.glDeleteShader(vertId);
        gl.glDeleteShader(tescId);
        gl.glDeleteShader(teseId);
        gl.glDeleteShader(fragId);
        gl.glDeleteShader(geomId);
        gl.glDeleteProgram(programId);

        gl.glDeleteVertexArrays(1, new int[] {vbo}, 0);
        gl.glDeleteBuffers(3, new int[] {bufferId, ixdArrayId, vertArrayId}, 0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        if(!atomsLoaded){
            setAtoms(atoms, drawable);
        }

        gl.glUseProgram(programId);
        gl.glBindVertexArray(vbo);

        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ixdArrayId);

        gl.glUniform3fv(lightId, 1, lightDirection, 0);

        gl.glUniformMatrix4fv(transId, 1, false, FloatBuffer.wrap(transformMatrix));

        gl.glUniformMatrix4fv(modelMatId, 1, false, FloatBuffer.wrap(modelMatrix));
        gl.glUniformMatrix4fv(projMatId, 1, false, FloatBuffer.wrap(projectionMatrix));

        gl.glPatchParameteri(GL4.GL_PATCH_VERTICES, 3);
        gl.glDrawElementsInstanced(GL4.GL_PATCHES, indices.length, GL4.GL_UNSIGNED_INT, 0, atomCount);

        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glViewport(0, 0, width, height);
    }

    public void setTransformMatrix(float[] mat){
        transformMatrix[0] = mat[0];
        transformMatrix[1] = mat[1];
        transformMatrix[2] = mat[2];
        transformMatrix[3] = 0;
        transformMatrix[4] = mat[3];
        transformMatrix[5] = mat[4];
        transformMatrix[6] = mat[5];
        transformMatrix[7] = 0;
        transformMatrix[8] = mat[6];
        transformMatrix[9] = mat[7];
        transformMatrix[10] = mat[8];
        transformMatrix[11] = 0;
    }

    private P3 toCartesian (
            org.jmulti.calc.P3 a,
            org.jmulti.calc.P3 b,
            org.jmulti.calc.P3 c,
            org.jmulti.calc.P3 coords)
    {
        org.jmulti.calc.P3 x1 = a.mul(coords.x());
        org.jmulti.calc.P3 y1 = b.mul(coords.y());
        org.jmulti.calc.P3 z1 = c.mul(coords.z());

        org.jmulti.calc.P3 r = (x1.plus(y1)).plus(z1);

        return P3.new3((float)r.x(), (float)r.y(), (float)r.z());
    }

    public void setAtoms(AtomDescr[] atomCollection, UnitCell uc, int repeat) {
        atomCount = atomCollection.length;

        double rad = Math.PI / 180;
        org.jmulti.calc.P3 a = new org.jmulti.calc.P3(uc.a(), 0, 0);

        org.jmulti.calc.P3 b = new org.jmulti.calc.P3(uc.b() * Math.cos(uc.beta()*rad), uc.b() * Math.sin(uc.beta()*rad), 0);

        double cz = uc.c() *Math.sqrt(Math.pow(1 - Math.cos(uc.alpha()*rad), 2) - Math.pow(Math.cos(uc.beta()*rad), 2) -
                Math.pow(Math.cos(uc.gamma()*rad), 2) +
                2*Math.cos(uc.alpha()*rad)*Math.cos(uc.beta()*rad)*Math.cos(uc.gamma()*rad))/Math.sin(uc.gamma()*rad);
        org.jmulti.calc.P3 c = new org.jmulti.calc.P3(uc.c() * Math.cos(uc.beta()*rad),
                uc.c() * (Math.cos(uc.alpha() * rad) - Math.cos(uc.beta()*rad)*Math.cos(uc.gamma()*rad))/Math.sin(uc.gamma()*rad),
                cz);

        atoms = new float[atomCount*ATOM_DESCR_LEN];
        int realCount  = 0;
        for(AtomDescr atom: atomCollection){
            P3 coords = toCartesian(a, b, c, atom.p());
            atoms[realCount*ATOM_DESCR_LEN] = coords.x;
            atoms[realCount*ATOM_DESCR_LEN + 1] = coords.y;
            atoms[realCount*ATOM_DESCR_LEN + 2] = coords.z;

            float[] color = GlUtils.colorFromElementName(atom.name());

            atoms[realCount*ATOM_DESCR_LEN + 3] = color[0];
            atoms[realCount*ATOM_DESCR_LEN + 4] = color[1];
            atoms[realCount*ATOM_DESCR_LEN + 5] = color[2];

            atoms[realCount*ATOM_DESCR_LEN + 6] = GlUtils.radiusFromElementName(atom.name());

            ++realCount;
        }

        atomCount = realCount;
    }

    public void setAtoms(AtomSetCollection atomsCollection, ShowAtoms showAtoms) {
        atomCount = atomsCollection.ac;
        atoms = new float[atomCount*ATOM_DESCR_LEN];

        SymmetryInterface symmetry = atomsCollection.getSymmetry();
        if( null != symmetry && atomsCollection.haveUnitCell) {
            symmetry.setUnitCell((float[])atomsCollection.getAtomSetAuxiliaryInfo(0).get("unitCellParams"), false);
        }

        int realCount = 0;

        for(int i = 0; i < atomCount; ++i){
            if(null == symmetry || !symmetry.haveUnitCell()){
                Atom atom = atomsCollection.atoms[i];
                atoms[realCount * ATOM_DESCR_LEN] = atom.x;
                atoms[realCount * ATOM_DESCR_LEN + 1] = atom.y;
                atoms[realCount * ATOM_DESCR_LEN + 2] = atom.z;
            } else {
                Atom atom = atomsCollection.atoms[i];
                symmetry.toCartesian(atom, true);

                atoms[realCount * ATOM_DESCR_LEN] = atom.x;
                atoms[realCount * ATOM_DESCR_LEN + 1] = atom.y;
                atoms[realCount * ATOM_DESCR_LEN + 2] = atom.z;
            }

            String element = atomsCollection.atoms[i].getElementSymbol();

            float[] color = GlUtils.colorFromElementName(element);

            atoms[realCount*ATOM_DESCR_LEN + 3] = color[0];
            atoms[realCount*ATOM_DESCR_LEN + 4] = color[1];
            atoms[realCount*ATOM_DESCR_LEN + 5] = color[2];

            atoms[realCount*ATOM_DESCR_LEN + 6] = GlUtils.radiusFromElementName(element);
            if(ShowAtoms.ALL == showAtoms){
                ++realCount;
            } else if (null != symmetry && symmetry.haveUnitCell()) {
                P3[] ucVectors = symmetry.getUnitCellVectors();
                boolean equivalent_atom = false;
                P3 a = P3.new3(atoms[realCount*ATOM_DESCR_LEN], atoms[realCount*ATOM_DESCR_LEN + 1], atoms[realCount*ATOM_DESCR_LEN + 2]);
                for(int j = 0; j < realCount; ++j){
                    P3 atom = P3.new3(atoms[j*ATOM_DESCR_LEN], atoms[j*ATOM_DESCR_LEN + 1], atoms[j*ATOM_DESCR_LEN + 2]);

                    P3 translatedAtom = P3.newP(atom);
                    translatedAtom.add(ucVectors[1]);
                    if(epsilonEquals(a, translatedAtom, 1e-5f)){
                        equivalent_atom = true;
                        break;
                    }

                    translatedAtom = P3.newP(atom);
                    translatedAtom.add(ucVectors[2]);
                    if(epsilonEquals(a, translatedAtom, 1e-5f)){
                        equivalent_atom = true;
                        break;
                    }

                    translatedAtom = P3.newP(atom);
                    translatedAtom.add(ucVectors[3]);
                    if(epsilonEquals(a, translatedAtom, 1e-5f)){
                        equivalent_atom = true;
                        break;
                    }

                    translatedAtom = P3.newP(atom);
                    translatedAtom.add(ucVectors[1]);
                    translatedAtom.add(ucVectors[3]);
                    if(epsilonEquals(a, translatedAtom, 1e-5f)){
                        equivalent_atom = true;
                        break;
                    }

                    translatedAtom = P3.newP(atom);
                    translatedAtom.add(ucVectors[2]);
                    translatedAtom.add(ucVectors[3]);
                    if(epsilonEquals(a, translatedAtom, 1e-5f)){
                        equivalent_atom = true;
                        break;
                    }

                    translatedAtom = P3.newP(atom);
                    translatedAtom.add(ucVectors[1]);
                    translatedAtom.add(ucVectors[2]);
                    if(epsilonEquals(a, translatedAtom, 1e-5f)){
                        equivalent_atom = true;
                        break;
                    }

                    translatedAtom = P3.newP(atom);
                    translatedAtom.add(ucVectors[1]);
                    translatedAtom.add(ucVectors[2]);
                    translatedAtom.add(ucVectors[3]);
                    if(epsilonEquals(a, translatedAtom, 1e-5f)){
                        equivalent_atom = true;
                        break;
                    }

                }

                if(!equivalent_atom){
                    ++realCount;
                }
            }
        }

        if(ShowAtoms.UNEQUIVALENT == showAtoms){
            float[] realAtoms = new float[realCount*ATOM_DESCR_LEN];
            System.arraycopy(atoms, 0, realAtoms, 0, realCount*ATOM_DESCR_LEN);
            atoms = realAtoms;
            atomCount = realCount;
        }

        atomsLoaded = false;
    }
}
